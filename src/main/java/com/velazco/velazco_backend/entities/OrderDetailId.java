package com.velazco.velazco_backend.entities;

import java.io.Serializable;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Embeddable
@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderDetailId implements Serializable {
  @Column(name = "pedido_id")
  private Long orderId;

  @Column(name = "producto_id")
  private Long productId;
}
