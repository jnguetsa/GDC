package com.example.demo.GDC.dto.notification;

import com.example.demo.common.enums.TypeNotification;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class NotificationResponse {

    private Long id;
    private Long destinataireId;
    private String titre;
    private String message;
    private TypeNotification type;
    private String lienAction;

    // Contexte optionnel
    private Long demandeCongeId;
    private Long retourCongeId;

    private boolean lue;
    private LocalDateTime dateLecture;
    private boolean envoyeeParEmail;
    private LocalDateTime dateEnvoiEmail;
    private LocalDateTime dateCreation;
}