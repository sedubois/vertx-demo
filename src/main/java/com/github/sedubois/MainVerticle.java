package com.github.sedubois;

import com.github.sedubois.factorization.persistence.FactorizationRepository;
import com.github.sedubois.factorization.presentation.FactorizationController;
import com.github.sedubois.factorization.service.FactorizationService;

import io.vertx.core.AbstractVerticle;

public class MainVerticle extends AbstractVerticle {

  @Override
  public void start() throws Exception {
    FactorizationRepository repository = new FactorizationRepository();
    FactorizationService service = new FactorizationService(repository);
    vertx.deployVerticle(new FactorizationController(service));
    vertx.deployVerticle(service);
  }
}
