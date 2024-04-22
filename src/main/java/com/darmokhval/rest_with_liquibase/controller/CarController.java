package com.darmokhval.rest_with_liquibase.controller;

import com.darmokhval.rest_with_liquibase.model.dto.CarSearchRequest;
import com.darmokhval.rest_with_liquibase.model.dto.CarSearchResponse;
import com.darmokhval.rest_with_liquibase.model.dto.PaginationConfig;
import com.darmokhval.rest_with_liquibase.service.CarService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/cars")
public class CarController {
    private CarService carService;

    @PostMapping("/_list")
    public ResponseEntity<CarSearchResponse> searchFilteredCars(
            @RequestBody CarSearchRequest request,
            @RequestBody PaginationConfig paginationConfig) {
        return ResponseEntity.ok().body(carService.findCars(request, paginationConfig));
    }
}
