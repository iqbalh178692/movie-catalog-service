package com.app.moviecatalog.v1.repository;

import java.util.List;
import java.util.UUID;
import com.app.moviecatalog.v1.domain.Show;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;

@Repository
public interface ShowRepository
    extends ReactiveCrudRepository<Show, UUID> {

    Flux<Show> findByMovieId(UUID movieId);

    @Query("""
        SELECT s.*
        FROM shows s
        JOIN screens sc ON s.screen_id = sc.id
        JOIN theatres t ON sc.theatre_id = t.id
        WHERE s.movie_id = :movieId
        AND LOWER(t.city) = LOWER(:city)
        """)
    Flux<Show> findShowsByMovieAndCity(UUID movieId, String city);
}
