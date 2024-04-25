package com.darmokhval.rest_with_liquibase.model.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class OwnerDTO {
    private Long id;
    @NotBlank(message = "Name shouldn't be blank!")
    private String name;
    @NotBlank(message = "Lastname shouldn't be blank!")
    private String lastname;
    @NotBlank(message = "Email shouldn't be blank!")
    @Email(message = "Provided email is not valid")
    private String email;
    private List<CarDTOLight> cars;

    public OwnerDTO(Long id, String name, String lastname, String email) {
        this.id = id;
        this.name = name;
        this.lastname = lastname;
        this.email = email;
    }
}
