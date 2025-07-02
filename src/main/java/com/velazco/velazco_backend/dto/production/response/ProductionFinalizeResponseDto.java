package com.velazco.velazco_backend.dto.production.response;

import lombok.Builder;
import lombok.Data;
import java.util.List;

@Data
@Builder
public class ProductionFinalizeResponseDto {

  private Long productionId;
  private String estadoFinal;
  private List<ProductResult> productos;

  @Data
  @Builder
  public static class ProductResult {
    private Long productId;
    private Integer cantidadProducida;
    private String motivo;
  }
}
