package com.ckyeon.springjwttemplate.security.jwt;

import com.ckyeon.springjwttemplate.auth.application.AuthService;
import com.ckyeon.springjwttemplate.auth.domain.AccessToken;
import com.ckyeon.springjwttemplate.auth.domain.RefreshToken;
import com.ckyeon.springjwttemplate.auth.domain.Token;
import com.ckyeon.springjwttemplate.auth.presentation.dto.AuthTokens;
import com.ckyeon.springjwttemplate.auth.presentation.dto.LoginRequest;
import com.ckyeon.springjwttemplate.common.exception.NotFoundException;
import com.ckyeon.springjwttemplate.common.model.Id;
import com.ckyeon.springjwttemplate.user.domain.Role;
import com.ckyeon.springjwttemplate.user.domain.User;
import java.util.List;
import org.apache.commons.lang3.ClassUtils;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.stereotype.Component;

@Component
public class JwtAuthProvider implements AuthenticationProvider {

  private final AuthService authService;

  public JwtAuthProvider(AuthService authService) {
    this.authService = authService;
  }

  @Override
  public Authentication authenticate(Authentication authentication) throws AuthenticationException {
    JwtAuthToken jwtAuthToken = (JwtAuthToken) authentication;
    return processUserAuth(jwtAuthToken.loginRequest());
  }

  @Override
  public boolean supports(Class<?> authentication) {
    return ClassUtils.isAssignable(JwtAuthToken.class, authentication);
  }

  private Authentication processUserAuth(LoginRequest loginRequest) {
    try {
      User user = authService.login(loginRequest);

      Id<User, Long> id = user.getId();
      Role role = user.getRole();
      JwtAuth jwtAuth = new JwtAuth(id, role);

      String roleName = role.name();
      List<GrantedAuthority> authorityList = AuthorityUtils.createAuthorityList(roleName);

      JwtAuthToken jwtAuthToken = new JwtAuthToken(jwtAuth, null, authorityList);

      Token<AccessToken> accessToken = authService.accessToken(id, role);
      Token<RefreshToken> refreshToken = authService.refreshToken(user);
      jwtAuthToken.setDetails(new AuthTokens(accessToken, refreshToken));

      return jwtAuthToken;
    } catch (NotFoundException | IllegalArgumentException e) {
      throw new BadCredentialsException("아이디 또는 비밀번호가 틀립니다.");
    }
  }
}
