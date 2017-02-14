package me.lachlanap.evelike;

import java.util.ArrayList;
import java.util.List;

import lombok.Getter;

/**
 * Created by lachlan on 11/02/2017.
 */
@Getter
public class Query extends Expression {
  public static Builder builder(Database set) {
    return builder(new DatabaseReference(set));
  }

  public static Builder builder(Expression set) {
    return new Builder(set);
  }

  private final Expression set;
  private final List<ComparisonExpression> expressions;

  public Query(Expression set, List<ComparisonExpression> expressions) {
    this.set = set;
    this.expressions = expressions;
  }

  @Override
  public String toString() {
    StringBuilder builder = new StringBuilder().append(set).append(" ?{");
    boolean first = true;
    for (Object expression : expressions) {
      if (!first) builder.append(" ");
      first = false;
      builder.append(expression);
    }
    return builder.append('}').toString();
  }


  public static class ComparisonExpression {
    private final String field, operator;
    private final int value;

    public ComparisonExpression(String field, String operator, int value) {
      this.field = field;
      this.operator = operator;
      this.value = value;
    }

    @Override
    public String toString() {
      return "#" + field + " " + operator + " " + value;
    }
  }

  public static class Builder {
    private final Expression expression;
    private final List<ComparisonExpression> expressions = new ArrayList<>();

    public Builder(Expression expression) {
      this.expression = expression;
    }

    public Builder greaterThan(String field, int value) {
      expressions.add(new ComparisonExpression(field, ">", value));
      return this;
    }

    public Query build() {
      return new Query(expression, expressions);
    }
  }
}
