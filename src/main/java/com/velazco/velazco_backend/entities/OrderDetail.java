package com.velazco.velazco_backend.entities;

import java.math.BigDecimal;

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
@Table(name = "detalle_pedidos")
@Data
@NoArgsConstructor
public class OrderDetail {
  @EmbeddedId
  private OrderDetailId id;

  @ManyToOne
  @MapsId("orderId")
  @JoinColumn(name = "pedido_id", nullable = false)
  private Order order;

  @ManyToOne
  @MapsId("productId")
  @JoinColumn(name = "producto_id", nullable = false)
  private Product product;

  @Column(name = "cantidad", nullable = false)
  private Integer quantity;

  @Column(name = "precio_unitario", precision = 10, scale = 2, nullable = false)
  private BigDecimal unitPrice;
}
