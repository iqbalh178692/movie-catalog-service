package com.app.moviecatalog.v1.repository;

import com.app.moviecatalog.v1.domain.Movie;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;

import java.util.UUID;

public interface MovieRepository
        extends ReactiveCrudRepository<Movie, UUID> {

    Flux<Movie> findByActiveTrue();

    Flux<Movie> findByTitleContainingIgnoreCaseAndActiveTrue(String title);
}
