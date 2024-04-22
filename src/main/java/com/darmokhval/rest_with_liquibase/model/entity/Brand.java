package com.darmokhval.rest_with_liquibase.model.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Brand {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String brandName;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "brand")
    private List<Car> cars = new ArrayList<>();

    public void addCar(Car car) {
        this.cars.add(car);
        car.setBrand(this);
    }

    public void deleteCar(Long id) {
        Optional<Car> carOptional = this.cars.stream()
                .filter(car -> car.getId().equals(id))
                .findFirst();
        if(carOptional.isPresent()) {
            this.cars.remove(carOptional.get());
            carOptional.get().setBrand(null);
        }
    }
}
