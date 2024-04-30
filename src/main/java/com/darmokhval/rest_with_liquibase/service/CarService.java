package com.darmokhval.rest_with_liquibase.service;

import com.darmokhval.rest_with_liquibase.exception.IOFileException;
import com.darmokhval.rest_with_liquibase.mapper.CarMapper;
import com.darmokhval.rest_with_liquibase.model.dto.*;
import com.darmokhval.rest_with_liquibase.model.entity.*;
import com.darmokhval.rest_with_liquibase.repository.*;
import com.darmokhval.rest_with_liquibase.utility.*;
import jakarta.annotation.PostConstruct;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.*;

/**
 * Class responsible for CRUD operations upon "Car" entity.
 */
@Service
@RequiredArgsConstructor
public class CarService {
    private final CarMapper carMapper;
    private final CarRepository carRepository;
    private final AccessoryRepository accessoryRepository;
    private final BrandRepository brandRepository;
    private final ModelRepository modelRepository;
    private final OwnerRepository ownerRepository;
    private final CSVUtility csvUtility;
    private final CarSpecificationBuilder carSpecificationBuilder;
    private final FileParser fileParser;
    private final PaginationUtility paginationUtility;
    private final CarValidator carValidator;
    private final RandomFileWithCarsGenerator generator;

    //    Method creates .json file to test upload methods. File contains random data, some of that records are invalid
//    on purpose, and these records shouldn't be written to database;
    @PostConstruct
    public void generateFile() {
        int numberOfRecords = 200;
        String filepath = "src/main/resources/car_data.json";
        generator.generateFile(numberOfRecords, filepath);
    }

    public Page<CarDTOLight> findCars(CarSearchRequest request) {
        // If the request is null, that mean client doesn't specify some filter criteria, so
        // create a default instance to return all cars
        if (request == null) {
            request = new CarSearchRequest(); // All fields are null, resulting in a query for all cars
        }
        PaginationConfig paginationConfig = paginationUtility.checkIfPaginationPresent(request);
        Pageable pageable = paginationUtility.getPageable(paginationConfig);
        Specification<Car> spec = carSpecificationBuilder.getCarSpecification(request);
        Page<Car> filteredCars = carRepository.findAll(spec, pageable);
        return filteredCars.map(carMapper::carEntityToDTOLight);
    }

    /**
     * get data from DB and transform into CSV-supported string. working.
     */
    public String getCarsReport(CarSearchRequest request) {
        if (request == null) {
            request = new CarSearchRequest(); // Default to retrieving all cars
        }
        Specification<Car> spec = carSpecificationBuilder.getCarSpecification(request);
        List<CarDTOLight> filteredCars = carRepository.findAll(spec)
                .stream()
                .map(carMapper::carEntityToDTOLight)
                .toList();
        String csvContent;
        try {
            csvContent = csvUtility.generateCSVString(filteredCars);
        } catch (IOException e) {
            throw new IOFileException("Error generating CSV file due to I/O issue", e);
        }
        return csvContent;
    }

    public CarDTOFullInfo findCar(Long id) {
        Car car = carRepository.findById(id).orElseThrow(
                () -> new IllegalArgumentException(String.format("Car with ID %d was not found", id)));
        return carMapper.carEntityToFullDTO(car);
    }

//    Creating, valid fields.
    @Transactional
    public CarDTO createCar(CarDTO carDTO) {
        Car car = new Car();
        car = processCar(carDTO, car);
        return carMapper.carEntityToDTO(car);
    }

    //update working.
    @Transactional
    public CarDTO updateCar(CarDTO carDTO, Long id) {
        Car car = carRepository.findById(id).orElseThrow(
                () -> new IllegalArgumentException(String.format("Car with ID %d was not found", id)));
        detachUnwantedAccessories(carDTO, car);
        car = processCar(carDTO, car);
        return carMapper.carEntityToDTO(car);
    }

