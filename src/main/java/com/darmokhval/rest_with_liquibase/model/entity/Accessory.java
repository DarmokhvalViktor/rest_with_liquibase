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
    private List<Car> cars = new ArrayList<>();

    // Method to add a car and ensure bidirectional consistency
    public void addCar(Car car) {
        if (!this.cars.contains(car)) {
            this.cars.add(car);
            car.getAccessories().add(this);
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
            car.getAccessories().remove(this);
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Accessory accessory = (Accessory) o;

        if (!Objects.equals(id, accessory.id)) return false;
        return Objects.equals(accessoryName, accessory.accessoryName);
    }

    @Override
    public int hashCode() {
        int result = id != null ? id.hashCode() : 0;
        result = 31 * result + (accessoryName != null ? accessoryName.hashCode() : 0);
        return result;
    }
}
