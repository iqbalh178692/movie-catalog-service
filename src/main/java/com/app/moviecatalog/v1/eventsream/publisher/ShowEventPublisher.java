package com.app.moviecatalog.v1.eventsream.publisher;

import com.app.moviecatalog.v1.eventsream.model.ShowCreatedEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ShowEventPublisher {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    private static final String TOPIC = "show.created";

    public void publish(ShowCreatedEvent event) {

        kafkaTemplate.send(
                TOPIC,
                event.getShowId().toString(),
                event
        );
    }
}
