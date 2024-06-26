package com.darmokhval.rest_with_liquibase.controller;

import com.darmokhval.rest_with_liquibase.model.dto.CarDTO;
import com.darmokhval.rest_with_liquibase.model.dto.CarDTOFullInfo;
import com.darmokhval.rest_with_liquibase.model.dto.CarDTOLight;
import com.darmokhval.rest_with_liquibase.model.dto.CarSearchRequest;
import com.darmokhval.rest_with_liquibase.service.CarService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;


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
    @PostMapping("/_report")
    public ResponseEntity<byte[]> downloadCarData(
            @RequestBody(required = false) CarSearchRequest request) {
        String result = carService.getCarsReport(request);
        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.CONTENT_TYPE, "text/csv");
        headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=report.csv");
        return ResponseEntity.status(HttpStatus.OK).headers(headers).body(result.getBytes());
    }
    @PostMapping("/upload")
    public ResponseEntity<Map<String, Long>> populateDatabaseFromFile(
            @RequestPart("file")MultipartFile multipartFile) {
        return ResponseEntity.status(HttpStatus.OK).body(carService.populateDatabaseFromFile(multipartFile));
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
    @PutMapping("/{id}")
    public ResponseEntity<CarDTO> updateCar(
            @RequestBody @Valid CarDTO carDTO,
            @PathVariable Long id) {
        return ResponseEntity.status(HttpStatus.OK).body(carService.updateCar(carDTO, id));
    }
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteCar(
            @PathVariable Long id) {
        return ResponseEntity.status(HttpStatus.OK).body(carService.deleteCar(id));
    }

}
