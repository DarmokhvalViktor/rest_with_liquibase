package com.darmokhval.rest_with_liquibase.service;

import com.darmokhval.rest_with_liquibase.exception.IOFileException;
import com.darmokhval.rest_with_liquibase.mapper.CarMapper;
import com.darmokhval.rest_with_liquibase.model.dto.*;
import com.darmokhval.rest_with_liquibase.model.entity.*;
import com.darmokhval.rest_with_liquibase.repository.*;
import com.darmokhval.rest_with_liquibase.utility.CSVUtility;
import com.darmokhval.rest_with_liquibase.utility.RandomFileWithCarsGenerator;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDate;
import java.util.*;

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
    private final ObjectMapper objectMapper;
    private final RandomFileWithCarsGenerator fileGenerator;

//    Method creates .json file to test upload methods. File contains random data, some of that records are invalid
//    on purpose, and these records shouldn't be written to database;
    @PostConstruct
    public void generateFile() {
        int numberOfRecords = 200;
        String filepath = "src/main/resources/car_data.json";
        fileGenerator.generateFile(numberOfRecords, filepath);
    }

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

    /**
     * get data from DB and transform into CSV-supported string. working.
     */
    public String getCarsReport(CarSearchRequest request) {
        if (request == null) {
            request = new CarSearchRequest(); // Default to retrieving all cars
        }
        Specification<Car> spec = getCarSpecification(request);
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
    public CarDTO createCar(CarDTO carDTO) {
        Car car = new Car();
        car = processCar(carDTO, car);
        return carMapper.carEntityToDTO(car);
    }

    //update working.
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
    public Map<String, Long> populateDatabaseFromFile(MultipartFile multipartFile) {
        Map<String, Long> resultMap = new HashMap<>();
        long successfulWrites = 0;
        long failedWrites = 0;

        Set<CarDTO> carDTOList = new HashSet<>();
        failedWrites = readFromFile(multipartFile, carDTOList, failedWrites);

        for(CarDTO carDTO: carDTOList) {
            String error = validateEntities(carDTO);
            if(error.isBlank()) {
                Car car = new Car();
                car = assignValues(carDTO, car);
//                carRepository.save(car);
                successfulWrites++;
            } else {
                failedWrites++;
            }
        }
        System.out.println(failedWrites + "Failed writes");
        resultMap.put("successfulWrites", successfulWrites);
        resultMap.put("failedWrites", failedWrites);
        return resultMap;
    }

//    TODO refactor code and extract methods into separate classes.

    private long readFromFile(MultipartFile multipartFile, Set<CarDTO> carDTOList, long failedWrites) {
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        try {
            JsonNode jsonNode = objectMapper.readTree(multipartFile.getInputStream());
            for (JsonNode node : jsonNode) {
                try {
                    // Attempt to deserialize each JSON node into a CarDTO
                    CarDTO carDTO = objectMapper.treeToValue(node, CarDTO.class);
                    carDTOList.add(carDTO);
                } catch (Exception e) {
                    failedWrites++;
                }
            }
        } catch (IOException e) {
            throw new IOFileException(String.format("Error occurred while trying to read a file %s",
                    multipartFile.getName()));
        }
        System.out.println(failedWrites + "Failed writes");
        return failedWrites;
    }


    //    Working, delete if exists, when wrong id handled too.
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
        String error = validateEntities(carDTO);
        if (!error.isBlank()) {
            throw new IllegalArgumentException(error);
        }
        System.out.println("Car validation is correct, moving to assigning values!!!");
        return assignValues(carDTO, car);
//        return carRepository.save(car);
    }

    /**
     * validate given data to save in db.
     * @param carDTO object to validate its fields.
     * @return empty string if no errors or message about error.
     */
    private String validateEntities(CarDTO carDTO) {
        if (!modelRepository.existsById(carDTO.getModelId())) {
            return "Invalid model ID";
        }

        if (!brandRepository.existsById(carDTO.getBrandId())) {
            return "Invalid brand ID";
        }

        if (!ownerRepository.existsById(carDTO.getOwnerId())) {
            return "Invalid owner ID";
        }

        if(carDTO.getYearOfRelease() > LocalDate.now().getYear()){
            return "Year of release cannot be greater than the current year.";
        }

        for (Long accessoryId : carDTO.getAccessoriesIds()) {
            if (!accessoryRepository.existsById(accessoryId)) {
                return "Invalid accessory ID: " + accessoryId;
            }
        }

        return "";
    }

    /**
     * assigns values from dto to entity. Not validating values, so could cause error while writing to DB!.
     * Use with validation method above.
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

    private PaginationConfig checkIfPaginationPresent(CarSearchRequest request) {
        if(request.getPaginationConfig() == null) {
            return new PaginationConfig(0, 20);
        } else {
            return request.getPaginationConfig();
        }
    }

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
     * Method verbose on purpose to avoid NullPointerException.
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
