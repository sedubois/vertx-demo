package com.github.sedubois.factorization.service;

import com.github.sedubois.factorization.FactorizationTask;
import com.github.sedubois.factorization.persistence.FactorizationRepository;

import java.util.Collection;

import javax.inject.Inject;
import javax.inject.Singleton;

import io.vertx.core.Vertx;

import static com.github.sedubois.factorization.FactorizationTask.State.DONE;
import static com.github.sedubois.factorization.FactorizationTask.State.ONGOING;

@Singleton
public class FactorizationService {

  private final FactorizationRepository repository;

  @Inject
  FactorizationService(FactorizationRepository repository) {
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
    Vertx.currentContext().owner().executeBlocking(future -> {
      task.setState(ONGOING);
      factorizeSync(task);
      future.complete(task);
    }, false, res -> task.setState(DONE));
  }

  private void factorizeSync(FactorizationTask task) {
    long n = task.getNumber();
    for (long i = 2; i <= n; i++) {
      while (n % i == 0) {
        task.addFactor(i);
        n /= i;
      }
    }
  }
}
