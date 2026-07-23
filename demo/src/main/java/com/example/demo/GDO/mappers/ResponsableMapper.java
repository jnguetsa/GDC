package com.example.demo.GDO.mappers;

import com.example.demo.GDU.dto.employe.EmployeInfo;
import com.example.demo.GDU.entity.Employe;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ResponsableMapper {

    @Mapping(source = "departement.id", target = "departementId")
    @Mapping(source = "departement.nom", target = "departementNom")
    EmployeInfo toEmployeInfo(Employe employe);
}