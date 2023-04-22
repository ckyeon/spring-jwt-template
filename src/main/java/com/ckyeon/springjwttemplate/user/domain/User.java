package com.ckyeon.springjwttemplate.user.domain;

import com.google.common.base.Preconditions;
import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

@Entity
public class User {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Embedded
  private Email email;

  private String password;

  @Enumerated(EnumType.STRING)
  @Column(length = 15, nullable = false)
  private Role role;

  protected User() {
  }

  public User(Email email, String password) {
    this(email, password, Role.ROLE_USER);
  }

  private User(Email email, String password, Role role) {
    Preconditions.checkArgument(email != null, "email must be provided.");
    Preconditions.checkArgument(password != null, "password must be provided.");
    Preconditions.checkArgument(role != null, "role must be provided.");

    this.email = email;
    this.password = password;
    this.role = role;
  }

  public com.ckyeon.springjwttemplate.common.model.Id<User, Long> getId() {
    return com.ckyeon.springjwttemplate.common.model.Id.of(User.class, id);
  }

  public Email getEmail() {
    return email;
  }

  public String getPassword() {
    return password;
  }

  public Role getRole() {
    return role;
  }

  @Override
  public String toString() {
    return new ToStringBuilder(this, ToStringStyle.JSON_STYLE)
      .append("id", id)
      .append("email", email)
      .append("password", password)
      .append("role", role)
      .toString();
  }
}
