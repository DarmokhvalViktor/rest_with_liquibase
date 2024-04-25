package com.darmokhval.rest_with_liquibase.model.dto;

import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class CarSearchRequest {
    private Long id;
    private BrandDTO brand;
    private ModelDTO model;
    private OwnerDTO owner;
    private Integer yearOfRelease;
    private Integer mileage;
    private Boolean wasInAccident;
    private PaginationConfig paginationConfig;
    private List<AccessoryDTO> accessoryDTOList;
}
