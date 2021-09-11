package emp.importer;

import emp.importer.service.EmployeeService;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Promise;
import io.vertx.core.http.HttpMethod;
import io.vertx.core.http.HttpServer;
import io.vertx.ext.web.Router;

public class MainVerticle extends AbstractVerticle {

  @Override
  public void start(Promise<Void> startPromise) throws Exception {
    HttpServer server = vertx.createHttpServer();
    Router router = Router.router(vertx);

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
