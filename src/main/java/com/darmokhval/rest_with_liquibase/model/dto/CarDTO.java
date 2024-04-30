package com.darmokhval.rest_with_liquibase.model.dto;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Year;
import java.util.List;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CarDTO {
    private Long id;
    @NotNull(message = "Model ID shouldn't be null.")
    @Positive(message = "Model ID must be positive.")
    private Long modelId;
    @NotNull(message = "Brand ID shouldn't be null.")
    @Positive(message = "Brand ID must be positive.")
    private Long brandId;
    @NotNull(message = "Owner ID shouldn't be null.")
    @Positive(message = "Owner ID must be positive.")
    private Long ownerId;
    @Positive
    @Min(value = 1970, message = "Release year shouldn't be earlier than 1970")
    private Integer yearOfRelease;
    @Positive
    @NotNull(message = "Mileage shouldn't be null or negative")
    private Integer mileage;
    @NotNull(message = "Must be specified if car was in accident or not.")
    private Boolean wasInAccident;
    @NotEmpty(message = "List accessoriesIds must not be empty")
    @Size(min = 2, message = "List accessoriesIds must contain at least 2 items")
    private List<
                @NotNull (message = "Accessory ID shouldn't be null.")
                @Positive (message = "Accessory ID must be positive.")
                        Long> accessoriesIds;

    public CarDTO(Long modelId, Long brandId, Long ownerId, Integer yearOfRelease, Integer mileage, Boolean wasInAccident, List<Long> accessoriesIds) {
        this.modelId = modelId;
        this.brandId = brandId;
        this.ownerId = ownerId;
        this.yearOfRelease = yearOfRelease;
        this.mileage = mileage;
        this.wasInAccident = wasInAccident;
        this.accessoriesIds = accessoriesIds;
    }
}
