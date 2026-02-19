package com.app.moviecatalog.v1.http.handler;


import com.app.moviecatalog.v1.config.TestConfig;
import com.app.moviecatalog.v1.domain.Movie;
import com.app.moviecatalog.v1.service.movie.MovieService;
import com.app.moviecatalog.v1.service.show.ShowService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.reactive.function.server.RouterFunctions;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class MovieHandlerTest {

    private MovieHandler movieHandler;
    private WebTestClient webTestClient;

    @Mock
    private MovieService movieService;

    @Mock
    private ShowService showService;

    @BeforeEach
    void setUp() {
        // Create handler with mocked services
        movieHandler = new MovieHandler(movieService, showService);

        // Create router with handler routes
        var router = RouterFunctions.route()
            .GET("/movies", movieHandler::getAllMovies)
            .GET("/movies/search", movieHandler::searchMovies)
            .GET("/movies/{id}", movieHandler::getMovie)
            .POST("/movies", movieHandler::create)
            .build();

        // Create test client bound to router
        webTestClient = WebTestClient.bindToRouterFunction(router).build();
    }

    // ============ GET /movies Tests ============

    @Test
    @DisplayName("Should get all active movies successfully")
    void testGetAllMovies_Success() {
        Movie movie1 = new Movie();
        movie1.setId(UUID.randomUUID());
        movie1.setTitle("Inception");
        movie1.setActive(true);

        Movie movie2 = new Movie();
        movie2.setId(UUID.randomUUID());
        movie2.setTitle("The Matrix");
        movie2.setActive(true);

        when(movieService.getAllActiveMovies())
            .thenReturn(Flux.just(movie1, movie2));

        webTestClient.get()
            .uri("/movies")
            .exchange()
            .expectStatus().isOk()
            .expectBodyList(Movie.class)
            .hasSize(2);
    }

    @Test
    @DisplayName("Should handle service error when getting all movies")
    void testGetAllMovies_ServiceError() {
        when(movieService.getAllActiveMovies())
            .thenReturn(Flux.error(new RuntimeException("Service error")));

        webTestClient.get()
            .uri("/movies")
            .exchange()
            .expectStatus().is5xxServerError();
    }

    @Test
    @DisplayName("Should search movies by title successfully")
    void testSearchMovies_Success() {
        Movie movie = new Movie();
        movie.setId(UUID.randomUUID());
        movie.setTitle("Inception");
        movie.setActive(true);

        when(movieService.searchMovies("Inception"))
            .thenReturn(Flux.just(movie));

        webTestClient.get()
            .uri(uriBuilder -> uriBuilder.path("/movies/search")
                .queryParam("title", "Inception")
                .build())
            .exchange()
            .expectStatus().isOk()
            .expectBodyList(Movie.class)
            .hasSize(1);
    }

    // ============ GET /movies/{id} Tests ============

    @Test
    @DisplayName("Should get movie by ID successfully")
    void testGetMovie_Success() {
        UUID movieId = UUID.randomUUID();
        Movie movie = new Movie();
        movie.setId(movieId);
        movie.setTitle("Inception");
        movie.setActive(true);

        when(movieService.getMovieById(movieId))
            .thenReturn(Mono.just(movie));

        webTestClient.get()
            .uri("/movies/" + movieId)
            .exchange()
            .expectStatus().isOk()
            .expectBody(Movie.class)
            .isEqualTo(movie);
    }

    // ============ POST /movies Tests ============

    @Test
    @DisplayName("Should create movie successfully")
    void testCreateMovie_Success() {
        Movie inputMovie = new Movie();
        inputMovie.setTitle("New Movie");
        inputMovie.setActive(true);

        Movie createdMovie = new Movie();
        createdMovie.setId(UUID.randomUUID());
        createdMovie.setTitle("New Movie");
        createdMovie.setActive(true);

        when(movieService.createMovie(any(Movie.class)))
            .thenReturn(Mono.just(createdMovie));

        webTestClient.post()
            .uri("/movies")
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(inputMovie)
            .exchange()
            .expectStatus().isOk()
            .expectBody(Movie.class)
            .isEqualTo(createdMovie);
    }
}
