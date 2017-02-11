package me.lachlanap.evelike;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by lachlan on 11/02/2017.
 */
public class Container {
  private final List<Database> databases;
  private final List<Main.Block> blocks;
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

  public void addBlock(Main.Block block) {
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
    for (Main.Block block : blocks) {
      builder.append(StringUtils.indent(block.toString())).append("\n");
    }

    return builder.toString();
  }
}
