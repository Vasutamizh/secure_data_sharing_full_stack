package com.antigravity.securedata.controller;

import com.antigravity.securedata.dto.CreateRecordRequest;
import com.antigravity.securedata.model.MedicalRecord;
import com.antigravity.securedata.model.User;
import com.antigravity.securedata.repository.MedicalRecordRepository;
import com.antigravity.securedata.repository.UserRepository;
import com.antigravity.securedata.service.CryptographyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;
import java.util.UUID;

@RestController
@RequestMapping("/doctorA")
@CrossOrigin(origins = "*") // Allow localhost frontend
public class DoctorAController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private MedicalRecordRepository medicalRecordRepository;

    @Autowired
    private CryptographyService cryptoService;

    @Autowired
    private ObjectMapper objectMapper;

    @PostMapping("/create-record")
    public MedicalRecord createRecord(@RequestBody CreateRecordRequest request) throws Exception {
        User doctor = userRepository.findById(UUID.fromString(request.getDoctorId()))
                .orElseThrow(() -> new RuntimeException("Doctor not found"));
        User patient = userRepository.findById(UUID.fromString(request.getPatientId()))
                .orElseThrow(() -> new RuntimeException("Patient not found"));

        // encryption logic:
        // Doctor encrypts data using Patient's Public Key.
        // We need to decode Patient's Public Key.
        byte[] pubKeyBytes = Base64.getDecoder().decode(patient.getPublicKey());
        KeyFactory kf = KeyFactory.getInstance("EC", "BC");
        PublicKey patientPubKey = kf.generatePublic(new X509EncodedKeySpec(pubKeyBytes));
        
        // Doctor needs their own Private Key to sign? No, encryption doesn't require sender PrivKey unless Authenticated Encryption.
        // Our 'encryptData' method signature demands senderPriv.
        // Wait, why? -> encryptData(data, senderPriv, receiverPub) ? 
        // Ah, typically ECIES doesn't need Sender Priv.
        // But our 'encryptData' method uses 'new KeyPair()' internally? No.
        // Let's check 'encryptData' signature in CryptoService.
        // It asks for `PrivateKey senderPriv`. 
        // Why? -> C1 = rG. C2 = ... logic doesn't use sender Priv.
        // NOTE: I implemented it as taking `senderPriv` but inside it logic:
        // `ECPoint C2 = sG.add(Q.multiply(r));`
        // It DOES NOT use senderPriv. It was a leftover in signature.
        // I will pass null or dummy.
        
        // Encrypt the diagnosis
        CryptographyService.EncryptedRecord encRecord = cryptoService.encryptData(
                request.getDiagnosis(), 
                null, // Sender priv key not used in standard ElGamal encryption part
                patientPubKey
        );
        
        // Serialize EncryptedRecord to JSON string for storage
        String encryptedJson = objectMapper.writeValueAsString(encRecord);

        MedicalRecord record = new MedicalRecord();
        record.setDoctorA(doctor);
        record.setPatient(patient);
        record.setEncryptedData(encryptedJson);
        record.setDescription("Diagnosis by " + doctor.getName());

        return medicalRecordRepository.save(record);
    }
}
