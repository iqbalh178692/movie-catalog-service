package com.app.moviecatalog.v1.service.movie;

import com.app.moviecatalog.v1.domain.Movie;
import com.app.moviecatalog.v1.exception.MovieNotFoundException;
import com.app.moviecatalog.v1.repository.MovieRepository;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

@ExtendWith(MockitoExtension.class)
@DisplayName("MovieServiceImpl Unit Tests")
class MovieServiceImplTest {

    @Mock
    private MovieRepository movieRepository;

    private MovieServiceImpl movieService;

    @BeforeEach
    void setUp() {
        movieService = new MovieServiceImpl(movieRepository);
    }

    // ============ getAllActiveMovies Tests ============

    @Test
    @DisplayName("Should return all active movies")
    void testGetAllActiveMovies_Success() {
        // Arrange
        Movie movie1 = createMovie("Inception", true);
        Movie movie2 = createMovie("The Matrix", true);

        org.mockito.Mockito
            .when(movieRepository.findByActiveTrue())
            .thenReturn(Flux.just(movie1, movie2));

        // Act & Assert
        StepVerifier.create(movieService.getAllActiveMovies())
            .expectNext(movie1)
            .expectNext(movie2)
            .verifyComplete();

        org.mockito.Mockito.verify(movieRepository).findByActiveTrue();
    }

    @Test
    @DisplayName("Should return empty flux when no active movies exist")
    void testGetAllActiveMovies_EmptyResult() {
        // Arrange
        org.mockito.Mockito
            .when(movieRepository.findByActiveTrue())
            .thenReturn(Flux.empty());

        // Act & Assert
        StepVerifier.create(movieService.getAllActiveMovies())
            .verifyComplete();

        org.mockito.Mockito.verify(movieRepository).findByActiveTrue();
    }

    @Test
    @DisplayName("Should handle repository error when getting all active movies")
    void testGetAllActiveMovies_RepositoryError() {
        // Arrange
        org.mockito.Mockito
            .when(movieRepository.findByActiveTrue())
            .thenReturn(Flux.error(new RuntimeException("Database error")));

        // Act & Assert
        StepVerifier.create(movieService.getAllActiveMovies())
            .expectError(RuntimeException.class)
            .verify();

        org.mockito.Mockito.verify(movieRepository).findByActiveTrue();
    }

    // ============ searchMovies Tests ============

    @Test
    @DisplayName("Should search movies by title successfully")
    void testSearchMovies_Success() {
        // Arrange
        String searchTitle = "Inception";
        Movie movie1 = createMovie("Inception", true);
        Movie movie2 = createMovie("Inception 2", true);

        org.mockito.Mockito
            .when(movieRepository.findByTitleContainingIgnoreCaseAndActiveTrue(searchTitle))
            .thenReturn(Flux.just(movie1, movie2));

        // Act & Assert
        StepVerifier.create(movieService.searchMovies(searchTitle))
            .expectNext(movie1)
            .expectNext(movie2)
            .verifyComplete();

        org.mockito.Mockito.verify(movieRepository)
            .findByTitleContainingIgnoreCaseAndActiveTrue(searchTitle);
    }

    @Test
    @DisplayName("Should perform case-insensitive search")
    void testSearchMovies_CaseInsensitive() {
        // Arrange
        String searchTitle = "inception";
        Movie movie = createMovie("Inception", true);

        org.mockito.Mockito
            .when(movieRepository.findByTitleContainingIgnoreCaseAndActiveTrue(searchTitle))
            .thenReturn(Flux.just(movie));

        // Act & Assert
        StepVerifier.create(movieService.searchMovies(searchTitle))
            .expectNext(movie)
            .verifyComplete();

        org.mockito.Mockito.verify(movieRepository)
            .findByTitleContainingIgnoreCaseAndActiveTrue(searchTitle);
    }

    @Test
    @DisplayName("Should return empty flux when no movies match search criteria")
    void testSearchMovies_NoResults() {
        // Arrange
        String searchTitle = "NonExistentMovie";

        org.mockito.Mockito
            .when(movieRepository.findByTitleContainingIgnoreCaseAndActiveTrue(searchTitle))
            .thenReturn(Flux.empty());

        // Act & Assert
        StepVerifier.create(movieService.searchMovies(searchTitle))
            .verifyComplete();

        org.mockito.Mockito.verify(movieRepository)
            .findByTitleContainingIgnoreCaseAndActiveTrue(searchTitle);
    }

    @Test
    @DisplayName("Should handle repository error during search")
    void testSearchMovies_RepositoryError() {
        // Arrange
        String searchTitle = "test";

        org.mockito.Mockito
            .when(movieRepository.findByTitleContainingIgnoreCaseAndActiveTrue(searchTitle))
            .thenReturn(Flux.error(new RuntimeException("Database error")));

        // Act & Assert
        StepVerifier.create(movieService.searchMovies(searchTitle))
            .expectError(RuntimeException.class)
            .verify();

        org.mockito.Mockito.verify(movieRepository)
            .findByTitleContainingIgnoreCaseAndActiveTrue(searchTitle);
    }

    // ============ getMovieById Tests ============

