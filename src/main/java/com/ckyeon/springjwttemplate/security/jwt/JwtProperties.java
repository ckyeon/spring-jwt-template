package com.ckyeon.springjwttemplate.security.jwt;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.bind.ConstructorBinding;

@ConfigurationProperties(prefix = "jwt")
public class JwtProperties {

  private final String headerKey;

  private final String issuer;

  private final String clientSecret;

  private final int accessTokenExpirySeconds;

  private final long refreshTokenExpiryMinutes;

  @ConstructorBinding
  public JwtProperties(String headerKey, String issuer, String clientSecret,
    int accessTokenExpirySeconds, long refreshTokenExpiryMinutes) {
    this.headerKey = headerKey;
    this.issuer = issuer;
    this.clientSecret = clientSecret;
    this.accessTokenExpirySeconds = accessTokenExpirySeconds;
    this.refreshTokenExpiryMinutes = refreshTokenExpiryMinutes;
  }

  public String getHeaderKey() {
    return headerKey;
  }

  public String getIssuer() {
    return issuer;
  }

  public String getClientSecret() {
    return clientSecret;
  }

  public int getAccessTokenExpirySeconds() {
    return accessTokenExpirySeconds;
  }

  public long getRefreshTokenExpiryDays() {
    return refreshTokenExpiryMinutes;
  }
}

