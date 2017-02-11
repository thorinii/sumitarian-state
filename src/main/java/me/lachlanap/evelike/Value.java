package me.lachlanap.evelike;

/**
 * Created by lachlan on 11/02/2017.
 */
public class Value {
  public enum Type {
    STRING, INTEGER
  }

  public static Value of(String value) {
    return new Value(Type.STRING, value, 0);
  }

  public static Value of(int value) {
    return new Value(Type.INTEGER, null, value);
  }

  private final Type type;
  private final String stringValue;
  private final int intValue;

  private Value(Type type, String stringValue, int intValue) {
    this.type = type;
    this.stringValue = stringValue;
    this.intValue = intValue;
  }

  @Override
  public String toString() {
    switch (type) {
      case STRING:
        return '"' + stringValue + '"';
      case INTEGER:
        return Integer.toString(intValue);
      default:
        return "?";
    }
  }
}
