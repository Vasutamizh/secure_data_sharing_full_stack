package com.antigravity.securedata.config;

import com.antigravity.securedata.model.User;
import com.antigravity.securedata.repository.UserRepository;
import com.antigravity.securedata.service.CryptographyService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.security.KeyPair;

@Configuration
public class DataInitializer {

    @Bean
    CommandLineRunner init(UserRepository userRepository, CryptographyService cryptoService) {
        return args -> {
            if (userRepository.count() == 0) {
                // Create Patient
                KeyPair patientKeys = cryptoService.generateKeyPair();
                User patient = new User();
                patient.setName("Alice Patient");
                patient.setRole(User.Role.PATIENT);
                patient.setPublicKey(cryptoService.encodePublicKey(patientKeys.getPublic()));
                patient.setPrivateKey(cryptoService.encodePrivateKey(patientKeys.getPrivate()));
                userRepository.save(patient);
                System.out.println("Created Patient: " + patient.getId());

                // Create Doctor A
                KeyPair docAKeys = cryptoService.generateKeyPair();
                User docA = new User();
                docA.setName("Dr. Smith (Primary)");
                docA.setRole(User.Role.DOCTOR_A);
                docA.setPublicKey(cryptoService.encodePublicKey(docAKeys.getPublic()));
                docA.setPrivateKey(cryptoService.encodePrivateKey(docAKeys.getPrivate()));
                userRepository.save(docA);
                System.out.println("Created Doctor A: " + docA.getId());

                // Create Doctor B
                KeyPair docBKeys = cryptoService.generateKeyPair();
                User docB = new User();
                docB.setName("Dr. Jones (Specialist)");
                docB.setRole(User.Role.DOCTOR_B);
                docB.setPublicKey(cryptoService.encodePublicKey(docBKeys.getPublic()));
                docB.setPrivateKey(cryptoService.encodePrivateKey(docBKeys.getPrivate()));
                userRepository.save(docB);
                System.out.println("Created Doctor B: " + docB.getId());
            }
        };
    }
}
