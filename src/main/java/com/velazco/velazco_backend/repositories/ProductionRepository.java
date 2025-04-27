package com.velazco.velazco_backend.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import com.velazco.velazco_backend.entities.Production;

public interface ProductionRepository extends JpaRepository<Production, Long> {
}