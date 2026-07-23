package com.example.demo.GDO.entity;

import com.example.demo.GDU.entity.Employe;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "departements")
public class Departement {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String nom;

    private String code;
    private String description;

    @ManyToOne
    @JoinColumn(name = "entreprise_id", nullable = false)
    private Entreprise entreprise;

    @ManyToOne
    @JoinColumn(name = "responsable_id")
    private Employe responsable;

    @OneToMany(mappedBy = "departement")
    private List<Employe> employes;

    private boolean actif;

    @CreationTimestamp
    private LocalDateTime dateCreation;
}