package com.velazco.velazco_backend.dto.production.response;

import java.time.LocalDate;
import java.util.List;

import com.velazco.velazco_backend.entities.Production.ProductionStatus;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductionProcessResponseDto {
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class AssignedByDto {
        private Long id;
        private String name;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class AssignedToDto {
        private Long id;
        private String name;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class ProductDto {
        private Long id;
        private String name;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class DetailDto {
        private ProductDto product;
        private Integer requestedQuantity;
        private Integer producedQuantity;
    }

    private Long id;
    private LocalDate productionDate;
    private ProductionStatus status;
    private String comments;

    private AssignedByDto assignedBy;
    private AssignedToDto assignedTo;
    private List<DetailDto> details;
}
