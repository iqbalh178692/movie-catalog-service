package com.app.moviecatalog.v1.service.show;

import com.app.moviecatalog.v1.domain.Show;
import com.app.moviecatalog.v1.repository.ShowRepository;
import com.app.moviecatalog.v1.repository.TheatreRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ShowServiceImpl implements ShowService{

    private final TheatreRepository theatreRepository;
    private final ShowRepository showRepository;

    @Override
    public Flux<Show> getShows(UUID movieId, String city) {
        return theatreRepository.findByCityIgnoreCase(city)
            .map(t -> t.getId())
            .collectList()
            .flatMapMany(ids ->
                showRepository
                    .findByMovieIdAndTheatreIdIn(movieId, ids));
    }
}
