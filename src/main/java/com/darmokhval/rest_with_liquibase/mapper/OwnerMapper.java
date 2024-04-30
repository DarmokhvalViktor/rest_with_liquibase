package com.darmokhval.rest_with_liquibase.mapper;

import com.darmokhval.rest_with_liquibase.model.dto.CarDTOLight;
import com.darmokhval.rest_with_liquibase.model.dto.OwnerDTO;
import com.darmokhval.rest_with_liquibase.model.entity.Car;
import com.darmokhval.rest_with_liquibase.model.entity.Owner;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * Utility method, transform Owner entity into DTO and vice versa.
 */
@Component
@RequiredArgsConstructor
public class OwnerMapper {
    private final CarMapper carMapper;

    public OwnerDTO convertOwnerToDTO(Owner owner) {
        OwnerDTO ownerDTO = new OwnerDTO();
        ownerDTO.setId(owner.getId());
        ownerDTO.setName(owner.getName());
        ownerDTO.setLastname(owner.getLastname());
        ownerDTO.setEmail(owner.getEmail());
        List<CarDTOLight> carDTOList = new ArrayList<>();
        for(Car car: owner.getCars()) {
            CarDTOLight carDTO = carMapper.carEntityToDTOLight(car);
            carDTOList.add(carDTO);
        }
        ownerDTO.setCars(carDTOList);
        return ownerDTO;
    }

    public Owner convertDTOToOwnerEntity(OwnerDTO ownerDTO) {
        Owner owner = new Owner();
        owner.setLastname(ownerDTO.getLastname());
        owner.setEmail(ownerDTO.getEmail());
        owner.setName(ownerDTO.getName());
        return owner;
    }
}
