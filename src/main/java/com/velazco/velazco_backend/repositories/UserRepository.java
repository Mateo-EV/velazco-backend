package com.velazco.velazco_backend.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import com.velazco.velazco_backend.entities.User;

public interface UserRepository extends JpaRepository<User, Long> {
}