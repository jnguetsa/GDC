package com.example.demo.GDC.dto.demandeConge;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DemandeCongeAnnulationRequest {

    @NotBlank(message = "Le motif d'annulation est requis")
    private String motifAnnulation;
}