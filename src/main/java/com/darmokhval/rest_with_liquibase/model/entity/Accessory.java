package com.darmokhval.rest_with_liquibase.model.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Accessory {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(unique = true, nullable = false)
    private String accessoryName;

    @ManyToMany
    @JoinTable(
            name = "car_accessory",
            joinColumns = @JoinColumn(name = "accessory_id"),
            inverseJoinColumns = @JoinColumn(name = "car_id")
    )
    private Set<Car> cars = new HashSet<>();

    // Method to add a car and ensure bidirectional consistency
    public void addCar(Car car) {
        if (!this.cars.contains(car)) {
            this.cars.add(car); // Add to the Accessory's list of Cars
            car.addAccessory(this); // Add to the Car's list of Accessories
        }
    }

    // Method to remove a car and ensure bidirectional consistency
    public void removeCar(Long id) {
        Optional<Car> carOptional = cars.stream()
                .filter(car -> car.getId().equals(id))
                .findFirst();

        if(carOptional.isPresent()) {
            Car car = carOptional.get();
            cars.remove(car);
            car.removeAccessory(this.getId());
        }
    }
}
