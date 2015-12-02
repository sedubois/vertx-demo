package com.github.sedubois.vertx;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.ext.web.Router;

public class MyFirstVerticle extends AbstractVerticle {

  @Override
  public void start(Future<Void> future) {
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
    router.route("/").handler(routingContext -> {
      HttpServerResponse response = routingContext.response();
      response
          .putHeader("content-type", "text/html")
          .end("<h1>Hello from my first Vert.x 3 application</h1>");
    });
    return router;
  }
}