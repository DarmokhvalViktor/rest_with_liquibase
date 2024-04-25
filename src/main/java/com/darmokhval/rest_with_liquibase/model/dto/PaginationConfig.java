package com.darmokhval.rest_with_liquibase.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
//TODO refactor, Page class is sufficient to use.
public class PaginationConfig {
    private int pageNumber = 0;
    private int resultsPerPage = 20;
}
