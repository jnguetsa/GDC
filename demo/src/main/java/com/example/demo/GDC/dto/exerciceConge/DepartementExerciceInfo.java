package com.example.demo.GDC.dto.exerciceConge;

import com.example.demo.GDO.dto.departement.DepartementResponseDetails;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class DepartementExerciceInfo {
    private DepartementResponseDetails departement;
    private List<ExerciceCongeResponse> exercices;
    private int nombreEmployes;
    private Double totalJoursPris;
    private Double totalJoursEnAttente;
    private Double totalJoursRestants;
}
