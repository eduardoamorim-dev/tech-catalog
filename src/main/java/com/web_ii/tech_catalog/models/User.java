package com.web_ii.tech_catalog.models;

import java.util.List;
import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Table;
import lombok.Data;

@Data
@Entity
@Table(name = "users")
public class User {

  @Id
  @GeneratedValue
  @Column(name = "user_id")

  private Integer id;
  @Column(name = "user_name")

  private String name;
  @Column(name = "user_passwd")

  private String password;
  @Column(name = "user_email")

  private String email;
  @ElementCollection(fetch = FetchType.EAGER)
  @CollectionTable(name = "roles", joinColumns = @JoinColumn(name = "user_id"))
  @Column(name = "user_role")
  private List<String> roles;
  
  public void setUsername(String username) {
      this.name = username;
  }

  public String getUsername() {
    return name;
  }
}
