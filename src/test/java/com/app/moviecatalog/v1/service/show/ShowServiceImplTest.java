package com.app.moviecatalog.v1.service.show;

import com.app.moviecatalog.v1.domain.Show;
import com.app.moviecatalog.v1.domain.Theatre;
import com.app.moviecatalog.v1.repository.ShowRepository;
import com.app.moviecatalog.v1.repository.TheatreRepository;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

@ExtendWith(MockitoExtension.class)
@DisplayName("ShowServiceImpl Unit Tests")
class ShowServiceImplTest {

    @Mock
    private TheatreRepository theatreRepository;

    @Mock
    private ShowRepository showRepository;

    private ShowServiceImpl showService;

    @BeforeEach
    void setUp() {
        showService = new ShowServiceImpl(theatreRepository, showRepository);
    }

    // ============ getShows Tests ============

    @Test
    @DisplayName("Should get shows by movie ID and city successfully")
    void testGetShows_Success() {
        // Arrange
        UUID movieId = UUID.randomUUID();
        String city = "Mumbai";

        Theatre theatre1 = createTheatre("PVR", city);
        Theatre theatre2 = createTheatre("INOX", city);

        Show show1 = createShow(movieId, theatre1.getId());
        Show show2 = createShow(movieId, theatre2.getId());

        org.mockito.Mockito
            .when(theatreRepository.findByCityIgnoreCase(city))
            .thenReturn(Flux.just(theatre1, theatre2));

        org.mockito.Mockito
            .when(showRepository.findByMovieIdAndTheatreIdIn(
                movieId,
                Arrays.asList(theatre1.getId(), theatre2.getId())))
            .thenReturn(Flux.just(show1, show2));

        // Act & Assert
        StepVerifier.create(showService.getShows(movieId, city))
            .expectNext(show1)
            .expectNext(show2)
            .verifyComplete();

        org.mockito.Mockito.verify(theatreRepository).findByCityIgnoreCase(city);
        org.mockito.Mockito.verify(showRepository)
            .findByMovieIdAndTheatreIdIn(movieId, Arrays.asList(theatre1.getId(), theatre2.getId()));
    }

    @Test
    @DisplayName("Should perform case-insensitive city search")
    void testGetShows_CaseInsensitiveCity() {
        // Arrange
        UUID movieId = UUID.randomUUID();
        String city = "MUMBAI";

        Theatre theatre = createTheatre("PVR", "Mumbai");
        Show show = createShow(movieId, theatre.getId());

        org.mockito.Mockito
            .when(theatreRepository.findByCityIgnoreCase(city))
            .thenReturn(Flux.just(theatre));

        org.mockito.Mockito
            .when(showRepository.findByMovieIdAndTheatreIdIn(
                movieId,
                Arrays.asList(theatre.getId())))
            .thenReturn(Flux.just(show));

        // Act & Assert
        StepVerifier.create(showService.getShows(movieId, city))
            .expectNext(show)
            .verifyComplete();

        org.mockito.Mockito.verify(theatreRepository).findByCityIgnoreCase(city);
    }

    @Test
    @DisplayName("Should return empty flux when no theatres found in city")
    void testGetShows_NoTheatresInCity() {
        // Arrange
        UUID movieId = UUID.randomUUID();
        String city = "NonExistentCity";

        org.mockito.Mockito
            .when(theatreRepository.findByCityIgnoreCase(city))
            .thenReturn(Flux.empty());

        org.mockito.Mockito
            .when(showRepository.findByMovieIdAndTheatreIdIn(movieId, List.of()))
            .thenReturn(Flux.empty());

        // Act & Assert
        StepVerifier.create(showService.getShows(movieId, city))
            .verifyComplete();

        org.mockito.Mockito.verify(theatreRepository).findByCityIgnoreCase(city);
        org.mockito.Mockito.verify(showRepository)
            .findByMovieIdAndTheatreIdIn(movieId, List.of());
    }

