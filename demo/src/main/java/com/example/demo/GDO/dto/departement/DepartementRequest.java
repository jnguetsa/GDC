package com.example.demo.GDO.dto.departement;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class DepartementRequest {

    @NotBlank(message = "Le nom du département est obligatoire")
    @Size(max = 100, message = "Le nom ne peut pas dépasser 100 caractères")
    private String nom;

    @Size(max = 20, message = "Le code ne peut pas dépasser 20 caractères")
    private String code;

    @Size(max = 255, message = "La description ne peut pas dépasser 255 caractères")
    private String description;

    @NotNull(message = "L'entreprise est obligatoire")
    private Long entrepriseId;

    private Long responsableId;

}
