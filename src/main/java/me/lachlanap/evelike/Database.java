package me.lachlanap.evelike;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by lachlan on 11/02/2017.
 */
public class Database {
  private final String name;
  private final Set<Record> records;

  public Database(String name) {
    this.name = name;
    this.records = new HashSet<>();
  }

  public void commit(Record record) {
    records.add(record);
  }

  public Iterable<Record> queryForAll() {
    return records;
  }
}
