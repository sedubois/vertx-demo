package com.github.sedubois.factorization.presentation;

import com.github.sedubois.factorization.FactorizationTask;
import com.github.sedubois.factorization.service.FactorizationService;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;
import io.vertx.core.json.Json;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.handler.BodyHandler;
import io.vertx.ext.web.handler.ErrorHandler;
import io.vertx.ext.web.handler.StaticHandler;
import io.vertx.ext.web.handler.sockjs.BridgeEventType;
import io.vertx.ext.web.handler.sockjs.BridgeOptions;
import io.vertx.ext.web.handler.sockjs.PermittedOptions;
import io.vertx.ext.web.handler.sockjs.SockJSHandler;

import static java.util.Optional.ofNullable;

public class FactorizationController extends AbstractVerticle {

  public static final String BUS_ADDR = "notif";
  private final FactorizationService service;

  public FactorizationController(FactorizationService service) {
    this.service = service;
  }

  @Override
  public void start(Future<Void> future) {
    vertx.createHttpServer().requestHandler(getRouter()::accept).listen(8080);
  }

  private Router getRouter() {
    Router router = Router.router(vertx);
    router.route("/eventbus/*").handler(eventBusHandler());
    router.mountSubRouter("/api", restRouter());
    router.route().failureHandler(ErrorHandler.create(true));
    router.route().handler(StaticHandler.create().setCachingEnabled(false));
    return router;
  }

  private SockJSHandler eventBusHandler() {
    BridgeOptions options = new BridgeOptions()
        .addOutboundPermitted(new PermittedOptions().setAddressRegex(".*"));
    return SockJSHandler
        .create(vertx)
        .bridge(options, event -> {
          if (event.type() == BridgeEventType.SOCKET_CREATED) {
            System.out.println("A socket was created");
          }
          event.complete(true);
        });
  }

  private Router restRouter() {
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