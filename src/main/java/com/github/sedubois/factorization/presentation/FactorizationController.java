package com.github.sedubois.factorization.presentation;

import com.github.sedubois.factorization.FactorizationTask;
import com.github.sedubois.factorization.service.FactorizationService;

import io.vertx.core.Vertx;
import io.vertx.core.json.Json;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.handler.BodyHandler;

import static java.util.Optional.ofNullable;

public class FactorizationController {

  private final FactorizationService service;

  public FactorizationController(FactorizationService service) {
    this.service = service;
  }

  public Router getRouter(Vertx vertx) {
    Router router = Router.router(vertx);
    router.route().consumes("application/json");
    router.route().produces("application/json");
    router.get("/tasks").handler(this::getAll);
    router.get("/tasks/:id").handler(this::getOne);
    router.route("/tasks*").handler(BodyHandler.create());
    router.post("/tasks").handler(this::addOne);
    router.delete("/tasks/:id").handler(this::deleteOne);
    return router;
  }

  private void getAll(RoutingContext routingContext) {
    routingContext.response()
        .putHeader("content-type", "application/json; charset=utf-8")
        .end(Json.encodePrettily(service.getAll()));
  }

  private void getOne(RoutingContext routingContext) {
    Long id = getId(routingContext);
    if (id == null) {
      routingContext.response().setStatusCode(400).end();
    } else {
      FactorizationTask task = service.getOne(id);
      if (task == null) {
        routingContext.response().setStatusCode(404).end();
      } else {
        routingContext.response()
            .putHeader("content-type", "application/json; charset=utf-8")
            // TODO don't serialize null values
            .end(Json.encodePrettily(task));
      }
    }
  }

  private void addOne(RoutingContext routingContext) {
    final FactorizationTask task = Json.decodeValue(routingContext.getBodyAsString(), FactorizationTask.class);
    service.create(task.getNumber());
    routingContext.response()
        .setStatusCode(201)
        .putHeader("content-type", "application/json; charset=utf-8")
        .end(Json.encodePrettily(task));
  }

  private void deleteOne(RoutingContext routingContext) {
    Long id = getId(routingContext);
    if (id == null) {
      routingContext.response().setStatusCode(400).end();
    } else {
      service.remove(id);
    }
    routingContext.response().setStatusCode(204).end();
  }

  private Long getId(RoutingContext routingContext) {
    return ofNullable(routingContext.request().getParam("id"))
        .map(Long::valueOf)
        .orElse(null);
  }
}