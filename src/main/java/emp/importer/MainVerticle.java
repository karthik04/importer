package emp.importer;

import emp.importer.service.EmployeeService;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Promise;
import io.vertx.core.http.HttpMethod;
import io.vertx.core.http.HttpServer;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.handler.BodyHandler;
import io.vertx.mysqlclient.MySQLConnectOptions;
import io.vertx.mysqlclient.MySQLPool;
import io.vertx.sqlclient.PoolOptions;

public class MainVerticle extends AbstractVerticle {

  //    DB connection configuration
  MySQLConnectOptions connectOptions = new MySQLConnectOptions()
    .setPort(3306)
    .setHost("localhost")
    .setDatabase("dev")
    .setUser("root")
    .setPassword("root");
  // Pool options
  PoolOptions poolOptions = new PoolOptions()
    .setMaxSize(20);
  // Create the client pool
  MySQLPool dbClient;

  @Override
  public void start(Promise<Void> startPromise) throws Exception {
    HttpServer server = vertx.createHttpServer();
    Router router = Router.router(vertx);
    router.route().handler(BodyHandler.create());

    dbClient = MySQLPool.pool(vertx, connectOptions, poolOptions);

    router.route(HttpMethod.POST, "/v1/employees").handler(
      ctx -> EmployeeService.postEmployeesRoute(ctx, dbClient)
    );
    router.route(HttpMethod.PUT, "/v1/employee").handler(
      ctx -> EmployeeService.putEmployeeRoute(ctx, dbClient)
    );
    router.route(HttpMethod.GET, "/").handler(EmployeeService::getPingRoute);

    server.requestHandler(router).listen(8080, http -> {
      if (http.succeeded()) {
        startPromise.complete();
        System.out.println("HTTP server started on port 8080");
      } else {
        startPromise.fail(http.cause());
      }
    });
  }

  @Override
  public void stop(Promise<Void> stopFuture) throws Exception {
    //must call super.stop() or call stopFuture.complete()
    super.stop(stopFuture);
    dbClient.close();
    System.out.println("MyVerticle stopped!");
  }
}
