package com.example.demo.GDC.entity;

import com.example.demo.GDU.entity.Employe;
import com.example.demo.common.enums.TypeAction;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "historique_actions")
public class HistoriqueAction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "employe_id", nullable = false)
    private Employe employe;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TypeAction action;

    private String entiteType;
    private Long entiteId;

    @Column(columnDefinition = "TEXT")
    private String details;

    private String adresseIp;
    private String userAgent;

    @CreationTimestamp
    private LocalDateTime dateAction;
}