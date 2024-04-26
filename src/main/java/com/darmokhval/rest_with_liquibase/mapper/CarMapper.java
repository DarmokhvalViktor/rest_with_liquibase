package com.darmokhval.rest_with_liquibase.mapper;

import com.darmokhval.rest_with_liquibase.model.dto.*;
import com.darmokhval.rest_with_liquibase.model.entity.Accessory;
import com.darmokhval.rest_with_liquibase.model.entity.Car;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Component
public class CarMapper {

//TODO in post we are receiving only id or name? brand/model, to get from DB and assign that to car?
//    TODO validation of carDTO, make sure that we passing valid data.
//    TODO dto to entity only when saving???
//    TODO entity to dto, for retrieving

    public CarDTOLight carEntityToDTOLight(Car car) {
        CarDTOLight carDTO = new CarDTOLight();
        carDTO.setBrand(car.getBrand().getBrandName());
        carDTO.setModel(car.getModel().getModelName());
        carDTO.setId(car.getId());
        carDTO.setOwnerId(car.getOwner().getId());
        carDTO.setOwnerName(car.getOwner().getName());
        return carDTO;
    }

    public CarDTO carEntityToDTO(Car car) {
        CarDTO carDTO = new CarDTO();
        carDTO.setId(car.getId());
        carDTO.setBrandId(car.getBrand().getId());
        carDTO.setModelId(car.getModel().getId());
        carDTO.setMileage(car.getMileage());
        carDTO.setWasInAccident(car.getWasInAccident());
        carDTO.setYearOfRelease(car.getYearOfRelease());
        carDTO.setOwnerId(car.getOwner().getId());
        List<Long> accessoriesIds = new ArrayList<>();
        for(Accessory accessory : car.getAccessories()) {
            accessoriesIds.add(accessory.getId());
        }
        carDTO.setAccessoriesIds(accessoriesIds);
        return carDTO;
    }

    public CarDTOFullInfo carEntityToFullDTO(Car car) {
        CarDTOFullInfo carDTO = new CarDTOFullInfo();
        carDTO.setId(car.getId());
        carDTO.setBrandDTO(new BrandDTO(car.getBrand().getId(), car.getBrand().getBrandName()));
        carDTO.setModelDTO(new ModelDTO(car.getModel().getId(), car.getModel().getModelName()));
        carDTO.setOwnerDTO(new OwnerDTOSimple(car.getOwner().getId(), car.getOwner().getName(),
                car.getOwner().getLastname(), car.getOwner().getEmail()));
        carDTO.setMileage(car.getMileage());
        carDTO.setWasInAccident(car.getWasInAccident());
        carDTO.setYearOfRelease(car.getYearOfRelease());
        Set<AccessoryDTO> accessoryDTOS = new HashSet<>();
        for(Accessory accessory: car.getAccessories()) {
            accessoryDTOS.add(accessoryEntityToDTO(accessory));
        }
        carDTO.setAccessoryDTOList(accessoryDTOS);
        return carDTO;
    }

    private AccessoryDTO accessoryEntityToDTO(Accessory accessory) {
        AccessoryDTO accessoryDTO = new AccessoryDTO();
        accessoryDTO.setId(accessory.getId());
        accessoryDTO.setAccessoryName(accessory.getAccessoryName());
        return accessoryDTO;
    }
}
