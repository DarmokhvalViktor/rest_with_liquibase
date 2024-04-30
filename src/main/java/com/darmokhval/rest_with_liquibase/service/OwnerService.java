package com.darmokhval.rest_with_liquibase.service;

import com.darmokhval.rest_with_liquibase.mapper.OwnerMapper;
import com.darmokhval.rest_with_liquibase.model.dto.OwnerDTO;
import com.darmokhval.rest_with_liquibase.model.entity.Accessory;
import com.darmokhval.rest_with_liquibase.model.entity.Car;
import com.darmokhval.rest_with_liquibase.model.entity.Owner;
import com.darmokhval.rest_with_liquibase.repository.AccessoryRepository;
import com.darmokhval.rest_with_liquibase.repository.CarRepository;
import com.darmokhval.rest_with_liquibase.repository.OwnerRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class OwnerService {
    private final OwnerRepository ownerRepository;
    private final OwnerMapper ownerMapper;

//    TODO unit tests for owner and car services. and readme!!! and postman collection.
//     and check again if this program is working correctly.
    public List<OwnerDTO> getOwners() {
        return ownerRepository.findAll().stream().map(ownerMapper::convertOwnerToDTO).toList();
    }

    /**
     * Method creates owner with provided data. Validates provided email, if already user with this email exists ->
     * throws IllegalArgumentException.
     */
    @Transactional
    public OwnerDTO createOwner(OwnerDTO ownerDTO) {
        checkIfEmailIsUsed(ownerDTO.getEmail(), null);
        Owner owner = ownerRepository.save(ownerMapper.convertDTOToOwnerEntity(ownerDTO));
        return ownerMapper.convertOwnerToDTO(owner);
    }

    /**
     * method that updates user by given ID, otherwise throws IAE.
     * Checks if email provided already in use, if already in database and not related to current user -> throws IAE.
     */
    @Transactional
    public OwnerDTO updateOwner(OwnerDTO ownerDTO, Long id) {
        Optional<Owner> existingOwner = ownerRepository.findById(id);
        if(existingOwner.isPresent()) {
            checkIfEmailIsUsed(ownerDTO.getEmail(), id);

            Owner owner = existingOwner.get();
            owner.setName(ownerDTO.getName());
            owner.setLastname(ownerDTO.getLastname());
            owner.setEmail(ownerDTO.getEmail());
            owner = ownerRepository.save(owner);

            return ownerMapper.convertOwnerToDTO(owner);
        } else {
            throw new IllegalArgumentException(String.format("Owner with ID %s was not found!", id));
        }
    }

    /**
     * method deletes user from database if user can be found by ID.
     * explicitly deletes all other entities that related to user (car), and makes sure to keep relations consistent.
     */
    @Transactional
    public String deleteOwner(Long id) {
        Optional<Owner> existingOwner = ownerRepository.findById(id);
        if(existingOwner.isEmpty()) {
            throw new IllegalArgumentException(String.format("Owner with ID %s was not found!", id));
        }
        Owner owner = existingOwner.get();
        for (Car car: new ArrayList<>(owner.getCars())) {
            for(Accessory accessory: new ArrayList<>(car.getAccessories())) {
                car.removeAccessory(accessory.getId());
                accessory.removeCar(car.getId());
            }
            owner.removeCar(car.getId());
        }
        ownerRepository.deleteById(id);
        return String.format("Owner with ID %s was successfully deleted!", id);
    }

    /**
     * method checks if owner with provided email already exists in database.
     * If creating and email is in use -> throw IAE.
     * If updating and email exists and not assigned to current user -> throw IAE too
     */
    private void checkIfEmailIsUsed(String email, Long currentOwnerId) {
        Optional<Owner> existingOwnerWithEmail = ownerRepository.findByEmail(email);
        if(existingOwnerWithEmail.isPresent() && (currentOwnerId == null ||
                !existingOwnerWithEmail.get().getId().equals(currentOwnerId))) {
            throw new IllegalArgumentException(String.format("Email %s already taken!",
                    email));
        }
    }
}
