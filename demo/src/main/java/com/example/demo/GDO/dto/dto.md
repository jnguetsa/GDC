package com.example.demo.GDO.dto.departement;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class DepartementResponse {
private Long id;
private String nom;
private String code;
private String description;
private Long responsableId;
private String responsableNom;
private boolean actif;
private int nbrEmpl;
private LocalDateTime dateCreation;
}

package com.example.demo.GDO.dto.departement;

import com.example.demo.GDU.dto.employe.EmployeResponse;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class DepartementResponseDetails {
private Long id;
private String nom;
private String code;
private ResponsableInfo responsable;
private boolean actif;
private int nbrEmpl;
private List<EmployeResponse> employes;
}

package com.example.demo.GDO.dto.departement;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ResponsableInfo {
private Long id;
private String nom;
private String prenom;
private String email;
private String telephone;
private String poste;
private String photoProfil;
}
package com.example.demo.GDU.dto.employe;

import com.example.demo.GDU.dto.role.RoleResponse;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.Set;

@Getter
@Setter
public class EmployeResponse {
private Long id;
private String nom;
private String prenom;
private String email;
private String telephone;
private String adresse;
private LocalDate dateEmbauche;
private LocalDate dateDebut;
private LocalDate dateFin;
private Set<RoleResponse> roles;
}
package com.example.demo.GDU.dto.role;

import com.example.demo.GDU.dto.permission.PermissionResponse;
import lombok.Getter;
import lombok.Setter;

import java.util.Set;

@Getter
@Setter
public class RoleResponse {
private Long id;
private String nom;
private String description;
private Boolean active;
private Set<PermissionResponse> permissions;
}
