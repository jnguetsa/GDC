package com.example.demo.GDO.dto.departement;

import com.example.demo.GDU.dto.employe.EmployeInfo;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class DepartementResponseDetails {
    private Long id;
    private String nom;
    private String code;
    private String description;
    private EmployeInfo responsable;
    private boolean actif;
    private int nbrEmpl;
    private LocalDateTime dateCreation;
    private List<EmployeInfo> employes;
}