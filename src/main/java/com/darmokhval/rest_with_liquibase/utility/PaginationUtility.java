package com.darmokhval.rest_with_liquibase.utility;

import com.darmokhval.rest_with_liquibase.model.dto.CarSearchRequest;
import com.darmokhval.rest_with_liquibase.model.dto.PaginationConfig;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

/**
 * Utility method for a custom pagination. Ensures that even client sends request without pagination,
 * the default values will be used.
 */
@Component
public class PaginationUtility {

    /**
     * checks if user provided pagination in request. If not -> creates default with predefined values.
     */
    public PaginationConfig checkIfPaginationPresent(CarSearchRequest request) {
        if(request.getPaginationConfig() == null) {
            return new PaginationConfig(0, 20);
        } else {
            return request.getPaginationConfig();
        }
    }

    /**
     * check for input how many pages per page requested. If number not 20, 50 or 100, change it to 20 per page.
     * Returns Pageable
     */
    public Pageable getPageable(PaginationConfig paginationConfig) {
        int resultsPerPage = switch (paginationConfig.getResultsPerPage()) {
            case 20, 50, 100 -> paginationConfig.getResultsPerPage();
            default -> 20;
        };
        return PageRequest.of(paginationConfig.getPageNumber(), resultsPerPage);
    }
}
