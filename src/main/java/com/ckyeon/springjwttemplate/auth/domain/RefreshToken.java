package com.ckyeon.springjwttemplate.auth.domain;

import com.ckyeon.springjwttemplate.user.domain.User;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.UUID;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

@Entity
public class RefreshToken {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @OneToOne
  @JoinColumn(name = "user_id", unique = true, nullable = false, updatable = false)
  private User user;

  @Column(name = "refresh_token", unique = true, nullable = false, updatable = false)
  private String value;

  @Column(nullable = false, updatable = false)
  private LocalDateTime expiresAt;

  protected RefreshToken() {
  }

  public RefreshToken(User user, LocalDateTime expiresAt) {
    this(user, UUID.randomUUID().toString(), expiresAt);
  }

  private RefreshToken(User user, String value, LocalDateTime expiresAt) {
    this.user = user;
    this.value = value;
    this.expiresAt = expiresAt;
  }

  public Long getId() {
    return id;
  }

  public User getUser() {
    return user;
  }

  public String value() {
    return value;
  }

  public LocalDateTime getExpiresAt() {
    return expiresAt;
  }

  @Override
  public String toString() {
    return new ToStringBuilder(this, ToStringStyle.JSON_STYLE)
      .append("id", id)
      .append("user", user)
      .append("value", value)
      .append("expiresAt", expiresAt)
      .toString();
  }

  public boolean isExpired() {
    LocalDateTime now = LocalDateTime.now();
    return now.isAfter(expiresAt);
  }

  public Instant getExpiredAt() {
    ZoneId zone = ZoneId.systemDefault();
    return expiresAt.atZone(zone).toInstant();
  }
}
