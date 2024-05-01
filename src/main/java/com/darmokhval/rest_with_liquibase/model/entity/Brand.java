package com.darmokhval.rest_with_liquibase.model.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.*;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Brand {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(unique = true, nullable = false)
    private String brandName;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "brand")
    private Set<Car> cars = new HashSet<>();

    public Brand(Long id, String brandName) {
        this.id = id;
        this.brandName = brandName;
    }

    public Brand(String brandName) {
        this.brandName = brandName;
    }

    public void addCar(Car car) {
        this.cars.add(car);
        car.setBrand(this);
    }

    public void removeCar(Long id) {
        Optional<Car> carOptional = this.cars.stream()
                .filter(car -> car.getId().equals(id))
                .findFirst();
        if(carOptional.isPresent()) {
            this.cars.remove(carOptional.get());
            carOptional.get().setBrand(null);
        }
    }
}
