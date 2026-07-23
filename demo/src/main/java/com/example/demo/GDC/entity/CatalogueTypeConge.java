package com.example.demo.GDC.entity;

import com.example.demo.common.enums.CodeTypeConge;
import com.example.demo.common.enums.PolitiqueFinAnnee;
import jakarta.persistence.*;

import java.math.BigDecimal;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "catalogue_types_conge")
public class CatalogueTypeConge {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String nom;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, unique = true)
    private CodeTypeConge code;
    private Integer joursParDefautSuggere;

    @Enumerated(EnumType.STRING)
    private PolitiqueFinAnnee politiqueFinAnneeSuggere = PolitiqueFinAnnee.NOUVEAU_DEPART;
    private Integer maxJoursReportSuggere;
    private BigDecimal tauxIndemnisationSuggere;
    private boolean necessiteJustificatifSuggere;
    private boolean necessiteValidationRhSuggere;
    private boolean sansSoldeSuggere;
    private boolean actif = true;
}