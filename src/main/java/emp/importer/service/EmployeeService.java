package emp.importer.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.wixpress.dst.greyhound.java.GreyhoundProducer;
import emp.importer.dao.EmployeeDao;
import emp.importer.payload.Employee;
import emp.importer.payload.KafkaPayload;
import emp.importer.utils.Utils;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.vertx.core.Handler;
import io.vertx.ext.web.RoutingContext;
import io.vertx.mysqlclient.MySQLPool;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.header.Header;
import org.apache.kafka.common.header.internals.RecordHeader;
import org.apache.kafka.common.serialization.StringSerializer;

import java.util.ArrayList;
import java.util.List;

import static emp.importer.utils.Constants.*;
import static emp.importer.utils.Utils.serializeKafkaPayload;

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
//      new RecordHeaders().add("type", COMPONENT_UPDATE.toCharArray.map(_.toByte))
      Employee emp = Utils.deserializeEmployee(ctx.getBodyAsString());
      EmployeeDao
        .updateEmployeeAsync(emp, dbClient)
        .map(rows -> {
          List<Header> headersList = new ArrayList<>();
          headersList.add(new RecordHeader(OBJECT_TYPE, OBJECT_TYPE_EMP.getBytes()));
          headersList.add(new RecordHeader(EVENT_TYPE, EVENT_TYPE_HEADER_UPDATE.getBytes()));
          KafkaPayload kafkaPayload = new KafkaPayload(emp.getEmployeeId(), OBJECT_TYPE_EMP, EVENT_TYPE_HEADER_UPDATE, emp);
          return kafkaProducer.produce(
            new ProducerRecord<>(EMP_TOPIC_V1, null, null, emp.getEmployeeId(),
              serializeKafkaPayload(kafkaPayload), headersList),
            new StringSerializer(),
            new StringSerializer());
        })
        .onSuccess(v ->
          ctx.response()
            .putHeader("content-type", "application/json")
            .setStatusCode(HttpResponseStatus.NO_CONTENT.code())
            .end()
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
