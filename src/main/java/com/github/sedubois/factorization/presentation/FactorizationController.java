package com.github.sedubois.factorization.presentation;

import com.github.sedubois.factorization.service.FactorizationService;
import com.github.sedubois.factorization.FactorizationTask;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;
import io.vertx.core.json.Json;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.handler.BodyHandler;

import static java.util.Optional.ofNullable;

public class FactorizationController extends AbstractVerticle {

  private final FactorizationService service;

  public FactorizationController(FactorizationService service) {
    this.service = service;
  }

  @Override
  public void start(Future<Void> future) {
    vertx
        .createHttpServer()
        .requestHandler(getRouter()::accept)
        // TODO make HTTP port configurable for parallel testing
        .listen(8080, result -> {
          if (result.succeeded()) {
            future.complete();
          } else {
            future.fail(result.cause());
          }
        });
  }

  private Router getRouter() {
    Router router = Router.router(vertx);
    router.get("/api/tasks").handler(this::getAll);
    router.get("/api/tasks/:id").handler(this::getOne);
    router.route("/api/tasks*").handler(BodyHandler.create());
    router.post("/api/tasks").handler(this::addOne);
    router.delete("/api/tasks/:id").handler(this::deleteOne);
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