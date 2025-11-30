package org.example.customerservice.mapper;

import org.example.customerservice.dto.PersonDto;
import org.example.customerservice.entity.Person;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface PersonMapper {
    
    PersonMapper INSTANCE = Mappers.getMapper(PersonMapper.class);
    
    PersonDto toDto(Person person);
    
    Person toEntity(PersonDto personDto);
    
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    void updateEntityFromDto(PersonDto personDto, @MappingTarget Person person);
}

