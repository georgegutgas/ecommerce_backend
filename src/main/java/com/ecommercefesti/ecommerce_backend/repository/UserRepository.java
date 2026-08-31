package com.ecommercefesti.ecommerce_backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.ecommercefesti.ecommerce_backend.entity.User;
import org.springframework.stereotype.Repository;

import java.util.Optional;

// UserRepository.java
@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);
}
