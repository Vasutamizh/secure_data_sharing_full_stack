package com.antigravity.securedata.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "shared_records")
public class SharedRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private java.util.UUID id;

    @ManyToOne
    @JoinColumn(name = "original_record_id", nullable = false)
    private MedicalRecord originalRecord;

    @ManyToOne
    @JoinColumn(name = "doctor_b_id", nullable = false)
    private User doctorB;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String reEncryptedData;

    @Column(nullable = false)
    private LocalDateTime sharedAt = LocalDateTime.now();

    public SharedRecord() {}

    public java.util.UUID getId() { return id; }
    public void setId(java.util.UUID id) { this.id = id; }

    public MedicalRecord getOriginalRecord() { return originalRecord; }
    public void setOriginalRecord(MedicalRecord originalRecord) { this.originalRecord = originalRecord; }

    public User getDoctorB() { return doctorB; }
    public void setDoctorB(User doctorB) { this.doctorB = doctorB; }

    public String getReEncryptedData() { return reEncryptedData; }
    public void setReEncryptedData(String reEncryptedData) { this.reEncryptedData = reEncryptedData; }

    public LocalDateTime getSharedAt() { return sharedAt; }
    public void setSharedAt(LocalDateTime sharedAt) { this.sharedAt = sharedAt; }
}
