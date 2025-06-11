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
public class ProductionListResponseDto {

  @Data
  @NoArgsConstructor
  @AllArgsConstructor
  @Builder
  public static class AssignedByProductionListResponseDto {
    private Long id;
    private String name;
  }

  @Data
  @NoArgsConstructor
  @AllArgsConstructor
  @Builder
  public static class AssignedToProductionListResponseDto {
    private Long id;
    private String name;
  }

  @Data
  @NoArgsConstructor
  @AllArgsConstructor
  @Builder
  public static class ProductProductionListResponseDto {
    private Long id;
    private String name;
  }

  @Data
  @NoArgsConstructor
  @AllArgsConstructor
  @Builder
  public static class DetailProductionListResponseDto {
    private ProductProductionListResponseDto product;
    private Integer requestedQuantity;
    private Integer producedQuantity;
    private String comments;
  }

  private Long id;
  private LocalDate productionDate;
  private ProductionStatus status;
  private AssignedByProductionListResponseDto assignedBy;
  private AssignedToProductionListResponseDto assignedTo;
  private List<DetailProductionListResponseDto> details;
}
