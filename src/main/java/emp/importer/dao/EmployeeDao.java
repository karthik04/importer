package emp.importer.dao;

import emp.importer.payload.Employee;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.ext.web.RoutingContext;
import io.vertx.mysqlclient.MySQLPool;
import io.vertx.sqlclient.Row;
import io.vertx.sqlclient.RowSet;
import io.vertx.sqlclient.SqlConnection;
import io.vertx.sqlclient.Tuple;

import java.util.ArrayList;
import java.util.List;

public class EmployeeDao {

  public static void insertEmployees(List<Employee> empList, MySQLPool dbClient) {
    List<Tuple> batch = new ArrayList<>();
    for (Employee emp : empList) {
      batch.add(Tuple.of(emp.getEmployeeId(), emp.getFirstName(), emp.getLastName(), emp.getEmail(), emp.getLocation()));
    }
    dbClient
      .preparedQuery("INSERT INTO Employee (EmployeeId, FirstName, LastName, Email, Location) VALUES (?, ?, ?, ?, ?)")
      .executeBatch(batch, res -> {
        if (res.succeeded()) {
          RowSet<Row> rows = res.result();
        } else {
          System.out.println("Batch failed " + res.cause());
        }
      });
  }

  public static Future<SqlConnection> insertEmployeesAsync(List<Employee> empList, MySQLPool dbClient,
                                                           Handler<Void> successHandler, Handler<Throwable> errorHandler) {
    List<Tuple> batch = new ArrayList<>();
    for (Employee emp : empList) {
      batch.add(Tuple.of(emp.getEmployeeId(), emp.getFirstName(), emp.getLastName(), emp.getEmail(), emp.getLocation()));
    }

    return dbClient.getConnection()
      .onSuccess(conn -> {
        conn.begin()
          .compose(tx -> conn
            .preparedQuery("INSERT INTO Employee (EmployeeId, FirstName, LastName, Email, Location) VALUES (?, ?, ?, ?, ?)")
            .executeBatch(batch)
            .compose(res3 -> tx.commit()))
          .eventually(v -> conn.close())
          .onSuccess(successHandler)
          .onFailure(errorHandler);
      });
  }


}
