package com.darmokhval.rest_with_liquibase.FileParser;

import com.darmokhval.utility.FileParser;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class FileParserTest {
    private final static String emptyDir = "src" + File.separator + "test" + File.separator + "empty" + File.separator;
    private final static String notEmptyDir = "src" + File.separator + "test" + File.separator + "not_empty" + File.separator;

    private FileParser fileParser;

    @BeforeEach
    void setUp() {
        fileParser = new FileParser();
        GenerateFilesForTestCases generator = new GenerateFilesForTestCases();
        generator.createDirectory(emptyDir);
        generator.createDirectory(notEmptyDir);
    }

    @AfterAll
    static void removeDirs() {
        if(new File(emptyDir).delete()) {
            System.out.println("Deleted empty directory");
        }
        if(new File(notEmptyDir).delete()) {
            System.out.println("Deleted not empty directory");
        }
    }

    @Test
    void testParseFileWithJson() {
        File file = new File(notEmptyDir + "test.json");
        String searchedField = "fieldName";
        Map<String, Integer> countFileMap = new HashMap<>();
        createFileToTest("test", ".json", true);

        fileParser.parseFile(file, searchedField, countFileMap);
        assertEquals(3, countFileMap.size());
        assertEquals(1, countFileMap.get("value1"));
        assertEquals(1, countFileMap.get("value2"));
        assertEquals(1, countFileMap.get("value3"));
        deleteTestFile(file);
    }

    @Test
    void testParseFileWithNonJson() {
        File file = new File("src" + File.separator + "test" + File.separator + "not_empty" + File.separator + "test.txt");
        String searchedField = "fieldName";
        Map<String, Integer> countFileMap = new HashMap<>();
        createFileToTest("test", ".txt", true);

        fileParser.parseFile(file, searchedField, countFileMap);
        assertEquals(0, countFileMap.size());
        deleteTestFile(file);
    }

    @Test
    void testParseFileWithNullFile() {
        File file = null;
        String searchedField = "fieldName";
        Map<String, Integer> countFileMap = new HashMap<>();

        fileParser.parseFile(file, searchedField, countFileMap);
        assertEquals(0, countFileMap.size());
    }

    @Test
    void testParseEmptyJsonFile() {
        createFileToTest("test_empty", ".json", false);
        File file = new File(notEmptyDir + "test_empty.json");
        String searchedField = "fieldName";
        Map<String, Integer> countFieldMap = new HashMap<>();

        fileParser.parseFile(file, searchedField, countFieldMap);
        assertEquals(0, countFieldMap.size());
        deleteTestFile(file);
    }

    private void createFileToTest(String filename, String fileExtension, boolean content) {
        File file = new File(notEmptyDir + filename + fileExtension);
        try (FileWriter writer = new FileWriter(file)) {
            if(content) {
                writer.write("{\"fieldName\": \"value1, value2, value3\"}");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void deleteTestFile(File file) {
        if(file.delete()) {
            System.out.println("File deleted");
        } else {
            System.out.println("Cannot delete a file");
        }
    }
}
