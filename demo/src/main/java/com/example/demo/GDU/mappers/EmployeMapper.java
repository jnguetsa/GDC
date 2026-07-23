package com.example.demo.GDU.mappers;

import com.example.demo.GDU.dto.employe.EmployeRequest;
import com.example.demo.GDU.dto.employe.EmployeResponse;
import com.example.demo.GDU.entity.Employe;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

import java.util.List;

@Mapper(componentModel = "spring", uses = {RoleMapper.class}, nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface EmployeMapper {

    Employe toEntity(EmployeRequest request);

    @Mapping(source = "departement.id", target = "departementId")
    @Mapping(source = "departement.nom", target = "departementNom")
    @Mapping(target = "responsable", ignore = true)
    EmployeResponse toDto(Employe employe);

    List<EmployeResponse> toDtoList(List<Employe> employes);
    void updateEntityFromRequest(EmployeRequest request, @MappingTarget Employe employe);
}
