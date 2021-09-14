package emp.importer.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.wixpress.dst.greyhound.java.GreyhoundProducer;
import emp.importer.dao.EmployeeDao;
import emp.importer.payload.Employee;
import emp.importer.utils.Utils;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.vertx.core.Handler;
import io.vertx.ext.web.RoutingContext;
import io.vertx.mysqlclient.MySQLPool;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.StringSerializer;

import java.util.List;

import static emp.importer.utils.Constants.EMP_TOPIC_V1;

public class EmployeeService {
  /**
   * Employees POST - Bulk operation
   */
  public static void postEmployeesRoute(RoutingContext ctx, MySQLPool dbClient) {
    try {
      List<Employee> empList = Utils.deserializeEmpList(ctx.getBodyAsString());
      EmployeeDao
        .insertEmployeesAsync(empList, dbClient)
        .onSuccess(successHandler(ctx))
        .onFailure(errorHandler(ctx));
    } catch (JsonProcessingException e) {
      errorHandler(ctx).handle(e);
    }
  }

  /**
   * Employee PUT
   * - Updates single employee in DB and Emits Kafka event
   */
  public static void putEmployeeRoute(RoutingContext ctx, MySQLPool dbClient, GreyhoundProducer kafkaProducer) {
    try {
      Employee emp = Utils.deserializeEmployee(ctx.getBodyAsString());
      EmployeeDao
        .updateEmployeeAsync(emp, dbClient)
        .onSuccess(v -> {
            kafkaProducer.produce(
              new ProducerRecord<>(EMP_TOPIC_V1, "hello world"),
              new StringSerializer(),
              new StringSerializer());
            ctx.response()
              .putHeader("content-type", "application/json")
              .setStatusCode(HttpResponseStatus.NO_CONTENT.code())
              .end();
          }
        )
        .onFailure(errorHandler(ctx));
    } catch (JsonProcessingException e) {
      errorHandler(ctx).handle(e);
    }
  }

  public static void getPingRoute(RoutingContext ctx) {
    ctx.response()
      .putHeader("content-type", "text/plain")
      .setStatusCode(HttpResponseStatus.NO_CONTENT.code())
      .end("App running!");
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
//    TODO: Add error message in a standard json format
  }

}
