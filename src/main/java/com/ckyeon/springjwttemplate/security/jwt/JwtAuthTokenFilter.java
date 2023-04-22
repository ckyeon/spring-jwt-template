package com.ckyeon.springjwttemplate.security.jwt;

import com.ckyeon.springjwttemplate.common.model.Id;
import com.ckyeon.springjwttemplate.user.domain.Role;
import com.ckyeon.springjwttemplate.user.domain.User;
import jakarta.servlet.FilterChain;
import jakarta.servlet.GenericFilter;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.regex.Pattern;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;

@Component
public class JwtAuthTokenFilter extends GenericFilter {

  private static final Pattern BEARER = Pattern.compile("^Bearer$", Pattern.CASE_INSENSITIVE);

  private final JwtProperties properties;

  private final Jwt jwt;

  public JwtAuthTokenFilter(JwtProperties properties, Jwt jwt) {
    this.properties = properties;
    this.jwt = jwt;
  }

  @Override
  public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain)
    throws IOException, ServletException {
    HttpServletRequest request = (HttpServletRequest) req;
    HttpServletResponse response = (HttpServletResponse) res;

    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    if (authentication == null) {
      String accessToken = obtainAccessToken(request);

      if (accessToken != null) {
        Jwt.Claims claims = jwt.verify(accessToken);

        Id<User, Long> id = claims.id;
        Role role = claims.role;

        if (id != null && role != null) {
          JwtAuth jwtAuth = new JwtAuth(id, role);
          var authorities = AuthorityUtils.createAuthorityList(role.name());

          JwtAuthToken jwtAuthToken = new JwtAuthToken(jwtAuth, null, authorities);
          jwtAuthToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

          SecurityContextHolder.getContext().setAuthentication(jwtAuthToken);
        }
      }
    }

    chain.doFilter(request, response);
  }

  private String obtainAccessToken(HttpServletRequest request) {
    String accessToken = request.getHeader(properties.getHeaderKey());
    if (accessToken == null) {
      return null;
    }

    String decodedAccessToken = URLDecoder.decode(accessToken, StandardCharsets.UTF_8);
    String[] decodedAccessTokenParts = decodedAccessToken.split(" ");
    if (decodedAccessTokenParts.length != 2) {
      return null;
    }

    String scheme = decodedAccessTokenParts[0];
    String obtainedAccessToken = decodedAccessTokenParts[1];
    return BEARER.matcher(scheme).matches() ? obtainedAccessToken : null;
  }
}
