package com.darmokhval.rest_with_liquibase;


import com.darmokhval.rest_with_liquibase.model.dto.BrandDTO;
import com.darmokhval.rest_with_liquibase.model.dto.CarDTO;
import com.darmokhval.rest_with_liquibase.model.dto.CarDTOLight;
import com.darmokhval.rest_with_liquibase.model.dto.CarSearchRequest;
import com.darmokhval.rest_with_liquibase.model.entity.*;
import com.darmokhval.rest_with_liquibase.repository.*;
import com.darmokhval.rest_with_liquibase.utility.PageDeserializer;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.util.List;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@AutoConfigureMockMvc
@ActiveProfiles("test")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
class CarIntegrationTests {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private CarRepository carRepository;
    @Autowired
    private OwnerRepository ownerRepository;
    @Autowired
    private BrandRepository brandRepository;
    @Autowired
    private ModelRepository modelRepository;
    @Autowired
    private AccessoryRepository accessoryRepository;

    private Long brandId;
    private Long modelId;
    private Long ownerId;
    private List<Long> accessoryIds;

    @BeforeEach
    public void setUp() {
        Brand brand = new Brand("Test brand");
        brand = brandRepository.save(brand);
        brandId = brand.getId();

        Model model = new Model("Test model");
        model = modelRepository.save(model);
        modelId = model.getId();

        Owner owner = new Owner("Test", "test.owner@example.com", "Owner");
        owner = ownerRepository.save(owner);
        ownerId = owner.getId();

        Accessory accessory1 = new Accessory("Accessory 1");
        Accessory accessory2 = new Accessory("Accessory 2");
        accessory1 = accessoryRepository.save(accessory1);
        accessory2 = accessoryRepository.save(accessory2);
        accessoryIds = List.of(accessory1.getId(), accessory2.getId());
    }

    @AfterEach
    public void cleanUp() {
        accessoryRepository.deleteAll();
        modelRepository.deleteAll();
        brandRepository.deleteAll();
        carRepository.deleteAll();
        ownerRepository.deleteAll();
    }

    @Test
    public void testSearchFilteredCars() throws Exception {
        // Create cars for testing
        CarDTO car1 = new CarDTO(modelId, brandId, ownerId, 1999, 50000, false, accessoryIds);
        CarDTO car2 = new CarDTO(modelId, brandId, ownerId, 2005, 60000, true, accessoryIds);

        String carJson1 = objectMapper.writeValueAsString(car1);
        String carJson2 = objectMapper.writeValueAsString(car2);

        // Create the cars
        mockMvc.perform(post("/api/cars")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(carJson1))
                .andExpect(status().isCreated());

        mockMvc.perform(post("/api/cars")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(carJson2))
                .andExpect(status().isCreated());

        // Create a CarSearchRequest with the desired filters
        CarSearchRequest searchRequest = new CarSearchRequest();
        searchRequest.setBrand(new BrandDTO(brandId, null)); // Search by brand

        String searchJson = objectMapper.writeValueAsString(searchRequest);

        MvcResult result = mockMvc.perform(post("/api/cars/_list")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(searchJson))
                .andExpect(status().isOk())
                .andReturn();

        // Deserialize the Page of CarDTOLight from the response
        Page<CarDTOLight> cars = PageDeserializer.deserializePage(objectMapper, result, new TypeReference<>() {});

        // Verify that the expected cars are returned
        assertThat(cars.getContent()).hasSize(2); // Check that both cars are present
        assertThat(cars.getContent().stream().map(CarDTOLight::getBrand).collect(Collectors.toList()))
                .contains("Test brand");
    }

    @Test
    public void testDownloadCarData() throws Exception {
        // Create a new car with specified IDs
        CarDTO carDTO = new CarDTO(
                null,  // ID is generated by the database
                modelId,
                brandId,
                ownerId,
                1999,
                50000,
                false,
                accessoryIds
        );

        // Convert CarDTO to JSON
        String carJson = objectMapper.writeValueAsString(carDTO);

        // Save the car in the database
        mockMvc.perform(post("/api/cars")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(carJson))
                .andExpect(status().isCreated());  // Ensure car is created

        // Fetch the CSV report for all cars
        MvcResult reportResult = mockMvc.perform(post("/api/cars/_report")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}")) // Empty request body
                .andExpect(status().isOk())
                .andReturn();

        // Validate response headers
        assertThat(reportResult.getResponse().getHeader("Content-Type")).isEqualTo("text/csv");
        assertThat(reportResult.getResponse().getHeader("Content-Disposition"))
                .contains("attachment; filename=report.csv");

        // Validate the CSV content
        String csvContent = reportResult.getResponse().getContentAsString();
        assertThat(csvContent).contains("Car ID", "Brand", "Model", "Owner ID", "Owner Name");

        // Ensure the CSV contains the expected brand data
        assertThat(csvContent).contains("Test brand"); // Brand name should be correct
        assertThat(csvContent).contains("Test model"); // Model name should be correct
    }

