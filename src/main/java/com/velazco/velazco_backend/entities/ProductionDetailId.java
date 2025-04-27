package com.velazco.velazco_backend.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.Data;
import lombok.NoArgsConstructor;

@Embeddable
@Data
@NoArgsConstructor
public class ProductionDetailId {
  @Column(name = "produccion_id")
  private Integer productionId;

  @Column(name = "producto_id")
  private Integer productId;
}
