package com.example.demo.common.enums;

public enum CodeTypeConge {

    CA,    // Congé annuel — le plus courant, décompté du solde principal
    CM,    // Congé maladie — nécessite un justificatif médical
    CSS,   // Congé sans solde — déduit du salaire, pas du solde de jours
    CP,    // Congé paternité — lié au genre (vérifié à la soumission)
    CM2,   // Congé maternité — lié au genre (vérifié à la soumission)
    CE,    // Congé exceptionnel (deuil, mariage, naissance...)
    CF,    // Congé de formation — peut être payé ou non selon politique
    AUTRE; // Valeur par défaut pour tout type non référencé ci-dessus

    // Valeur utilisée quand un RH crée un type de congé personnalisé
    // qui ne correspond à aucun code standard.
    // Permet de ne pas bloquer la création tout en gardant un enum typé.
    public static CodeTypeConge defaut() {
        return AUTRE;
    }
}

/*
 * Pourquoi un enum pour le code et non un String libre ?
 *
 *   String libre → risque de fautes de frappe ("ca", "CA", "Ca" seraient différents),
 *   impossible de faire un switch/case fiable dans le service pour appliquer
 *   des règles métier selon le type (ex: vérifier le genre pour CP/CM2).
 *
 *   Enum → typage fort, autocomplétion, switch exhaustif possible, et
 *   validation automatique par Spring (Jackson lève une erreur si la valeur
 *   reçue n'existe pas dans l'enum).
 *
 * AUTRE comme valeur par défaut :
 *   Les entreprises ont souvent des types de congé spécifiques non listés ici.
 *   AUTRE leur permet de créer ces types sans erreur de validation,
 *   tout en gardant les codes standards pour les règles métier automatiques.
 *
 * Utilisation dans TypeConge :
 *   @Enumerated(EnumType.STRING) — stocké en BDD comme "CA", "CM", "AUTRE"...
 *   Lisible directement en SQL, contrairement à l'ordinal.
 */
