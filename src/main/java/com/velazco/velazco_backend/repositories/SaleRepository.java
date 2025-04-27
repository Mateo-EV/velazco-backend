package com.velazco.velazco_backend.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import com.velazco.velazco_backend.entities.Sale;

public interface SaleRepository extends JpaRepository<Sale, Long> {
}