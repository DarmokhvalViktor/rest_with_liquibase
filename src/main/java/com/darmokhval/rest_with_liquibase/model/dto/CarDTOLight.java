package com.darmokhval.rest_with_liquibase.model.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CarDTOLight {
    private Long id;
    private String model;
    private String brand;
    private String ownerName;
    private Long ownerId;
}
