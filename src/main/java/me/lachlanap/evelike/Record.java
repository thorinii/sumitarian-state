package me.lachlanap.evelike;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by lachlan on 11/02/2017.
 */
public class Record {
  public static Builder builder() {
    return new Builder();
  }

  private final Map<String, Value> values;

  public Record(Map<String, Value> values) {
    this.values = values;
  }

  @Override
  public boolean equals(Object obj) {
    return obj instanceof Record && values.equals(((Record) obj).values);
  }

  @Override
  public int hashCode() {
    return values.hashCode();
  }

  @Override
  public String toString() {
    StringBuilder builder = new StringBuilder("{");
    boolean first = true;
    for (Map.Entry<String, Value> entry : values.entrySet()) {
      if (!first) builder.append(" ");
      first = false;
      builder.append('#').append(entry.getKey()).append(':').append(entry.getValue());
    }
    builder.append('}');
    return builder.toString();
  }

  public static class Builder {
    private final Map<String, Value> values = new HashMap<>();

    public Builder add(String key, String value) {
      values.put(key, Value.of(value));
      return this;
    }

    public Builder add(String key, int value) {
      values.put(key, Value.of(value));
      return this;
    }

    public Record build() {
      return new Record(values);
    }
  }
}
