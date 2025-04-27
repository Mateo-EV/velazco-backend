package com.velazco.velazco_backend.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import com.velazco.velazco_backend.entities.Role;

public interface RoleRepository extends JpaRepository<Role, Long> {
}