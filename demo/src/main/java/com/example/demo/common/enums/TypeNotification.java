package com.example.demo.common.enums;

public enum TypeNotification {
    // Demande de congé
    DEMANDE_SOUMISE,
    DEMANDE_APPROUVEE_MANAGER,
    DEMANDE_APPROUVEE_RH,
    DEMANDE_REJETEE,
    DEMANDE_ANNULEE,

    // Retour de congé
    RETOUR_PREVU_DEMAIN,       // rappel la veille du retour prévu
    RETOUR_EN_RETARD,          // l'employé n'est pas revenu à la date prévue
    RETOUR_ANTICIPE,           // l'employé est revenu avant la fin du congé
    RETOUR_ENREGISTRE,         // le RH a enregistré le retour

    // Solde
    SOLDE_FAIBLE,              // solde restant sous un seuil (ex: < 3 jours)
    SOLDE_EXPIRE,              // des jours vont expirer bientôt
    SOLDE_AJUSTE,              // le RH a modifié manuellement le solde

    // Exercice
    EXERCICE_CLOTURE,          // clôture annuelle effectuée
    JOURS_REPORTES,            // jours reportés sur l'année suivante
    JOURS_PERDUS               // jours non reportables perdus à la clôture
}