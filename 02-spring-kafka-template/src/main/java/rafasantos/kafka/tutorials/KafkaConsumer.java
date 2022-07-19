package rafasantos.kafka.tutorials;

import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.listener.ContainerProperties;
import org.springframework.kafka.listener.KafkaMessageListenerContainer;
import org.springframework.kafka.listener.MessageListener;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.Map;

import static org.apache.kafka.clients.consumer.ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG;
import static rafasantos.kafka.tutorials.ConfigValues.BOOTSTRAP_SERVERS;
import static rafasantos.kafka.tutorials.ConfigValues.TOPIC_NAME;

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
