package com.example.demo.common.enums;

public enum StatutRetour {
    EN_ATTENTE,         // retour pas encore enregistré
    A_L_HEURE,          // retour effectif = date prévue
    EN_RETARD,          // retour effectif > date prévue, justifié
    ABSENT_INJUSTIFIE,  // retour effectif > date prévue, sans justificatif
    RETOUR_ANTICIPE     // retour effectif < date prévue
}