package com.github.sedubois.factorization.persistence;

import com.github.sedubois.factorization.FactorizationTask;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;

public class FactorizationRepository {

  private static final AtomicLong COUNTER = new AtomicLong();
  private final Map<Long, FactorizationTask> tasks = new LinkedHashMap<>();

  public Collection<FactorizationTask> getAll() {
    return tasks.values();
  }

  public FactorizationTask getOne(long id) {
    return tasks.get(id);
  }

  public FactorizationTask create(long number) {
    FactorizationTask task = new FactorizationTask(COUNTER.getAndIncrement(), number);
    tasks.put(task.getId(), task);
    return task;
  }

  public void remove(long id) {
    tasks.remove(id);
  }
}
