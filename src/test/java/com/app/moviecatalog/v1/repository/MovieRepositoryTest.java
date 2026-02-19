package com.app.moviecatalog.v1.repository;

import com.app.moviecatalog.v1.config.TestConfig;
import com.app.moviecatalog.v1.domain.Movie;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.r2dbc.DataR2dbcTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import reactor.test.StepVerifier;

@DataR2dbcTest
@ActiveProfiles("test")
@Import(TestConfig.class)
@DisplayName("MovieRepository Integration Tests")
class MovieRepositoryTest {
    @Autowired
    private MovieRepository movieRepository;

    @BeforeEach
    void setUp() {
        // Clear and seed the database before each test to ensure a clean state
        movieRepository.deleteAll()
            .thenMany(movieRepository.saveAll(List.of(
                new Movie(UUID.randomUUID(), "The Dark Knight", "English", "Action", 120, LocalDate.of(2002,11,22), false),
                new Movie(UUID.randomUUID(), "Avenger", "English", "Action", 120, LocalDate.of(2020,11,22), true),
                new Movie(UUID.randomUUID(), "Sadak2", "Hindi", "Romantic", 120, LocalDate.of(2023,11,22), true)
            )))
            .blockLast();
    }

    @Test
    void findByActiveTrue_ShouldReturnOnlyActiveMovies() {
        movieRepository.findByActiveTrue()
            .as(StepVerifier::create) // Verify async stream
            .expectNextCount(2)
            .verifyComplete();
    }

    @Test
    void findByTitleContainingIgnoreCaseAndActiveTrue_ShouldFilterCorrectly() {
        // Test case-insensitive search and "active" filter
        movieRepository.findByTitleContainingIgnoreCaseAndActiveTrue("sadak2")
            .as(StepVerifier::create)
            .expectNextMatches(movie -> {
                return movie.getTitle().equals("Sadak2");
            })
            .verifyComplete();
    }

    @Test
    void findByTitleContainingIgnoreCaseAndActiveTrue_ShouldReturnEmpty_WhenNoMatch() {
        movieRepository.findByTitleContainingIgnoreCaseAndActiveTrue("Non-existent")
            .as(StepVerifier::create)
            .expectNextCount(0)
            .verifyComplete();
    }

}
