package emp.importer.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import emp.importer.dao.EmployeeDao;
import emp.importer.payload.Employee;
import emp.importer.utils.Utils;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.vertx.core.Handler;
import io.vertx.ext.web.RoutingContext;
import io.vertx.mysqlclient.MySQLPool;

import java.util.List;

public class EmployeeService {
  public static void postEmployeeBulkRoute(RoutingContext ctx, MySQLPool dbClient) {
    try {
      List<Employee> empList = Utils.deserializeEmpList(ctx.getBodyAsString());
      EmployeeDao.insertEmployeesAsync(empList, dbClient, successHandler(ctx), errorHandler(ctx));
    } catch (JsonProcessingException e) {
      errorHandler(ctx).handle(e);
    }
  }

  private static Handler<Void> successHandler(RoutingContext ctx) {
    return v ->
      ctx.response()
        .putHeader("content-type", "application/json")
        .setStatusCode(HttpResponseStatus.NO_CONTENT.code())
        .end();
  }

  private static Handler<Throwable> errorHandler(RoutingContext ctx) {
    return v ->
      ctx.response()
        .putHeader("content-type", "application/json")
        .setStatusCode(HttpResponseStatus.BAD_REQUEST.code())
        .end(v.getMessage());
//    TODO: Send error message in a standard json format
  }

  public static void getPingRoute(RoutingContext ctx) {
    ctx.response()
      .putHeader("content-type", "text/plain")
      .end("App running!");
  }
}
