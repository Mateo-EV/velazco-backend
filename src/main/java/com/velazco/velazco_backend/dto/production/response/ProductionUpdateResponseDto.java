package com.velazco.velazco_backend.dto.production.response;

import java.time.LocalDate;
import java.util.List;

import com.velazco.velazco_backend.entities.Production.ProductionStatus;

import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductionUpdateResponseDto {
  @Data
  @NoArgsConstructor
  @AllArgsConstructor
  @Builder
  public static class AssignedByProductionUpdateResponseDto {
    private Long id;
    private String name;
  }

  @Data
  @NoArgsConstructor
  @AllArgsConstructor
  @Builder
  public static class AssignedToProductionUpdateResponseDto {
    private Long id;
    private String name;
  }

  @Data
  @NoArgsConstructor
  @AllArgsConstructor
  @Builder
  public static class ProductProductionUpdateResponseDto {
    private Long id;
    private String name;
  }

  @Data
  @NoArgsConstructor
  @AllArgsConstructor
  @Builder
  public static class DetailProductionUpdateResponseDto {
    private ProductProductionUpdateResponseDto product;
    private Integer requestedQuantity;
    private Integer producedQuantity;

  }

  private Long id;
  private LocalDate productionDate;
  private ProductionStatus status;
  private AssignedByProductionUpdateResponseDto assignedBy;
  private AssignedToProductionUpdateResponseDto assignedTo;
  
  @Size(max = 1000)
  private String comments;

  private List<DetailProductionUpdateResponseDto> details;
}
