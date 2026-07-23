package com.example.demo.GDU.repository;

import com.example.demo.GDU.entity.Permission;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface PermissionRepository extends JpaRepository<Permission, Long> {
    Optional<Permission> findByNom(String nom);
    List<Permission> findByActif(boolean actif);
}
