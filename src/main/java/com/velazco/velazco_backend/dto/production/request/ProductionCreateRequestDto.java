package com.velazco.velazco_backend.dto.production.request;

import java.time.LocalDate;
import java.util.List;

import com.velazco.velazco_backend.entities.Production.ProductionStatus;
import com.velazco.velazco_backend.validation.UniqueField;

import jakarta.validation.Valid;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductionCreateRequestDto {
  @Data
  @NoArgsConstructor
  @AllArgsConstructor
  @Builder
  public static class ProductionDetailCreateRequestDto {
    @NotNull
    @Positive
    Long productId;

    @NotNull
    @Positive
    @Min(1)
    Integer requestedQuantity;

    @Size(min = 1, max = 500)
    String comments;
  }

  @NotNull
  @FutureOrPresent
  LocalDate productionDate;

  @NotNull
  @Positive
  Long assignedToId;

  @NotNull
  ProductionStatus status;

  @NotNull
  @Size(min = 1)
  @UniqueField(fieldName = "productId")
  private List<@Valid ProductionDetailCreateRequestDto> details;
}
