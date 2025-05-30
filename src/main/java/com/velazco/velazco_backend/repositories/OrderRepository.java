package com.velazco.velazco_backend.repositories;

import java.time.LocalDateTime;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import com.velazco.velazco_backend.entities.Order;

import jakarta.transaction.Transactional;

public interface OrderRepository extends JpaRepository<Order, Long> {

    Page<Order> findByStatus(Order.OrderStatus status, Pageable pageable);

    @Transactional
    void deleteByStatusAndDateBefore(Order.OrderStatus status, LocalDateTime date);

}