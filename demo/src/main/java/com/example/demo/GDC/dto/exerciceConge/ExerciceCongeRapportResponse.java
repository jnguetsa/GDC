package com.example.demo.GDC.dto.exerciceConge;

import com.example.demo.GDO.dto.entreprise.EntrepriseResponse;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class ExerciceCongeRapportResponse {

    private Integer annee;
    private EntrepriseResponse entreprise;
    private int totalEmployes;
    private Double totalJoursPris;
    private Double totalJoursEnAttente;
    private Double totalJoursRestants;

    private List<DepartementExerciceInfo> parDepartement;
}