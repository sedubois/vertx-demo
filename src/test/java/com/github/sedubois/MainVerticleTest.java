package com.github.sedubois;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import io.vertx.core.Vertx;
import io.vertx.ext.unit.Async;
import io.vertx.ext.unit.TestContext;
import io.vertx.ext.unit.junit.VertxUnitRunner;

@RunWith(VertxUnitRunner.class)
public class MainVerticleTest {

  private Vertx vertx = Vertx.vertx();

  @Before
  public void setUp(TestContext context) {
    vertx.deployVerticle(MainVerticle.class.getName(), context.asyncAssertSuccess());
  }

  @After
  public void tearDown(TestContext context) {
    vertx.close(context.asyncAssertSuccess());
  }

  @Test
  // TODO use assertJ / rest assured
  public void getTasksReturnsEmptyCollection(TestContext context) {
    final Async async = context.async();

    vertx.createHttpClient().getNow(8080, "localhost", "/api/tasks",
     response -> {
      response.handler(body -> {
        context.assertTrue(body.toString().equals("[ ]"));
        async.complete();
      });
    });
  }
}
