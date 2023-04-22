package com.ckyeon.springjwttemplate.auth.presentation;

import com.ckyeon.springjwttemplate.auth.application.AuthService;
import com.ckyeon.springjwttemplate.auth.domain.AccessToken;
import com.ckyeon.springjwttemplate.auth.domain.RefreshToken;
import com.ckyeon.springjwttemplate.auth.domain.Token;
import com.ckyeon.springjwttemplate.auth.presentation.dto.AuthTokens;
import com.ckyeon.springjwttemplate.auth.presentation.dto.JoinRequest;
import com.ckyeon.springjwttemplate.auth.presentation.dto.LoginRequest;
import com.ckyeon.springjwttemplate.security.jwt.JwtAuthToken;
import java.net.URI;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class AuthController {

  private final AuthService authService;

  private final AuthenticationManager authenticationManager;

  public AuthController(AuthService authService, AuthenticationManager authenticationManager) {
    this.authService = authService;
    this.authenticationManager = authenticationManager;
  }

  @PostMapping("/join")
  public ResponseEntity<Void> join(@RequestBody JoinRequest dto) {
    URI uri = authService.join(dto);
    return ResponseEntity.created(uri).build();
  }

  @PostMapping("/login")
  public ResponseEntity<AuthTokens> login(@RequestBody LoginRequest dto) {
    JwtAuthToken jwtAuthToken = new JwtAuthToken(dto);
    Authentication authentication = authenticationManager.authenticate(jwtAuthToken);
    SecurityContextHolder.getContext().setAuthentication(authentication);

    AuthTokens authTokens = (AuthTokens) authentication.getDetails();
    return ResponseEntity.ok(authTokens);
  }

  @GetMapping("/{refreshToken}/reissue")
  public ResponseEntity<AuthTokens> reissue(@PathVariable("refreshToken") String token) {
    Token<RefreshToken> refreshToken = Token.of(RefreshToken.class, token);
    Token<AccessToken> accessToken = authService.reissue(refreshToken);

    AuthTokens authTokens = new AuthTokens(accessToken, refreshToken);
    return ResponseEntity.ok(authTokens);
  }
}
