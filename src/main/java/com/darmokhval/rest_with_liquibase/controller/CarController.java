package com.darmokhval.rest_with_liquibase.controller;

import com.darmokhval.rest_with_liquibase.model.dto.CarDTO;
import com.darmokhval.rest_with_liquibase.model.dto.CarDTOFullInfo;
import com.darmokhval.rest_with_liquibase.model.dto.CarDTOLight;
import com.darmokhval.rest_with_liquibase.model.dto.CarSearchRequest;
import com.darmokhval.rest_with_liquibase.service.CarService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequiredArgsConstructor
@RequestMapping("api/cars")
public class CarController {
    private final CarService carService;

    @PostMapping("/_list")
    public ResponseEntity<Page<CarDTOLight>> searchFilteredCars(
            @RequestBody(required = false) CarSearchRequest request) {
        return ResponseEntity.status(HttpStatus.OK).body(carService.findCars(request));
    }
    @PostMapping()
    public ResponseEntity<CarDTO> createCar(
            @RequestBody @Valid CarDTO carDTO) {
        return ResponseEntity.status(HttpStatus.CREATED).body(carService.createCar(carDTO));
    }
    @GetMapping("/{id}")
    public ResponseEntity<CarDTOFullInfo> getCar(
            @PathVariable Long id) {
        return ResponseEntity.status(HttpStatus.OK).body(carService.findCar(id));
    }

}
