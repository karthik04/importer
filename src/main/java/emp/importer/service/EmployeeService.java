package emp.importer.service;

import io.netty.handler.codec.http.HttpResponseStatus;
import io.vertx.ext.web.RoutingContext;

public class EmployeeService {
  public static void postEmployeeBulkRoute(RoutingContext ctx) {
    String payload = ctx.getBodyAsString();

    ctx.response()
      .putHeader("content-type", "application/json")
      .setStatusCode(HttpResponseStatus.NO_CONTENT.code())
      .end();
  }

  public static void getPingRoute(RoutingContext ctx) {
    ctx.response()
      .putHeader("content-type", "text/plain")
      .end("App running!");
  }
}
