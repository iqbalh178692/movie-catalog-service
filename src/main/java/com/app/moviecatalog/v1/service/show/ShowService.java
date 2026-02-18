package com.app.moviecatalog.v1.service.show;

import com.app.moviecatalog.v1.domain.Show;
import reactor.core.publisher.Flux;

import java.util.UUID;

public interface ShowService {
    Flux<Show> getShows(UUID movieId, String city);
}
