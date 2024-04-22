package com.darmokhval.rest_with_liquibase.service;

import com.darmokhval.rest_with_liquibase.model.dto.CarSearchRequest;
import com.darmokhval.rest_with_liquibase.model.dto.CarSearchResponse;
import com.darmokhval.rest_with_liquibase.model.dto.PaginationConfig;
import com.darmokhval.rest_with_liquibase.model.entity.Car;
import com.darmokhval.rest_with_liquibase.repository.CarRepository;
import com.darmokhval.rest_with_liquibase.repository.CarSpecification;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CarService {
    private CarRepository carRepository;

    public CarSearchResponse findCars(CarSearchRequest request, PaginationConfig paginationConfig) {
        Pageable pageable = getPageable(paginationConfig);
        Specification<Car> spec = getCarSpecification(request);
        Page<Car> filteredCars = carRepository.findAll(spec, pageable);

        return new CarSearchResponse(
                filteredCars,
                paginationConfig.getPageNumber(),
                filteredCars.getTotalPages(),
                filteredCars.getTotalElements()
        );
    }

    /**
     *create specification from user's input to return records that matches input fields.
     */
    private static Specification<Car> getCarSpecification(CarSearchRequest request) {
        return Specification
                .where(CarSpecification.hasBrand(request.getBrand()))
                .and(CarSpecification.hasMileage(request.getMileage()))
                .and(CarSpecification.hasModel(request.getModel()))
                .and(CarSpecification.hasWasInAccident(request.getWasInAccident()))
                .and(CarSpecification.hasYearOfRelease(request.getYearOfRelease()))
                .and(CarSpecification.hasOwnerId(request.getOwner().getId()))
                .and(CarSpecification.hasOwnerName(request.getOwner().getName()))
                .and(CarSpecification.hasOwnerLastname(request.getOwner().getLastname()));
    }

    /**
     * check for input how many pages per page requested. If number not 20, 50 or 100, change it to 20 per page.
     * Returns Pageable
     */
    private static Pageable getPageable(PaginationConfig paginationConfig) {
        int resultsPerPage = switch (paginationConfig.getResultsPerPage()) {
            case 20, 50, 100 -> paginationConfig.getResultsPerPage();
            default -> 20;
        };
        return PageRequest.of(paginationConfig.getPageNumber(), resultsPerPage);
    }
}
