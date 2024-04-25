package com.darmokhval.rest_with_liquibase.model.dto;

import com.darmokhval.rest_with_liquibase.model.entity.Car;
import lombok.*;
import org.springframework.data.domain.Page;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CarSearchResponse {
//    TODO not in a use for now. Page instead.
    private Page<CarDTO> cars;
    private int currentPage;
    private int totalPages;
    private long totalElements;
}