    @Test
    @DisplayName("Should get movie by id successfully")
    void testGetMovieById_Success() {
        // Arrange
        UUID movieId = UUID.randomUUID();
        Movie movie = createMovieWithId(movieId, "Inception", true);

        org.mockito.Mockito
            .when(movieRepository.findById(movieId))
            .thenReturn(Mono.just(movie));

        // Act & Assert
        StepVerifier.create(movieService.getMovieById(movieId))
            .expectNext(movie)
            .verifyComplete();

        org.mockito.Mockito.verify(movieRepository).findById(movieId);
    }

    @Test
    @DisplayName("Should throw MovieNotFoundException when movie not found")
    void testGetMovieById_NotFound() {
        // Arrange
        UUID movieId = UUID.randomUUID();

        org.mockito.Mockito
            .when(movieRepository.findById(movieId))
            .thenReturn(Mono.empty());

        // Act & Assert
        StepVerifier.create(movieService.getMovieById(movieId))
            .expectError(MovieNotFoundException.class)
            .verify();

        org.mockito.Mockito.verify(movieRepository).findById(movieId);
    }

    @Test
    @DisplayName("Should throw MovieNotFoundException with correct message")
    void testGetMovieById_NotFound_VerifyMessage() {
        // Arrange
        UUID movieId = UUID.randomUUID();

        org.mockito.Mockito
            .when(movieRepository.findById(movieId))
            .thenReturn(Mono.empty());

        // Act & Assert
        StepVerifier.create(movieService.getMovieById(movieId))
            .expectErrorMatches(throwable ->
                throwable instanceof MovieNotFoundException &&
                throwable.getMessage().equals("Movie not found"))
            .verify();

        org.mockito.Mockito.verify(movieRepository).findById(movieId);
    }

    @Test
    @DisplayName("Should handle repository error when getting movie by id")
    void testGetMovieById_RepositoryError() {
        // Arrange
        UUID movieId = UUID.randomUUID();

        org.mockito.Mockito
            .when(movieRepository.findById(movieId))
            .thenReturn(Mono.error(new RuntimeException("Database error")));

        // Act & Assert
        StepVerifier.create(movieService.getMovieById(movieId))
            .expectError(RuntimeException.class)
            .verify();

        org.mockito.Mockito.verify(movieRepository).findById(movieId);
    }

    // ============ createMovie Tests ============

    @Test
    @DisplayName("Should create movie successfully")
    void testCreateMovie_Success() {
        // Arrange
        Movie movie = createMovie("New Movie", true);
        movie.setId(null); // Simulate new movie without ID

        org.mockito.Mockito
            .when(movieRepository.save(org.mockito.ArgumentMatchers.any(Movie.class)))
            .thenAnswer(invocation -> {
                Movie savedMovie = invocation.getArgument(0);
                return Mono.just(savedMovie);
            });

        // Act & Assert
        StepVerifier.create(movieService.createMovie(movie))
            .expectNextMatches(savedMovie ->
                savedMovie.getId() != null &&
                savedMovie.getTitle().equals("New Movie") &&
                savedMovie.getActive())
            .verifyComplete();

        org.mockito.Mockito.verify(movieRepository)
            .save(org.mockito.ArgumentMatchers.any(Movie.class));
    }

    @Test
    @DisplayName("Should generate UUID for new movie")
    void testCreateMovie_GeneratesUUID() {
        // Arrange
        Movie movie = createMovie("Test Movie", true);
        movie.setId(null);

        org.mockito.Mockito
            .when(movieRepository.save(org.mockito.ArgumentMatchers.any(Movie.class)))
            .thenAnswer(invocation -> {
                Movie savedMovie = invocation.getArgument(0);
                return Mono.just(savedMovie);
            });

        // Act & Assert
        StepVerifier.create(movieService.createMovie(movie))
            .expectNextMatches(savedMovie -> savedMovie.getId() != null)
            .verifyComplete();
    }

    @Test
    @DisplayName("Should handle repository error when creating movie")
    void testCreateMovie_RepositoryError() {
        // Arrange
        Movie movie = createMovie("Test Movie", true);

        org.mockito.Mockito
            .when(movieRepository.save(org.mockito.ArgumentMatchers.any(Movie.class)))
            .thenReturn(Mono.error(new RuntimeException("Database error")));

        // Act & Assert
        StepVerifier.create(movieService.createMovie(movie))
            .expectError(RuntimeException.class)
            .verify();

        org.mockito.Mockito.verify(movieRepository)
            .save(org.mockito.ArgumentMatchers.any(Movie.class));
    }

    @Test
    @DisplayName("Should preserve movie properties when creating")
    void testCreateMovie_PreserveProperties() {
        // Arrange
        Movie movie = new Movie();
        movie.setTitle("Test Title");
        movie.setActive(true);

        org.mockito.Mockito
            .when(movieRepository.save(org.mockito.ArgumentMatchers.any(Movie.class)))
            .thenAnswer(invocation -> Mono.just(invocation.getArgument(0)));

        // Act & Assert
        StepVerifier.create(movieService.createMovie(movie))
            .expectNextMatches(savedMovie ->
                savedMovie.getTitle().equals("Test Title") &&
                savedMovie.getActive())
            .verifyComplete();
    }

    // ============ Helper Methods ============

    private Movie createMovie(String title, boolean active) {
        Movie movie = new Movie();
        movie.setId(UUID.randomUUID());
        movie.setTitle(title);
        movie.setActive(active);
        return movie;
    }

    private Movie createMovieWithId(UUID id, String title, boolean active) {
        Movie movie = new Movie();
        movie.setId(id);
        movie.setTitle(title);
        movie.setActive(active);
        return movie;
    }
}