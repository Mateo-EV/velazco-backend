package com.velazco.velazco_backend.entities;

import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MapsId;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "detalle_produccion")
@Data
@NoArgsConstructor
public class ProductionDetail {
  @EmbeddedId
  private ProductionDetailId id;

  @ManyToOne
  @MapsId("productionId")
  @JoinColumn(name = "produccion_id", nullable = false)
  private Production production;

  @ManyToOne
  @MapsId("productId")
  @JoinColumn(name = "producto_id", nullable = false)
  private Product product;

  @Column(name = "cantidad_solicitada", nullable = false)
  private Integer requestedQuantity;

  @Column(name = "cantidad_producida", nullable = false)
  private Integer producedQuantity;

  @Column(name = "comentarios", columnDefinition = "TEXT")
  private String comments;
}
