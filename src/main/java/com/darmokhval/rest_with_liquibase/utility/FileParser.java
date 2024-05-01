package com.darmokhval.rest_with_liquibase.utility;

import com.darmokhval.rest_with_liquibase.exception.IOFileException;
import com.darmokhval.rest_with_liquibase.model.dto.CarDTO;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.StreamReadFeature;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Set;

/**
 * class for parsing .json file and convert records into CarDTO objects.
 */
@Component
@RequiredArgsConstructor
public class FileParser {
    private final ObjectMapper objectMapper;

    /**
     * method specifies objectMapper configuration, throws exception if error while reading from a file.
     * In successful case populates list with a valid data from a file. List is passed as argument.
     */
    public long readFromFile(MultipartFile multipartFile, Set<CarDTO> carDTOList, long failedWrites) {
        if (multipartFile == null) {
            throw new IOFileException("MultipartFile is null");
        }
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        objectMapper.configure(JsonParser.Feature.ALLOW_COMMENTS, true);
        try {
            failedWrites = parseData(multipartFile, carDTOList, failedWrites);
        } catch (IOException e) {
            throw new IOFileException(String.format("Error occurred while trying to read a file %s",
                    multipartFile.getName()));
        }
        return failedWrites;
    }

    /**
     * method reads file and writes valid records into list that passed as argument.
     * returns Long number how many records were not valid/incomplete
     */
    private long parseData(MultipartFile multipartFile, Set<CarDTO> carDTOList, long failedWrites) throws IOException {
        JsonNode jsonNode = objectMapper.readTree(multipartFile.getInputStream());
        for (JsonNode node : jsonNode) {
            try {
                // Attempt to deserialize each JSON node into a CarDTO
                CarDTO carDTO = objectMapper.treeToValue(node, CarDTO.class);
                if(!isValid(carDTO)) {
                    failedWrites++;
                    continue;
                }
                carDTOList.add(carDTO);
            } catch (Exception e) {
                failedWrites++;
            }
        }
        return failedWrites;
    }

    /**
     * check if provided data is valid
     */
    private boolean isValid(CarDTO carDTO) {
        // Perform explicit checks for required fields
        return carDTO.getModelId() != null && carDTO.getModelId() > 0
                && carDTO.getBrandId() != null && carDTO.getBrandId() > 0
                && carDTO.getOwnerId() != null && carDTO.getOwnerId() > 0
                && carDTO.getYearOfRelease() != null && carDTO.getYearOfRelease() >= 1970
                && carDTO.getMileage() != null && carDTO.getMileage() > 0
                && carDTO.getWasInAccident() != null
                && carDTO.getAccessoriesIds() != null
                && carDTO.getAccessoriesIds().size() >= 2;
    }
}
