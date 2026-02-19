package com.app.moviecatalog.v1.service.show;

import com.app.moviecatalog.v1.domain.Show;
import com.app.moviecatalog.v1.domain.Theatre;
import com.app.moviecatalog.v1.eventsream.model.ShowCreatedEvent;
import com.app.moviecatalog.v1.eventsream.publisher.ShowEventPublisher;
import com.app.moviecatalog.v1.repository.ShowRepository;
import com.app.moviecatalog.v1.repository.TheatreRepository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("ShowServiceImpl Unit Tests")
class ShowServiceImplTest {
    @Mock
    private ShowRepository showRepository;

    @Mock
    private ShowEventPublisher publisher;

    @InjectMocks
    private ShowServiceImpl showService;

    @Test
    void getShows_shouldReturnFluxOfShows() {
        // Arrange
        UUID movieId = UUID.randomUUID();
        String city = "London";
        Show show = Show.builder().id(UUID.randomUUID()).movieId(movieId).build();

        when(showRepository.findShowsByMovieAndCity(movieId, city))
            .thenReturn(Flux.just(show));

        // Act & Assert
        StepVerifier.create(showService.getShows(movieId, city))
            .expectNext(show)
            .verifyComplete();

        verify(showRepository).findShowsByMovieAndCity(movieId, city);
    }

    @Test
    void createShow_shouldSaveAndPublishEvent() {
        // Arrange
        Show inputShow = Show.builder()
            .movieId(UUID.randomUUID())
            .screenId(UUID.randomUUID())
            .showTime(LocalDateTime.now())
            .price(BigDecimal.TEN)
            .build();

        // Repository returns the show with the UUID set by service
        when(showRepository.save(any(Show.class)))
            .thenAnswer(invocation -> Mono.just(invocation.getArgument(0)));

        // Act & Assert
        StepVerifier.create(showService.createShow(inputShow))
            .assertNext(savedShow -> {
                assert savedShow.getId() != null;
                assert savedShow.getMovieId().equals(inputShow.getMovieId());
            })
            .verifyComplete();

        // Verify interactions
        verify(showRepository, times(1)).save(any(Show.class));
        verify(publisher, times(1)).publish(any(ShowCreatedEvent.class));
    }
}