package com.darmokhval.rest_with_liquibase.utility;

import com.darmokhval.rest_with_liquibase.model.dto.AccessoryDTO;
import com.darmokhval.rest_with_liquibase.model.dto.CarSearchRequest;
import com.darmokhval.rest_with_liquibase.model.entity.Car;
import com.darmokhval.rest_with_liquibase.repository.CarSpecification;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

@Component
public class CarSpecificationBuilder {
    /**
     *create specification from user's input to return records that matches input fields.
     * Method verbose on purpose to avoid NullPointerException.
     */
    public Specification<Car> getCarSpecification(CarSearchRequest request) {
        Specification<Car> spec = Specification.where(null); //starting with empty specs
        if(request == null) {
            return spec;
        }
        if(request.getBrand() != null) {
            if(request.getBrand().getBrandName() != null) {
                spec = spec.and(CarSpecification.hasBrandByName(request.getBrand().getBrandName()));
            }
            if(request.getBrand().getId() != null) {
                spec = spec.and(CarSpecification.hasBrandById(request.getBrand().getId()));
            }
        }
        if(request.getModel() != null) {
            if(request.getModel().getModelName() != null) {
                spec = spec.and(CarSpecification.hasModelByName(request.getModel().getModelName()));
            }
            if(request.getModel().getId() != null) {
                spec = spec.and(CarSpecification.hasModelById(request.getModel().getId()));
            }
        }
        if(request.getWasInAccident() != null) {
            spec = spec.and(CarSpecification.hasWasInAccident(request.getWasInAccident()));
        }
        if(request.getYearOfRelease() != null) {
            spec = spec.and(CarSpecification.hasYearOfRelease(request.getYearOfRelease()));
        }
        if(request.getMileage() != null) {
            spec = spec.and(CarSpecification.hasMileage(request.getMileage()));
        }
        if(request.getOwner() != null) {
            if(request.getOwner().getId() != null) {
                spec = spec.and(CarSpecification.hasOwnerId(request.getOwner().getId()));
            }
            if(request.getOwner().getName() != null) {
                spec = spec.and(CarSpecification.hasOwnerName(request.getOwner().getName()));
            }
            if(request.getOwner().getLastname() != null) {
                spec = spec.and(CarSpecification.hasOwnerLastname(request.getOwner().getLastname()));
            }
        }
        if(request.getAccessoryDTOList() != null) {
            for(AccessoryDTO accessoryDTO : request.getAccessoryDTOList()) {
                if(accessoryDTO.getId() != null) {
                    spec = spec.and(CarSpecification.hasAccessoryById(accessoryDTO.getId()));
                }
                if(accessoryDTO.getAccessoryName() != null) {
                    spec = spec.and(CarSpecification.hasAccessoryByAccessoryName(accessoryDTO.getAccessoryName()));
                }
            }
        }
        return spec;
    }
}
