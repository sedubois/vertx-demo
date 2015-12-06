package com.github.sedubois.factorization;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import io.vertx.core.Vertx;
import io.vertx.core.json.Json;

import static com.github.sedubois.MainVerticle.BUS_ADDR;

public class FactorizationTask {

  private long id;
  private long number;
  private State state = State.CREATED;
  private List<Long> factors = new ArrayList<>();

  public FactorizationTask(long id, long number) {
    this.id = id;
    this.number = number;
  }

  private FactorizationTask() {}

  public long getId() {
    return id;
  }

  public long getNumber() {
    return number;
  }

  public void setState(State state) {
    this.state = state;
    publish();
  }

  public State getState() {
    return state;
  }

  public void addFactor(long factor) {
    this.factors.add(factor);
    publish();
  }

  public List<Long> getFactors() {
    return this.factors;
  }

  public enum State {
    CREATED,
    ONGOING,
    DONE
  }

  @Override
  public String toString() {
    return String.format("[id = %d, number = %d, state = %s, factors = %s]", id, number, state, factors);
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    FactorizationTask task = (FactorizationTask) o;
    return Objects.equals(id, task.id) &&
           Objects.equals(number, task.number) &&
           Objects.equals(state, task.state) &&
           Objects.equals(factors, task.factors);
  }

  @Override
  public int hashCode() {
    return Objects.hash(id, number, state, factors);
  }

  private void publish() {
    Vertx.currentContext().owner().eventBus().publish(BUS_ADDR, Json.encode(this));
  }
}