package me.lachlanap.evelike;

import lombok.Data;

/**
 * Created by lachlan on 13/02/2017.
 */
@Data
public class Block {
  public static Code.Builder<Block> builder() {
    return new Code.Builder<>(Block::new);
  }

  private final Code code;

  public Block(Code code) {
    this.code = code;
  }

  @Override
  public String toString() {
    return code.toString();
  }

  public String getId() {
    return String.format("%08x", System.identityHashCode(this));
  }
}
