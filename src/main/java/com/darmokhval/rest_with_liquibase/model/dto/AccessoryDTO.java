package com.darmokhval.rest_with_liquibase.model.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AccessoryDTO {
    private Long id;
    private String accessoryName;
}
