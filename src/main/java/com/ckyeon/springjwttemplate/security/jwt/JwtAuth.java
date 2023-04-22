package com.ckyeon.springjwttemplate.security.jwt;

import com.ckyeon.springjwttemplate.common.model.Id;
import com.ckyeon.springjwttemplate.user.domain.Role;
import com.ckyeon.springjwttemplate.user.domain.User;
import com.google.common.base.Preconditions;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

public class JwtAuth {

  private final Id<User, Long> id;

  private final Role role;

  public JwtAuth(Id<User, Long> id, Role role) {
    Preconditions.checkArgument(id != null, "id must be provided.");
    Preconditions.checkArgument(role != null, "role must be provided.");

    this.id = id;
    this.role = role;
  }

  @Override
  public String toString() {
    return new ToStringBuilder(this, ToStringStyle.JSON_STYLE)
      .append("id", id)
      .append("role", role)
      .toString();
  }
}
