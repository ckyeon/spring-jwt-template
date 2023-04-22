package com.ckyeon.springjwttemplate.auth.application;

import com.auth0.jwt.exceptions.TokenExpiredException;
import com.ckyeon.springjwttemplate.auth.domain.AccessToken;
import com.ckyeon.springjwttemplate.auth.domain.RefreshToken;
import com.ckyeon.springjwttemplate.auth.domain.Token;
import com.ckyeon.springjwttemplate.auth.domain.repository.RefreshTokenRepository;
import com.ckyeon.springjwttemplate.auth.presentation.dto.JoinRequest;
import com.ckyeon.springjwttemplate.auth.presentation.dto.LoginRequest;
import com.ckyeon.springjwttemplate.common.exception.NotFoundException;
import com.ckyeon.springjwttemplate.common.model.Id;
import com.ckyeon.springjwttemplate.security.jwt.Jwt;
import com.ckyeon.springjwttemplate.security.jwt.Jwt.Claims;
import com.ckyeon.springjwttemplate.user.domain.Email;
import com.ckyeon.springjwttemplate.user.domain.Role;
import com.ckyeon.springjwttemplate.user.domain.User;
import com.ckyeon.springjwttemplate.user.domain.repository.UserRepository;
import com.google.common.base.Preconditions;
import java.net.URI;
import java.time.Instant;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AuthService {

  private final PasswordEncoder passwordEncoder;

  private final Jwt jwt;

  private final UserRepository userRepository;

  private final RefreshTokenRepository refreshTokenRepository;

  public AuthService(PasswordEncoder passwordEncoder, Jwt jwt, UserRepository userRepository,
    RefreshTokenRepository refreshTokenRepository) {
    this.passwordEncoder = passwordEncoder;
    this.jwt = jwt;
    this.userRepository = userRepository;
    this.refreshTokenRepository = refreshTokenRepository;
  }

  @Transactional
  public URI join(JoinRequest dto) {
    Preconditions.checkArgument(dto != null, "dto must be provided.");

    Email email = dto.getEmail();
    String password = dto.getPassword();
    String encodedPassword = passwordEncoder.encode(password);

    User user = new User(email, encodedPassword);
    userRepository.save(user);

    return URI.create("/users/me");
  }

  @Transactional
  public User login(LoginRequest dto) {
    Preconditions.checkArgument(dto != null, "dto must be provided.");

    Email email = dto.getEmail();
    User user = userRepository.findByEmail(email)
      .orElseThrow(NotFoundException::new);

    String rawPassword = dto.getPassword();
    String encodedPassword = user.getPassword();
    if (!passwordEncoder.matches(rawPassword, encodedPassword)) {
      throw new IllegalArgumentException();
    }

    return user;
  }

  public Token<AccessToken> accessToken(Id<User, Long> id, Role role) {
    Claims claims = Claims.of(id, role);
    String accessToken = jwt.accessToken(claims);
    return Token.of(AccessToken.class, accessToken);
  }

  @Transactional
  public Token<AccessToken> reissue(Token<RefreshToken> token) {
    RefreshToken refreshToken = refreshTokenRepository.findByValue(token.value())
      .orElseThrow(() -> new NotFoundException("Not found Refresh Token."));

    if (refreshToken.isExpired()) {
      refreshTokenRepository.delete(refreshToken);

      Instant expiredAt = refreshToken.getExpiredAt();
      throw new TokenExpiredException("Refresh Token has expired.", expiredAt);
    }

    User user = refreshToken.getUser();

    Id<User, Long> id = user.getId();
    Role role = user.getRole();
    return accessToken(id, role);
  }

  @Transactional
  public Token<RefreshToken> refreshToken(User user) {
    RefreshToken refreshToken = refreshTokenRepository.findByUser(user)
      .orElseGet(() -> {
        RefreshToken token = jwt.refreshToken(user);
        return refreshTokenRepository.save(token);
      });
    return Token.of(RefreshToken.class, refreshToken.value());
  }
}
