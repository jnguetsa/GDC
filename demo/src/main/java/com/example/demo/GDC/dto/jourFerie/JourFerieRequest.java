package com.example.demo.GDC.dto.jourFerie;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class JourFerieRequest {
    @NotBlank(message = "Le nom est requis")
    private String nom;
    @NotNull(message = "La date est requise")
    private LocalDate date;
    private boolean recurrentAnnuel;
    private String description;
    @NotNull(message = "L'entreprise est requise")
    private Long entrepriseId;
}
