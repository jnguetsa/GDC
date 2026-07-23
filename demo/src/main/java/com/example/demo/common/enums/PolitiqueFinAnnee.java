package com.example.demo.common.enums;

public enum PolitiqueFinAnnee {

    NOUVEAU_DEPART,  // jours non pris perdus définitivement au 31 déc
    REPORTER,        // jours transférés vers N+1, plafonnés par maxJoursReport
    PAYER            // jours indemnisés financièrement (taux défini par tauxIndemnisation)
}