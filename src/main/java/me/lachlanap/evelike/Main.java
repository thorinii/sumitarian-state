package me.lachlanap.evelike;

public class Main {
  public static void main(String[] args) {
    Database people = new Database("people");

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

    System.out.println("All records:");
    for (Record record : people.queryForAll()) {
      System.out.println(record);
    }
  }

}
