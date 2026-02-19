package com.app.moviecatalog.v1.service.show;

import com.app.moviecatalog.v1.domain.Show;
import com.app.moviecatalog.v1.eventsream.model.ShowCreatedEvent;
import com.app.moviecatalog.v1.eventsream.publisher.ShowEventPublisher;
import com.app.moviecatalog.v1.repository.ShowRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ShowServiceImpl implements ShowService{

    private final ShowRepository showRepository;
    private final ShowEventPublisher publisher;

    @Override
    public Flux<Show> getShows(UUID movieId, String city) {
        return showRepository.findShowsByMovieAndCity(movieId, city);
    }

    public Mono<Show> createShow(Show show) {

        show.setId(UUID.randomUUID());

        return showRepository.save(show)
            .doOnSuccess(saved -> {
                ShowCreatedEvent event =
                    ShowCreatedEvent.builder()
                        .showId(saved.getId())
                        .movieId(saved.getMovieId())
                        .screenId(saved.getScreenId())
                        .showTime(saved.getShowTime())
                        .build();

                publisher.publish(event);
            });
    }
}
