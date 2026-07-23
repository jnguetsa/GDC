package com.example.demo.GDC.dto.catalogueTypeConge;

import com.example.demo.common.enums.CodeTypeConge;
import com.example.demo.common.enums.PolitiqueFinAnnee;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class CatalogueTypeCongeResponse {

    private Long id;

    private String nom;
    private String description;
    private CodeTypeConge code;

    private Integer joursParDefautSuggere;
    private PolitiqueFinAnnee politiqueFinAnneeSuggere;
    private Integer maxJoursReportSuggere;           // pertinent si politiqueFinAnneeSuggere = REPORTER
    private BigDecimal tauxIndemnisationSuggere;     // pertinent si politiqueFinAnneeSuggere = PAYER

    private boolean necessiteJustificatifSuggere;
    private boolean necessiteValidationRhSuggere;
    private boolean sansSoldeSuggere;

    private boolean actif;
}