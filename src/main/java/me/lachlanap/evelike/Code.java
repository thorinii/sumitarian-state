package me.lachlanap.evelike;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

/**
 * Created by lachlan on 11/02/2017.
 */
public class Code {
  private final List<Statement> statements;

  public Code(List<Statement> statements) {
    this.statements = statements;
  }

  @Override
  public String toString() {
    StringBuilder builder = new StringBuilder();
    for (Statement statement : statements) {
      builder.append(statement).append('\n');
    }
    return builder.toString();
  }

  public static abstract class Statement {
    @Override
    public abstract String toString();
  }

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

  public static class Print extends Statement {
    @Override
    public String toString() {
      return "print ?";
    }
  }

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


  public static class Builder<T> {
    private final Function<Code, T> build;

    private final List<Statement> statements = new ArrayList<>();

    public Builder(Function<Code, T> build) {
      this.build = build;
    }

    public Builder<T> bind(Variable variable, Database database) {
      statements.add(Binding.ofRecordSet(variable, database));
      return this;
    }

    public Builder<T> bind(Variable variable, Query query) {
      statements.add(Binding.ofRecordSet(variable, query));
      return this;
    }

    public Builder<T> bind(Variable variable, FunctionCall call) {
      statements.add(Binding.ofValue(variable, call));
      return this;
    }

    public Builder<T> print(Function<Context, String> transformer) {
      statements.add(new Print());
      return this;
    }

    public Builder<Builder<T>> forAll(Variable set, Variable pointer) {
      return new Builder<>(code -> {
        statements.add(new ForAll(set, pointer, code));
        return this;
      });
    }

    public T build() {
      return build.apply(new Code(statements));
    }
  }
}
