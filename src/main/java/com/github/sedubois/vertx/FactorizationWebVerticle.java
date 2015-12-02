package com.github.sedubois.vertx;

import java.util.LinkedHashMap;
import java.util.Map;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;
import io.vertx.core.json.Json;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.handler.BodyHandler;

import static java.util.Optional.ofNullable;

public class FactorizationWebVerticle extends AbstractVerticle {

  private Map<Long, FactorizationTask> tasks = new LinkedHashMap<>();

  @Override
  public void start(Future<Void> future) {
    createSomeData();
    vertx
        .createHttpServer()
        .requestHandler(getRouter()::accept)
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

  private void createSomeData() {
    FactorizationTask sessionA = new FactorizationTask(151327932445664L);
    tasks.put(sessionA.getId(), sessionA);
    FactorizationTask sessionB = new FactorizationTask(4132793244345664L);
    tasks.put(sessionB.getId(), sessionB);
  }

  private void getAll(RoutingContext routingContext) {
    routingContext.response()
        .putHeader("content-type", "application/json; charset=utf-8")
        .end(Json.encodePrettily(tasks.values()));
  }

  private void getOne(RoutingContext routingContext) {
    Long id = getId(routingContext);
    if (id == null) {
      routingContext.response().setStatusCode(400).end();
    } else {
      routingContext.response()
          .putHeader("content-type", "application/json; charset=utf-8")
          .end(Json.encodePrettily(tasks.get(id)));
    }
  }

  private void addOne(RoutingContext routingContext) {
    final FactorizationTask task = Json.decodeValue(routingContext.getBodyAsString(), FactorizationTask.class);
    tasks.put(task.getId(), task);
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
      tasks.remove(id);
    }
    routingContext.response().setStatusCode(204).end();
  }

  private Long getId(RoutingContext routingContext) {
    return ofNullable(routingContext.request().getParam("id"))
        .map(Long::valueOf)
        .orElse(null);
  }
}