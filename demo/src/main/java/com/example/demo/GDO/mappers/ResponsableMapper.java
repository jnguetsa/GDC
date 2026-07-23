package com.example.demo.GDO.mappers;

import com.example.demo.GDO.dto.departement.ResponsableInfo;
import com.example.demo.GDU.entity.Employe;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface ResponsableMapper {
    ResponsableInfo toResponsableInfo(Employe employe);
}