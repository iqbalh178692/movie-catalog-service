package com.app.moviecatalog.v1.http.handler;

import com.app.moviecatalog.v1.domain.Movie;
import com.app.moviecatalog.v1.domain.Show;
import com.app.moviecatalog.v1.service.show.ShowService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

import java.util.UUID;


@Component
@RequiredArgsConstructor
public class ShowHandler {
    private final ShowService showService;

    public Mono<ServerResponse> getShows(ServerRequest request) {

        final UUID movieId = UUID.fromString(
            request.pathVariable("movieId"));

        final String city = request.queryParam("city")
            .orElseThrow(() ->
                new RuntimeException("City is required"));

        return ServerResponse.ok()
            .body(showService.getShows(movieId, city), Object.class);
    }

    //ADMIN only
    public Mono<ServerResponse> create(ServerRequest request) {
        return request.bodyToMono(Show.class)
            .flatMap(showService::createShow)
            .flatMap(show -> ServerResponse.ok().bodyValue(show));
    }

}
