package com.app.moviecatalog.v1.service.screen;

import com.app.moviecatalog.v1.domain.Screen;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

public interface ScreenService {
    Mono<Screen> create(Screen screen);
    Flux<Screen> getByTheatre(UUID theatreId);
}
