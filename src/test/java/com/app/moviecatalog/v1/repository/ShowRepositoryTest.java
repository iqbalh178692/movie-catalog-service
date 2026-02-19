package com.app.moviecatalog.v1.repository;

import com.app.moviecatalog.v1.config.TestConfig;
import com.app.moviecatalog.v1.domain.Show;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.r2dbc.DataR2dbcTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import reactor.test.StepVerifier;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@DataR2dbcTest
@ActiveProfiles("test")
@Import(TestConfig.class)
@DisplayName("ShowRepository Tests")
public class ShowRepositoryTest {

    @Autowired
    private ShowRepository showRepository;

    private UUID movieId;
    private UUID screenId1;
    private UUID screenId2;

    @BeforeEach
    void setUp() {
        movieId = UUID.randomUUID();
        screenId1 = UUID.randomUUID();
        screenId2 = UUID.randomUUID();

        // Clear and seed data
        var shows = List.of(
            Show
                .builder()
                .id(UUID.randomUUID())
                .movieId(movieId)
                .screenId(screenId1)
                .showTime(LocalDateTime.now())
                .build(),
            Show
                .builder()
                .id(UUID.randomUUID())
                .movieId(movieId)
                .screenId(screenId2)
                .showTime(LocalDateTime.now())
                .build(),
            Show
                .builder()
                .id(UUID.randomUUID())
                .movieId(UUID.randomUUID())
                .screenId(screenId1)
                .showTime(LocalDateTime.now())
                .build() // Different movie
        );

        showRepository.deleteAll()
            .thenMany(showRepository.saveAll(shows))
            .blockLast(); // Block only in setup
    }

    @Test
    void findByMovieId_ShouldReturnMatches() {
        showRepository.findByMovieId(movieId)
            .as(StepVerifier::create)
            .expectNextCount(2)
            .verifyComplete();
    }

}
