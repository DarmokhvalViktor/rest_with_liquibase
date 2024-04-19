package com.darmokhval.rest_with_liquibase.utility;

import lombok.extern.slf4j.Slf4j;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.File;
import java.util.Map;

@Slf4j
public class FileWriter {
    private final String pathToSaveFile = "src" + File.separator + "main" + File.separator + "resources" + File.separator + "statistics_by_";

    /**
     * method writes to file data from a map.
     * @param countFieldMap map from which method takes data to write into file
     * @param searchedField field that was searched, is used to name output file
     */
    public void generateXML(Map<String, Integer> countFieldMap, String searchedField){
        long startWritingTime = System.currentTimeMillis();
        System.out.println("Start to write into file " + pathToSaveFile + searchedField);
        try {
            if(checkIfMapNotNullOrEmpty(countFieldMap)) {
                File outputFile = new File(pathToSaveFile + searchedField + ".xml");
                Document doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();
                Element rootElement = doc.createElement("statistic");
                doc.appendChild(rootElement);

                mapValuesToDoc(rootElement, doc, countFieldMap);

                Transformer transformer = TransformerFactory.newInstance().newTransformer();
                transformer.setOutputProperty(OutputKeys.METHOD, "xml");
                transformer.setOutputProperty(OutputKeys.INDENT, "yes");
                DOMSource source = new DOMSource(doc);
                StreamResult result = new StreamResult(outputFile);
                transformer.transform(source, result);
            }
        } catch (TransformerException | ParserConfigurationException e) {
            log.error("An error occurred while writing to file: ", e);
        }
        System.out.println("File was successfully created, time in millis: " + (System.currentTimeMillis() - startWritingTime));
    }

    /**
     * populate file in memory with data
     */
    private void mapValuesToDoc(Element rootElement, Document doc, Map<String, Integer> countFieldMap) {
        countFieldMap.entrySet().stream()
                .sorted(Map.Entry.<String, Integer>comparingByValue().reversed())
                .forEachOrdered(entry -> {
                    Element item = doc.createElement("item");
                    rootElement.appendChild(item);

                    Element value = doc.createElement("value");
                    value.appendChild(doc.createTextNode(entry.getKey()));
                    item.appendChild(value);

                    Element count = doc.createElement("count");
                    count.appendChild(doc.createTextNode(String.valueOf(entry.getValue())));
                    item.appendChild(count);
                });
    }
    private boolean checkIfMapNotNullOrEmpty(Map<String, Integer> map) {
        if (map == null || map.isEmpty()) {
            throw new IllegalArgumentException("Nothing to write into file");
        }
        return true;
    }
}
