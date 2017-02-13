package me.lachlanap.evelike;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by lachlan on 11/02/2017.
 */
public class Context {
  public static Context empty() {
    return new Context(null);
  }

  private final Context parent;
  private final Map<Variable, Object> thingMap;

  private Context(Context parent) {
    this.parent = parent;
    this.thingMap = new HashMap<>();
  }

  public Value getValue(Variable variable) {
    Object thing = thingMap.get(variable);
    if (thing == null) {
      if (parent == null) {
        throw new IllegalArgumentException("Unknown variable: " + variable);
      } else {
        return parent.getValue(variable);
      }
    } else if (thing instanceof Value) {
      return (Value) thing;
    } else {
      throw new IllegalArgumentException("Variable: " + variable + " is not bound to a value; it is: " + thing);
    }
  }

  public Record getRecord(Variable variable) {
    Object thing = thingMap.get(variable);
    if (thing == null) {
      if (parent == null) {
        throw new IllegalArgumentException("Unknown variable: " + variable);
      } else {
        return parent.getRecord(variable);
      }
    } else if (thing instanceof Record) {
      return (Record) thing;
    } else {
      throw new IllegalArgumentException("Variable: " + variable + " is not bound to a record; it is: " + thing);
    }
  }

  public void bind(Variable variable, Value value) {
    thingMap.put(variable, value);
  }

  public void bind(Variable variable, Record record) {
    thingMap.put(variable, record);
  }

  public Context createNested() {
    return new Context(this);
  }
}
