package emp.importer;

import com.wixpress.dst.greyhound.core.CleanupPolicy;
import com.wixpress.dst.greyhound.future.AdminClient;
import com.wixpress.dst.greyhound.java.*;
import com.wixpress.dst.greyhound.core.admin.AdminClientConfig;
import com.wixpress.dst.greyhound.core.TopicConfig;
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

import java.time.Duration;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import scala.collection.immutable.HashMap;

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


  public static final String BOOT_START_SERVERS = "localhost:9092";
  private static final HashMap<String, String> EMPTY_MAP = new HashMap<>();
  private static final Set<String> servers = Stream.of(BOOT_START_SERVERS).collect(Collectors.toCollection(HashSet::new));
  private static final GreyhoundConfig config = new GreyhoundConfig(servers);
  GreyhoundProducer kafkaProducer;

  @Override
  public void start(Promise<Void> startPromise) throws Exception {
    HttpServer server = vertx.createHttpServer();
    Router router = Router.router(vertx);
    router.route().handler(BodyHandler.create());

    dbClient = MySQLPool.pool(vertx, connectOptions, poolOptions);

    createTopics(new String[]{
      "test"
    }); //Not necessary for topic with default configurations
    kafkaProducer = createProducer(config);

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
    //must call super.stop() or call stopFuture.complete()
    super.stop(stopFuture);
    dbClient.close();
    kafkaProducer.close();
    System.out.println("Verticle stopped!");
  }

  /// Greyhound Configurations ///
  private static GreyhoundProducer createProducer(GreyhoundConfig config) {
    return new GreyhoundProducerBuilder(config).build();
  }

  static void createTopics(String[] topics) {
    AdminClient adminClient = AdminClient.create(new AdminClientConfig(BOOT_START_SERVERS, EMPTY_MAP));
    for (String topic : topics) {
      adminClient.createTopic(new TopicConfig(
        topic,
        1, //For quick testing partition is kept as 1, but should be increased for production use
        1,
        new CleanupPolicy.Delete(Duration.ofHours(1).toMillis()),
        EMPTY_MAP)).isCompleted();
    }
  }

}
