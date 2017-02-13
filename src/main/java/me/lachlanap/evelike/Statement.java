package me.lachlanap.evelike;

import java.util.function.Function;

import lombok.Getter;

/**
 * Created by lachlan on 13/02/2017.
 */
public abstract class Statement {
  @Override
  public abstract String toString();

  @Getter
  public static class Binding extends Statement {
    public static Binding ofRecordSet(Variable variable, Database database) {
      return new Binding(variable, database);
    }

    public static Binding ofRecordSet(Variable variable, Query query) {
      return new Binding(variable, query);
    }

    public static Binding ofValue(Variable variable, FunctionCall call) {
      return new Binding(variable, call);
    }

    private final Variable variable;
    private final Object target;

    private Binding(Variable variable, Object target) {
      this.variable = variable;
      this.target = target;
    }

    @Override
    public String toString() {
      return variable + " = " + target;
    }
  }

  @Getter
  public static class Print extends Statement {
    private final Function<Context, String> transformer;

    public Print(Function<Context, String> transformer) {
      this.transformer = transformer;
    }

    @Override
    public String toString() {
      return "print ?";
    }
  }

  @Getter
  public static class ForAll extends Statement {
    private final Variable set;
    private final Variable pointer;
    private final Code code;

    public ForAll(Variable set, Variable pointer, Code code) {
      this.set = set;
      this.pointer = pointer;
      this.code = code;
    }

    @Override
    public String toString() {
      return "forall " + set + " " + pointer + "\n" + StringUtils.indent(code.toString().trim());
    }
  }
}
