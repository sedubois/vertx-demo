package com.github.sedubois.vertx;

import java.util.concurrent.atomic.AtomicInteger;

public class FactorizationTask {

  private static final AtomicInteger COUNTER = new AtomicInteger();

  private long id;
  private long number;
  private State state;

  public FactorizationTask(long number) {
    this();
    this.number = number;
    this.state = State.CREATED;
  }

  private FactorizationTask() {
    this.id = COUNTER.getAndIncrement();
  }

  public long getId() {
    return id;
  }

  public long getNumber() {
    return number;
  }

  public State getState() {
    return state;
  }

  enum State {
    CREATED
  }
}