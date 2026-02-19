package com.app.moviecatalog.v1.repository;

import com.app.moviecatalog.v1.domain.Screen;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import java.util.UUID;

@Repository
public interface ScreenRepository extends ReactiveCrudRepository<Screen, UUID> {
    Flux<Screen> findByTheatreId(UUID theatreId);
}
