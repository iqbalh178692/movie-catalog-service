package com.app.moviecatalog.v1.http.handler;


import com.app.moviecatalog.v1.domain.Movie;
import com.app.moviecatalog.v1.service.movie.MovieService;
import com.app.moviecatalog.v1.service.show.ShowService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.reactive.function.server.RouterFunctions;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@WebFluxTest(controllers = MovieHandler.class)
public class MovieHandlerTest {


    @Autowired
    private MovieHandler movieHandler;

    @MockBean
    private MovieService movieService;

    @MockBean
    private ShowService showService;

    // Use a manual router for isolated handler testing
    private WebTestClient getClient() {
        var router = RouterFunctions.route()
            .GET("/movies", movieHandler::getAllMovies)
            .GET("/movies/search", movieHandler::searchMovies)
            .GET("/movies/{id}", movieHandler::getMovie)
            .GET("/movies/{movieId}/shows", movieHandler::getShows)
            .POST("/movies", movieHandler::create)
            .build();
        return WebTestClient.bindToRouterFunction(router).build();
    }

    @Test
    void getAllMovies_ShouldReturnList() {
        when(movieService.getAllActiveMovies()).thenReturn(Flux.just(new Movie()));

        getClient().get().uri("/movies")
            .exchange()
            .expectStatus().isOk()
            .expectBodyList(Object.class);
    }

    @Test
    void searchMovies_ShouldPassQueryParam() {
        when(movieService.searchMovies("Inception")).thenReturn(Flux.empty());

        getClient().get().uri(uriBuilder -> uriBuilder.path("/movies/search")
                .queryParam("title", "Inception").build())
            .exchange()
            .expectStatus().isOk();
    }

    @Test
    void getMovie_ShouldReturnMovie_WhenIdValid() {
        UUID id = UUID.randomUUID();
        when(movieService.getMovieById(id)).thenReturn(Mono.just(new Movie()));

        getClient().get().uri("/movies/" + id)
            .exchange()
            .expectStatus().isOk();
    }

    @Test
    void getShows_ShouldThrowException_WhenCityMissing() {
        UUID movieId = UUID.randomUUID();

        getClient().get().uri("/movies/" + movieId + "/shows")
            .exchange()
            .expectStatus().is5xxServerError(); // Triggered by the RuntimeException
    }

    @Test
    void create_ShouldReturnCreatedMovie() {
        Movie movie = new Movie();
        when(movieService.createMovie(any(Movie.class))).thenReturn(Mono.just(movie));

        getClient().post().uri("/movies")
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(movie)
            .exchange()
            .expectStatus().isOk();
    }
}
