package com.example.demo.GDU.services.serviceImpl;

import com.example.demo.GDO.entity.Departement;
import com.example.demo.GDO.exception.DepartementNotFoundException;
import com.example.demo.GDO.repository.DepartementReepository;
import com.example.demo.GDU.dto.employe.EmployeRequest;
import com.example.demo.GDU.dto.employe.EmployeResponse;
import com.example.demo.GDU.entity.Employe;
import com.example.demo.GDU.entity.Role;
import com.example.demo.GDU.exeption.EmployeNotFoundException;
import com.example.demo.GDU.exeption.RoleNotFoundException;
import com.example.demo.GDU.mappers.EmployeMapper;
import com.example.demo.GDU.repository.EmployeRepository;
import com.example.demo.GDU.repository.RoleRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class EmployeService {
    private final EmployeMapper employerMapper;
    private final EmployeRepository employerRepository;
    private final RoleRepository roleRepository;
    private final RoleService roleService;
    private final DepartementReepository departementRepository;

    public EmployeResponse getEmployer(Long id){
        Employe employe = employerRepository.findById(id).orElseThrow(()->new EmployeNotFoundException("Employer introuvable"));
        return toResponse(employe);
    }

    public void deleteEmployer(Long id) {
        Employe employe = employerRepository.findById(id).orElseThrow(()->new EmployeNotFoundException("Employer introuvable"));
        employerRepository.delete(employe);
    }

    public EmployeResponse addEmployer(EmployeRequest request) {
        log.info("Création d'un employé avec l'email : {}", request.getEmail());
        employerRepository.findByEmail(request.getEmail()).ifPresent(e -> {
            log.warn("Email déjà utilisé : {}", request.getEmail());
            throw new EmployeNotFoundException("Un employé avec cet email existe déjà : " + request.getEmail());
        });
        Employe employe = employerMapper.toEntity(request);
        employe = employerRepository.save(employe);
        log.info("Employé créé avec succès. Id : {}", employe.getId());
        return toResponse(employe);
    }

    public EmployeResponse addRoleToEmployer(Long employerId, Long roleId) {
        log.info("Ajout du rôle {} à l'employé {}", roleId, employerId);
        Employe employe = employerRepository.findById(employerId)
                .orElseThrow(() -> new EmployeNotFoundException("Employé introuvable avec l'id : " + employerId));
        Role role = roleRepository.findById(roleId)
                .orElseThrow(() -> new RoleNotFoundException("Rôle introuvable avec l'id : " + roleId));
        employe.getRoles().add(role);
        log.info("Rôle {} ajouté avec succès à l'employé {}", role.getNom(), employe.getNom());
        return toResponse(employerRepository.save(employe));
    }

    public EmployeResponse updateEmployer(Long id, EmployeRequest request) {
        log.info("Modification de l'employé avec l'id : {}", id);
        Employe employe = employerRepository.findById(id)
                .orElseThrow(() -> new EmployeNotFoundException("Employé introuvable avec l'id : " + id));

        if (request.getDepartementId() != null) {
            Departement ancienDept = employe.getDepartement();
            boolean changeDepartement = ancienDept != null &&
                !ancienDept.getId().equals(request.getDepartementId());

            if (changeDepartement &&
                ancienDept.getResponsable() != null &&
                ancienDept.getResponsable().getId().equals(employe.getId())) {
                ancienDept.setResponsable(null);
                departementRepository.save(ancienDept);
                log.info("Employé {} retiré du poste de responsable du département {}", employe.getNom(), ancienDept.getNom());
            }

            Departement nouveauDept = departementRepository.findById(request.getDepartementId())
                .orElseThrow(() -> new DepartementNotFoundException("Département introuvable avec l'id : " + request.getDepartementId()));
            employe.setDepartement(nouveauDept);
        }

        employerMapper.updateEntityFromRequest(request, employe);
        log.info("Employé {} {} modifié avec succès", employe.getNom(), employe.getPrenom());
        return toResponse(employerRepository.save(employe));
    }

    @Transactional(readOnly = true)
    public List<EmployeResponse> getAllEmployers() {
        log.info("Récupération de tous les employés");
        List<EmployeResponse> employers = employerRepository.findAll()
                .stream().map(this::toResponse).toList();
        log.info("{} employé(s) trouvé(s)", employers.size());
        return employers;
    }

    @Transactional(readOnly = true)
    public List<EmployeResponse> getResponsablesByDepartement(Long departementId) {
        log.info("Récupération des responsables du département {}", departementId);
        List<EmployeResponse> responsables = employerRepository.findResponsablesByDepartementId(departementId)
                .stream().map(this::toResponse).toList();
        log.info("{} responsable(s) trouvé(s)", responsables.size());
        return responsables;
    }

    private EmployeResponse toResponse(Employe employe) {
        EmployeResponse response = employerMapper.toDto(employe);
        response.setResponsable(
            employe.getDepartement() != null &&
            employe.getDepartement().getResponsable() != null &&
            employe.getDepartement().getResponsable().getId().equals(employe.getId())
        );
        return response;
    }


}