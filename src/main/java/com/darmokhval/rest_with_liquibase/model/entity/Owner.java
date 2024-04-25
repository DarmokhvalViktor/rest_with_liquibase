package com.darmokhval.rest_with_liquibase.model.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

import java.util.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@ToString
public class Owner {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Name shouldn't be blank!")
    private String name;
//    TODO add email, ensure that it's unique. Add more validation.

    @NotBlank(message = "Email shouldn't be blank!")
    @Email(message = "Provided email is not valid")
    @Column(unique = true, nullable = false)
    private String email;

    @NotBlank(message = "Lastname shouldn't be blank!")
    private String lastname;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "owner", orphanRemoval = true)
    private Set<Car> cars = new HashSet<>();

    public void addCar(Car car) {
        this.cars.add(car);
        car.setOwner(this);
    }

    public void removeCar(Long id) {
        Optional<Car> carOptional = this.cars.stream()
                .filter(car -> car.getId().equals(id))
                .findFirst();
        if(carOptional.isPresent()) {
            this.cars.remove(carOptional.get());
            carOptional.get().setOwner(null);
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Owner owner = (Owner) o;

        if (!Objects.equals(id, owner.id)) return false;
        if (!Objects.equals(name, owner.name)) return false;
        if (!Objects.equals(email, owner.email)) return false;
        return Objects.equals(lastname, owner.lastname);
    }

    @Override
    public int hashCode() {
        int result = id != null ? id.hashCode() : 0;
        result = 31 * result + (name != null ? name.hashCode() : 0);
        result = 31 * result + (email != null ? email.hashCode() : 0);
        result = 31 * result + (lastname != null ? lastname.hashCode() : 0);
        return result;
    }
}
