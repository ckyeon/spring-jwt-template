package com.ckyeon.springjwttemplate.common.model;

import com.google.common.base.Objects;
import com.google.common.base.Preconditions;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

public class Id<R, V> {

  private final Class<R> reference;
  private final V value;

  private Id(Class<R> reference, V value) {
    this.reference = reference;
    this.value = value;
  }

  public static <R, V> Id<R, V> of(Class<R> reference, V value) {
    Preconditions.checkArgument(reference != null, "reference must be provided.");
    Preconditions.checkArgument(value != null, "value must be provided.");

    return new Id<>(reference, value);
  }

  public V value() {
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
    Id<?, ?> id = (Id<?, ?>) o;
    return Objects.equal(reference, id.reference)
      && Objects.equal(value, id.value);
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
