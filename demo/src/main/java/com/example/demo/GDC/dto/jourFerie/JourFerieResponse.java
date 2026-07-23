package com.example.demo.GDC.dto.jourFerie;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class JourFerieResponse {
    private Long id;
    private String nom;
    private LocalDate date;
    private boolean recurrentAnnuel;
    private String description;
    private Long entrepriseId;
    private String entrepriseNom;
    private boolean actif;
}
