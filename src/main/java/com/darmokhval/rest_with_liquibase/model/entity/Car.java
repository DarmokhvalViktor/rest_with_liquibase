package com.darmokhval.rest_with_liquibase.model.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Car {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "brand_id")
    private Brand brand;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "model_id")
    private Model model;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "owner_id")
    private Owner owner;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "car")
    private List<Accessory> accessories = new ArrayList<>();

    private Integer yearOfRelease;
    private Integer mileage;
    private boolean wasInAccident;


    public void addAccessory(Accessory accessory) {
        this.accessories.add(accessory);
        accessory.setCar(this);
    }

    public void addAccessory(Long id) {
        Optional<Accessory> accessoryOptional = this.accessories.stream()
                .filter(accessory -> accessory.getId().equals(id))
                .findFirst();
        if(accessoryOptional.isPresent()) {
            this.accessories.remove(accessoryOptional.get());
            accessoryOptional.get().setCar(null);
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Car car = (Car) o;

        if (wasInAccident != car.wasInAccident) return false;
        if (brand != car.brand) return false;
        if (model != car.model) return false;
        if (!Objects.equals(yearOfRelease, car.yearOfRelease)) return false;
        if (!Objects.equals(owner, car.owner)) return false;
        if (!Objects.equals(mileage, car.mileage)) return false;
        return Objects.equals(accessories, car.accessories);
    }

    @Override
    public int hashCode() {
        int result = brand != null ? brand.hashCode() : 0;
        result = 31 * result + (model != null ? model.hashCode() : 0);
        result = 31 * result + (yearOfRelease != null ? yearOfRelease.hashCode() : 0);
        result = 31 * result + (owner != null ? owner.hashCode() : 0);
        result = 31 * result + (mileage != null ? mileage.hashCode() : 0);
        result = 31 * result + (accessories != null ? accessories.hashCode() : 0);
        result = 31 * result + (wasInAccident ? 1 : 0);
        return result;
    }

    @Override
    public String toString() {
        return "Car{" +
                "brand=" + brand +
                ", model=" + model +
                ", yearOfRelease=" + yearOfRelease +
                ", owner=" + owner +
                ", mileage=" + mileage +
                ", accessories=" + accessories +
                ", wasInAccident=" + wasInAccident +
                '}';
    }
}
