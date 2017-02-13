package me.lachlanap.evelike;

import lombok.Data;

/**
 * Represents the identity of a local variable.
 */
@Data
public class Variable {
  private final String name;
  private final Type type;

  public Variable(String name, Type type) {
    this.name = name;
    this.type = type;
  }

  @Override
  public String toString() {
    return name;
  }
}