    @Test
    public void testCreateCar() throws Exception {
        // Step 1: Create a CarDTO with the required fields
        CarDTO carDTO = new CarDTO(
                null,  // Leave the ID as null for a new car
                modelId,
                brandId,
                ownerId,
                1999,
                50000,
                false,
                accessoryIds
        );

        // Step 2: Convert the CarDTO to JSON for the POST request
        String carJson = objectMapper.writeValueAsString(carDTO);

        // Step 3: Perform the POST request to create the car and get the response
        MvcResult result = mockMvc.perform(post("/api/cars")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(carJson))
                .andExpect(status().isCreated())
                .andReturn();

        // Step 4: Extract the ID of the created car from the response
        CarDTO createdCarDTO = objectMapper.readValue(result.getResponse().getContentAsString(), CarDTO.class);

        // Step 5: Verify the data saved in the database
        Car createdCar = carRepository.findById(createdCarDTO.getId()).orElse(null);

        // Step 6: Assert the saved car's details
        assertThat(createdCar).isNotNull();
        assertThat(createdCar.getYearOfRelease()).isEqualTo(1999);
        assertThat(createdCar.getMileage()).isEqualTo(50000);
        assertThat(createdCar.getWasInAccident()).isEqualTo(false);
        assertThat(createdCar.getModel().getId()).isEqualTo(modelId);
        assertThat(createdCar.getBrand().getId()).isEqualTo(brandId);
        assertThat(createdCar.getOwner().getId()).isEqualTo(ownerId);
    }

    @Test
    public void testCreateCar_InvalidData() throws Exception {
        CarDTO invalidCar = new CarDTO(
                null,
                null, // Missing modelId
                brandId,
                ownerId,
                1999,
                50000,
                false,
                accessoryIds
        );

        String carJson = objectMapper.writeValueAsString(invalidCar);

        mockMvc.perform(post("/api/cars")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(carJson))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.messages").isArray());
    }

    @Test
    public void testGetCar() throws Exception {
        CarDTO carDTO = new CarDTO(
                modelId,
                brandId,
                ownerId,
                1999,
                50000,
                false,
                accessoryIds
        );
        String carJson = objectMapper.writeValueAsString(carDTO);

        MvcResult result = mockMvc.perform(post("/api/cars")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(carJson))
                .andExpect(status().isCreated())
                .andReturn();
        CarDTO createdCarDTO = objectMapper.readValue(result.getResponse().getContentAsString(), CarDTO.class);
        Car createdCar = carRepository.findById(createdCarDTO.getId()).orElse(null);

        mockMvc.perform(get("/api/cars/" + createdCar.getId())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.yearOfRelease").value(1999))
                .andExpect(jsonPath("$.mileage").value(50000))
                .andExpect(jsonPath("$.wasInAccident").value(false));
    }
    @Test
    public void testUpdateCar() throws Exception {
        CarDTO carDTO = new CarDTO(
                null,
                modelId,
                brandId,
                ownerId,
                1999,
                50000,
                false,
                accessoryIds
        );
        String carJson = objectMapper.writeValueAsString(carDTO);

        MvcResult result = mockMvc.perform(post("/api/cars")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(carJson))
                .andExpect(status().isCreated())
                .andReturn();

        CarDTO updatedCarDTO = new CarDTO(
                null,
                modelId,
                brandId,
                ownerId,
                2005,
                60000,
                true,
                accessoryIds
        );

        CarDTO createdCarDTO = objectMapper.readValue(result.getResponse().getContentAsString(), CarDTO.class);
        Car createdCar = carRepository.findById(createdCarDTO.getId()).orElse(null);
        String updatedCarJson = objectMapper.writeValueAsString(updatedCarDTO);

        mockMvc.perform(put("/api/cars/" + createdCar.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(updatedCarJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.yearOfRelease").value(2005))
                .andExpect(jsonPath("$.mileage").value(60000))
                .andExpect(jsonPath("$.wasInAccident").value(true));

        Car updatedCar = carRepository.findById(createdCar.getId()).orElse(null);
        assertThat(updatedCar).isNotNull();
        assertThat(updatedCar.getYearOfRelease()).isEqualTo(2005);
        assertThat(updatedCar.getMileage()).isEqualTo(60000);
        assertThat(updatedCar.getWasInAccident()).isEqualTo(true);
    }
    @Test
    public void testDeleteCar() throws Exception {
        CarDTO carDTO = new CarDTO(
                null,
                modelId,
                brandId,
                ownerId,
                1999,
                50000,
                false,
                accessoryIds
        );

        String carJson = objectMapper.writeValueAsString(carDTO);

        MvcResult result = mockMvc.perform(post("/api/cars")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(carJson))
                .andExpect(status().isCreated())
                .andReturn();
        CarDTO createdCarDTO = objectMapper.readValue(result.getResponse().getContentAsString(), CarDTO.class);
        Car createdCar = carRepository.findById(createdCarDTO.getId()).orElse(null);

        mockMvc.perform(delete("/api/cars/" + createdCar.getId())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().string(String.format("Car with ID %d was deleted!", createdCar.getId())));

        Car deletedCar = carRepository.findById(createdCar.getId()).orElse(null);
        assertThat(deletedCar).isNull(); // Ensure car is really deleted
    }
    @Test
    public void testUploadCarData() throws Exception {
        String carData = """
            [{
                "brandId": %d,
                "modelId": %d,
                "ownerId": %d,
                "yearOfRelease": 1999,
                "mileage": 50000,
                "wasInAccident": false,
                "accessoriesIds": [%d, %d]
            }]
        """.formatted(brandId, modelId, ownerId, accessoryIds.get(0), accessoryIds.get(1));

        MockMultipartFile multipartFile = new MockMultipartFile(
                "file",
                "car_data.json",
                "application/json",
                carData.getBytes()
        );

        mockMvc.perform(multipart("/api/cars/upload")
                        .file(multipartFile))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.successfulWrites").value(1))
                .andExpect(jsonPath("$.failedWrites").value(0));
    }

    @Test
    public void testGetCar_NotFound() throws Exception {
        mockMvc.perform(get("/api/cars/9999")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Car with ID 9999 was not found"));
    }


}
