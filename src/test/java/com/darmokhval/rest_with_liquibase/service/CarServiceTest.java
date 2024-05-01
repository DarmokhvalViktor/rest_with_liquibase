package com.darmokhval.rest_with_liquibase.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.darmokhval.rest_with_liquibase.mapper.CarMapper;
import com.darmokhval.rest_with_liquibase.model.dto.*;
import com.darmokhval.rest_with_liquibase.model.entity.*;
import com.darmokhval.rest_with_liquibase.repository.*;
import com.darmokhval.rest_with_liquibase.utility.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.jpa.domain.Specification;

import java.io.IOException;
import java.util.*;

@ExtendWith(MockitoExtension.class)
public class CarServiceTest {
    @Mock
    private OwnerRepository ownerRepository;
    @Mock
    private CarRepository carRepository;
    @Mock
    private CarMapper carMapper;
    @Mock
    private AccessoryRepository accessoryRepository;
    @Mock
    private BrandRepository brandRepository;
    @Mock
    private ModelRepository modelRepository;
    @Mock
    private CSVUtility csvUtility;
    @Mock
    private FileParser fileParser;
    @Mock
    private CarSpecificationBuilder carSpecificationBuilder;
    @Mock
    private PaginationUtility paginationUtility;
    @Mock
    private CarValidator carValidator;
    @Mock
    private RandomFileWithCarsGenerator generator;
    @InjectMocks
    private CarService carService;
    @BeforeEach
    public void setUp() {
        carService = new CarService(carMapper,
                carRepository,
                accessoryRepository,
                brandRepository,
                modelRepository,
                ownerRepository,
                csvUtility,
                carSpecificationBuilder,
                fileParser,
                paginationUtility,
                carValidator,
                generator);
    }

    @Test
    public void testFindCars_WithNullRequest() {
        Brand brandToyota = new Brand(1L, "Toyota");
        Brand brandHonda = new Brand(2L, "Honda");

        Car car1 = new Car();
        car1.setId(1L);
        car1.setBrand(brandToyota);
        car1.setModel(new Model(1L, "Camry"));
        car1.setOwner(new Owner(1L, "John", "Doe", "john.doe@example.com"));

        Car car2 = new Car();
        car2.setId(2L);
        car2.setBrand(brandHonda);
        car2.setModel(new Model(2L, "Civic"));
        car2.setOwner(new Owner(2L, "Jane", "Doe", "jane.doe@example.com"));
        Page<Car> carPage = new PageImpl<>(Arrays.asList(car1, car2));

        Specification<Car> specification = mock(Specification.class);
        Pageable pageable = mock(Pageable.class);
        when(paginationUtility.checkIfPaginationPresent(any(CarSearchRequest.class)))
                .thenReturn(new PaginationConfig());
        when(paginationUtility.getPageable(any(PaginationConfig.class)))
                .thenReturn(pageable);
        when(carSpecificationBuilder.getCarSpecification(any(CarSearchRequest.class)))
                .thenReturn(specification);
        when(carRepository.findAll(specification, pageable))
                .thenReturn(carPage);
        when(carMapper.carEntityToDTOLight(car1))
                .thenReturn(new CarDTOLight(1L,  "Camry", "Toyota","John Doe", 101L));
        when(carMapper.carEntityToDTOLight(car2))
                .thenReturn(new CarDTOLight(2L,  "Civic", "Honda","Jane Doe", 102L));

        Page<CarDTOLight> result = carService.findCars(null);

        assertEquals(2, result.getTotalElements());
        assertTrue(result.getContent().stream().anyMatch(c -> c.getBrand().equals("Toyota")));
        assertTrue(result.getContent().stream().anyMatch(c -> c.getBrand().equals("Honda")));
    }

    @Test
    public void testFindCars_WithSpecificRequest() {
        Brand brandToyota = new Brand(1L, "Toyota");
        Brand brandHonda = new Brand(2L, "Honda");

        Car car1 = new Car();
        car1.setId(1L);
        car1.setBrand(brandToyota);
        car1.setModel(new Model(1L, "Camry"));
        car1.setOwner(new Owner(1L, "John", "Doe", "john.doe@example.com"));

        Car car2 = new Car();
        car2.setId(2L);
        car2.setBrand(brandHonda);
        car2.setModel(new Model(2L, "Civic"));
        car2.setOwner(new Owner(2L, "Jane", "Doe", "jane.doe@example.com"));

        Page<Car> carPage = new PageImpl<>(Arrays.asList(car1, car2));
        Specification<Car> specification = mock(Specification.class);
        Pageable pageable = mock(Pageable.class);
        when(paginationUtility.checkIfPaginationPresent(any(CarSearchRequest.class)))
                .thenReturn(new PaginationConfig());
        when(paginationUtility.getPageable(any(PaginationConfig.class)))
                .thenReturn(pageable);
        when(carSpecificationBuilder.getCarSpecification(any(CarSearchRequest.class)))
                .thenReturn(specification);
        when(carRepository.findAll(specification, pageable))
                .thenReturn(carPage);
        when(carMapper.carEntityToDTOLight(car1))
                .thenReturn(new CarDTOLight(1L, "Camry", "Toyota", "John Doe", 101L));
        when(carMapper.carEntityToDTOLight(car2))
                .thenReturn(new CarDTOLight(2L, "Civic", "Honda", "Jane Doe", 102L));

        CarSearchRequest request = new CarSearchRequest();
        request.setBrand(new BrandDTO(1L, "Toyota"));
        Page<CarDTOLight> result = carService.findCars(request);

        assertEquals(2, result.getTotalElements());
        assertTrue(result.getContent().stream().anyMatch(c -> c.getBrand().equals("Toyota")));
        assertTrue(result.getContent().stream().anyMatch(c -> c.getBrand().equals("Honda")));
    }

