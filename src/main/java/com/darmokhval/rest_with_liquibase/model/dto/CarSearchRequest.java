package com.darmokhval.rest_with_liquibase.model.dto;

import com.darmokhval.rest_with_liquibase.model.entity.Brand;
import com.darmokhval.rest_with_liquibase.model.entity.Model;
import com.darmokhval.rest_with_liquibase.model.entity.Owner;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CarSearchRequest {
    private Long id;
    private Brand brand;
    private Model model;
    private Owner owner;
    private Integer yearOfRelease;
    private Integer mileage;
    private Boolean wasInAccident;
}
