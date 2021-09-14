package emp.importer;

import com.wixpress.dst.greyhound.java.GreyhoundProducer;
import emp.importer.service.EmployeeService;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Promise;
import io.vertx.core.http.HttpMethod;
import io.vertx.core.http.HttpServer;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.handler.BodyHandler;
import io.vertx.mysqlclient.MySQLPool;

import static emp.importer.utils.Constants.EMP_TOPIC_V1;
import static emp.importer.utils.DBConnectionUtils.connectOptions;
import static emp.importer.utils.DBConnectionUtils.poolOptions;
import static emp.importer.utils.KafkaUtils.createProducer;
import static emp.importer.utils.KafkaUtils.createTopics;

public class MainVerticle extends AbstractVerticle {

  MySQLPool dbClient;
  GreyhoundProducer kafkaProducer;

  @Override
  public void start(Promise<Void> startPromise) {
    HttpServer server = vertx.createHttpServer();
    Router router = Router.router(vertx);
    router.route().handler(BodyHandler.create());

    //DB connection
    dbClient = MySQLPool.pool(vertx, connectOptions, poolOptions);

    //Kafka configs
    createTopics(new String[]{EMP_TOPIC_V1});
    kafkaProducer = createProducer();

    //Routers
    router.route(HttpMethod.POST, "/v1/employees").handler(
      ctx -> EmployeeService.postEmployeesRoute(ctx, dbClient)
    );
    router.route(HttpMethod.PUT, "/v1/employee").handler(
      ctx -> EmployeeService.putEmployeeRoute(ctx, dbClient, kafkaProducer)
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
    super.stop(stopFuture);
    dbClient.close();
    kafkaProducer.close();
    System.out.println("Verticle stopped!");
  }

}
