package com.app.moviecatalog.v1.http.handler;

import com.app.moviecatalog.v1.domain.Screen;
import com.app.moviecatalog.v1.service.screen.ScreenService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class ScreenHandler {

    private final ScreenService screenService;

    // ADMIN
    public Mono<ServerResponse> create(ServerRequest request) {
        return request.bodyToMono(Screen.class)
                .flatMap(screenService::create)
                .flatMap(saved ->
                        ServerResponse.ok().bodyValue(saved));
    }

    // ADMIN + USER
    public Mono<ServerResponse> getByTheatre(ServerRequest request) {

        UUID theatreId = UUID.fromString(
                request.pathVariable("theatreId"));

        return ServerResponse.ok()
                .body(screenService.getByTheatre(theatreId),
                        Screen.class);
    }
}
