package com.example.demo.GDO.mappers;

import com.example.demo.GDO.dto.departement.DepartementRequest;
import com.example.demo.GDO.dto.departement.DepartementResponse;
import com.example.demo.GDO.dto.departement.DepartementResponseDetails;
import com.example.demo.GDO.entity.Departement;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

import java.util.List;

@Mapper(componentModel = "spring", uses = {ResponsableMapper.class}, nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface DepartementMapper {

    @Mapping(target = "entreprise", ignore = true)
    @Mapping(target = "responsable", ignore = true)
    Departement toEntity(DepartementRequest request);

    @Mapping(source = "responsable", target = "responsable", qualifiedByName = {})
    DepartementResponse toDto(Departement departement);

    DepartementResponseDetails toDetailsDto(Departement departement);

    List<DepartementResponse> toDtoList(List<Departement> departements);
    List<DepartementResponseDetails> toDetailsDtoList(List<Departement> departements);

    @Mapping(target = "entreprise", ignore = true)
    @Mapping(target = "responsable", ignore = true)
    void updateEntityFromRequest(DepartementRequest request, @MappingTarget Departement departement);
}