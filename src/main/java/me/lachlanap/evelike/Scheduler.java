package me.lachlanap.evelike;

import java.util.LinkedList;
import java.util.Queue;
import java.util.function.Consumer;

import lombok.extern.slf4j.Slf4j;

/**
 * Created by lachlan on 13/02/2017.
 */
@Slf4j
public class Scheduler {
  private final Container container;
  private final Queue<Task> tasks;

  public Scheduler(Container container) {
    this.container = container;
    this.tasks = new LinkedList<>();
  }

  public void scheduleNewBlock(Block block) {
    log.info("Scheduling new block: {}", block.getId());
    tasks.add(new ExecuteNewBlockTask(container, block));
  }

  public void executeAllAndShutdown() {
    pumpTasks();
  }

  private void pumpTasks() {
    Task task;
    while ((task = tasks.poll()) != null) {
      task.execute();
    }
  }

  private static abstract class Task {

    public abstract void execute();
  }

  private static class ExecuteNewBlockTask extends Task {
    private final Container container;
    private final Block block;

    private ExecuteNewBlockTask(Container container, Block block) {
      this.container = container;
      this.block = block;
    }

    @Override
    public void execute() {
      log.info("Executing new block {}", block.getId());

      Context context = Context.empty();
      execute(block.getCode(), context);
    }

    private void execute(Code code, Context context) {
      for (Statement statement : code.getStatements()) {
        DynamicDispatch.of(statement)
            .unit(Statement.Binding.class, binding -> {
              log.info("E1: {}", binding.getClass().getSimpleName());

              // TODO: calculate something
              context.bind(binding.getVariable(), Value.of(3));
            })
            .unit(Statement.Print.class, print -> {
              log.info("E2: {}", print.getClass().getSimpleName());
              System.out.println(print.getTransformer().apply(context));
            })
            .unit(Statement.ForAll.class, forAll -> {
              log.info("E3: {}", forAll.getClass().getSimpleName());

              // TODO: iterate over set
              Context nested = context.createNested();
              nested.bind(forAll.getPointer(), Record.builder().add("thing", "value").build());
              execute(forAll.getCode(), nested);
            })
            .elseExplode();
      }
    }
  }

  public static class DynamicDispatch<T, R> {
    public static <T, R> DynamicDispatch<T, R> of(T value) {
      return new DynamicDispatch<T, R>(value);
    }

    private final T value;
    private boolean matched;

    public DynamicDispatch(T value) {
      this.value = value;
      this.matched = false;
    }

    public <A extends T> DynamicDispatch<T, R> unit(Class<? super A> match, Consumer<A> callable) {
      if (!matched && match.isInstance(value)) {
        matched = true;
        callable.accept((A) value);
      }
      return this;
    }

    public void elseExplode() {
      if (!matched) throw new RuntimeException("DynamicDispatch was not matched: " + value);
    }
  }
}
