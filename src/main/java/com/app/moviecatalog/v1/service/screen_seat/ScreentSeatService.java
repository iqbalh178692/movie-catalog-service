package com.app.moviecatalog.v1.service.screen_seat;

import com.app.moviecatalog.v1.domain.ScreenSeat;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

public interface ScreentSeatService {
    Mono<ScreenSeat> create(ScreenSeat seat);
    Flux<ScreenSeat> getByScreen(UUID screenId);
}
