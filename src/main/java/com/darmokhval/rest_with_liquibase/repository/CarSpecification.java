package com.darmokhval.rest_with_liquibase.repository;

import com.darmokhval.rest_with_liquibase.model.entity.Car;
import jakarta.persistence.criteria.JoinType;
import org.springframework.data.jpa.domain.Specification;

public class CarSpecification {
    public static Specification<Car> hasBrandByName(String brand) {
        return (root, query, criteriaBuilder) ->
                (brand != null) ? criteriaBuilder.equal(root.get("brand").get("brandName"), brand) : criteriaBuilder.conjunction();
    }
    public static Specification<Car> hasBrandById(Long id) {
        return (root, query, criteriaBuilder) ->
                (id != null) ? criteriaBuilder.equal(root.get("brand").get("id"), id) : criteriaBuilder.conjunction();
    }

    public static Specification<Car> hasModelByName(String model) {
        return (root, query, criteriaBuilder) ->
                (model != null) ? criteriaBuilder.equal(root.get("model").get("modelName"), model) : criteriaBuilder.conjunction();
    }
    public static Specification<Car> hasModelById(Long id) {
        return (root, query, criteriaBuilder) ->
                (id != null) ? criteriaBuilder.equal(root.get("model").get("id"), id) : criteriaBuilder.conjunction();
    }
    public static Specification<Car> hasYearOfRelease(Integer year) {
        return (root, query, criteriaBuilder) ->
                (year != null) ? criteriaBuilder.equal(root.get("yearOfRelease"), year) : criteriaBuilder.conjunction();
    }
    public static Specification<Car> hasWasInAccident(Boolean wasInAccident) {
        return (root, query, criteriaBuilder) ->
                (wasInAccident != null) ? criteriaBuilder.equal(root.get("wasInAccident"), wasInAccident) : criteriaBuilder.conjunction();
    }
    public static Specification<Car> hasMileage(Integer mileage) {
        return (root, query, criteriaBuilder) ->
                (mileage != null) ? criteriaBuilder.equal(root.get("yearOfRelease"), mileage) : criteriaBuilder.conjunction();
    }
    public static Specification<Car> hasOwnerId(Long ownerId) {
        return (root, query, criteriaBuilder) ->
                (ownerId != null) ? criteriaBuilder.equal(root.get("owner").get("id"), ownerId) : criteriaBuilder.conjunction();
    }
    public static Specification<Car> hasOwnerName(String name) {
        return (root, query, criteriaBuilder) ->
                (name != null) ? criteriaBuilder.equal(root.get("owner").get("name"), name) : criteriaBuilder.conjunction();
    }
    public static Specification<Car> hasOwnerLastname(String lastname) {
        return (root, query, criteriaBuilder) ->
                (lastname != null) ? criteriaBuilder.equal(root.get("owner").get("lastname"), lastname) : criteriaBuilder.conjunction();
    }
    public static Specification<Car> hasAccessoryById(Long accessoryId) {
        return (root, query, criteriaBuilder) -> {
            if (accessoryId == null) {
                return criteriaBuilder.conjunction();
            }
            var accessoriesJoin = root.join("accessories", JoinType.INNER);
            return criteriaBuilder.equal(accessoriesJoin.get("id"), accessoryId);
        };
    }
    public static Specification<Car> hasAccessoryByAccessoryName(String accessoryName) {
        return (root, query, criteriaBuilder) -> {
            if (accessoryName == null) {
                return criteriaBuilder.conjunction();
            }
            var accessoriesJoin = root.join("accessories", JoinType.INNER);
            return criteriaBuilder.equal(accessoriesJoin.get("accessoryName"), accessoryName);
        };
    }
}
