package com.ecommercefesti.ecommerce_backend.repository;

import com.ecommercefesti.ecommerce_backend.entity.Order;
import com.ecommercefesti.ecommerce_backend.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {

    // Buscar por ID de usuario
    List<Order> findByUserIdOrderByCreatedAtDesc(Long userId);

    // Buscar directamente por el email del usuario
    List<Order> findByUserEmailOrderByCreatedAtDesc(String email);

    // Buscar por usuario
    List<Order> findByUser(User user);
}