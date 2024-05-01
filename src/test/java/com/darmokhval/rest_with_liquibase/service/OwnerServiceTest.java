package com.darmokhval.rest_with_liquibase.service;

import com.darmokhval.rest_with_liquibase.mapper.OwnerMapper;
import com.darmokhval.rest_with_liquibase.model.dto.OwnerDTO;
import com.darmokhval.rest_with_liquibase.model.entity.Owner;
import com.darmokhval.rest_with_liquibase.repository.OwnerRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class OwnerServiceTest {
    @Mock
    private OwnerRepository ownerRepository;
    @Mock
    private OwnerMapper ownerMapper;
    @InjectMocks
    private OwnerService ownerService;

    @Test
    public void testGetOwners() {
        Owner owner1 = new Owner(1L, "John", "Doe", "john.doe@example.com");
        Owner owner2 = new Owner(2L, "Jane", "Doe", "jane.doe@example.com");

        when(ownerRepository.findAll()).thenReturn(Arrays.asList(owner1, owner2));

        OwnerDTO ownerDTO1 = new OwnerDTO("John", "Doe", "john.doe@example.com");
        OwnerDTO ownerDTO2 = new OwnerDTO("Jane", "Doe", "jane.doe@example.com");

        when(ownerMapper.convertOwnerToDTO(owner1)).thenReturn(ownerDTO1);
        when(ownerMapper.convertOwnerToDTO(owner2)).thenReturn(ownerDTO2);

        List<OwnerDTO> owners = ownerService.getOwners();
        assertEquals(2, owners.size());
        assertTrue(owners.stream().anyMatch(o -> o.getEmail().equals("john.doe@example.com")));
        assertTrue(owners.stream().anyMatch(o -> o.getEmail().equals("jane.doe@example.com")));
    }
    @Test
    public void testCreateOwnerSuccess() {
        OwnerDTO ownerDTO = new OwnerDTO("John", "Doe", "john.doe@example.com");
        Owner owner = new Owner(1L, "John", "Doe", "john.doe@example.com");

        when(ownerMapper.convertDTOToOwnerEntity(ownerDTO)).thenReturn(owner);
        when(ownerRepository.save(owner)).thenReturn(owner);
        when(ownerMapper.convertOwnerToDTO(owner)).thenReturn(ownerDTO);

        OwnerDTO result = ownerService.createOwner(ownerDTO);
        assertEquals("john.doe@example.com", result.getEmail());
    }

    @Test
    public void testCreateOwnerEmailAlreadyUsed() {
        OwnerDTO ownerDTO = new OwnerDTO("John", "Doe", "john.doe@example.com");
        Owner existingOwner = new Owner(1L, "Existing", "Owner", "john.doe@example.com");

        when(ownerRepository.findByEmail("john.doe@example.com")).thenReturn(Optional.of(existingOwner));

        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            ownerService.createOwner(ownerDTO);
        });

        assertEquals("Email john.doe@example.com already taken!", exception.getMessage());
    }
    @Test
    public void testUpdateOwnerSuccess() {
        OwnerDTO ownerDTO = new OwnerDTO("John", "Doe", "john.doe@example.com");
        Owner existingOwner = new Owner(1L, "Existing", "Owner", "existing.email@example.com");

        when(ownerRepository.findById(1L)).thenReturn(Optional.of(existingOwner));
        when(ownerRepository.save(existingOwner)).thenReturn(existingOwner);
        when(ownerMapper.convertOwnerToDTO(existingOwner)).thenReturn(ownerDTO);

        OwnerDTO result = ownerService.updateOwner(ownerDTO, 1L);
        assertEquals("john.doe@example.com", result.getEmail());
    }
    @Test
    public void testUpdateOwnerNotFound() {
        OwnerDTO ownerDTO = new OwnerDTO("John", "Doe", "john.doe@example.com");

        when(ownerRepository.findById(1L)).thenReturn(Optional.empty());

        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            ownerService.updateOwner(ownerDTO, 1L);
        });

        assertEquals("Owner with ID 1 was not found!", exception.getMessage());
    }
    @Test
    public void testDeleteOwnerSuccess() {
        Owner existingOwner = new Owner(1L, "Existing", "Owner", "existing.email@example.com");

        when(ownerRepository.findById(1L)).thenReturn(Optional.of(existingOwner));

        String result = ownerService.deleteOwner(1L);

        assertEquals("Owner with ID 1 was successfully deleted!", result);
        verify(ownerRepository, times(1)).deleteById(1L);
    }

    @Test
    public void testDeleteOwnerNotFound() {
        when(ownerRepository.findById(1L)).thenReturn(Optional.empty());

        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            ownerService.deleteOwner(1L);
        });

        assertEquals("Owner with ID 1 was not found!", exception.getMessage());
    }

}
