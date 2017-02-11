package me.lachlanap.evelike;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by lachlan on 11/02/2017.
 */
public class Query {
  public static Builder builder(Database database) {
    return new Builder(database);
  }

  private final Database database;
  private final List<ComparisonExpression> expressions;

  public Query(Database database, List<ComparisonExpression> expressions) {
    this.database = database;
    this.expressions = expressions;
  }

  @Override
  public String toString() {
    StringBuilder builder = new StringBuilder().append(database).append(" ?{");
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
    private final Database database;
    private final List<ComparisonExpression> expressions = new ArrayList<>();

    public Builder(Database database) {
      this.database = database;
    }

    public Builder greaterThan(String field, int value) {
      expressions.add(new ComparisonExpression(field, ">", value));
      return this;
    }

    public Query build() {
      return new Query(database, expressions);
    }
  }
}
