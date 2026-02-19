package com.app.moviecatalog.v1.repository;

import com.app.moviecatalog.v1.config.TestConfig;
import com.app.moviecatalog.v1.domain.Theatre;
import com.app.moviecatalog.v1.eventsream.publisher.ShowEventPublisher;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.r2dbc.DataR2dbcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import reactor.test.StepVerifier;

import java.util.List;
import java.util.UUID;

@DataR2dbcTest
@ActiveProfiles("test")
@Import(TestConfig.class)
@DisplayName("TheatreRepository Tests")
public class TheatreRepositoryTest {
    @Autowired
    private TheatreRepository theatreRepository;

    @BeforeEach
    void setUp() {
        // Seed data: mix of cases for the same city
        var theatres = List.of(
            new Theatre(UUID.randomUUID(), "Grand Cinema", "NEW YORK"),
            new Theatre(UUID.randomUUID(), "Indie Films", "New York"),
            new Theatre(UUID.randomUUID(), "Cineplex", "Chicago")
        );

        theatreRepository.deleteAll()
            .thenMany(theatreRepository.saveAll(theatres))
            .blockLast(); // Ensures DB is ready before tests run
    }

    @Test
    void findByCityIgnoreCase_ShouldReturnMatches_RegardlessOfCase() {
        // Test with lowercase input for uppercase/mixed-case stored data
        theatreRepository.findByCityIgnoreCase("new york")
            .as(StepVerifier::create)
            .expectNextCount(2) // Should find both "NEW YORK" and "New York"
            .verifyComplete();
    }

    @Test
    void findByCityIgnoreCase_ShouldReturnEmpty_WhenCityNotFound() {
        theatreRepository.findByCityIgnoreCase("Los Angeles")
            .as(StepVerifier::create)
            .expectNextCount(0)
            .verifyComplete();
    }
}

