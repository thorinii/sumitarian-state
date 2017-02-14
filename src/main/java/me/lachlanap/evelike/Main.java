package me.lachlanap.evelike;

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


    Variable allPeople = new Variable("all-people", Type.RECORD_SET);
    Variable person = new Variable("person", Type.RECORD);
    container.addBlock(Block.builder()
        .bind(allPeople, people)
        .forAll(allPeople, person)
        .print(context -> context.getRecord(person).toString())
        .build()
        .build());

    Variable numberOfPeople = new Variable("number-of-people", Type.INTEGER);
    container.addBlock(Block.builder()
        .bind(numberOfPeople, FunctionCall.of("count", new Expression.DatabaseReference(people)))
        .print(context -> "Number of people: " + context.getValue(numberOfPeople).toHumanString())
        .build());

    Variable numberOfSurnames = new Variable("number-of-people", Type.INTEGER);
    container.addBlock(Block.builder()
        .bind(numberOfSurnames, FunctionCall.of("count", Projection.of(new Expression.DatabaseReference(people), "last-name")))
        .print(context -> "Number of surnames: " + context.getValue(numberOfSurnames).toHumanString())
        .build());

    Variable maxAge = new Variable("max-age", Type.INTEGER);
    container.addBlock(Block.builder()
        .bind(maxAge, FunctionCall.of("max", new Expression.DatabaseReference(people), Value.of("age")))
        .print(context -> "Max age: " + context.getValue(maxAge).toHumanString())
        .build());

    Variable peopleOlderThanTwenty = new Variable("people-older-than-twenty", Type.RECORD_SET);
    container.addBlock(Block.builder()
        .bind(peopleOlderThanTwenty, Query.builder(people).greaterThan("age", 20).build())
        .forAll(peopleOlderThanTwenty, person)
        .print(context -> context.getRecord(person).toString())
        .build()
        .build());


    System.out.println("Container Dump:\n" + container);
    container.saveAndShutdown();

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
}
