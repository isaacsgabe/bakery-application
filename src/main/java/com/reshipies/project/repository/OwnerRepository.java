package com.reshipies.project.repository;

import com.reshipies.project.model.Owner;
import org.springframework.data.jpa.repository.JpaRepository;


public interface OwnerRepository extends JpaRepository<Owner, Long> {
    Owner findByUsername(String username);
}
