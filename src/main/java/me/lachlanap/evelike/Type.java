package me.lachlanap.evelike;

/**
 * Created by lachlan on 11/02/2017.
 */
public class Type {
  public static Type INTEGER = new Type();
  public static Type RECORD = new Type();
  public static Type RECORD_SET = new Type();

  public static Type function() {
    return new Type();
  }
}