    @Test
    @DisplayName("Should return empty flux when no shows found for movie in theatres")
    void testGetShows_NoShowsForMovie() {
        // Arrange
        UUID movieId = UUID.randomUUID();
        String city = "Mumbai";

        Theatre theatre1 = createTheatre("PVR", city);
        Theatre theatre2 = createTheatre("INOX", city);

        org.mockito.Mockito
            .when(theatreRepository.findByCityIgnoreCase(city))
            .thenReturn(Flux.just(theatre1, theatre2));

        org.mockito.Mockito
            .when(showRepository.findByMovieIdAndTheatreIdIn(
                movieId,
                Arrays.asList(theatre1.getId(), theatre2.getId())))
            .thenReturn(Flux.empty());

        // Act & Assert
        StepVerifier.create(showService.getShows(movieId, city))
            .verifyComplete();

        org.mockito.Mockito.verify(theatreRepository).findByCityIgnoreCase(city);
        org.mockito.Mockito.verify(showRepository)
            .findByMovieIdAndTheatreIdIn(movieId, Arrays.asList(theatre1.getId(), theatre2.getId()));
    }

    @Test
    @DisplayName("Should return multiple shows from multiple theatres")
    void testGetShows_MultipleShowsMultipleTheatres() {
        // Arrange
        UUID movieId = UUID.randomUUID();
        String city = "Bangalore";

        Theatre theatre1 = createTheatre("Cinepolis", city);
        Theatre theatre2 = createTheatre("Carnival", city);
        Theatre theatre3 = createTheatre("IMAX", city);

        Show show1 = createShow(movieId, theatre1.getId());
        Show show2 = createShow(movieId, theatre1.getId());
        Show show3 = createShow(movieId, theatre2.getId());
        Show show4 = createShow(movieId, theatre3.getId());

        org.mockito.Mockito
            .when(theatreRepository.findByCityIgnoreCase(city))
            .thenReturn(Flux.just(theatre1, theatre2, theatre3));

        org.mockito.Mockito
            .when(showRepository.findByMovieIdAndTheatreIdIn(
                movieId,
                Arrays.asList(theatre1.getId(), theatre2.getId(), theatre3.getId())))
            .thenReturn(Flux.just(show1, show2, show3, show4));

        // Act & Assert
        StepVerifier.create(showService.getShows(movieId, city))
            .expectNext(show1)
            .expectNext(show2)
            .expectNext(show3)
            .expectNext(show4)
            .verifyComplete();

        org.mockito.Mockito.verify(theatreRepository).findByCityIgnoreCase(city);
        org.mockito.Mockito.verify(showRepository)
            .findByMovieIdAndTheatreIdIn(
                movieId,
                Arrays.asList(theatre1.getId(), theatre2.getId(), theatre3.getId()));
    }

    @Test
    @DisplayName("Should handle theatre repository error")
    void testGetShows_TheatreRepositoryError() {
        // Arrange
        UUID movieId = UUID.randomUUID();
        String city = "Mumbai";

        org.mockito.Mockito
            .when(theatreRepository.findByCityIgnoreCase(city))
            .thenReturn(Flux.error(new RuntimeException("Theatre database error")));

        // Act & Assert
        StepVerifier.create(showService.getShows(movieId, city))
            .expectError(RuntimeException.class)
            .verify();

        org.mockito.Mockito.verify(theatreRepository).findByCityIgnoreCase(city);
    }

    @Test
    @DisplayName("Should handle show repository error")
    void testGetShows_ShowRepositoryError() {
        // Arrange
        UUID movieId = UUID.randomUUID();
        String city = "Delhi";

        Theatre theatre1 = createTheatre("PVR", city);
        Theatre theatre2 = createTheatre("INOX", city);

        org.mockito.Mockito
            .when(theatreRepository.findByCityIgnoreCase(city))
            .thenReturn(Flux.just(theatre1, theatre2));

        org.mockito.Mockito
            .when(showRepository.findByMovieIdAndTheatreIdIn(
                movieId,
                Arrays.asList(theatre1.getId(), theatre2.getId())))
            .thenReturn(Flux.error(new RuntimeException("Show database error")));

        // Act & Assert
        StepVerifier.create(showService.getShows(movieId, city))
            .expectError(RuntimeException.class)
            .verify();

        org.mockito.Mockito.verify(theatreRepository).findByCityIgnoreCase(city);
        org.mockito.Mockito.verify(showRepository)
            .findByMovieIdAndTheatreIdIn(movieId, Arrays.asList(theatre1.getId(), theatre2.getId()));
    }

