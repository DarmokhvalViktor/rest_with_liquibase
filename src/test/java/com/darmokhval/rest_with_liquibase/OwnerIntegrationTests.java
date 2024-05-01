package com.darmokhval.rest_with_liquibase;

import com.darmokhval.rest_with_liquibase.mapper.OwnerMapper;
import com.darmokhval.rest_with_liquibase.model.dto.OwnerDTO;
import com.darmokhval.rest_with_liquibase.model.entity.Owner;
import com.darmokhval.rest_with_liquibase.repository.OwnerRepository;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@ActiveProfiles("test")
@AutoConfigureMockMvc
class OwnerIntegrationTests {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private OwnerRepository ownerRepository;
    @Autowired
    private OwnerMapper ownerMapper;
    @Autowired
    private ObjectMapper objectMapper;

    @Test
    public void testCreateOwner() throws Exception {
        String name = "John";
        String lastname = "Doe";
        String email = "johny.doe@example.com";

        // Create JSON request body
        String body = """
            {
                "name": "%s",
                "lastname": "%s",
                "email": "%s"
            }
        """.formatted(name, lastname, email);
        MvcResult mvcResult = mockMvc.perform(post("/api/owners")
                .contentType(MediaType.APPLICATION_JSON)
                .content(body))
                .andExpect(status().isCreated())
                .andReturn();

        OwnerDTO response = objectMapper.readValue(mvcResult.getResponse().getContentAsString(), OwnerDTO.class);
        assertThat(response.getId()).isGreaterThanOrEqualTo(1);

        Owner savedOwner = ownerRepository.findById(response.getId()).orElse(null);
        assertThat(savedOwner).isNotNull();
        assertThat(savedOwner.getName()).isEqualTo(name);
        assertThat(savedOwner.getLastname()).isEqualTo(lastname);
        assertThat(savedOwner.getEmail()).isEqualTo(email);
    }

    @Test
    public void testCreateInvalidOwner() throws Exception {
        mockMvc.perform(post("/api/owners")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.messages").isArray());
    }

    @Test
    public void testGetOwners() throws Exception {
        OwnerDTO owner1 = new OwnerDTO("John", "Doe", "john.doe2@example.com");
        OwnerDTO owner2 = new OwnerDTO("Jane", "Doe", "jane.doe2@example.com");
        ownerRepository.save(ownerMapper.convertDTOToOwnerEntity(owner1));
        ownerRepository.save(ownerMapper.convertDTOToOwnerEntity(owner2));

        MvcResult result = mockMvc.perform(get("/api/owners")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();
        List<OwnerDTO> owners = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {});
        assertThat(owners.size()).isGreaterThanOrEqualTo(2);
        assertThat(owners).extracting(OwnerDTO::getEmail).contains("john.doe2@example.com", "jane.doe2@example.com");
    }

    @Test
    public void testUpdateOwner() throws Exception {
        OwnerDTO ownerDTO = new OwnerDTO("Nick", "Cage", "nick.cage@example.com");
        Owner savedOwner = ownerRepository.save(ownerMapper.convertDTOToOwnerEntity(ownerDTO));
        String updatedBody = """
            {
                "name": "Robert",
                "lastname": "Smith",
                "email": "robert.smith@example.com"
            }
        """;
        MvcResult result = mockMvc.perform(put("/api/owners/" + savedOwner.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(updatedBody))
                .andExpect(status().isOk())
                .andReturn();
        OwnerDTO response = objectMapper.readValue(result.getResponse().getContentAsString(), OwnerDTO.class);
        assertThat(response.getName()).isEqualTo("Robert");
        assertThat(response.getLastname()).isEqualTo("Smith");
        assertThat(response.getEmail()).isEqualTo("robert.smith@example.com");
    }

    @Test
    public void testDeleteOwner() throws Exception {
        OwnerDTO ownerDTO = new OwnerDTO("Nick", "Cage", "nick.cage@example.com");
        Owner savedOwner = ownerRepository.save(ownerMapper.convertDTOToOwnerEntity(ownerDTO));
        mockMvc.perform(delete("/api/owners/" + savedOwner.getId()))
                .andExpect(status().isOk());
        assertThat(ownerRepository.findById(savedOwner.getId()).isEmpty()).isTrue();
    }

}
