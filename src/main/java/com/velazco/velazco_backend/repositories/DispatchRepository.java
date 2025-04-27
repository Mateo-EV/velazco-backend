package com.velazco.velazco_backend.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import com.velazco.velazco_backend.entities.Dispatch;

public interface DispatchRepository extends JpaRepository<Dispatch, Long> {
}