    @Test
    public void testFindCar() {
        Car car = new Car();
        when(carRepository.findById(1L)).thenReturn(Optional.of(car));
        when(carMapper.carEntityToFullDTO(car)).thenReturn(new CarDTOFullInfo());

        CarDTOFullInfo result = carService.findCar(1L);
        assertNotNull(result);
    }

    @Test
    public void testFindCarNotFound() {
        when(carRepository.findById(1L)).thenReturn(Optional.empty());

        Exception exception = assertThrows(IllegalArgumentException.class, () -> carService.findCar(1L));

        assertEquals("Car with ID 1 was not found", exception.getMessage());
    }

    @Test
    public void testCreateCar() {
        CarDTO carDTO = new CarDTO();
        carDTO.setModelId(1L);
        carDTO.setBrandId(2L);
        carDTO.setOwnerId(3L);
        carDTO.setYearOfRelease(2020);
        carDTO.setMileage(50000);
        carDTO.setWasInAccident(false);
        carDTO.setAccessoriesIds(Arrays.asList(1L, 2L));

        Model model = new Model(1L, "Camry");
        Brand brand = new Brand(2L, "Toyota");
        Owner owner = new Owner(3L, "John", "Doe", "john.doe@example.com");
        Accessory accessory1 = new Accessory(1L, "Accessory 1");
        Accessory accessory2 = new Accessory(2L, "Accessory 2");

        when(modelRepository.findById(1L)).thenReturn(Optional.of(model));
        when(brandRepository.findById(2L)).thenReturn(Optional.of(brand));
        when(ownerRepository.findById(3L)).thenReturn(Optional.of(owner));
        when(accessoryRepository.findById(1L)).thenReturn(Optional.of(accessory1));
        when(accessoryRepository.findById(2L)).thenReturn(Optional.of(accessory2));

        when(carValidator.validate(any(CarDTO.class))).thenReturn("");

        Car car = new Car();
        when(carRepository.save(any(Car.class))).thenReturn(car);
        when(carMapper.carEntityToDTO(car)).thenReturn(carDTO);

        CarDTO result = carService.createCar(carDTO);

        assertNotNull(result);
    }

    @Test
    public void testUpdateCar() {
        CarDTO carDTO = new CarDTO();
        carDTO.setModelId(1L);
        carDTO.setBrandId(2L);
        carDTO.setOwnerId(3L);
        carDTO.setYearOfRelease(2020);
        carDTO.setMileage(50000);
        carDTO.setWasInAccident(false);
        carDTO.setAccessoriesIds(Arrays.asList(1L, 2L));

        Brand brand = new Brand(1L, "Toyota");
        Model model = new Model(1L, "Camry");
        Owner owner = new Owner(1L, "John", "Doe", "john.doe@example.com");

        Car car = new Car();
        car.setId(1L);
        car.setBrand(brand);
        car.setModel(model);
        car.setOwner(owner);
        car.setAccessories(new ArrayList<>());

        when(carRepository.findById(1L)).thenReturn(Optional.of(car));
        when(carRepository.save(car)).thenReturn(car);
        when(modelRepository.findById(1L)).thenReturn(Optional.of(model));
        when(brandRepository.findById(2L)).thenReturn(Optional.of(brand));
        when(ownerRepository.findById(3L)).thenReturn(Optional.of(owner));
        when(accessoryRepository.findById(1L)).thenReturn(Optional.of(new Accessory(1L, "Accessory 1")));
        when(accessoryRepository.findById(2L)).thenReturn(Optional.of(new Accessory(2L, "Accessory 2")));
        when(carMapper.carEntityToDTO(car)).thenReturn(carDTO);
        when(carValidator.validate(any(CarDTO.class))).thenReturn("");

        CarDTO result = carService.updateCar(carDTO, 1L);

        assertNotNull(result);
    }

    @Test
    public void testUpdateCarNotFound() {
        CarDTO carDTO = new CarDTO();

        when(carRepository.findById(1L)).thenReturn(Optional.empty());

        Exception exception = assertThrows(IllegalArgumentException.class, () -> carService.updateCar(carDTO, 1L));

        assertEquals("Car with ID 1 was not found", exception.getMessage());
    }

    @Test
    public void testDeleteCar() {
        Car car = new Car();

        when(carRepository.findById(1L)).thenReturn(Optional.of(car));
        doNothing().when(carRepository).deleteById(1L);

        String result = carService.deleteCar(1L);
        assertEquals("Car with ID 1 was deleted!", result);
    }

    @Test
    public void testDeleteCarNotFound() {
        when(carRepository.findById(1L)).thenReturn(Optional.empty());

        Exception exception = assertThrows(IllegalArgumentException.class, () -> carService.deleteCar(1L));

        assertEquals("Car with ID 1 was not found.", exception.getMessage());
    }

    @Test
    public void testGetCarsReport() throws IOException {
        when(csvUtility.generateCSVString(anyList()))
                .thenReturn("Car ID,Brand,Model,Owner ID,Owner Name\n1,Toyota,Camry,101,John Doe");

        CarSearchRequest request = new CarSearchRequest();
        String report = carService.getCarsReport(request);

        assertNotNull(report);
        assertTrue(report.contains("Toyota"));
    }

}
