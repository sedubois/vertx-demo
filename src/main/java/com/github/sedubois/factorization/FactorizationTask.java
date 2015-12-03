package com.github.sedubois.factorization;

import java.util.List;

public class FactorizationTask {

  private long id;
  private long number;
  private State state = State.CREATED;
  private List<Long> factors;

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
  }

  public State getState() {
    return state;
  }

  public void setFactors(List<Long> factors) {
    this.factors = factors;
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
}