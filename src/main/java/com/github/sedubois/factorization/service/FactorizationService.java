package com.github.sedubois.factorization.service;

import com.github.sedubois.factorization.FactorizationTask;
import com.github.sedubois.factorization.persistence.FactorizationRepository;

import java.util.Collection;
import java.util.List;

import io.vertx.core.AbstractVerticle;

import static com.github.sedubois.factorization.FactorizationTask.State.DONE;
import static com.github.sedubois.factorization.FactorizationTask.State.ONGOING;

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

  public void create(long number) {
    FactorizationTask task = repository.create(number);
    factorize(task);
  }

  public void remove(long id) {
    repository.remove(id);
    // TODO cancel ongoing task
  }

  private void factorize(FactorizationTask task) {
    vertx.executeBlocking(future -> {
      task.setState(ONGOING);
      List<Long> factors = Factorizer.factorize(task.getNumber());
      task.setFactors(factors);
      task.setState(DONE);
      future.complete(task);
    }, false, res -> {
      System.out.println("The result is: " + res.result());
    });
  }
}
