package com.antigravity.securedata.controller;

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
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.antigravity.securedata.model.SharedRecord;
import com.antigravity.securedata.model.User;
import com.antigravity.securedata.repository.SharedRecordRepository;
import com.antigravity.securedata.repository.UserRepository;
import com.antigravity.securedata.service.CryptographyService;
import com.fasterxml.jackson.databind.ObjectMapper;

@RestController
@RequestMapping("/doctorB")
@CrossOrigin(origins = "*")
public class DoctorBController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private SharedRecordRepository sharedRecordRepository;

    @Autowired
    private CryptographyService cryptoService;

    @Autowired
    private ObjectMapper objectMapper;

    @GetMapping("/{doctorId}/shared-records")
	public List<SharedRecord> getSharedRecords(@PathVariable("doctorId") String doctorId) {
        User doctorB = userRepository.findById(UUID.fromString(doctorId))
                .orElseThrow(() -> new RuntimeException("Doctor B not found"));
        return sharedRecordRepository.findByDoctorB(doctorB);
    }

    @GetMapping("/{doctorId}/shared-records/{sharedId}/decrypt")
	public String decryptSharedRecord(@PathVariable("doctorId") String doctorId,
			@PathVariable("sharedId") String sharedId) throws Exception {
        User doctorB = userRepository.findById(UUID.fromString(doctorId)).orElseThrow();
        SharedRecord shared = sharedRecordRepository.findById(UUID.fromString(sharedId)).orElseThrow();
        
        byte[] privKeyBytes = Base64.getDecoder().decode(doctorB.getPrivateKey());
        KeyFactory kf = KeyFactory.getInstance("EC", "BC");
        PrivateKey docBPrivKey = kf.generatePrivate(new PKCS8EncodedKeySpec(privKeyBytes));
        
        CryptographyService.EncryptedRecord reEncRecord = objectMapper.readValue(shared.getReEncryptedData(), CryptographyService.EncryptedRecord.class);
        
        // Decrypt using Doctor B's Key (which works because Proxy transformed it to be encryptable by B)
        return cryptoService.decryptData(reEncRecord, docBPrivKey);
    }
}
