package com.app.moviecatalog.v1.service.screen_seat;

import com.app.moviecatalog.v1.domain.ScreenSeat;
import com.app.moviecatalog.v1.repository.ScreenSeatTemplateRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Service
@RequiredArgsConstructor

public class ScreentSeatServiceImpl implements ScreentSeatService{

    private final ScreenSeatTemplateRepository repository;

    @Override
    public Mono<ScreenSeat> create(ScreenSeat seat) {
        seat.setId(UUID.randomUUID());
        return repository.save(seat);
    }

    @Override
    public Flux<ScreenSeat> getByScreen(UUID screenId) {
        return repository.findByScreenId(screenId);
    }
}
