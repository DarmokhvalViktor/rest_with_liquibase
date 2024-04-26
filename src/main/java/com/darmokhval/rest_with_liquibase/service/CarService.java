package com.darmokhval.rest_with_liquibase.service;

import com.darmokhval.rest_with_liquibase.mapper.CarMapper;
import com.darmokhval.rest_with_liquibase.model.dto.*;
import com.darmokhval.rest_with_liquibase.model.entity.*;
import com.darmokhval.rest_with_liquibase.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CarService {
    private final CarMapper carMapper;
    private final CarRepository carRepository;
    private final AccessoryRepository accessoryRepository;
    private final BrandRepository brandRepository;
    private final ModelRepository modelRepository;
    private final OwnerRepository ownerRepository;

    public Page<CarDTOLight> findCars(CarSearchRequest request) {
        // If the request is null, that mean client doesn't specify some filter criteria, so
        // create a default instance to return all cars
        if (request == null) {
            request = new CarSearchRequest(); // All fields are null, resulting in a query for all cars
        }
        PaginationConfig paginationConfig = checkIfPaginationPresent(request);
        Pageable pageable = getPageable(paginationConfig);
        Specification<Car> spec = getCarSpecification(request);
        Page<Car> filteredCars = carRepository.findAll(spec, pageable);
        return filteredCars.map(carMapper::carEntityToDTOLight);
    }

//    Creating, valid fields.
    public CarDTO createCar(CarDTO carDTO) {
        Car car = new Car();
        assignValuesToCarEntityIfValid(carDTO, car);
        car = carRepository.save(car);
        return carMapper.carEntityToDTO(car);
    }

    public CarDTOFullInfo findCar(Long id) {
        Car car = carRepository.findById(id).orElseThrow(
                () -> new IllegalArgumentException(String.format("Car with ID %d was not found", id)));
        return carMapper.carEntityToFullDTO(car);
    }

    //update working.
    public CarDTO updateCar(CarDTO carDTO, Long id) {
        Car car = carRepository.findById(id).orElseThrow(
                () -> new IllegalArgumentException(String.format("Car with ID %d was not found", id)));
        assignValuesToCarEntityIfValid(carDTO, car);
        car = carRepository.save(car);
        return carMapper.carEntityToDTO(car);
    }

    /**
     * Method, receives dto and checks if data correct. If yes -> populate car entity passed as 2nd arg.
     * @param carDTO clients input to get data from
     * @param car resulting entity that will be returned in successful case
     * @throws IllegalArgumentException if argument can't be validated.
     */
    private void assignValuesToCarEntityIfValid(CarDTO carDTO, Car car) {
        detachUnwantedAccessories(carDTO, car);
        Model model = modelRepository.findById(carDTO.getModelId())
                .orElseThrow(() -> new IllegalArgumentException("Invalid model ID"));
        Brand brand = brandRepository.findById(carDTO.getBrandId())
                .orElseThrow(() -> new IllegalArgumentException("Invalid brand ID"));
        Owner owner = ownerRepository.findById(carDTO.getOwnerId())
                .orElseThrow(() -> new IllegalArgumentException("Invalid owner ID"));
        Set<Accessory> newAccessories = new HashSet<>();
        for(Long accessoryId: carDTO.getAccessoriesIds()) {
            Accessory accessory = accessoryRepository.findById(accessoryId)
                    .orElseThrow(() -> new IllegalArgumentException("Invalid accessory ID"));
            newAccessories.add(accessory);
        }
        validateYearOfRelease(carDTO.getYearOfRelease());
        car.setBrand(brand);
        car.setModel(model);
        car.setOwner(owner);
        car.setAccessories(newAccessories);
        car.setYearOfRelease(carDTO.getYearOfRelease());
        car.setMileage(carDTO.getMileage());
        car.setWasInAccident(carDTO.getWasInAccident());

        model.addCar(car);
        brand.addCar(car);
        owner.addCar(car);
        for (Accessory accessory: newAccessories) {
            accessory.addCar(car);
        }
    }

    /**
     * In this method we are detaching entities/removing if they are not in a new Set passed by user.
     */
    private void detachUnwantedAccessories(CarDTO carDTO, Car car) {
        Set<Long> newAccessoryIds = new HashSet<>(carDTO.getAccessoriesIds());
        Set<Accessory> tempSet = new HashSet<>(car.getAccessories());
        for (Accessory existingAccessory : tempSet) {
            if (!newAccessoryIds.contains(existingAccessory.getId())) {
                car.removeAccessory(existingAccessory.getId());
                existingAccessory.removeCar(car.getId());
            }
        }
    }

    private void validateYearOfRelease(int year) {
        int currentYear = LocalDate.now().getYear();
        if (year > currentYear) {
            throw new IllegalArgumentException("Year of release cannot be greater than the current year.");
        }
    }

    //    Working, delete if exists, when wrong id handled too.
    public String deleteCar(Long id) {
        if(id == null) {
            throw new IllegalArgumentException("Car ID must be not null.");
        }
        if(!carRepository.existsById(id)) {
            throw new IllegalArgumentException(String.format("Car with ID %d was not found.", id));
        }
        carRepository.deleteById(id);
        return String.format("Car with ID %d was deleted!", id);
    }


    private PaginationConfig checkIfPaginationPresent(CarSearchRequest request) {
        if(request.getPaginationConfig() == null) {
            return new PaginationConfig(0, 20);
        } else {
            return request.getPaginationConfig();
        }
    };

    /**
     * check for input how many pages per page requested. If number not 20, 50 or 100, change it to 20 per page.
     * Returns Pageable
     */
    private Pageable getPageable(PaginationConfig paginationConfig) {
        int resultsPerPage = switch (paginationConfig.getResultsPerPage()) {
            case 20, 50, 100 -> paginationConfig.getResultsPerPage();
            default -> 20;
        };
        return PageRequest.of(paginationConfig.getPageNumber(), resultsPerPage);
    }

    /**
     *create specification from user's input to return records that matches input fields.
     * Method purposely verbose to avoid NPE.
     */
    private Specification<Car> getCarSpecification(CarSearchRequest request) {
        Specification<Car> spec = Specification.where(null); //starting with empty specs.
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
