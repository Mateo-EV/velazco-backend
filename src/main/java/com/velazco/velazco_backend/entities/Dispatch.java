package com.velazco.velazco_backend.entities;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "entregas")
@Data
@NoArgsConstructor
public class Dispatch {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "id")
  private Integer id;

  @Column(name = "fecha_entrega", nullable = false)
  private LocalDateTime deliveryDate;

  @OneToOne
  @JoinColumn(name = "pedido_id", nullable = false, unique = true)
  private Order order;

  @ManyToOne
  @JoinColumn(name = "usuario_entrega_id", nullable = false)
  private User dispatchedBy;
}
