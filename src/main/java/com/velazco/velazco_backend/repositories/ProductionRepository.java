package com.velazco.velazco_backend.repositories;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import com.velazco.velazco_backend.entities.Production;

public interface ProductionRepository extends JpaRepository<Production, Long> {
  
  List<Production> findProductionsByProductionDate(LocalDate date);

  List<Production> findByStatusIn(List<Production.ProductionStatus> statuses);

  List<Production> findByStatus(Production.ProductionStatus status);

  boolean existsByProductionDate(LocalDate productionDate);

}