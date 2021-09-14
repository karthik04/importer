package emp.importer.utils;

import com.wixpress.dst.greyhound.core.CleanupPolicy;
import com.wixpress.dst.greyhound.core.TopicConfig;
import com.wixpress.dst.greyhound.core.admin.AdminClientConfig;
import com.wixpress.dst.greyhound.future.AdminClient;
import com.wixpress.dst.greyhound.java.GreyhoundConfig;
import com.wixpress.dst.greyhound.java.GreyhoundProducer;
import com.wixpress.dst.greyhound.java.GreyhoundProducerBuilder;
import scala.collection.immutable.HashMap;

import java.time.Duration;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class KafkaUtils {

  public static final String BOOT_START_SERVER = Optional.ofNullable(System.getenv("KAFKA_BROKER")).orElse("localhost:9092");;
  private static final HashMap<String, String> EMPTY_MAP = new HashMap<>();
  private static final Set<String> servers = Stream.of(BOOT_START_SERVER).collect(Collectors.toCollection(HashSet::new));
  private static final GreyhoundConfig config = new GreyhoundConfig(servers);

  public static GreyhoundProducer createProducer() {
    return new GreyhoundProducerBuilder(config).build();
  }

  /**
   * For quick testing partition size is kept as 1
   * Should be increased for production use.
   */
  public static void createTopics(String[] topics) {
    AdminClient adminClient = AdminClient.create(new AdminClientConfig(BOOT_START_SERVER, EMPTY_MAP));
    for (String topic : topics) {
      adminClient.createTopic(new TopicConfig(
        topic,
        1,
        1,
        new CleanupPolicy.Delete(Duration.ofHours(1).toMillis()),
        EMPTY_MAP)).isCompleted();
    }
  }
}
