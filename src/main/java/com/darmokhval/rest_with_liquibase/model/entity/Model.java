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
public class Model {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(unique = true, nullable = false)
    private String modelName;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "model",fetch = FetchType.LAZY)
    private Set<Car> cars = new HashSet<>();

    public Model(String modelName) {
        this.modelName = modelName;
    }

    public void addCar(Car car) {
        this.cars.add(car);
        car.setModel(this);
    }

    public void removeCar(Long id) {
        Optional<Car> carOptional = this.cars.stream()
                .filter(car -> car.getId().equals(id))
                .findFirst();
        if(carOptional.isPresent()) {
            this.cars.remove(carOptional.get());
            carOptional.get().setModel(null);
        }
    }
}
