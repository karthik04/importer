package emp.importer.utils;

import io.vertx.mysqlclient.MySQLConnectOptions;
import io.vertx.sqlclient.PoolOptions;

import java.util.Optional;

/**
 * DB connection configs
 */
public class DBConnectionUtils {

  private static final String HOST = Optional.ofNullable(System.getenv("DB_HOST")).orElse("localhost");
  private static final String DB = Optional.ofNullable(System.getenv("DB_NAME")).orElse("dev");
  private static final String PORT = Optional.ofNullable(System.getenv("DB_PORT")).orElse("3306");
  private static final String USER = Optional.ofNullable(System.getenv("DB_USER")).orElse("root");
  private static final String PWD = Optional.ofNullable(System.getenv("DB_PWD")).orElse("root");

  public static MySQLConnectOptions connectOptions =
    new MySQLConnectOptions()
      .setPort(Integer.parseInt(PORT))
      .setHost(HOST)
      .setDatabase(DB)
      .setUser(USER)
      .setPassword(PWD);

  public static PoolOptions poolOptions = new PoolOptions()
    .setMaxSize(20);
}
