package com.velazco.velazco_backend.dto.production.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import java.util.List;

@Data
public class ProductionFinalizeRequestDto {

  @NotNull(message = "Se requiere la lista de productos producidos")
  private List<ProductResultDto> productos;

  @Data
  public static class ProductResultDto {

    @NotNull(message = "ID del producto requerido")
    private Long productId;

    @NotNull(message = "Cantidad producida requerida")
    @Min(value = 0, message = "La cantidad producida no puede ser negativa")
    private Integer producedQuantity;

    private String motivoIncompleto; // Solo si es incompleto
  }
}
