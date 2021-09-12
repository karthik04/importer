package emp.importer.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import emp.importer.dao.EmployeeDao;
import emp.importer.payload.Employee;
import emp.importer.utils.Utils;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.vertx.ext.web.RoutingContext;
import io.vertx.mysqlclient.MySQLPool;

import java.util.List;

public class EmployeeService {
  public static void postEmployeeBulkRoute(RoutingContext ctx, MySQLPool dbClient) {
    try {
      List<Employee> empList = Utils.deserializeEmpList(ctx.getBodyAsString());
      EmployeeDao.insertEmployees(empList, dbClient);
      ctx.response()
        .putHeader("content-type", "application/json")
        .setStatusCode(HttpResponseStatus.NO_CONTENT.code())
        .end();
    } catch (JsonProcessingException e) {
      ctx.response()
        .putHeader("content-type", "application/json")
        .setStatusCode(HttpResponseStatus.BAD_REQUEST.code())
        .end();
    }

  }

  public static void getPingRoute(RoutingContext ctx) {
    ctx.response()
      .putHeader("content-type", "text/plain")
      .end("App running!");
  }
}
