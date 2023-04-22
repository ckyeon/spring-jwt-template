package com.ckyeon.springjwttemplate.auth.domain.repository;

import com.ckyeon.springjwttemplate.auth.domain.RefreshToken;
import com.ckyeon.springjwttemplate.user.domain.User;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {

  Optional<RefreshToken> findByUser(User user);

  Optional<RefreshToken> findByValue(String value);
}
