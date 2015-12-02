package com.github.sedubois.vertx;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;

public class MyFirstVerticle extends AbstractVerticle {

  @Override
  public void start(Future<Void> future) {
    vertx
        .createHttpServer()
        .requestHandler(r -> r.response().end("<h1>Hello World!</h1>"))
        .listen(8080, result -> {
          if (result.succeeded()) {
            future.complete();
          } else {
            future.fail(result.cause());
          }
        });
  }
}