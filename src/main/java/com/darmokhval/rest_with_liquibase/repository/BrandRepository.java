package com.darmokhval.rest_with_liquibase.repository;

import com.darmokhval.rest_with_liquibase.model.entity.Brand;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BrandRepository extends JpaRepository<Brand, Long> {
}
