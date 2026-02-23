package com.antigravity.securedata.dto;

public class CreateUserRequest {
    private String name;
    private String role; // "PATIENT", "DOCTOR_A", "DOCTOR_B"

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }
}
