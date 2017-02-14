package me.lachlanap.evelike;

import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.function.Consumer;
import java.util.function.Function;

import lombok.extern.slf4j.Slf4j;

import static java.util.stream.Collectors.toList;

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
        executeStatement(context, statement);
      }
    }

    private void executeStatement(Context context, Statement statement) {
      Match.of(statement)
          .unit(Statement.Binding.class, binding -> {
            log.info("BINDING");

            Object result = evaluateExpression(context, binding.getExpression());

            if (result instanceof Value) {
              context.bind(binding.getVariable(), (Value) result);
            } else if (result instanceof Record) {
              context.bind(binding.getVariable(), (Record) result);
            } else if (result instanceof Iterable) {
              context.bind(binding.getVariable(), (Iterable<Record>) result);
            } else {
              throw new IllegalArgumentException("Unknown expression result: " + result);
            }
          })
          .unit(Statement.Print.class, print -> {
            log.info("PRINT {}", print.getTransformer().apply(context));
          })
          .unit(Statement.ForAll.class, forAll -> {
            log.info("FORALL");

            Iterable<Record> recordSet = context.getRecordSet(forAll.getSet());
            for (Record record : recordSet) {
              Context nested = context.createNested();
              nested.bind(forAll.getPointer(), record);
              execute(forAll.getCode(), nested);
            }

            log.info("END FORALL");
          })
          .elseExplode();
    }

    private Object evaluateExpression(Context context, Expression expression) {
      return Match.of(expression)
          .<Value, Object>map(Value.class, value -> {
            log.info("_VALUE {}", value);
            return value;
          })
          .<Expression.DatabaseReference, Object>map(Expression.DatabaseReference.class, db -> {
            log.info("_DB {}", db.getDatabase().getName());
            return db.getDatabase().queryForAll();
          })
          .<Query, Object>map(Query.class, query -> {
            Object input = evaluateExpression(context, query.getSet());
            log.info("_QUERY");
            return input; // TODO: filter
          })
          .map(Projection.class, projection -> {
            Object input = evaluateExpression(context, projection.getExpression());
            log.info("_PROJECTION");
            return input; // TODO: project and deduplicate
          })
          .map(FunctionCall.class, fc -> {
            List<Object> args = fc.getArguments().stream()
                .map(arg -> evaluateExpression(context, arg))
                .collect(toList());
            log.info("_CALL {}", fc.getFunction());
            return callFunction(context, fc.getFunction(), args);
          })
          .result();
    }

    private Object callFunction(Context context, String function, List<Object> args) {
      Iterable<Record> recordSet;

      switch (function) {
        case "count":
          recordSet = (Iterable<Record>) args.get(0);
          int i = 0;
          for (Record record : recordSet) i++;
          return Value.of(i);

        case "max":
          recordSet = (Iterable<Record>) args.get(0);
          String field = ((Value) args.get(1)).getStringValue();
          int max = Integer.MIN_VALUE;
          for (Record record : recordSet) {
            if (!record.hasField(field)) continue;
            max = Math.max(max, record.getField(field).getIntValue());
          }
          return Value.of(max);

        default:
          throw new IllegalArgumentException("Unknown function: " + function);
      }
    }
  }

  public static class Match<T, R> {
    public static <T, R> Match<T, R> of(T value) {
      return new Match<>(value);
    }

    private final T value;
    private R result;
    private boolean matched;

    public Match(T value) {
      this.value = value;
      this.matched = false;
    }

    public <T2 extends T> Match<T, R> unit(Class<? super T2> match, Consumer<T2> callable) {
      if (!matched && match.isInstance(value)) {
        matched = true;
        //noinspection unchecked
        callable.accept((T2) value);
      }
      return this;
    }

    public <T2 extends T, R2 extends R> Match<T, R2> map(Class<T2> match, Function<T2, R2> callable) {
      if (!matched && match.isInstance(value)) {
        matched = true;
        //noinspection unchecked
        result = callable.apply((T2) value);
      }
      //noinspection unchecked
      return (Match<T, R2>) this;
    }

    public Match<T, R> elseExplode() {
      if (!matched) {
        throw new RuntimeException("Match was not matched: " + value + " : " + value.getClass());
      }
      return this;
    }

    public R result() {
      elseExplode();
      return result;
    }
  }
}
