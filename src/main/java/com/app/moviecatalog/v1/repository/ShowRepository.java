package com.app.moviecatalog.v1.repository;

import java.util.List;
import java.util.UUID;
import com.app.moviecatalog.v1.domain.Show;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;

public interface ShowRepository
        extends ReactiveCrudRepository<Show, UUID> {

    Flux<Show> findByMovieId(UUID movieId);

    Flux<Show> findByMovieIdAndTheatreIdIn(UUID movieId, List<UUID> theatreIds);
}
