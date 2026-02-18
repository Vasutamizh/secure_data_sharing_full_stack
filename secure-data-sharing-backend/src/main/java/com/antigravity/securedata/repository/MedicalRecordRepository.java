package com.antigravity.securedata.repository;

import com.antigravity.securedata.model.MedicalRecord;
import com.antigravity.securedata.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.UUID;
import java.util.List;

public interface MedicalRecordRepository extends JpaRepository<MedicalRecord, UUID> {
    List<MedicalRecord> findByPatient(User patient);
    List<MedicalRecord> findByDoctorA(User doctorA);
}
