package com.darmokhval.rest_with_liquibase.model.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CarDTOFullInfo {
    private Long id;
    private BrandDTO brandDTO;
    private ModelDTO modelDTO;
    private OwnerDTOSimple ownerDTO;
    private Integer yearOfRelease;
    private Integer mileage;
    private Boolean wasInAccident;
    private List<AccessoryDTO> accessoryDTOList;
}
