package com.darmokhval.rest_with_liquibase;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@SpringBootApplication
@EnableCaching
public class RestWithLiquibaseApplication {

	public static void main(String[] args) {
		SpringApplication.run(RestWithLiquibaseApplication.class, args);
	}

}