    /**
     * reading from a file, and write to DB if fields is valid.
     * @param multipartFile file to read from.
     * @return Map with failed and success count.
     */
//    TODO add in readme that we are assuming that .json file will be in valid format.
//     Maybe invalid fields, but structure is correct.
//     Plus check and add comments into service layers. Add unit tests for service layers???
    @Transactional
    public Map<String, Long> populateDatabaseFromFile(MultipartFile multipartFile) {
        if(multipartFile == null || multipartFile.isEmpty()) {
            throw new IllegalArgumentException("File shouldn't be null or empty.");
        }
        Map<String, Long> resultMap = new HashMap<>();
        long successfulWrites = 0;
        long failedWrites = 0;

        Set<CarDTO> carDTOList = new HashSet<>();
        failedWrites = fileParser.readFromFile(multipartFile, carDTOList, failedWrites);

        for(CarDTO carDTO: carDTOList) {
            try {
                String error = carValidator.validate(carDTO);
                if(error.isBlank()) {
                    Car car = new Car();
                    assignValues(carDTO, car);
                    successfulWrites++;
                } else {
                    failedWrites++;
                }
            } catch (IllegalArgumentException e) {
                System.out.println("Error processing car: " + e.getMessage());
                failedWrites++;
            }

        }
        System.out.println(failedWrites + "Failed writes");
        resultMap.put("successfulWrites", successfulWrites);
        resultMap.put("failedWrites", failedWrites);
        return resultMap;
    }

    //    Working, delete if exists, when wrong id handled too.
    @Transactional
    public String deleteCar(Long id) {
        if(id == null) {
            throw new IllegalArgumentException("Car ID must be not null.");
        }
        Optional<Car> optionalCar = carRepository.findById(id);
        if(optionalCar.isPresent()) {
            Car car = optionalCar.get();
            for(Accessory accessory: new HashSet<>(car.getAccessories())) {
                car.removeAccessory(accessory.getId());
                accessory.removeCar(car.getId());
            }
            carRepository.deleteById(id);
            return String.format("Car with ID %d was deleted!", id);
        }
        throw new IllegalArgumentException(String.format("Car with ID %d was not found.", id));
    }

    /**
     * utility, save entity into DB if validation passes. Else throws IAE.
     */
    private Car processCar(CarDTO carDTO, Car car) {
        String error = carValidator.validate(carDTO);
        if (!error.isBlank()) {
            throw new IllegalArgumentException(error);
        }
        System.out.println("Car validation is correct, moving to assigning values!!!");
        return assignValues(carDTO, car);
    }

    /**
     * assigns values from dto to entity. Used in conjunction with validation method.
     */
    private Car assignValues(CarDTO carDTO, Car car) {
        Model model = modelRepository.findById(carDTO.getModelId()).get();
        Brand brand = brandRepository.findById(carDTO.getBrandId()).get();
        Owner owner = ownerRepository.findById(carDTO.getOwnerId()).get();

        for (Long accessoryId : carDTO.getAccessoriesIds()) {
            Accessory accessory = accessoryRepository.findById(accessoryId).get();
            car.addAccessory(accessory);
        }

        car.setBrand(brand);
        car.setModel(model);
        car.setOwner(owner);
        car.setYearOfRelease(carDTO.getYearOfRelease());
        car.setMileage(carDTO.getMileage());
        car.setWasInAccident(carDTO.getWasInAccident());

        model.addCar(car);
        brand.addCar(car);
        owner.addCar(car);

        return carRepository.save(car);
    }

    /**
     * In this method we are detaching entities/removing if they are not in a new Set passed by user.
     */
    private void detachUnwantedAccessories(CarDTO carDTO, Car car) {
        System.out.println("detachUnwantedAccessories executed!!!");
        List<Long> newAccessoryIds = new ArrayList<>(carDTO.getAccessoriesIds());
        List<Accessory> tempSet = new ArrayList<>(car.getAccessories());

        for (Accessory existingAccessory : tempSet) {
            if (!newAccessoryIds.contains(existingAccessory.getId())) {
                car.removeAccessory(existingAccessory.getId());
                existingAccessory.removeCar(car.getId());
            }
        }
    }

}