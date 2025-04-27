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
import jakarta.persistence.Table;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "produccion")
@Data
@NoArgsConstructor
public class Production {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "id")
  private Integer id;

  @Column(name = "fecha_produccion", nullable = false)
  private LocalDateTime productionDate;

  @Enumerated(EnumType.STRING)
  @Column(name = "estado", nullable = false, length = 20)
  private ProductionStatus status;

  @ManyToOne
  @JoinColumn(name = "asignado_por", nullable = false)
  private User assignedBy;

  @ManyToOne
  @JoinColumn(name = "asignado_a", nullable = false)
  private User assignedTo;

  @OneToMany(mappedBy = "production")
  private List<ProductionDetail> productionDetails;

  public static enum ProductionStatus {
    PENDIENTE, EN_PROCESO, COMPLETO, INCOMPLETO
  }
}
