package com.antigravity.securedata.repository;

import com.antigravity.securedata.model.SharedRecord;
import com.antigravity.securedata.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.UUID;
import java.util.List;

public interface SharedRecordRepository extends JpaRepository<SharedRecord, UUID> {
    List<SharedRecord> findByDoctorB(User doctorB);
}
