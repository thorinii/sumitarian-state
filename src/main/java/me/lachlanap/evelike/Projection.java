package me.lachlanap.evelike;

import java.util.Arrays;
import java.util.List;

import lombok.Getter;

/**
 * Created by lachlan on 11/02/2017.
 */
@Getter
public class Projection extends Expression {
  public static Projection of(Expression set, String... fields) {
    return new Projection(set, Arrays.asList(fields));
  }

  private final Expression expression;
  private final List<String> fields;

  public Projection(Expression expression, List<String> fields) {
    this.expression = expression;
    this.fields = fields;
  }

  @Override
  public String toString() {
    StringBuilder builder = new StringBuilder().append(expression).append(" &{");
    boolean first = true;
    for (Object field : fields) {
      if (!first) builder.append(", ");
      first = false;
      builder.append('#').append(field);
    }
    return builder.append('}').toString();
  }
}
