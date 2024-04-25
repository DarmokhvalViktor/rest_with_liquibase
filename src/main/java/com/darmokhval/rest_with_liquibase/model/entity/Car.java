package com.darmokhval.rest_with_liquibase.model.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.*;

import java.util.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity

public class Car {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "brand_id")
    private Brand brand;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "model_id")
    private Model model;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "owner_id")
    private Owner owner;

//    TODO create indexes in database???

    @ManyToMany(mappedBy = "cars")
    private Set<Accessory> accessories = new HashSet<>();

    @Positive
    private Integer yearOfRelease;
    @Positive
    private Integer mileage;
    @NotNull
    private Boolean wasInAccident;


    // Method to add an accessory and ensure bidirectional consistency
    public void addAccessory(Accessory accessory) {
        if(!this.accessories.contains(accessory)) {
            this.accessories.add(accessory);
            accessory.addCar(this);
        }
    }

    // Method to remove an accessory and ensure bidirectional consistency
    public void removeAccessory(Long id) {
        Optional<Accessory> accessoryOptional = this.accessories.stream()
                .filter(accessory -> accessory.getId().equals(id))
                .findFirst();
        if(accessoryOptional.isPresent()) {
            Accessory accessory = accessoryOptional.get();
            this.accessories.remove(accessory); //remove accessory from car's list
            accessory.removeCar(this.getId()); //remove car from accessory's list of Cars
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Car car = (Car) o;

        if (!Objects.equals(id, car.id)) return false;
        if (!Objects.equals(brand, car.brand)) return false;
        if (!Objects.equals(model, car.model)) return false;
        if (!Objects.equals(owner, car.owner)) return false;
        if (!Objects.equals(accessories, car.accessories)) return false;
        if (!Objects.equals(yearOfRelease, car.yearOfRelease)) return false;
        if (!Objects.equals(mileage, car.mileage)) return false;
        return Objects.equals(wasInAccident, car.wasInAccident);
    }

    @Override
    public int hashCode() {
        int result = id != null ? id.hashCode() : 0;
        result = 31 * result + (brand != null ? brand.hashCode() : 0);
        result = 31 * result + (model != null ? model.hashCode() : 0);
        result = 31 * result + (owner != null ? owner.hashCode() : 0);
        result = 31 * result + (accessories != null ? accessories.hashCode() : 0);
        result = 31 * result + (yearOfRelease != null ? yearOfRelease.hashCode() : 0);
        result = 31 * result + (mileage != null ? mileage.hashCode() : 0);
        result = 31 * result + (wasInAccident != null ? wasInAccident.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "Car{" +
                "id=" + id +
                ", brand=" + brand +
                ", model=" + model +
                ", owner=" + owner +
                ", accessories=" + accessories +
                ", yearOfRelease=" + yearOfRelease +
                ", mileage=" + mileage +
                ", wasInAccident=" + wasInAccident +
                '}';
    }
}
