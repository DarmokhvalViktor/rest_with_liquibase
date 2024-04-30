package com.darmokhval.rest_with_liquibase.utility;

import com.darmokhval.rest_with_liquibase.model.dto.CarDTO;
import com.darmokhval.rest_with_liquibase.repository.AccessoryRepository;
import com.darmokhval.rest_with_liquibase.repository.BrandRepository;
import com.darmokhval.rest_with_liquibase.repository.ModelRepository;
import com.darmokhval.rest_with_liquibase.repository.OwnerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

@Component
@RequiredArgsConstructor
public class CarValidator {
    private final ModelRepository modelRepository;
    private final BrandRepository brandRepository;
    private final OwnerRepository ownerRepository;
    private final AccessoryRepository accessoryRepository;

    /**
     * validate given data to save in db.
     * @param carDTO object to validate its fields.
     * @return empty string if no errors or message about error.
     */
    public String validate(CarDTO carDTO) {
        if (carDTO.getModelId() == null || !modelRepository.existsById(carDTO.getModelId())) {
            return "Invalid or missing model ID";
        }

        if (carDTO.getBrandId() == null || !brandRepository.existsById(carDTO.getBrandId())) {
            return "Invalid or missing brand ID";
        }

        if (carDTO.getOwnerId() == null || !ownerRepository.existsById(carDTO.getOwnerId())) {
            return "Invalid or missing owner ID";
        }

        if(carDTO.getYearOfRelease() == null || carDTO.getYearOfRelease() > LocalDate.now().getYear()){
            return "Year of release is missing or greater than the current year.";
        }
        if(carDTO.getMileage() == null || carDTO.getMileage() < 0) {
            return "Mileage cannot be empty or below zero";
        }
        if(carDTO.getWasInAccident() == null) {
            return "Car either was in accident, or not, cannot be empty";
        }

        for (Long accessoryId : carDTO.getAccessoriesIds()) {
            if (accessoryId == null || !accessoryRepository.existsById(accessoryId)) {
                return "Invalid accessory ID: " + accessoryId;
            }
        }

        return "";
    }
}
