package com.example.demo.GDU.repository;

import com.example.demo.GDU.entity.Role;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface RoleRepository extends JpaRepository<Role, Long> {
    Optional<Role> findByNom(String nom);
    List<Role> findByActif(boolean actif);
}
