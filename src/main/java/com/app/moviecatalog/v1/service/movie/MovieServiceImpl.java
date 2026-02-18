package com.app.moviecatalog.v1.service.movie;

import com.app.moviecatalog.v1.domain.Movie;
import com.app.moviecatalog.v1.exception.MovieNotFoundException;
import com.app.moviecatalog.v1.repository.MovieRepository;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class MovieServiceImpl implements MovieService{

    private final MovieRepository movieRepository;

    @Override
    public Flux<Movie> getAllActiveMovies() {
        return movieRepository.findByActiveTrue();
    }

    @Override
    public Flux<Movie> searchMovies(String title) {
        return movieRepository
            .findByTitleContainingIgnoreCaseAndActiveTrue(title);
    }

    @Override
    public Mono<Movie> getMovieById(UUID id) {
        return movieRepository.findById(id)
            .switchIfEmpty(Mono.error(
                new MovieNotFoundException("Movie not found")));
    }

    @Override
    public Mono<Movie> createMovie(Movie movie) {
        movie.setId(UUID.randomUUID());
        return movieRepository.save(movie);
    }
}
