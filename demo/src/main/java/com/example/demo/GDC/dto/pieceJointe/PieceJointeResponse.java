package com.example.demo.GDC.dto.pieceJointe;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class PieceJointeResponse {
    private Long id;
    private String nomFichier;
    private String typeMime;
    private Long tailleOctets;
    private LocalDateTime dateUpload;
}

