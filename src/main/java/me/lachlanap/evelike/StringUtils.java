package me.lachlanap.evelike;

/**
 * Created by lachlan on 11/02/2017.
 */
public class StringUtils {
  public static String indent(String in) {
    return "  " + in.replace("\n", "\n  ");
  }
}
