package com.app.moviecatalog.v1.service.screen;

import com.app.moviecatalog.v1.domain.Screen;
import com.app.moviecatalog.v1.repository.ScreenRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ScreenServiceImpl implements ScreenService{
    private final ScreenRepository repository;

    @Override
    public Mono<Screen> create(Screen screen) {
        screen.setId(UUID.randomUUID());
        return repository.save(screen);
    }

    @Override
    public Flux<Screen> getByTheatre(UUID theatreId) {
        return repository.findByTheatreId(theatreId);
    }
}
