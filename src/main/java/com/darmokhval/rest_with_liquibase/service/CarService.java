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
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.*;

@Service
@RequiredArgsConstructor
@Slf4j
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
        String filepath = "src/main/resources/car_data0.json";
        generator.generateFile(numberOfRecords, filepath);
    }

    /**
     * returns page with Car entity in "light" format. If no page/number per page is specified, default values are used.
     * If no filter criteria is specified, method will return all cars available.
     */
    public Page<CarDTOLight> findCars(CarSearchRequest request) {
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
     * get data from DB and transform into CSV-supported string, in controller converts into bytes[] to behave as file.
     */
    public String getCarsReport(CarSearchRequest request) {
        if (request == null) {
            request = new CarSearchRequest(); // Default empty implementation to retrieve all cars
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

    /**
     * Return full info about one Car entity, if ID is valid, otherwise -> throws IAE.
     */
    public CarDTOFullInfo findCar(Long id) {
        Car car = carRepository.findById(id).orElseThrow(
                () -> new IllegalArgumentException(String.format("Car with ID %d was not found", id)));
        return carMapper.carEntityToFullDTO(car);
    }

    /**
     * Creates Car record in database if fields are valid.
     */
    @Transactional
    public CarDTO createCar(CarDTO carDTO) {
        Car car = new Car();
        car = processCar(carDTO, car);
        return carMapper.carEntityToDTO(car);
    }

    /**
     * Updates Car entity by ID if new data passed and ID are valid, else throws IAE.
     * Uses utility method to safely remove relations from entities.
     */
    @Transactional
    public CarDTO updateCar(CarDTO carDTO, Long id) {
        Car car = carRepository.findById(id).orElseThrow(
                () -> new IllegalArgumentException(String.format("Car with ID %d was not found", id)));
        detachUnwantedAccessories(carDTO, car);
        car = processCar(carDTO, car);
        return carMapper.carEntityToDTO(car);
    }

    /**
     * Reading from a file, and writes records into DB if fields are valid.
     * Uses utility methods to parse file, validate result and assign values.
     * @param multipartFile file to read from.
     * @return Map with failed and success count.
     */
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
                log.error("\"Error processing car: \" + e.getMessage()");
                failedWrites++;
            }

        }
        log.info(failedWrites + "Failed writes");
        resultMap.put("successfulWrites", successfulWrites);
        resultMap.put("failedWrites", failedWrites);
        return resultMap;
    }

    //    Working, delete if exists, when wrong id handled too.

    /**
     * Deletes Car record from a database if passed ID is valid, otherwise -> throws IAE.
     * Upon delete process removing all existing relations that are connected to entity.
     */
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
     * Utility method, saves entity into DB if validation passes, otherwise throws IAE.
     */
    private Car processCar(CarDTO carDTO, Car car) {
        String error = carValidator.validate(carDTO);
        if (!error.isBlank()) {
            throw new IllegalArgumentException(error);
        }
        log.info("Car validation is correct, moving to assigning values");
        return assignValues(carDTO, car);
    }
//    TODO test how parse file with only 1 record, or without array.

    /**
     * Assigns values from dto to entity. Used in conjunction with validation method.
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
     * In this method we are detaching entities/removing if they are not in a new List passed by user.
     */
    private void detachUnwantedAccessories(CarDTO carDTO, Car car) {
        log.info("detachUnwantedAccessories executed");
        List<Long> newAccessoryIds = new ArrayList<>(carDTO.getAccessoriesIds());
        List<Accessory> tempList = new ArrayList<>(car.getAccessories());

        for (Accessory existingAccessory : tempList) {
            if (!newAccessoryIds.contains(existingAccessory.getId())) {
                car.removeAccessory(existingAccessory.getId());
                existingAccessory.removeCar(car.getId());
            }
        }
    }

}