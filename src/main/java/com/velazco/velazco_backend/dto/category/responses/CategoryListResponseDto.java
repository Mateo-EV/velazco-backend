package com.velazco.velazco_backend.dto.category.responses;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CategoryListResponseDto {
    private Integer id;
    private String name;
}
