package com.github.sedubois;

import io.vertx.core.AbstractVerticle;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.handler.ErrorHandler;
import io.vertx.ext.web.handler.StaticHandler;
import io.vertx.ext.web.handler.sockjs.BridgeEventType;
import io.vertx.ext.web.handler.sockjs.BridgeOptions;
import io.vertx.ext.web.handler.sockjs.PermittedOptions;
import io.vertx.ext.web.handler.sockjs.SockJSHandler;

import static java.util.Arrays.stream;

public class MainVerticle extends AbstractVerticle {

  public static final String BUS_ADDR = "notif";

  @Override
  public void start() throws Exception {
    App app = DaggerApp.create();
    Router router = getRouter(app.controller().getRouter());
    vertx.createHttpServer().requestHandler(router::accept).listen(8080);
  }

  private Router getRouter(Router... subRouters) {
    Router router = Router.router(vertx);
    stream(subRouters).forEach(r -> router.mountSubRouter("/api", r));
    router.route("/eventbus/*").handler(eventBusHandler());
    router.route().failureHandler(ErrorHandler.create(true));
    router.route().handler(StaticHandler.create().setCachingEnabled(false));
    return router;
  }

  private SockJSHandler eventBusHandler() {
    BridgeOptions options = new BridgeOptions()
        .addOutboundPermitted(new PermittedOptions().setAddress(BUS_ADDR));
    return SockJSHandler
        .create(vertx)
        .bridge(options, event -> {
          if (event.type() == BridgeEventType.SOCKET_CREATED) {
            System.out.println("A socket was created");
          }
          event.complete(true);
        });
  }
}
