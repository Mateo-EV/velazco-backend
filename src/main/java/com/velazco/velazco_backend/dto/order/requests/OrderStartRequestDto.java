package com.velazco.velazco_backend.dto.order.requests;

import java.util.List;

import com.velazco.velazco_backend.validation.UniqueField;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
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
public class OrderStartRequestDto {

  @Data
  @NoArgsConstructor
  @AllArgsConstructor
  @Builder
  public static class DetailOrderStartRequestDto {
    @NotNull
    @Positive
    private Long productId;

    @NotNull
    @Positive
    @Min(1)
    private Integer quantity;
  }

  @NotBlank
  private String clientName;

  @NotNull
  @Size(min = 1)
  @UniqueField(fieldName = "productId")
  private List<@Valid DetailOrderStartRequestDto> details;

}
