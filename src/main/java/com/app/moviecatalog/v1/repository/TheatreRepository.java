package com.app.moviecatalog.v1.repository;


import com.app.moviecatalog.v1.domain.Theatre;
import java.util.UUID;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;

public interface TheatreRepository
        extends ReactiveCrudRepository<Theatre, UUID> {

    Flux<Theatre> findByCityIgnoreCase(String city);
}
