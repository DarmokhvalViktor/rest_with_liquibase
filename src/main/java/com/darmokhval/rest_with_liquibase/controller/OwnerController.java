package com.darmokhval.rest_with_liquibase.controller;

import com.darmokhval.rest_with_liquibase.model.dto.OwnerDTO;
import com.darmokhval.rest_with_liquibase.service.OwnerService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/owners")
public class OwnerController {
    private final OwnerService ownerService;

    @GetMapping()
    public ResponseEntity<List<OwnerDTO>> getOwners(){
        return ResponseEntity.status(HttpStatus.OK).body(ownerService.getOwners());
    }
    @PostMapping()
    public ResponseEntity<OwnerDTO> createOwner(
            @RequestBody @Valid OwnerDTO owner) {
        return ResponseEntity.status(HttpStatus.CREATED).body(ownerService.createOwner(owner));
    }
    @PutMapping("/{id}")
    public ResponseEntity<OwnerDTO> updateOwner(
            @RequestBody @Valid OwnerDTO ownerDTO,
            @PathVariable Long id) {
        return ResponseEntity.status(HttpStatus.OK).body(ownerService.updateOwner(ownerDTO, id));
    }
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteOwner(
            @PathVariable Long id) {
        return ResponseEntity.status(HttpStatus.OK).body(ownerService.deleteOwner(id));
    }
}
