package com.darmokhval.rest_with_liquibase.utility;

import com.darmokhval.rest_with_liquibase.entity.*;
import com.darmokhval.rest_with_liquibase.model.entity.CarAccessories;
import com.darmokhval.rest_with_liquibase.model.entity.CarBrand;
import com.darmokhval.rest_with_liquibase.model.entity.CarModel;
import com.darmokhval.rest_with_liquibase.model.entity.Owner;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONObject;

import java.io.FileWriter;
import java.io.*;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * This class actually can be deleted!!! Only use to generate some .json files, so no point to test this. Or is it?
 * Utility class to generate files to test if the program works.
 * methods that starts with "generateRandom..." -> utility methods to generates random values to fill in file.
 * main method -> createFile, final values can be changed to test different conditions.
 * method .createFiles() writes line-by-line, so it shouldn't consume too much RAM.
 */
@Slf4j
public class RandomFilesWithCarsGenerator {
    private static final Random random = new Random();
    private final static int numberOfObjectsToCreate = 500_000;
    private static final int minYear = 1990;
    private static final int maxYear = 2024;
    private static final String pathToSaveFile = "src" + File.separator + "main" + File.separator + "resources" + File.separator + "large_car_data_";
    private static final String characters = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";
    private static final String directoryPath = "src/main/resources";
    private static final String[] famousPeople = {
            "Albert Einstein", "Marie Curie", "Isaac Newton", "Galileo Galilei",
            "Nikola Tesla", "Stephen Hawking", "Ada Lovelace", "Alan Turing", "Bill Gates", "Steve Jobs",
            "Elon Musk", "Mark Zuckerberg", "Oprah Winfrey", "Ellen DeGeneres", "David Beckham", "Cristiano Ronaldo",
            "Serena Williams", "Michael Jordan", "Kobe Bryant", "Usain Bolt", "Roger Federer", "Lionel Messi",
            "Tom Brady", "Muhammad Ali", "Bruce Lee", "Pablo Picasso", "Frida Kahlo",
            "Vincent Price", "Marilyn Monroe", "Audrey Hepburn", "James Dean", "Elvis Presley", "John Lennon",
            "Bob Marley", "Freddie Mercury", "Michael Jackson", "Whitney Houston",
            "Taylor Swift", "Kanye West", "Angelina Jolie", "Brad Pitt", "Jennifer Aniston",
            "Leonardo DiCaprio", "Vincent van Gogh"
    };

    static {
        if(!new File(directoryPath).exists()) {
            if(new File(directoryPath).mkdir()) {
                log.info("Directory created " + directoryPath);
            } else {
                log.error("Failed to created directory " + directoryPath);
            }
        }
    }

    /**
     * one can choose how many files should be created.
     * @param numberOfFiles specify how many files should be created.
     */
    public void createFiles(int numberOfFiles, int numberOfThreads) {
        ExecutorService executorService = Executors.newFixedThreadPool(numberOfThreads);
        System.out.println("Creating files.... ");
        for(int j = 1; j <= numberOfFiles; j++) {
            int finalJ = j;
            executorService.submit(() -> writeFile(finalJ));
        }
        executorService.shutdown();
        try {
            if (!executorService.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS)) {
                log.info("Executor service did not terminate within the specified timeout");
            }
        } catch (InterruptedException e) {
            log.error("Thread interrupted while awaiting termination of executor service", e);
            Thread.currentThread().interrupt();
        } finally {
            System.out.println("Files were successfully created!!!");
        }
    }

    /**
     * creates file in defined directory. By default - "src/main/resources". Name "large_car_data_{fileNumber}.json".
     * Writes line by line, so it shouldn't use too much RAM.
     * @param fileNumber int number of file that needs to be written.
     */
    public void writeFile(int fileNumber) {
        System.out.println("Create file %" + fileNumber);
        try (PrintWriter writer = new PrintWriter(new BufferedWriter(new FileWriter(pathToSaveFile + fileNumber + ".json")))) {
            for (int i = 0; i < numberOfObjectsToCreate; i++) {
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("brand", generateRandomBrand());
                jsonObject.put("model", generateRandomModel());
                jsonObject.put("year_of_release", generateRandomYear());
                jsonObject.put("owner", generateRandomOwner());
                jsonObject.put("mileage", generateRandomMileage());
                jsonObject.put("accessories", generateRandomAccessoriesString());
                jsonObject.put("was_in_accident", generateRandomAccident());
                writer.println(jsonObject);
            }
        } catch (IOException e) {
            log.error("An error occurred while writing to file", e);
        }
    }

    /**
     * this method returns "true" in 25% of time. Using to decide if car was in accident.
     * @return random boolean with 75% change it to be "false".
     */
    private boolean generateRandomAccident() {
        int randomNumber = random.nextInt(100);
        return randomNumber >= 75;
    }

    /**
     * generate random number of accessories, minimum at 2, maximum -> 6
     * @return random number of accessories (in range from 2 to 6), grouped in one string divided by comma ","
     */
    //    TODO change from enums to records from database. Need to deleete these enums from project.
    private String generateRandomAccessoriesString() {
        int numberOfAccessories = random.nextInt(5) + 2;
        StringBuilder accessoriesBuilder = new StringBuilder();
        for (int i = 0; i < numberOfAccessories; i++) {
            CarAccessories accessory = CarAccessories.values()[random.nextInt(CarAccessories.values().length)];
            accessoriesBuilder.append(accessory.getAccessory());
            if(i < numberOfAccessories - 1) {
                accessoriesBuilder.append(", ");
            }
        }
        return accessoriesBuilder.toString();
    }

    private int generateRandomMileage() {
        return (int) (Math.random() * (200_000 - 50 + 1));
    }

    /**
     * get random string from an array defined in memory, and creates an owner object.
     * if in that array one occurrence not exactly 2 words, method will create some random name + surname for owner object.
     * @return string with owner name and lastname from predefined array
     */
    private String generateRandomOwner() {
        String[] ownerFromArray = famousPeople[random.nextInt(famousPeople.length)].split(" ");
        Owner owner = new Owner();
        if(ownerFromArray.length == 2) {
            owner.setName(ownerFromArray[0]);
            owner.setLastname(ownerFromArray[1]);
        } else {
            owner.setName("randomName" + characters.charAt(random.nextInt(characters.length())));
            owner.setLastname("randomLastname" + characters.charAt(random.nextInt(characters.length())));
        }
        return owner.getName() + " " + owner.getLastname();
    }

//    TODO change from enums to records from database.
    private CarBrand generateRandomBrand() {
        return CarBrand.values()[random.nextInt(CarBrand.values().length)];
    }
//    TODO change from enums to records from database.
    private CarModel generateRandomModel() {
        return CarModel.values()[random.nextInt(CarModel.values().length)];
    }

    /**
     * return random year between minYear and maxYear. 50% chance to return between maxYear and 2019. Using it to assign to carYear
     */
    private int generateRandomYear() {
        int randomNumber = random.nextInt(100);
        if(randomNumber < 50) {
            return 2019 + (int) (Math.random() * (maxYear - 2019 + 1));
        } else {
            return minYear + (int) (Math.random() * (maxYear - minYear + 1));
        }
    }

}
