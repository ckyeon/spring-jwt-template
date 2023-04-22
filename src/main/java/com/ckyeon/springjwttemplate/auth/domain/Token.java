package com.ckyeon.springjwttemplate.auth.domain;

import com.google.common.base.Objects;
import com.google.common.base.Preconditions;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

public class Token<R> {

  private final Class<R> reference;

  private final String value;

  private Token(Class<R> reference, String value) {
    this.reference = reference;
    this.value = value;
  }

  public static <R> Token<R> of(Class<R> reference, String value) {
    Preconditions.checkArgument(reference != null, "reference must be provided.");
    Preconditions.checkArgument(value != null, "value must be provided.");

    return new Token<>(reference, value);
  }

  public String value() {
    return value;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    Token<?> token = (Token<?>) o;
    return Objects.equal(reference, token.reference)
      && Objects.equal(value, token.value);
  }

  @Override
  public int hashCode() {
    return Objects.hashCode(reference, value);
  }

  @Override
  public String toString() {
    return new ToStringBuilder(this, ToStringStyle.JSON_STYLE)
      .append("reference", reference)
      .append("value", value)
      .toString();
  }
}
