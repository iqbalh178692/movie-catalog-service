package com.app.moviecatalog.v1.http.handler;

import com.app.moviecatalog.v1.domain.Movie;
import com.app.moviecatalog.v1.service.movie.MovieService;
import com.app.moviecatalog.v1.service.show.ShowService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class MovieHandler {

    private final MovieService movieService;
    private final ShowService showService;

    public Mono<ServerResponse> getAllMovies(ServerRequest request) {
        return ServerResponse.ok()
                .body(movieService.getAllActiveMovies(), Object.class);
    }

    public Mono<ServerResponse> searchMovies(ServerRequest request) {

        final String title = request.queryParam("title").orElse("");

        return ServerResponse.ok()
                .body(movieService.searchMovies(title), Movie.class);
    }

    public Mono<ServerResponse> getMovie(ServerRequest request) {

        final UUID id = UUID.fromString(request.pathVariable("id"));

        return movieService.getMovieById(id)
                .flatMap(movie ->
                        ServerResponse.ok().bodyValue(movie));
    }


    //ADMIN only
    public Mono<ServerResponse> create(ServerRequest request) {
        return request.bodyToMono(Movie.class)
            .flatMap(movieService::createMovie)
            .flatMap(movie -> ServerResponse.ok().bodyValue(movie));
    }
}
