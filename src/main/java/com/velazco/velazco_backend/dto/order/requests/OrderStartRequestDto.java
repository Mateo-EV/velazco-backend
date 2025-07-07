package com.velazco.velazco_backend.dto.order.requests;

import java.util.List;

import com.velazco.velazco_backend.validation.QuantityAvailable;
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

@QuantityAvailable
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

    @NotNull(message = "El ID del producto es obligatorio.")
    @Positive(message = "El ID del producto debe ser un número positivo.")
    private Long productId;

    @NotNull(message = "La cantidad es obligatoria.")
    @Positive(message = "La cantidad debe ser un número positivo.")
    @Min(value = 1, message = "La cantidad mínima debe ser 1.")
    private Integer quantity;
  }

  @NotBlank(message = "El nombre del cliente es obligatorio.")
  private String clientName;

  @NotNull(message = "La lista de detalles no puede ser nula.")
  @Size(min = 1, message = "Debe haber al menos un detalle en la orden.")
  @UniqueField(fieldName = "productId")
  private List<@Valid DetailOrderStartRequestDto> details;

}
