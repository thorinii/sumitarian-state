package me.lachlanap.evelike;

import java.util.Arrays;
import java.util.List;

import lombok.Getter;

/**
 * Created by lachlan on 11/02/2017.
 */
@Getter
public class FunctionCall extends Expression {
  public static FunctionCall of(String function, Expression... arguments) {
    return new FunctionCall(function, Arrays.asList(arguments));
  }

  private final String function;
  private final List<Expression> arguments;

  public FunctionCall(String function, List<Expression> arguments) {
    this.function = function;
    this.arguments = arguments;
  }

  @Override
  public String toString() {
    StringBuilder builder = new StringBuilder().append(function).append('(');
    boolean first = true;
    for (Object argument : arguments) {
      if (!first) builder.append(", ");
      first = false;
      builder.append(argument);
    }
    return builder.append(')').toString();
  }
}
