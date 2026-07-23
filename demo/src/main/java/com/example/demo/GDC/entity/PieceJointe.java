package com.example.demo.GDC.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "pieces_jointes")
public class PieceJointe {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne
    @JoinColumn(name = "demande_conge_id")
    private DemandeConge demandeConge;
    @ManyToOne
    @JoinColumn(name = "retour_conge_id")
    private RetourConge retourConge;
    private String nomFichier;
    private String cheminFichier;
    private String typeMime;
    private Long tailleOctets;
    @CreationTimestamp
    private LocalDateTime dateUpload;
}