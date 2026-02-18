package com.antigravity.securedata.controller;

import java.math.BigInteger;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.util.Base64;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.antigravity.securedata.dto.ShareRecordRequest;
import com.antigravity.securedata.model.MedicalRecord;
import com.antigravity.securedata.model.SharedRecord;
import com.antigravity.securedata.model.User;
import com.antigravity.securedata.repository.MedicalRecordRepository;
import com.antigravity.securedata.repository.SharedRecordRepository;
import com.antigravity.securedata.repository.UserRepository;
import com.antigravity.securedata.service.CryptographyService;
import com.fasterxml.jackson.databind.ObjectMapper;

@RestController
@RequestMapping("/patient")
@CrossOrigin(origins = "*")
public class PatientController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private MedicalRecordRepository medicalRecordRepository;

    @Autowired
    private SharedRecordRepository sharedRecordRepository;

    @Autowired
    private CryptographyService cryptoService;

    @Autowired
    private ObjectMapper objectMapper;

	@GetMapping("/{patientId}/records")
	public List<MedicalRecord> getRecords(@PathVariable("patientId") String patientId) {
		System.out.println("Inside getRecords method.");
		User patient = userRepository.findById(UUID.fromString(patientId))
				.orElseThrow(() -> new RuntimeException("Patient not found"));
		return medicalRecordRepository.findByPatient(patient);
	}
    
    // Decrypt record for Patient (Demo purpose: Patient sees own data)
    @GetMapping("/{patientId}/records/{recordId}/decrypt")
	public String decryptRecord(@PathVariable("patientId") String patientId, @PathVariable("recordId") String recordId)
			throws Exception {
        User patient = userRepository.findById(UUID.fromString(patientId)).orElseThrow();
        MedicalRecord record = medicalRecordRepository.findById(UUID.fromString(recordId)).orElseThrow();
        
        // Recover Patient Private Key (Simulated, in real app key is on client)
        // We decode it from DB
        byte[] privKeyBytes = Base64.getDecoder().decode(patient.getPrivateKey());
        KeyFactory kf = KeyFactory.getInstance("EC", "BC");
        PrivateKey patientPrivKey = kf.generatePrivate(new PKCS8EncodedKeySpec(privKeyBytes));
        
        CryptographyService.EncryptedRecord encForPatient = objectMapper.readValue(record.getEncryptedData(), CryptographyService.EncryptedRecord.class);
        
        return cryptoService.decryptData(encForPatient, patientPrivKey);
    }

    @PostMapping("/share")
    public SharedRecord shareRecord(@RequestBody ShareRecordRequest request) throws Exception {
        MedicalRecord originalRecord = medicalRecordRepository.findById(UUID.fromString(request.getRecordId()))
                .orElseThrow(() -> new RuntimeException("Record not found"));
        
        User patient = originalRecord.getPatient();
        User doctorB = userRepository.findById(UUID.fromString(request.getTargetDoctorId()))
                .orElseThrow(() -> new RuntimeException("Doctor B not found"));
        
        // --- Proxy Re-Encryption Logic ---
        // 1. Generate Re-Encryption Key (Simulated: Patient authorizes this)
        // We need Patient Priv Key and Doctor B Priv Key? NO.
        // The Service method 'generateReEncryptionKey' needs PrivateKeys of both for the math: d_B - d_A.
        // Wait, 'generateReEncryptionKey' implementation: return dB.subtract(dA).
        // This means we need BOTH private keys loaded in memory.
        // In this demo, since we store encrypted private keys (or simulated keys), we load them.
        
        byte[] patientPrivBytes = Base64.getDecoder().decode(patient.getPrivateKey());
        byte[] docBPrivBytes = Base64.getDecoder().decode(doctorB.getPrivateKey());
        
        KeyFactory kf = KeyFactory.getInstance("EC", "BC");
        PrivateKey patientPrivKey = kf.generatePrivate(new PKCS8EncodedKeySpec(patientPrivBytes));
        PrivateKey docBPrivKey = kf.generatePrivate(new PKCS8EncodedKeySpec(docBPrivBytes));
        
        // Generate Token
        BigInteger rk = cryptoService.generateReEncryptionKey(patientPrivKey, docBPrivKey);
        
        // 2. Perform Transformation (Proxy Action)
        // Load original ciphertext
        CryptographyService.EncryptedRecord encRecord = objectMapper.readValue(originalRecord.getEncryptedData(), CryptographyService.EncryptedRecord.class);
        
        // Transform
        CryptographyService.EncryptedRecord reEncRecord = cryptoService.reEncrypt(encRecord, rk);
        
        // Save Shared Record
        String reEncJson = objectMapper.writeValueAsString(reEncRecord);
        
        SharedRecord shared = new SharedRecord();
        shared.setOriginalRecord(originalRecord);
        shared.setDoctorB(doctorB);
        shared.setReEncryptedData(reEncJson);
        
        return sharedRecordRepository.save(shared);
    }
}
