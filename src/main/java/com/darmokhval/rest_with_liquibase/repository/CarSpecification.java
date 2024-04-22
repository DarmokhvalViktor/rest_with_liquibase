package com.darmokhval.rest_with_liquibase.repository;

import com.darmokhval.rest_with_liquibase.model.entity.Brand;
import com.darmokhval.rest_with_liquibase.model.entity.Car;
import com.darmokhval.rest_with_liquibase.model.entity.Model;
import com.darmokhval.rest_with_liquibase.model.entity.Owner;
import org.springframework.data.jpa.domain.Specification;

public class CarSpecification {
    public static Specification<Car> hasBrand(Brand brand) {
        return ((root, query, criteriaBuilder) ->
                (brand != null) ? criteriaBuilder.equal(root.get("brand"), brand) : null);
    }

    public static Specification<Car> hasModel(Model model) {
        return ((root, query, criteriaBuilder) ->
                (model != null) ? criteriaBuilder.equal(root.get("model"), model) : null);
    }
    public static Specification<Car> hasYearOfRelease(Integer year) {
        return (root, query, criteriaBuilder) ->
                (year != null) ? criteriaBuilder.equal(root.get("yearOfRelease"), year) : null;
    }
    public static Specification<Car> hasWasInAccident(Boolean wasInAccident) {
        return (root, query, criteriaBuilder) ->
                (wasInAccident != null) ? criteriaBuilder.equal(root.get("wasInAccident"), wasInAccident) : null;
    }
    public static Specification<Car> hasMileage(Integer mileage) {
        return (root, query, criteriaBuilder) ->
                (mileage != null) ? criteriaBuilder.equal(root.get("yearOfRelease"), mileage) : null;
    }
    public static Specification<Car> hasOwnerId(Long ownerId) {
        return (root, query, criteriaBuilder) ->
                (ownerId != null) ? criteriaBuilder.equal(root.get("owner").get("id"), ownerId) : null;
    }
    public static Specification<Car> hasOwnerName(String name) {
        return (root, query, criteriaBuilder) ->
                (name != null) ? criteriaBuilder.equal(root.get("owner").get("name"), name) : null;
    }
    public static Specification<Car> hasOwnerLastname(String lastname) {
        return (root, query, criteriaBuilder) ->
                (lastname != null) ? criteriaBuilder.equal(root.get("owner").get("lastname"), lastname) : null;
    }
}
