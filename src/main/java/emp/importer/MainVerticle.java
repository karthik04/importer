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
import io.vertx.sqlclient.Row;
import io.vertx.sqlclient.RowSet;

public class MainVerticle extends AbstractVerticle {

  @Override
  public void start(Promise<Void> startPromise) throws Exception {
    HttpServer server = vertx.createHttpServer();
    Router router = Router.router(vertx);
    router.route().handler(BodyHandler.create());

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
    MySQLPool dbCient = MySQLPool.pool(vertx, connectOptions, poolOptions);
// A simple query
    dbCient
      .query("select * from Employee")
      .execute(ar -> {
        if (ar.succeeded()) {
          RowSet<Row> result = ar.result();
          System.out.println("Got " + result.size() + " rows ");
        } else {
          System.out.println("Failure: " + ar.cause().getMessage());
        }
        // Now close the pool
        dbCient.close();
      });


    router.route(HttpMethod.POST, "/v1/employee/bulk").handler(EmployeeService::postEmployeeBulkRoute);
    router.route(HttpMethod.GET, "/ping").handler(EmployeeService::getPingRoute);
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
}
