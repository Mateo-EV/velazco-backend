package com.velazco.velazco_backend.repositories;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import com.velazco.velazco_backend.entities.Order;

public interface OrderRepository extends JpaRepository<Order, Long> {
    
    @EntityGraph(attributePaths = { "details", "details.product", "attendedBy" })
    Page<Order> findByStatus(Order.OrderStatus status, Pageable pageable);

    List<Order> findByStatusAndDateBefore(Order.OrderStatus status, LocalDateTime date);

}
