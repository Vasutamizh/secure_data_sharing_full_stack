package com.antigravity.securedata.controller;

import com.antigravity.securedata.dto.CreateUserRequest;
import com.antigravity.securedata.model.User;
import com.antigravity.securedata.repository.UserRepository;
import com.antigravity.securedata.service.CryptographyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.security.KeyPair;
import java.util.List;

@RestController
@RequestMapping("/users")
@CrossOrigin(origins = "*")
public class UserController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CryptographyService cryptoService;

    @GetMapping
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    @PostMapping
    public User createUser(@RequestBody CreateUserRequest request) throws Exception {
        if (request.getName() == null || request.getName().trim().isEmpty()) {
            throw new RuntimeException("User name is required.");
        }
        if (request.getRole() == null || request.getRole().trim().isEmpty()) {
            throw new RuntimeException("User role is required.");
        }

        User.Role role;
        try {
            role = User.Role.valueOf(request.getRole().toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new RuntimeException("Invalid role. Must be PATIENT, DOCTOR_A, or DOCTOR_B.");
        }

        // Generate ECC key pair for the new user
        KeyPair keyPair = cryptoService.generateKeyPair();

        User user = new User();
        user.setName(request.getName().trim());
        user.setRole(role);
        user.setPublicKey(cryptoService.encodePublicKey(keyPair.getPublic()));
        user.setPrivateKey(cryptoService.encodePrivateKey(keyPair.getPrivate()));

        return userRepository.save(user);
    }
}
