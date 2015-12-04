package com.github.sedubois.factorization.service;

import com.github.sedubois.factorization.FactorizationTask;
import com.github.sedubois.factorization.FactorizationTask.State;
import com.github.sedubois.factorization.persistence.FactorizationRepository;

import java.util.Collection;
import java.util.List;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.json.Json;

import static com.github.sedubois.factorization.FactorizationTask.State.DONE;
import static com.github.sedubois.factorization.FactorizationTask.State.ONGOING;
import static com.github.sedubois.MainVerticle.BUS_ADDR;

public class FactorizationService extends AbstractVerticle {

  private final FactorizationRepository repository;

  public FactorizationService(FactorizationRepository repository) {
    this.repository = repository;
  }

  public Collection<FactorizationTask> getAll() {
    return repository.getAll();
  }

  public FactorizationTask getOne(long id) {
    return repository.getOne(id);
  }

  public FactorizationTask create(long number) {
    FactorizationTask task = repository.create(number);
    factorize(task);
    return task;
  }

  public void remove(long id) {
    repository.remove(id);
    // TODO cancel ongoing task
  }

  private void factorize(FactorizationTask task) {
    vertx.executeBlocking(future -> {
      setTaskState(task, ONGOING);
      List<Long> factors = Factorizer.factorize(task.getNumber());
      task.setFactors(factors);
      future.complete(task);
    }, false, res -> setTaskState(task, DONE));
  }

  private void setTaskState(FactorizationTask task, State state) {
    task.setState(state);
    vertx.eventBus().publish(BUS_ADDR, Json.encode(task));
  }
}
