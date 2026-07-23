package com.example.demo.GDC.entity;

import com.example.demo.GDU.entity.Employe;
import com.example.demo.common.enums.TypeNotification;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "notifications")
public class Notification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "destinataire_id", nullable = false)
    private Employe destinataire;

    @Column(nullable = false)
    private String titre;

    @Column(columnDefinition = "TEXT")
    private String message;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TypeNotification type;

    private String lienAction;

    // Lien vers la demande concernée (optionnel)
    @ManyToOne
    @JoinColumn(name = "demande_conge_id")
    private DemandeConge demandeConge;

    // Lien vers le retour concerné (optionnel)
    @ManyToOne
    @JoinColumn(name = "retour_conge_id")
    private RetourConge retourConge;

    private boolean lue;
    private LocalDateTime dateLecture;

    private boolean envoyeeParEmail;
    private LocalDateTime dateEnvoiEmail;

    @CreationTimestamp
    private LocalDateTime dateCreation;
}