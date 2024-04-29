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
    private Integer mileage;
    @NotNull
    private Boolean wasInAccident;
    @NotEmpty(message = "List accessoriesIds must not be empty")
    @Size(min = 2, message = "List accessoriesIds must contain at least 2 items")
    private List<
                @NotNull (message = "Accessory ID shouldn't be null.")
                @Positive (message = "Accessory ID must be positive.")
                        Long> accessoriesIds;
}
