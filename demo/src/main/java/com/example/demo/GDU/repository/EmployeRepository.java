package com.example.demo.GDU.repository;

import com.example.demo.GDU.entity.Employe;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;


public interface EmployeRepository extends JpaRepository<Employe, Long> {
    Page<Employe> findAllByNomContainingIgnoreCaseAndPrenomContainingIgnoreCase(String nom, String prenom, Pageable pageable);
    Optional<Employe> findByEmail(String email);
    Optional<Employe> findByMatricule(String matricule);

    @Query("SELECT e FROM Employe e JOIN Departement d ON d.responsable = e WHERE d.id = :departementId")
    List<Employe> findResponsablesByDepartementId(@Param("departementId") Long departementId);
}
