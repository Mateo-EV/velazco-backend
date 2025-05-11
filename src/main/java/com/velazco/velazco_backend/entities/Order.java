package com.velazco.velazco_backend.entities;

import java.time.LocalDateTime;
import java.util.List;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "pedidos")
@Data
@NoArgsConstructor
public class Order {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(name = "fecha_pedido", nullable = false)
  private LocalDateTime date;

  @Column(name = "nombre_cliente", length = 100, nullable = false)
  private String clientName;

  @Enumerated(EnumType.STRING)
  @Column(name = "estado", nullable = false, length = 20)
  private OrderStatus status;

  @ManyToOne
  @JoinColumn(name = "usuario_atencion_id", nullable = false)
  private User attendedBy;

  @OneToOne(mappedBy = "order")
  private Sale sale;

  @OneToMany(mappedBy = "order")
  private List<OrderDetail> details;

  public static enum OrderStatus {
    PENDIENTE, PAGADO, CANCELADO, ENTREGADO
  }
}
