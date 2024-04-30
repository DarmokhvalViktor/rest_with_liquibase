package com.darmokhval.rest_with_liquibase.utility;

import com.darmokhval.rest_with_liquibase.exception.IOFileException;
import com.darmokhval.rest_with_liquibase.model.dto.CarDTO;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockMultipartFile;

import java.util.HashSet;
import java.util.Set;
import static org.junit.jupiter.api.Assertions.*;

class FileParserTest {
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final FileParser fileParser = new FileParser(objectMapper);

    @Test
    void testReadFromFile_ValidJson() {
        Set<CarDTO> carDTOList = new HashSet<>();
        String validJsonContent = """
            [
                {
                    "modelId": 1,
                    "brandId": 1,
                    "ownerId": 1,
                    "yearOfRelease": 2020,
                    "mileage": 10000,
                    "wasInAccident": false,
                    "accessoriesIds": [1, 2]
                }
            ]
        """;
        MockMultipartFile validMultipartFile = new MockMultipartFile(
                "file", "cars.json", "application/json", validJsonContent.getBytes());

        long failedWrites = fileParser.readFromFile(validMultipartFile, carDTOList, 0);

        assertEquals(0, failedWrites);
        assertEquals(1, carDTOList.size()); // One valid CarDTO should be added
    }

    @Test
    void testReadFromFile_InvalidJson() {
        Set<CarDTO> carDTOList = new HashSet<>();
        String invalidJsonContent = """
            [
                {
                    "modelId": -1, // Invalid model ID
                    "brandId": 1,
                    "ownerId": 1,
                    "yearOfRelease": 2020,
                    "mileage": 10000,
                    "wasInAccident": false,
                    "accessoriesIds": [1, 2]
                }
            ]
        """;
        MockMultipartFile invalidMultipartFile = new MockMultipartFile(
                "file321", "cars.json", "application/json", invalidJsonContent.getBytes());

        long failedWrites = fileParser.readFromFile(invalidMultipartFile, carDTOList, 0);

        assertEquals(1, failedWrites); // Should fail due to invalid model ID
        assertTrue(carDTOList.isEmpty()); // Invalid data should not be added
    }

    @Test
    void testReadFromFile_IOError() {
        Set<CarDTO> carDTOList = new HashSet<>();
        MockMultipartFile nullMultipartFile = null;

        assertThrows(IOFileException.class, () -> {
            fileParser.readFromFile(nullMultipartFile, carDTOList, 0);
        });
    }

    @Test
    void testReadFromFile_EmptyFile() {
        Set<CarDTO> carDTOList = new HashSet<>();
        MockMultipartFile emptyMultipartFile = new MockMultipartFile(
                "file", "cars.json", "application/json", new byte[0]);

        long failedWrites = fileParser.readFromFile(emptyMultipartFile, carDTOList, 0);

        assertEquals(0, failedWrites); // No data, no parsing errors
        assertTrue(carDTOList.isEmpty()); // No valid CarDTO should be added
    }

    @Test
    void testReadFromFile_MissingRequiredFields() {
        Set<CarDTO> carDTOList = new HashSet<>();
        String missingFieldsJson = """
            [
                {
                    "modelId": 1,
                    "brandId": 1,
                    "yearOfRelease": 2020 // Missing ownerId and other fields
                }
            ]
        """;
        MockMultipartFile multipartFile = new MockMultipartFile(
                "file", "cars.json", "application/json", missingFieldsJson.getBytes());

        long failedWrites = fileParser.readFromFile(multipartFile, carDTOList, 0);

        assertEquals(1, failedWrites); // Should fail due to missing required fields
        assertTrue(carDTOList.isEmpty()); // No valid CarDTO should be added
    }
}