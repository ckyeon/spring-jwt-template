package com.ckyeon.springjwttemplate.security.jwt;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.Claim;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.ckyeon.springjwttemplate.auth.domain.RefreshToken;
import com.ckyeon.springjwttemplate.common.model.Id;
import com.ckyeon.springjwttemplate.user.domain.Role;
import com.ckyeon.springjwttemplate.user.domain.User;
import com.google.common.base.Preconditions;
import java.time.LocalDateTime;
import java.util.Date;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.springframework.stereotype.Component;

@Component
public final class Jwt {

  private final JwtProperties properties;

  private final Algorithm algorithm;

  private final JWTVerifier verifier;

  public Jwt(JwtProperties properties) {
    this.properties = properties;

    String clientSecret = properties.getClientSecret();
    this.algorithm = Algorithm.HMAC512(clientSecret);

    String issuer = properties.getIssuer();
    this.verifier = JWT.require(algorithm)
      .withIssuer(issuer)
      .build();
  }

  public Claims verify(String accessToken) throws JWTVerificationException {
    DecodedJWT decodedJWT = verifier.verify(accessToken);
    return new Claims(decodedJWT);
  }

  public String accessToken(Claims claims) {
    String issuer = properties.getIssuer();
    int expirySeconds = properties.getAccessTokenExpirySeconds();
    Date issuedAt = new Date();
    Date expiresAt = new Date(issuedAt.getTime() + (expirySeconds * 1_000L));
    return JWT.create()
      .withIssuer(issuer)
      .withIssuedAt(issuedAt)
      .withExpiresAt(expiresAt)
      .withClaim("id", claims.id.value())
      .withClaim("role", claims.role.name())
      .sign(algorithm);
  }

  public RefreshToken refreshToken(User user) {
    long expiryDays = properties.getRefreshTokenExpiryDays();
    LocalDateTime expiresAt = LocalDateTime.now().plusMinutes(expiryDays);
    return new RefreshToken(user, expiresAt);
  }

  public static class Claims {

    Id<User, Long> id;
    Role role;
    Date iat;
    Date exp;

    Claims(DecodedJWT decodedJWT) {
      Preconditions.checkArgument(decodedJWT != null, "decoded jwt must be provided.");

      Claim id = decodedJWT.getClaim("id");
      Claim role = decodedJWT.getClaim("role");

      this.id = Id.of(User.class, id.asLong());
      this.role = role.as(Role.class);
      this.iat = decodedJWT.getIssuedAt();
      this.exp = decodedJWT.getExpiresAt();
    }

    private Claims(Id<User, Long> id, Role role, Date iat, Date exp) {
      Preconditions.checkArgument(id != null, "id must be provided.");
      Preconditions.checkArgument(role != null, "role must be provided.");

      this.id = id;
      this.role = role;
      this.iat = iat;
      this.exp = exp;
    }

    public static Claims of(Id<User, Long> id, Role role) {
      return new Claims(id, role, null, null);
    }

    @Override
    public String toString() {
      return new ToStringBuilder(this, ToStringStyle.JSON_STYLE)
        .append("id", id)
        .append("role", role)
        .append("iat", iat)
        .append("exp", exp)
        .toString();
    }
  }
}
