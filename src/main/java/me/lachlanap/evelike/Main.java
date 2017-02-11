package me.lachlanap.evelike;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

public class Main {
  public static void main(String[] args) {
    Container container = new Container();

    Database people = container.database("people");

    people.commit(Record.builder()
        .add("id", "a7ff")
        .add("first-name", "Toby")
        .add("last-name", "Jenla")
        .add("age", 24)
        .build());
    people.commit(Record.builder()
        .add("id", "8db1")
        .add("first-name", "Lucy")
        .add("last-name", "Treble")
        .add("age", 35)
        .build());
    people.commit(Record.builder()
        .add("id", "319c")
        .add("first-name", "Jacob")
        .add("last-name", "Neffelad")
        .add("age", 63)
        .build());


    Variable allPeople = new Variable("all-oeople", Type.RECORD_SET);
    Variable person = new Variable("person", Type.RECORD);
    container.addBlock(Block.builder()
        .bind(allPeople, people)
        .forAll(allPeople, person)
        .print(context -> context.getRecord(person).toString())
        .end()
        .build());

    Variable numberOfPeople = new Variable("number-of-people", Type.INTEGER);
    container.addBlock(Block.builder()
        .bind(numberOfPeople, FunctionCall.of("count", people))
        .print(context -> "Number of people: " + context.getValue(numberOfPeople).toHumanString())
        .build());

    Variable numberOfSurnames = new Variable("number-of-people", Type.INTEGER);
    container.addBlock(Block.builder()
        .bind(numberOfSurnames, FunctionCall.of("count", Projection.of(people, "last-name")))
        .print(context -> "Number of surnames: " + context.getValue(numberOfSurnames).toHumanString())
        .build());

    Variable maxAge = new Variable("max-age", Type.INTEGER);
    container.addBlock(Block.builder()
        .bind(maxAge, FunctionCall.of("max", people, "last-name"))
        .print(context -> "Max age: " + context.getValue(maxAge).toHumanString())
        .build());

    Variable peopleOlderThanTwenty = new Variable("people-older-than-twenty", Type.RECORD_SET);
    container.addBlock(Block.builder()
        .bind(peopleOlderThanTwenty, Query.builder(people).greaterThan("age", 20).build())
        .forAll(peopleOlderThanTwenty, person)
        .print(context -> "Max age: " + context.getRecord(person).toString())
        .end()
        .build());


    System.out.println("Container Dump:\n" + container);

    /*
      count all names
        number-of-people = count(people)
        print "Number of people: {number-of-people}"

      count distinct surnames
        number-of-surnames = count(people@{#last-name})
        print "Number of surnames: {number-of-surnames}"

      report max age
        # max : record+, field -> number
        max-age = max(people, #age)

      find people older than 20
        people-older-than-twenty = people?{#age > 20}
        forall people-older-than-twenty person
          print person
     */
  }

  public static class Block {
    public static Builder builder() {
      return new Builder();
    }

    public Block() {
    }

    @Override
    public String toString() {
      return "???";
    }

    public static class Builder {
      public Builder bind(Variable variable, Database database) {
        return this;
      }

      public Builder bind(Variable variable, Query query) {
        return this;
      }

      public Builder bind(Variable variable, FunctionCall call) {
        return this;
      }

      public Builder print(Function<Context, String> transformer) {
        return this;
      }

      public ForAllBuilder<Builder> forAll(Variable set, Variable pointer) {
        return new ForAllBuilder<>(this);
      }

      public Block build() {
        return new Block();
      }
    }

    public static class ForAllBuilder<T> {
      private final T parent;

      public ForAllBuilder(T parent) {
        this.parent = parent;
      }

      public ForAllBuilder<T> print(Function<Context, String> transformer) {
        return this;
      }

      public T end() {
        return parent;
      }
    }

    public static class Context {
      public Value getValue(Variable variable) {
        return null;
      }

      public Record getRecord(Variable variable) {
        return null;
      }
    }
  }

  public static class Container {
    private final List<Database> databases;
    private final List<Block> blocks;
    private final Map<String, Database> databaseByName;

    public Container() {
      this.databases = new ArrayList<>();
      this.blocks = new ArrayList<>();
      this.databaseByName = new HashMap<>();
    }

    public Database database(String name) {
      Database database = databaseByName.get(name);
      if (database == null) {
        database = new Database(name);
        databases.add(database);
        databaseByName.put(name, database);
      }
      return database;
    }

    public void addBlock(Block block) {
      blocks.add(block);
    }

    @Override
    public String toString() {
      StringBuilder builder = new StringBuilder();

      builder.append("Databases:\n");
      for (Database database : databases) {
        builder.append("  ").append(database.getName()).append(":\n");
        for (Record record : database.queryForAll()) {
          builder.append("    ").append(record).append('\n');
        }
      }

      builder.append("Blocks:\n");
      for (Block block : blocks) {
        builder.append(StringUtils.indent(block.toString())).append("\n");
      }

      return builder.toString();
    }
  }

  /**
   * Represents the identity of a local variable.
   */
  public static class Variable {
    public Variable(String name, Type type) {
    }
  }

  public static class Query {
    public static Builder builder(Database database) {
      return new Builder();
    }

    public static class Builder {
      public Builder greaterThan(String field, int value) {
        return this;
      }

      public Query build() {
        return new Query();
      }
    }
  }

  public static class Type {
    public static Type INTEGER = new Type();
    public static Type RECORD = new Type();
    public static Type RECORD_SET = new Type();

    public static Type function() {
      return new Type();
    }
  }

  public static class FunctionCall {
    public static FunctionCall of(String function, Object... arguments) {
      return new FunctionCall();
    }
  }

  public static class Projection {
    public static Projection of(Database database, String... fields) {
      return new Projection();
    }
  }
}
