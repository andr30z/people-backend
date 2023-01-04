package com.management.people.util;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.management.people.dto.PaginationResponse;

public class PaginationResponseFactory {
    public static <T> PaginationResponse<T> create(Page<T> page) {
        Pageable pageable = page.getPageable();
        return PaginationResponse.<T>builder()
                .data(page.getContent())
                .totalPages(page.getTotalPages())
                .perPage(pageable.getPageSize())
                .currentPage(pageable.getPageNumber() + 1)
                .totalItems(page.getTotalElements())
                .build();
    }

}
