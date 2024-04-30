package com.darmokhval.rest_with_liquibase.utility;

import com.darmokhval.rest_with_liquibase.model.dto.CarDTOLight;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
public class CSVUtilityTest {
    private final CSVUtility csvUtility = new CSVUtility();

    @Test
    void testGenerateCSVString_Basic() throws IOException {
        List<CarDTOLight> cars = new ArrayList<>();
        cars.add(new CarDTOLight(1L, "Camry", "Toyota", "John Doe", 101L));
        cars.add(new CarDTOLight(2L, "Civic", "Honda", "Jane Doe", 102L));

        String csvResult = csvUtility.generateCSVString(cars);

        // Normalize line separators to Unix-style (LF) to ensure consistent comparison


        String expectedCSV = "Car ID,Brand,Model,Owner ID,Owner Name\n"
                + "1,Toyota,Camry,101,John Doe\n"
                + "2,Honda,Civic,102,Jane Doe";

        assertEquals(
                normalizeNewlines(expectedCSV).trim(),
                normalizeNewlines(csvResult).trim(),
                "CSV output does not match expected"
        );
    }
    private String normalizeNewlines(String s) {
        return s.replace("\r\n", "\n").replace("\r", "\n");
    }

    @Test
    void testGenerateCSVString_EmptyList() throws IOException {
        List<CarDTOLight> emptyCars = new ArrayList<>();

        String csvResult = csvUtility.generateCSVString(emptyCars);

        String expectedCSV = """
            Car ID,Brand,Model,Owner ID,Owner Name
        """;

        assertEquals(expectedCSV.trim(), csvResult.trim()); // Ensure only header is generated
    }

    @Test
    void testGenerateCSVString_SpecialCharacters() throws IOException {
        List<CarDTOLight> cars = new ArrayList<>();
        cars.add(new CarDTOLight(1L, "Ford, Inc.", "Focus \"Model\"", "Sam O'Neil", 103L));

        String csvResult = csvUtility.generateCSVString(cars);

        // Define the expected CSV with proper quoting and consistent structure
        String expectedCSV = "Car ID,Brand,Model,Owner ID,Owner Name\n"
                + "1,\"Focus \"\"Model\"\"\",\"Ford, Inc.\",103,Sam O'Neil";

        assertEquals(
                normalize(expectedCSV),
                normalize(csvResult),
                "CSV output does not match expected with special characters"
        );
    }
    public String normalize(String s) {
        return s.replaceAll("[\\r\\n]+", "\n").trim();  // Normalize line endings and remove extra spaces
    }

    @Test
    void testGenerateCSVString_IOException() {
        List<CarDTOLight> cars = new ArrayList<>();
        cars.add(new CarDTOLight(1L, "Toyota", "Camry",  "John Doe", 101L));
        assertDoesNotThrow(() -> csvUtility.generateCSVString(cars));
    }
}
