package com.app.moviecatalog.v1.eventsream.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class KafkaTopicConfig {

    @Bean
    public NewTopic showCreatedTopic() {
        return new NewTopic(
                "show.created",
                3,      // partitions
                (short) 1  // replication factor
        );
    }
}
