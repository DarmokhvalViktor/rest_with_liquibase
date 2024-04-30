package com.darmokhval.rest_with_liquibase.utility;

import com.darmokhval.rest_with_liquibase.model.dto.OwnerDTO;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.core.type.TypeReference;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.test.web.servlet.MvcResult;

import java.util.List;

public class PageDeserializer {

    public static <T> Page<T> deserializePage(ObjectMapper objectMapper, MvcResult mvcResult, TypeReference<List<T>> typeRef) throws Exception {
        // Get the raw JSON response
        String jsonResponse = mvcResult.getResponse().getContentAsString();

        // Parse the JSON response into an ObjectNode
        ObjectNode root = (ObjectNode) objectMapper.readTree(jsonResponse);

        // Remove the 'pageable' field to prevent deserialization issues
        root.remove("pageable");

        // Deserialize the modified JSON into a List of the given type
        List<T> content = objectMapper.readValue(
                root.get("content").traverse(),
                typeRef
        );

        long totalElements = root.get("totalElements").asLong();
        int totalPages = root.get("totalPages").asInt();
        int number = root.get("number").asInt();

        // Create a default pageable instance with a predefined page size and sorting
        Pageable pageable = PageRequest.of(number, 20, Sort.by("id")); // Default Pageable

        // Return the reconstructed Page
        return new PageImpl<>(content, pageable, totalElements);
    }
}