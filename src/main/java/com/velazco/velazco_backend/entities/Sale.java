package com.velazco.velazco_backend.entities;

import java.math.BigDecimal;
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
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "ventas")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Sale {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(name = "fecha_venta", nullable = false)
  private LocalDateTime saleDate;

  @Column(name = "metodo_pago", length = 50, nullable = false)
  private String paymentMethod;

  @Column(name = "monto_total", precision = 10, scale = 2, nullable = false)
  private BigDecimal totalAmount;

  @OneToOne
  @JoinColumn(name = "pedido_id", nullable = false, unique = true)
  private Order order;

  @ManyToOne
  @JoinColumn(name = "cajero_id", nullable = false)
  private User cashier;
}
