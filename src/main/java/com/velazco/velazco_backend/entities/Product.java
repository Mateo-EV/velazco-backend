package com.velazco.velazco_backend.entities;

import java.math.BigDecimal;
import java.util.List;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "productos")
@Data
@NoArgsConstructor
public class Product {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(name = "nombre", length = 100, nullable = false, unique = true)
  private String name;

  @Column(name = "precio", precision = 10, scale = 2, nullable = false)
  private BigDecimal price;

  @Column(name = "stock", nullable = false)
  private Integer stock;

  @Column(name = "imagen", length = 255)
  private String image;

  @Column(name = "activo", nullable = false)
  private Boolean active;

  @ManyToOne
  @JoinColumn(name = "categoria_id", nullable = false)
  private Category category;

  @OneToMany(mappedBy = "product")
  private List<OrderDetail> orderDetails;

  @OneToMany(mappedBy = "product")
  private List<ProductionDetail> productionDetails;

}
