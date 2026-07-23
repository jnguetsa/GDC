package com.example.demo.GDU.repository;

import com.example.demo.GDU.entity.Utilisateur;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface UtilisateurRepository extends JpaRepository<Utilisateur, Long> {

    // Charge roles ET permissions en une seule requête — évite LazyInitializationException dans getAuthorities()
    @Query("SELECT u FROM Utilisateur u LEFT JOIN FETCH u.roles r LEFT JOIN FETCH r.permissions WHERE u.email = :email")
    Optional<Utilisateur> findByEmailWithRolesAndPermissions(@Param("email") String email);

    Optional<Utilisateur> findByEmail(String email);
}
