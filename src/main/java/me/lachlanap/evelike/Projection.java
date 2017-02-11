package me.lachlanap.evelike;

import java.util.Arrays;
import java.util.List;

/**
 * Created by lachlan on 11/02/2017.
 */
public class Projection {
  public static Projection of(Database database, String... fields) {
    return new Projection(database, Arrays.asList(fields));
  }

  private final Database database;
  private final List<String> fields;

  public Projection(Database database, List<String> fields) {
    this.database = database;
    this.fields = fields;
  }

  @Override
  public String toString() {
    StringBuilder builder = new StringBuilder().append(database).append(" &{");
    boolean first = true;
    for (Object field : fields) {
      if (!first) builder.append(", ");
      first = false;
      builder.append('#').append(field);
    }
    return builder.append('}').toString();
  }
}
