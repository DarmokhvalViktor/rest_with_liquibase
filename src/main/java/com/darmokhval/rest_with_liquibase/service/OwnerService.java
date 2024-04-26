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
    private final AccessoryRepository accessoryRepository;
    private final CarRepository carRepository;
    private final OwnerMapper ownerMapper;

//    TODO create owner without cars. Assign car to owner when creating car.
    public Page<OwnerDTO> getOwners(Pageable pageable) {
        return ownerRepository.findAll(pageable).map(ownerMapper::convertOwnerToDTO);
    }

//    Tested, create, validation is working.
    public OwnerDTO createOwner(OwnerDTO ownerDTO) {
        checkIfEmailIsUsed(ownerDTO.getEmail(), null);
        Owner owner = ownerRepository.save(ownerMapper.convertDTOToOwnerEntity(ownerDTO));
        return ownerMapper.convertOwnerToDTO(owner);
    }

//    tested, update working on owner, if existing email -> error, validation confirm too.
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

//    TODO when in SQL specified "ON DELETE CASCADE" it deletes all that needed.
//     But this seems to be bad practice.
    @Transactional
    public String deleteOwner(Long id) {
        Optional<Owner> existingOwner = ownerRepository.findById(id);
        if(existingOwner.isEmpty()) {
            throw new IllegalArgumentException(String.format("Owner with ID %s was not found!", id));
        }

        Owner owner = existingOwner.get();

        List<Car> cars = new ArrayList<>(owner.getCars());
        for (Car car: cars) {
            for(Accessory accessory: car.getAccessories()) {
//                car.removeAccessory(accessory.getId());
            }
//            owner.removeCar(car.getId());
        }

        ownerRepository.deleteById(id);
        return String.format("Owner with ID %s was successfully deleted!", id);
    }
}
