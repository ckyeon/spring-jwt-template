package com.ckyeon.springjwttemplate.config;

import com.ckyeon.springjwttemplate.security.jwt.JwtAuthTokenFilter;
import java.util.List;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

  private final JwtAuthTokenFilter jwtAuthTokenFilter;

  public SecurityConfig(JwtAuthTokenFilter jwtAuthTokenFilter) {
    this.jwtAuthTokenFilter = jwtAuthTokenFilter;
  }

  @Bean
  public PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder();
  }

  @Bean
  public AuthenticationManager authenticationManager(List<AuthenticationProvider> providers) {
    return new ProviderManager(providers);
  }

  @Bean
  public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
    http.csrf()
      .disable();

    http.headers()
      .disable();

    http.sessionManagement()
      .sessionCreationPolicy(SessionCreationPolicy.STATELESS);

    http.authorizeHttpRequests()
      .requestMatchers("/join").permitAll()
      .requestMatchers("/login").permitAll()
      .requestMatchers("/{refreshToken}/reissue").permitAll()
      .anyRequest().permitAll();

    http.formLogin()
      .disable();

    http
      .addFilterBefore(jwtAuthTokenFilter, UsernamePasswordAuthenticationFilter.class);

    return http.build();
  }
}
