package com.darmokhval.rest_with_liquibase.model.dto;

import com.darmokhval.rest_with_liquibase.model.entity.Car;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.domain.Page;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CarSearchResponse {
    private Page<Car> cars;
    private int currentPage;
    private int totalPages;
    private long totalElements;
}
