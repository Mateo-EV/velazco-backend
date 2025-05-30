package com.velazco.velazco_backend.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Embeddable
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductionDetailId {
  @Column(name = "produccion_id")
  private Long productionId;

  @Column(name = "producto_id")
  private Long productId;
}
