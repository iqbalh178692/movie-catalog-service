package com.app.moviecatalog.v1.http.handler;

import com.app.moviecatalog.v1.domain.ScreenSeat;
import com.app.moviecatalog.v1.service.screen_seat.ScreentSeatService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class ScreenSeatTemplateHandler {

    private final ScreentSeatService seatService;

    // ADMIN
    public Mono<ServerResponse> create(ServerRequest request) {

        return request.bodyToMono(ScreenSeat.class)
                .flatMap(seatService::create)
                .flatMap(saved ->
                        ServerResponse.ok().bodyValue(saved));
    }

    // ADMIN + USER
    public Mono<ServerResponse> getByScreen(ServerRequest request) {

        UUID screenId = UUID.fromString(
                request.pathVariable("screenId"));

        return ServerResponse.ok()
                .body(seatService.getByScreen(screenId),
                        ScreenSeat.class);
    }
}