    @Test
    @DisplayName("Should correctly collect theatre IDs before querying shows")
    void testGetShows_CollectTheatreIds() {
        // Arrange
        UUID movieId = UUID.randomUUID();
        String city = "Pune";

        UUID theatreId1 = UUID.randomUUID();
        UUID theatreId2 = UUID.randomUUID();
        UUID theatreId3 = UUID.randomUUID();

        Theatre theatre1 = new Theatre();
        theatre1.setId(theatreId1);
        theatre1.setName("PVR");
        theatre1.setCity(city);

        Theatre theatre2 = new Theatre();
        theatre2.setId(theatreId2);
        theatre2.setName("INOX");
        theatre2.setCity(city);

        Theatre theatre3 = new Theatre();
        theatre3.setId(theatreId3);
        theatre3.setName("Cinepolis");
        theatre3.setCity(city);

        Show show1 = createShow(movieId, theatreId1);
        Show show2 = createShow(movieId, theatreId2);

        org.mockito.Mockito
            .when(theatreRepository.findByCityIgnoreCase(city))
            .thenReturn(Flux.just(theatre1, theatre2, theatre3));

        org.mockito.Mockito
            .when(showRepository.findByMovieIdAndTheatreIdIn(
                movieId,
                Arrays.asList(theatreId1, theatreId2, theatreId3)))
            .thenReturn(Flux.just(show1, show2));

        // Act & Assert
        StepVerifier.create(showService.getShows(movieId, city))
            .expectNext(show1)
            .expectNext(show2)
            .verifyComplete();

        org.mockito.Mockito.verify(showRepository)
            .findByMovieIdAndTheatreIdIn(movieId, Arrays.asList(theatreId1, theatreId2, theatreId3));
    }

    @Test
    @DisplayName("Should handle single theatre with multiple shows")
    void testGetShows_SingleTheatreMultipleShows() {
        // Arrange
        UUID movieId = UUID.randomUUID();
        String city = "Chennai";

        Theatre theatre = createTheatre("Escape", city);

        Show show1 = createShow(movieId, theatre.getId());
        Show show2 = createShow(movieId, theatre.getId());
        Show show3 = createShow(movieId, theatre.getId());

        org.mockito.Mockito
            .when(theatreRepository.findByCityIgnoreCase(city))
            .thenReturn(Flux.just(theatre));

        org.mockito.Mockito
            .when(showRepository.findByMovieIdAndTheatreIdIn(
                movieId,
                Arrays.asList(theatre.getId())))
            .thenReturn(Flux.just(show1, show2, show3));

        // Act & Assert
        StepVerifier.create(showService.getShows(movieId, city))
            .expectNext(show1)
            .expectNext(show2)
            .expectNext(show3)
            .verifyComplete();

        org.mockito.Mockito.verify(theatreRepository).findByCityIgnoreCase(city);
        org.mockito.Mockito.verify(showRepository)
            .findByMovieIdAndTheatreIdIn(movieId, Arrays.asList(theatre.getId()));
    }

    @Test
    @DisplayName("Should handle different movie IDs correctly")
    void testGetShows_DifferentMovieIds() {
        // Arrange
        UUID movieId1 = UUID.randomUUID();
        UUID movieId2 = UUID.randomUUID();
        String city = "Hyderabad";

        Theatre theatre = createTheatre("Imax", city);

        Show showForMovie1 = createShow(movieId1, theatre.getId());
        Show showForMovie2 = createShow(movieId2, theatre.getId());

        org.mockito.Mockito
            .when(theatreRepository.findByCityIgnoreCase(city))
            .thenReturn(Flux.just(theatre));

        org.mockito.Mockito
            .when(showRepository.findByMovieIdAndTheatreIdIn(
                movieId1,
                Arrays.asList(theatre.getId())))
            .thenReturn(Flux.just(showForMovie1));

        org.mockito.Mockito
            .when(showRepository.findByMovieIdAndTheatreIdIn(
                movieId2,
                Arrays.asList(theatre.getId())))
            .thenReturn(Flux.just(showForMovie2));

        // Act & Assert - Test with movieId1
        StepVerifier.create(showService.getShows(movieId1, city))
            .expectNext(showForMovie1)
            .verifyComplete();

        // Act & Assert - Test with movieId2
        StepVerifier.create(showService.getShows(movieId2, city))
            .expectNext(showForMovie2)
            .verifyComplete();
    }

    // ============ Helper Methods ============

    private Theatre createTheatre(String name, String city) {
        Theatre theatre = new Theatre();
        theatre.setId(UUID.randomUUID());
        theatre.setName(name);
        theatre.setCity(city);
        return theatre;
    }

    private Show createShow(UUID movieId, UUID theatreId) {
        Show show = new Show();
        show.setId(UUID.randomUUID());
        show.setMovieId(movieId);
        show.setTheatreId(theatreId);
        return show;
    }
}