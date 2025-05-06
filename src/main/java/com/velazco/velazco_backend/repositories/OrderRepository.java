package com.velazco.velazco_backend.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import com.velazco.velazco_backend.entities.Order;

public interface OrderRepository extends JpaRepository<Order, Long> {

}