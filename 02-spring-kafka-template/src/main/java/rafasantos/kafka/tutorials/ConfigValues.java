package rafasantos.kafka.tutorials;

public class ConfigValues {
    // The kafka boostrapServers in our cluster
    public static final String BOOTSTRAP_SERVERS = "localhost:9092";

    // The groupId for our kafka consumer
    public static final String GROUP_ID = "tutorial-2-group-id";

    // The kafka topic we should produce and read from
    public static final String TOPIC_NAME = "tutorial-2-kafka-template";

    // The key for every message we produce into the kafka topic
    public static final String MESSAGE_KEY = "tutorial-2-key";
}
