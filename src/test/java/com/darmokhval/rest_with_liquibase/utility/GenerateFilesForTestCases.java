package com.darmokhval.rest_with_liquibase.utility;

import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Random;

@Slf4j
public class GenerateFilesForTestCases {

    private static final String[] JSON_EXTENSIONS = {".json"};
    private static final String[] OTHER_EXTENSIONS = {".txt", ".xml", ".csv"};

    private File emptyDirectory;
    private File notEmptyDirectory;

    public GenerateFilesForTestCases(File emptyDirectory, File notEmptyDirectory) {
        this.emptyDirectory = emptyDirectory;
        this.notEmptyDirectory = notEmptyDirectory;
    }

    public GenerateFilesForTestCases() {
    }

    public void generateFiles() throws IOException {
        if (!emptyDirectory.exists() && !emptyDirectory.mkdirs()) {
            log.error("Failed to create empty directory.");
            return;
        }

        if (!notEmptyDirectory.exists() && !notEmptyDirectory.mkdirs()) {
            log.error("Failed to create not empty directory.");
            return;
        }

        // Generate random number of JSON files (5-10)
        int numJsonFiles = getRandomNumberInRange(5, 10);
        generateFilesWithExtension(numJsonFiles, JSON_EXTENSIONS);

        // Generate random number of files with other extensions (5-10)
        int numOtherFiles = getRandomNumberInRange(5, 10);
        generateFilesWithExtension(numOtherFiles, OTHER_EXTENSIONS);
    }

    public void generateSpecificAmountOfFiles() throws IOException {
        if (!emptyDirectory.exists() && !emptyDirectory.mkdirs()) {
            log.error("Failed to create empty directory.");
            return;
        }

        if (!notEmptyDirectory.exists() && !notEmptyDirectory.mkdirs()) {
            log.error("Failed to create not empty directory.");
            return;
        }
        if(notEmptyDirectory.listFiles() != null && notEmptyDirectory.listFiles().length == 0) {
            generateFilesWithExtension(1, JSON_EXTENSIONS);
        }
    }

    public void createDirectory(String directory) {
        if (new File(directory).mkdirs()) {
            System.out.println("Directory created!");
        }
    }

    private void generateFilesWithExtension(int numFiles, String[] extensions) throws IOException {
        for (int i = 0; i < numFiles; i++) {
            String fileName = "file" + (i + 1) + extensions[new Random().nextInt(extensions.length)];
            File file = new File(notEmptyDirectory, fileName);
            try (FileWriter writer = new FileWriter(file)) {
                writer.write("This is a test file.");
            }
        }
    }

    private int getRandomNumberInRange(int min, int max) {
        Random random = new Random();
        return random.nextInt(max - min + 1) + min;
    }
}