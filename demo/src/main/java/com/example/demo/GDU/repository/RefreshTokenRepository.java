package com.example.demo.GDU.repository;

import com.example.demo.GDU.entity.RefreshToken;
import com.example.demo.GDU.entity.Utilisateur;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {
    Optional<RefreshToken> findByToken(String token);
    @Modifying
    @Query("DELETE FROM RefreshToken r WHERE r.utilisateur = :utilisateur")
    void deleteByUtilisateur(Utilisateur utilisateur);
}