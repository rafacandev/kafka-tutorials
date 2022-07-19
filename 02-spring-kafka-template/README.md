Tutorial 2: A simple spring boot with KafkaTemplate
---------------------------------------------------
In this tutorial we are going to run a Spring Boot application which is going to produce messages to a Kafka topic
with `KafkaTemplate` and consume the messages just produced with `KafkaMessageListenerContainer`.

This is a simple and somewhat low level to interact with Kafka, later we are going to see other ways to interact with Kafka
with Spring Boot.

Prerequisites
-------------
Docker and docker compose installed in your computer.
See [docker engine installation](https://docs.docker.com/engine/install/).

Gradle. See [Gradle Install](https://gradle.org/install/)

Java JDK 18. See [Java Downloads](https://www.oracle.com/java/technologies/downloads/)

Note: For installing Gradle and JDK 18 I strongly recommend installing them via [sdkman](https://sdkman.io/) which manages the installation of various development tools.


Run Kafka Cluster
---------------------
Run our cluster: 

```bash
docker compose -f docker-compose.yml up
```

Run Spring Boot
-----------------
Run spring boot application:

```bash
./gradlew bootRun
```

Implementation
--------------
Our application produces a few messages and consumes these same messages.
The implementation for producing messages is found in `KafkaProducer.java`.
The implementation for consuming messages if found in `KafkaConsumer.java`.
Meanwhile, `ConfigValues.java` contains some configuration values used in this example.


### ConfigValues
The [ConfigValues](./src/main/java/rafasantos/kafka/tutorials/ConfigValues.java) is a simple class that holds
any configuration values we may use in this application:
```java
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
```

### KafkaProducer
The [KafkaProducer.java](./src/main/java/rafasantos/kafka/tutorials/KafkaProducer.java) produces messages into a Kafka topic:
```java
@Component
@AllArgsConstructor
@Slf4j
public class KafkaProducer {
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.RFC_1123_DATE_TIME;
    // Messages to be sent from this producer
    private static final List<String> MESSAGES_TO_SEND =
            Stream.of("First", "Second", "Third", "Forth")
                    .map(m -> String.format("%s message at %s", m, ZonedDateTime.now().format(DATE_FORMATTER)))
                    .toList();

    // Autowire a default KafkaTemplate
    private KafkaTemplate<String, String> kafkaTemplate;

    /**
     * This method is annotated with '@PostConstruct' which tells spring to call this method once this
     * class is instantiated by spring.
     */
    @PostConstruct
    public void sendMessages() throws InterruptedException {
        log.info("Sending a total of {} messages.", MESSAGES_TO_SEND.size());
        for (String messageToSend : MESSAGES_TO_SEND) {
            Thread.sleep(5000); // Just for better visualization when running the application
            log.info("Sending message: {}", messageToSend);
            kafkaTemplate.send(TOPIC_NAME, MESSAGE_KEY, messageToSend);
        }
    }
}
```

Here are the relevant logs from the producer side.
Note that `bootstrap.servers = [localhost:9092]` was never set in our application;
it is the default value which worked well in our example.  
```log
o.a.k.c.producer.ProducerConfig - ProducerConfig values: 
        bootstrap.servers = [localhost:9092]
...
r.kafka.tutorials.KafkaProducer - Sending a total of 4 messages.
r.kafka.tutorials.KafkaProducer - Sending message: First message at Mon, 18 Jul 2022 18:54:46 -0400
r.kafka.tutorials.KafkaProducer - Sending message: Second message at Mon, 18 Jul 2022 18:54:53 -0400
r.kafka.tutorials.KafkaProducer - Sending message: Third message at Mon, 18 Jul 2022 18:54:58 -0400
r.kafka.tutorials.KafkaProducer - Sending message: Forth message at Mon, 18 Jul 2022 18:55:03 -0400
```

### KafkaConsumer
The [KafkaConsumer.java](./src/main/java/rafasantos/kafka/tutorials/KafkaConsumer.java) consumes messages from a Kafka topic:

```java
@Slf4j
@Component
public class KafkaConsumer {

    /**
     * This method is annotated with '@PostConstruct' which tells spring to call this method once this
     * class is instantiated by spring.
     */
    @PostConstruct
    public void consumeMessages() {
        val messageListener = messageListener();

        val containerProps = new ContainerProperties(TOPIC_NAME);
        containerProps.setMessageListener(messageListener);
        final Map<String, Object> consumerProps = Map.of(
                BOOTSTRAP_SERVERS_CONFIG, BOOTSTRAP_SERVERS,
                ConsumerConfig.GROUP_ID_CONFIG, ConfigValues.GROUP_ID,
                ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class,
                ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class
        );

        val consumerFactory = new DefaultKafkaConsumerFactory<>(consumerProps);
        /*
         * There are many considerations when unsing 'KafkaMessageListenerContainer', see the documentation for details:
         * https://docs.spring.io/spring-kafka/docs/current/reference/html/#message-listener-container
         *
         * Examples:
         * - The KafkaMessageListenerContainer receives all message from all topics or partitions on a single thread.
         * - You should not execute any methods that affect the consumer’s positions and or committed offsets in these interceptors; the container needs to manage such information.
         * - If the interceptor mutates the record (by creating a new one), the topic, partition, and offset must remain the same to avoid unexpected side effects such as record loss.
         */
        val container = new KafkaMessageListenerContainer<>(consumerFactory, containerProps);
        container.start();
    }

    /**
     * There are several implementations of `MessageListener`, take a look at the documentation:
     * https://docs.spring.io/spring-kafka/docs/current/reference/html/#receiving-messages
     */
    private MessageListener<String, String> messageListener() {
        return message -> log.info("Message received. key: {}, value: {}", message.key(), message.value());
    }
}
```

Here are some relevant logs from the consumer side.
Note that `bootstrap.servers = [localhost:9092]` and `allow.auto.create.topics = true` was never set in our application;
it is the default value which worked well in our example.
```log
o.a.k.c.consumer.ConsumerConfig - ConsumerConfig values: 
        allow.auto.create.topics = true
        bootstrap.servers = [localhost:9092]
        group.id = tutorial-2-group-id
...
r.kafka.tutorials.KafkaConsumer - Message received. key: tutorial-2-key, value: First message at Mon, 18 Jul 2022 18:54:46 -0400
r.kafka.tutorials.KafkaConsumer - Message received. key: tutorial-2-key, value: Second message at Mon, 18 Jul 2022 18:54:53 -0400
r.kafka.tutorials.KafkaConsumer - Message received. key: tutorial-2-key, value: Third message at Mon, 18 Jul 2022 18:54:58 -0400
r.kafka.tutorials.KafkaConsumer - Message received. key: tutorial-2-key, value: Forth message at Mon, 18 Jul 2022 18:55:03 -0400
```
