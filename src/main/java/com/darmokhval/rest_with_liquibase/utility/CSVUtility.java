package com.darmokhval.rest_with_liquibase.utility;

import com.darmokhval.rest_with_liquibase.model.dto.CarDTOLight;
import org.springframework.stereotype.Component;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;

import java.io.IOException;
import java.io.StringWriter;
import java.util.List;

@Component
public class CSVUtility {
    /**
     * generates csv file from passed data.
     */
    public String generateCSVString(List<CarDTOLight> cars) throws IOException {
        StringWriter writer = new StringWriter();
        CSVFormat format = CSVFormat.Builder.create(CSVFormat.DEFAULT)
                .setDelimiter(',')
                .setQuote('"')
                .setIgnoreSurroundingSpaces(true)
                .setHeader("Car ID", "Brand", "Model", "Owner ID", "Owner Name")
                .build();
        try (CSVPrinter printer = new CSVPrinter(writer, format)) {
            for (CarDTOLight car: cars) {
                printer.printRecord(
                        car.getId(),
                        car.getBrand(),
                        car.getModel(),
                        car.getOwnerId(),
                        car.getOwnerName()
                );
            }
            printer.flush();
        }
        return writer.toString();
    }
}
