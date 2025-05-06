package com.velazco.velazco_backend.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PaginatedResponseDto<T> {
    private List<T> content;
    private int currentPage;
    private int totalPages;
    private long totalItems;
}
