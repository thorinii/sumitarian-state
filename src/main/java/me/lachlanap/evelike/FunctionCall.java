package me.lachlanap.evelike;

import java.util.Arrays;
import java.util.List;

/**
 * Created by lachlan on 11/02/2017.
 */
public class FunctionCall {
  public static FunctionCall of(String function, Object... arguments) {
    return new FunctionCall(function, Arrays.asList(arguments));
  }

  private final String function;
  private final List<Object> arguments;

  public FunctionCall(String function, List<Object> arguments) {
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
