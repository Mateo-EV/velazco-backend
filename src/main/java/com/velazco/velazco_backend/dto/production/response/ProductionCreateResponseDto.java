package com.velazco.velazco_backend.dto.production.response;

import java.time.LocalDateTime;
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
public class ProductionCreateResponseDto {
  @Data
  @NoArgsConstructor
  @AllArgsConstructor
  @Builder
  public static class AssignedByProductionCreateResponseDto {
    private Long id;
    private String name;
  }

  @Data
  @NoArgsConstructor
  @AllArgsConstructor
  @Builder
  public static class AssignedToProductionCreateResponseDto {
    private Long id;
    private String name;
  }

  @Data
  @NoArgsConstructor
  @AllArgsConstructor
  @Builder
  public static class ProductProductionCreateResponseDto {
    private Long id;
    private String name;
  }

  @Data
  @NoArgsConstructor
  @AllArgsConstructor
  @Builder
  public static class DetailProductionCreateResponseDto {
    private ProductProductionCreateResponseDto product;
    private Integer requestedQuantity;
    private Integer producedQuantity;
    private String comments;
  }

  private Long id;
  private LocalDateTime productionDate;
  private ProductionStatus status;
  private AssignedByProductionCreateResponseDto assignedBy;
  private AssignedToProductionCreateResponseDto assignedTo;
  private List<DetailProductionCreateResponseDto> details;
}
