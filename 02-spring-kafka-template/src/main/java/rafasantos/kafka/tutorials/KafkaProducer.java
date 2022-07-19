package rafasantos.kafka.tutorials;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Stream;

import static rafasantos.kafka.tutorials.ConfigValues.MESSAGE_KEY;
import static rafasantos.kafka.tutorials.ConfigValues.TOPIC_NAME;

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
