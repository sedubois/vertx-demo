package com.github.sedubois;

import com.github.sedubois.factorization.FactorizationTask;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import io.vertx.core.Vertx;
import io.vertx.core.http.HttpClient;
import io.vertx.core.json.Json;
import io.vertx.ext.unit.Async;
import io.vertx.ext.unit.TestContext;
import io.vertx.ext.unit.junit.VertxUnitRunner;

import static com.github.sedubois.factorization.FactorizationTask.State.ONGOING;

// TODO use Spock / assertJ / rest assured to get given/when/then style
@RunWith(VertxUnitRunner.class)
public class MainVerticleTest {

  private static final int PORT = 8080;
  private static final String ADDRESS = "localhost";
  private Vertx vertx = Vertx.vertx();
  private HttpClient httpClient;

  @Before
  public void setUp(TestContext context) {
    vertx.deployVerticle(MainVerticle.class.getName(), context.asyncAssertSuccess());
    httpClient = vertx.createHttpClient();
  }

  @After
  public void tearDown(TestContext context) {
    vertx.close(context.asyncAssertSuccess());
  }

  @Test
  public void getTasksReturnsEmptyCollection(TestContext context) {
    final Async async = context.async();

    httpClient
        .getNow(PORT, ADDRESS, "/api/tasks", response -> response.handler(body -> {
          context.assertTrue(body.toString().equals("[ ]"));
          async.complete();
        }));
  }

  @Test
  public void createTaskReturnsCreatedTask(TestContext context) {
    final Async async = context.async();

    long number = 10;
    String body = "{\"number\": " + number + "}";
    httpClient
        .post(PORT, ADDRESS, "/api/tasks", response -> response.handler(responseBody -> {
          System.out.println(responseBody);
          FactorizationTask actual = Json.decodeValue(responseBody.toString(), FactorizationTask.class);
          FactorizationTask expected = new FactorizationTask(0, number);
          expected.setState(ONGOING);
          context.assertTrue(actual.equals(expected));
          async.complete();
        }))
        .putHeader("content-type", "application/json")
        .putHeader("content-length", Integer.toString(body.length()))
        .write(body)
        .end();
  }
}
