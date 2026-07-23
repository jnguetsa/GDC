package com.example.demo.GDC.dto.historiqueAction;

import com.example.demo.GDU.dto.employe.EmployeInfo;
import com.example.demo.common.enums.TypeAction;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class HistoriqueActionResponse {

    private Long id;
    private EmployeInfo employe;
    private TypeAction action;

    // Entité concernée (ex: "DemandeConge", id=42)
    private String entiteType;
    private Long entiteId;

    private String details;
    private String adresseIp;
    private LocalDateTime dateAction;
}