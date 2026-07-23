package com.example.demo.GDC.dto.catalogueTypeConge;

import com.example.demo.common.enums.CodeTypeConge;
import com.example.demo.common.enums.PolitiqueFinAnnee;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class CatalogueTypeCongeRequest {

    @NotBlank(message = "Le nom est requis")
    private String nom;

    @NotBlank(message = "La description est requise")
    private String description;

    @NotNull(message = "Le code est requis")
    private CodeTypeConge code;

    private Integer joursParDefautSuggere;

    @NotNull(message = "La politique de fin d'année est requise")
    private PolitiqueFinAnnee politiqueFinAnneeSuggere;

    private Integer maxJoursReportSuggere;

    @DecimalMin(value = "0.01", message = "Le taux d'indemnisation doit être supérieur à 0")
    @DecimalMax(value = "1.0", message = "Le taux d'indemnisation ne peut pas dépasser 1.0 (100%)")
    private BigDecimal tauxIndemnisationSuggere;

    private boolean necessiteJustificatifSuggere;
    private boolean necessiteValidationRhSuggere;
    private boolean sansSoldeSuggere;
}