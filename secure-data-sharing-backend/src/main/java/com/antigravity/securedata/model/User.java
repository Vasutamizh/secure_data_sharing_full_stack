package com.antigravity.securedata.model;

import jakarta.persistence.*;

@Entity
@Table(name = "users")
public class User {
    
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private java.util.UUID id;

    @Column(nullable = false)
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role;

    @Column(columnDefinition = "TEXT")
    private String publicKey; 

    @Column(columnDefinition = "TEXT") 
    private String privateKey; 

    public enum Role {
        PATIENT,
        DOCTOR_A,
        DOCTOR_B
    }

    public User() {}

    public User(java.util.UUID id, String name, Role role, String publicKey, String privateKey) {
        this.id = id;
        this.name = name;
        this.role = role;
        this.publicKey = publicKey;
        this.privateKey = privateKey;
    }

    public java.util.UUID getId() { return id; }
    public void setId(java.util.UUID id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public Role getRole() { return role; }
    public void setRole(Role role) { this.role = role; }

    public String getPublicKey() { return publicKey; }
    public void setPublicKey(String publicKey) { this.publicKey = publicKey; }

    public String getPrivateKey() { return privateKey; }
    public void setPrivateKey(String privateKey) { this.privateKey = privateKey; }
}
