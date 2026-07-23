package com.example.demo.GDO.mappers;

import com.example.demo.GDO.dto.entreprise.EntrepriseRequest;
import com.example.demo.GDO.dto.entreprise.EntrepriseResponse;
import com.example.demo.GDO.entity.Entreprise;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

import java.util.List;

@Mapper(componentModel = "spring", uses = {DepartementMapper.class}, nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface EntrepriseMapper {
    Entreprise toEntity(EntrepriseRequest request);
    EntrepriseResponse toDto(Entreprise entreprise);
    List<EntrepriseResponse> toDtoList(List<Entreprise> entreprises);
    void updateEntityFromRequest(EntrepriseRequest request, @MappingTarget Entreprise entreprise);
}