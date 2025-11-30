package org.example.customerservice.mapper;

import org.example.customerservice.dto.CustomerDto;
import org.example.customerservice.entity.Customer;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface CustomerMapper {
    
    CustomerMapper INSTANCE = Mappers.getMapper(CustomerMapper.class);
    
    CustomerDto toDto(Customer customer);
    
    Customer toEntity(CustomerDto customerDto);
    
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    void updateEntityFromDto(CustomerDto customerDto, @MappingTarget Customer customer);
}

