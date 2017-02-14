package me.lachlanap.evelike;

/**
 * Created by lachlan on 14/02/2017.
 */
public abstract class Expression {

  public static class DatabaseReference extends Expression {
    private final Database database;

    public DatabaseReference(Database database) {
      this.database = database;
    }

    public Database getDatabase() {
      return database;
    }

    @Override
    public String toString() {
      return database.toString();
    }
  }
}
