package com.darmokhval.rest_with_liquibase.utility;

import com.darmokhval.rest_with_liquibase.model.dto.CarDTO;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.stereotype.Component;

import java.io.FileWriter;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

/**
 * This class actually can be deleted!!! Only use to generate some .json files, so no point to test this. Or is it?
 * Utility class to generate files to test if the program works.
 * methods that starts with "generateRandom..." -> utility methods to generates random values to fill in file.
 * main method -> createFile, final values can be changed to test different conditions.
 * method .createFiles() writes line-by-line, so it shouldn't consume too much RAM.
 */
@Slf4j
@Component
public class RandomFileWithCarsGenerator {
    private static final Random random = new Random();

    public void generateFile(int numberOfRecords, String filePath) {

        try {
            // Ensure the directory exists
            Path directoryPath = Paths.get(filePath).getParent();
            if (directoryPath != null && !Files.exists(directoryPath)) {
                Files.createDirectories(directoryPath);
                log.info("Directory created: " + directoryPath);
            }

            // Create or overwrite the file
            File file = new File(filePath);
            if (!file.exists()) {
                if(file.createNewFile()) {
                    log.info("File created: " + filePath);
                }
            } else {
                log.info("File exists: " + filePath + ". Overwriting.");
            }

            log.info("Creating JSON file with " + numberOfRecords + " records at " + filePath);
            JSONArray carArray = new JSONArray();

            for (int i = 0; i < numberOfRecords; i++) {
                CarDTO car = generateRandomCar(i + 1);
                JSONObject json = new JSONObject();
                json.put("modelId", car.getModelId());
                json.put("brandId", car.getBrandId());
                json.put("ownerId", car.getOwnerId());
                json.put("yearOfRelease", car.getYearOfRelease());
                json.put("mileage", car.getMileage());
                json.put("wasInAccident", car.getWasInAccident());
                json.put("accessoriesIds", car.getAccessoriesIds());
                carArray.put(json);
            }

            try (PrintWriter writer = new PrintWriter(new BufferedWriter(new FileWriter(filePath)))) {
                writer.println(carArray.toString(2));
                log.info("File successfully created at " + filePath);
            }
        } catch (IOException e) {
            log.error("An error occurred while writing to file: " + filePath, e);
        }
    }

    private CarDTO generateRandomCar(long id) {
        CarDTO car = new CarDTO();
        car.setModelId((long) random.nextInt(26) + 1);
        car.setBrandId((long) random.nextInt(31) + 1);
        car.setOwnerId((long) random.nextInt(5) + 1);
        car.setYearOfRelease(1970 + random.nextInt(55)); // Between 1970 and 2024
        car.setMileage(random.nextInt(200_000)); // Random mileage between 0 and 200,000
        car.setWasInAccident(random.nextInt(4) == 0); // 25% chance of being in an accident
        car.setAccessoriesIds(generateRandomAccessoriesIds());
        return car;
    }

    private Set<Long> generateRandomAccessoriesIds() {
        Set<Long> accessories = new HashSet<>();
        int count = random.nextInt(6 - 2 + 1) + 2; // Between 2 and 6 accessories
        for (int i = 0; i < count; i++) {
            accessories.add((long) random.nextInt(15) + 1);
        }
        return accessories;
    }
}
