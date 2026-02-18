package com.app.moviecatalog.v1.repository;

import com.app.moviecatalog.v1.domain.Show;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.r2dbc.DataR2dbcTest;
import org.springframework.test.context.ActiveProfiles;
import reactor.test.StepVerifier;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@DataR2dbcTest
@ActiveProfiles("test")
@DisplayName("ShowRepository Tests")
public class ShowRepositoryTest {

    @Autowired
    private ShowRepository showRepository;

    private UUID movieId;
    private UUID theatreId1;
    private UUID theatreId2;

    @BeforeEach
    void setUp() {
        movieId = UUID.randomUUID();
        theatreId1 = UUID.randomUUID();
        theatreId2 = UUID.randomUUID();

        // Clear and seed data
        var shows = List.of(
            Show
                .builder()
                .id(UUID.randomUUID())
                .movieId(movieId)
                .theatreId(theatreId1)
                .showTime(LocalDateTime.now())
                .build(),
            Show
                .builder()
                .id(UUID.randomUUID())
                .movieId(movieId)
                .theatreId(theatreId2)
                .showTime(LocalDateTime.now())
                .build(),
            Show
                .builder()
                .id(UUID.randomUUID())
                .movieId(UUID.randomUUID())
                .theatreId(theatreId1)
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

    @Test
    void findByMovieIdAndTheatreIdIn_ShouldFilterCorrectly() {
        showRepository.findByMovieIdAndTheatreIdIn(movieId, List.of(theatreId1))
            .as(StepVerifier::create)
            .assertNext(show -> {
                assert show.getMovieId().equals(movieId);
                assert show.getTheatreId().equals(theatreId1);
            })
            .expectNextCount(0)
            .verifyComplete();
    }

    @Test
    void findByMovieIdAndTheatreIdIn_ShouldReturnEmpty_WhenNoMatch() {
        showRepository.findByMovieIdAndTheatreIdIn(movieId, List.of(UUID.randomUUID()))
            .as(StepVerifier::create)
            .expectNextCount(0)
            .verifyComplete();
    }
}
