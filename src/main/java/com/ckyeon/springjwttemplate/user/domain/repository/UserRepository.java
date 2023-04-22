package com.ckyeon.springjwttemplate.user.domain.repository;

import com.ckyeon.springjwttemplate.user.domain.Email;
import com.ckyeon.springjwttemplate.user.domain.User;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {

  Optional<User> findByEmail(Email email);
}
