package me.lachlanap.evelike;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

import lombok.Data;

/**
 * Created by lachlan on 11/02/2017.
 */
@Data
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

  public static class Builder<T> {
    private final Function<Code, T> build;

    private final List<Statement> statements = new ArrayList<>();

    public Builder(Function<Code, T> build) {
      this.build = build;
    }

    public Builder<T> bind(Variable variable, Database database) {
      statements.add(Statement.Binding.ofRecordSet(variable, database));
      return this;
    }

    public Builder<T> bind(Variable variable, Query query) {
      statements.add(Statement.Binding.ofRecordSet(variable, query));
      return this;
    }

    public Builder<T> bind(Variable variable, FunctionCall call) {
      statements.add(Statement.Binding.ofValue(variable, call));
      return this;
    }

    public Builder<T> print(Function<Context, String> transformer) {
      statements.add(new Statement.Print(transformer));
      return this;
    }

    public Builder<Builder<T>> forAll(Variable set, Variable pointer) {
      return new Builder<>(code -> {
        statements.add(new Statement.ForAll(set, pointer, code));
        return this;
      });
    }

    public T build() {
      return build.apply(new Code(statements));
    }
  }
}
