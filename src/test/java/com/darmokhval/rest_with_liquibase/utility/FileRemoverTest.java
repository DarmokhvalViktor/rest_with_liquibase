package com.darmokhval.rest_with_liquibase.utility;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@Slf4j
public class FileRemoverTest {
    private static final File emptyDirectory = new File("src" + File.separator + "test" + File.separator + "empty");
    private static final File notEmptyDirectory = new File("src" + File.separator + "test" + File.separator + "not_empty");

    /**
     * method creates two directories, one empty, second directory populates with files to test remove function
     */
    @BeforeEach
    void setUp() {
        GenerateFilesForTestCases generator = new GenerateFilesForTestCases(emptyDirectory, notEmptyDirectory);
        try {
            generator.generateFiles();
            System.out.println("Files generated successfully.");
        } catch (IOException e) {
            System.out.println("Error generating files: " + e.getMessage());
        }
    }

    /**
     * utility class to remove all files used for testing.
     */
    @AfterAll
    static void deleteAllFiles() {
        File[] files = notEmptyDirectory.listFiles();
        if(files != null) {
            for (File file: files) {
                boolean deleted = file.delete();
                if(deleted) {
                    System.out.println("deleted file: " + file.getName());
                } else {
                    System.out.println("Failed to delete file: " + file.getName());
                }
            }
        }
        if(emptyDirectory.delete()) {
            System.out.println("Deleted empty directory");
        }
        if(notEmptyDirectory.delete()) {
            System.out.println("Deleted not empty directory");
        }
    }

    @Test
    void testDeleteJsonFiles() {
        FileRemover fileRemover = new FileRemover();
        fileRemover.deleteFiles(notEmptyDirectory.getPath());
        assertFilesDeleted(notEmptyDirectory, ".json");
    }

    @Test
    void testDeleteJsonFilesWithEmptyDirectory() {
        FileRemover fileRemover = new FileRemover();
        fileRemover.deleteFiles(emptyDirectory.getPath());
        assertNoFilesDeleted(emptyDirectory);
    }

    @Test
    void testDeleteJsonFilesWithInvalidDirectory() {
        FileRemover fileRemover = new FileRemover();
        fileRemover.deleteFiles("invalid_directory");
        assertNoFilesDeleted(emptyDirectory);
    }

    private void assertFilesDeleted(File directory, String extension) {
        File[] files = directory.listFiles();
        if (files != null) {
            for (File file : files) {
                assertFalse(file.getName().endsWith(extension));
            }
        }
    }
    private void assertNoFilesDeleted(File directory) {
        File[] files = directory.listFiles();
        if (files != null) {
            for (File file : files) {
                assertTrue(file.exists());
            }
        }
    }
}
