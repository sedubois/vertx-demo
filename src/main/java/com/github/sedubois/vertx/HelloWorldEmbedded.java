package com.github.sedubois.vertx;

import io.vertx.core.Vertx;

class HelloWorldEmbedded {

  public static void main(String[] args) {
    Vertx.vertx()
      .createHttpServer()
      .requestHandler(req -> req.response().end("Hello World!"))
      .listen(8080, handler -> {
        if (handler.succeeded()) {
          System.out.println("Listening on http://localhost:8080/");
        } else {
          System.err.println("Failed to listen on port 8080");
        }
      });
  }
}
