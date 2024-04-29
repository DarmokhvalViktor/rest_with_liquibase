package com.darmokhval.rest_with_liquibase.utility;

import org.json.JSONException;
import org.json.JSONObject;
import org.junit.jupiter.api.Test;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class RandomFilesWithCarsGeneratorTest {

//    @Test
//    void testWriteFile() throws IOException, JSONException {
//        // Define test parameters
//        int fileNumber = -1;
//        String filePath = "src/main/resources/large_car_data_" + fileNumber + ".json";
//
//        RandomFileWithCarsGenerator generator = new RandomFileWithCarsGenerator();
//        generator.writeFile(fileNumber);
//
//        Path path = Paths.get(filePath);
//        assertTrue(Files.exists(path), "File should be created");
//
//        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
//            String line;
//            while ((line = reader.readLine()) != null) {
//                JSONObject jsonObject = new JSONObject(line);
//                // Perform assertions on each field in the JSON object
//                assertTrue(jsonObject.has("brand"));
//                assertTrue(jsonObject.has("model"));
//                assertTrue(jsonObject.has("year_of_release"));
//                assertTrue(jsonObject.has("owner"));
//                assertTrue(jsonObject.has("mileage"));
//                assertTrue(jsonObject.has("accessories"));
//                assertTrue(jsonObject.has("was_in_accident"));
//            }
//        }
//
//        Files.deleteIfExists(path);
//    }
//    @Test
//    void testCreateFiles() throws IOException {
//        RandomFileWithCarsGenerator generator = new RandomFileWithCarsGenerator();
//        generator.createFiles(1, 1); // Assuming 1 file with 1 thread
//        // Assert that the file is created
//        File file = new File("src/main/resources/large_car_data_1.json");
//        file.createNewFile();
//        assertTrue(file.exists());
//        // Cleanup
//        Files.deleteIfExists(file.toPath());
//    }
}
