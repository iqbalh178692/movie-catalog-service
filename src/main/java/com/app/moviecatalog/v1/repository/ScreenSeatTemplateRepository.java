package com.app.moviecatalog.v1.repository;

import com.app.moviecatalog.v1.domain.ScreenSeat;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;

import java.util.UUID;

@Repository
public interface ScreenSeatTemplateRepository
        extends ReactiveCrudRepository<ScreenSeat, UUID> {

    Flux<ScreenSeat> findByScreenId(UUID screenId);
}
