package com.velazco.velazco_backend.entities;

import java.util.Collection;
import java.util.List;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

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
@Table(name = "usuarios")
@Data
@NoArgsConstructor
public class User implements UserDetails {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(name = "nombre", nullable = false, length = 100)
  private String name;

  @Column(name = "correo", nullable = false, unique = true, length = 255)
  private String email;

  @Column(name = "password", nullable = false, length = 255)
  private String password;

  @Column(name = "activo", nullable = false)
  private Boolean active = true;

  @ManyToOne
  @JoinColumn(name = "rol_id", nullable = false)
  private Role role;

  @OneToMany(mappedBy = "attendedBy")
  private List<Order> attendedOrders;

  @OneToMany(mappedBy = "cashier")
  private List<Sale> sales;

  @OneToMany(mappedBy = "dispatchedBy")
  private List<Dispatch> dispatches;

  @OneToMany(mappedBy = "assignedBy")
  private List<Production> assignedProductions;

  @OneToMany(mappedBy = "assignedTo")
  private List<Production> responsibleProductions;

  @Override
  public String getUsername() {
    return name;
  }

  @Override
  public Collection<? extends GrantedAuthority> getAuthorities() {
    return List.of(() -> "ROLE_" + role.getName());
  }
}
