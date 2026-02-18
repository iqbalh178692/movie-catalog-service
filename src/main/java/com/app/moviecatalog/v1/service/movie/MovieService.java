package com.app.moviecatalog.v1.service.movie;

import com.app.moviecatalog.v1.domain.Movie;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

public interface MovieService {
    Flux<Movie> getAllActiveMovies();
    Flux<Movie> searchMovies(String title);
    Mono<Movie> getMovieById(UUID id);
    Mono<Movie> createMovie(Movie movie);
}
