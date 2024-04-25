package com.darmokhval.rest_with_liquibase.model.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class OwnerDTOSimple {
    private Long id;
    private String name;
    private String lastname;
    private String email;
}
