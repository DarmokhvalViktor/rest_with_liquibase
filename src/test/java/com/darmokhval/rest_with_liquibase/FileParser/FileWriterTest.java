package com.darmokhval.rest_with_liquibase.FileParser;

import com.darmokhval.utility.FileWriter;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

public class FileWriterTest {
    private FileWriter fileWriter;

    @BeforeEach
    void setUp() {
        fileWriter = new FileWriter();
    }

    /**
     * Method checks if document created, and if document's structure is valid
     */
    @Test
    void testGenerateXMLValidInput() {
        Map<String, Integer> countFieldMap = new HashMap<>();
        countFieldMap.put("value1", 10);
        countFieldMap.put("value2", 20);
        String searchedField = "test";

        fileWriter.generateXML(countFieldMap, searchedField);

        File outputFile = new File("src" + File.separator + "main" + File.separator + "resources" + File.separator + "statistics_by_test.xml");
        assertTrue(outputFile.exists());
        try {
            Document doc = Jsoup.parse(outputFile, "UTF-8", "");
            Element rootElement = doc.selectFirst("statistic");
            assertNotNull(rootElement);
            Elements itemElements = rootElement.select("item");
            for (Element item: itemElements) {
                Element valueElement = item.selectFirst("value");
                assertNotNull(valueElement);
                Element countElement = item.selectFirst("count");
                assertNotNull(countElement);
            }
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }
//    TODO not working tests. Don't forget to delete or finish!!!
//
//    @Test
//    void testGenerateXMLTransformerExceptionHandling() {
////        FileWriter fileWriter = mock(FileWriter.class);
////        doThrow(TransformerException.class).when(fileWriter).generateXML(any(),any());
//        TransformerException exception = assertThrows(TransformerException.class,
//                () -> fileWriter.generateXML(null, "test"));
//        System.out.println("exception" + exception);
//        assertEquals("An error occurred while writing to file", exception.getMessage());
//    }
//
//    @Test
//    void testGenerateXMLParserConfigurationExceptionHandling() {
//        FileWriter fileWriter = mock(FileWriter.class);
//        doThrow(ParserConfigurationException.class).when(fileWriter).generateXML(any(),any());
//        ParserConfigurationException exception = assertThrows(ParserConfigurationException.class,
//                () -> fileWriter.generateXML(null, "test"));
//        assertEquals("An error occurred while writing to file", exception.getMessage());
//    }

    @Test
    void testGenerateXMLNullMap() {
        FileWriter fileWriter = new FileWriter();
        Map<String, Integer> countFieldMap = null;
        String searchedField = "test";
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> fileWriter.generateXML(countFieldMap, searchedField));
        assertEquals("Nothing to write into file", exception.getMessage());
    }

    @Test
    void testGenerateXMLEmptyMap() {
        FileWriter fileWriter = new FileWriter();
        Map<String, Integer> countFieldMap = new HashMap<>();
        String searchedField = "test";
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> fileWriter.generateXML(countFieldMap, searchedField));
        assertEquals("Nothing to write into file", exception.getMessage());
    }
}
