package com.antigravity.securedata.repository;

import com.antigravity.securedata.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.UUID;
import java.util.Optional;
import java.util.List;

public interface UserRepository extends JpaRepository<User, UUID> {
    Optional<User> findByName(String name);
    List<User> findByRole(User.Role role);
}
