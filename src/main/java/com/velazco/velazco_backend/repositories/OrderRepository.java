package com.velazco.velazco_backend.repositories;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.velazco.velazco_backend.entities.Order;

public interface OrderRepository extends JpaRepository<Order, Long> {

  @EntityGraph(attributePaths = { "details", "details.product", "attendedBy" })
  Page<Order> findByStatus(Order.OrderStatus status, Pageable pageable);

  List<Order> findByStatusAndDateBefore(Order.OrderStatus status, LocalDateTime date);

  @Query("""
      SELECT o FROM Order o
      WHERE o.status = :status
      AND (:orderId IS NULL OR o.id = :orderId)
      AND (:clientName IS NULL OR LOWER(o.clientName) LIKE LOWER(CONCAT('%', :clientName, '%')))
      """)
  Page<Order> findByStatusAndOptionalFilters(
      @Param("status") Order.OrderStatus status,
      @Param("orderId") Long orderId,
      @Param("clientName") String clientName,
      Pageable pageable);

  @Query("""
        SELECT
          DATE(o.date) AS saleDate,
          p.name AS productName,
          d.quantity AS quantity,
          d.unitPrice AS unitPrice,
          (d.quantity * d.unitPrice) AS subtotal
        FROM Order o
        JOIN o.details d
        JOIN d.product p
        WHERE o.status = com.velazco.velazco_backend.entities.Order.OrderStatus.ENTREGADO
      """)
  List<Object[]> findDetailedDeliveredSales();

}
