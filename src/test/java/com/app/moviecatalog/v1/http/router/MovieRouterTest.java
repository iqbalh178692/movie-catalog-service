package com.app.moviecatalog.v1.http.router;

import com.app.moviecatalog.v1.config.SecurityConfig;
import com.app.moviecatalog.v1.http.handler.MovieHandler;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.reactive.function.server.ServerResponse;

import java.util.Collections;
import java.util.function.Supplier;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@WebFluxTest
@ContextConfiguration(classes = {MovieRouter.class, SecurityConfig.class})
@DisplayName("MovieRouter Integration Tests")
class MovieRouterTest {

    @Autowired
    private WebTestClient webTestClient;

    @MockBean
    private MovieHandler movieHandler;

    private final String VALID_UUID = "550e8400-e29b-41d4-a716-446655440000";

    final Supplier<HttpHeaders> HTTP_HEADERS_BASIC = () -> {
        final HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
        return headers;
    };

    final Supplier<HttpHeaders> HTTP_HEADERS_ADMIN = () -> {
        final HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
        headers.put(HttpHeaders.AUTHORIZATION,
            Collections.singletonList("Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiJhZG1pbiIsInJvbGVzIjpbIkFETUlOIl19.nsNqZnbUH0xMUj0uKSmRzMJKcb9NKQIVJEtLqHNQkYc"));
        return headers;
    };

    // ============ GET /movies Tests ============

    @Test
    @DisplayName("Should map GET /movies route and return 200 OK")
    void route_GetAllMovies_ShouldMap() {
        when(movieHandler.getAllMovies(any()))
            .thenReturn(ServerResponse.ok().bodyValue("[]"));

        webTestClient.get()
            .uri("/movies")
            .headers(httpHeaders -> httpHeaders.putAll(HTTP_HEADERS_BASIC.get()))
            .exchange()
            .expectStatus().isOk();
    }



    @Test
    @DisplayName("Should get all movies with correct content type")
    void route_GetAllMovies_VerifyContentType() {
        when(movieHandler.getAllMovies(any()))
            .thenReturn(ServerResponse.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue("[]")
                );

        webTestClient.get()
            .uri("/movies")
            .exchange()
            .expectStatus().isOk()
            .expectHeader().contentType(MediaType.APPLICATION_JSON);
    }

    // ============ GET /movies/search Tests ============

    @Test
    @DisplayName("Should map GET /movies/search route with title parameter")
    void route_SearchMovies_ShouldMap() {
        when(movieHandler.searchMovies(any()))
            .thenReturn(ServerResponse.ok().bodyValue("[]"));

        webTestClient.get()
            .uri("/movies/search?title=Inception")
            .headers(httpHeaders -> httpHeaders.putAll(HTTP_HEADERS_BASIC.get()))
            .exchange()
            .expectStatus().isOk();
    }

    @Test
    @DisplayName("Should search movies with different title values")
    void route_SearchMovies_DifferentTitles() {
        when(movieHandler.searchMovies(any()))
            .thenReturn(ServerResponse.ok().bodyValue("[]"));

        webTestClient.get()
            .uri("/movies/search?title=Matrix")
            .exchange()
            .expectStatus().isOk();
    }

    @Test
    @DisplayName("Should search movies with empty result")
    void route_SearchMovies_EmptyResult() {
        when(movieHandler.searchMovies(any()))
            .thenReturn(ServerResponse.ok().bodyValue("[]"));

        webTestClient.get()
            .uri("/movies/search?title=NonExistent")
            .exchange()
            .expectStatus().isOk();
    }

    @Test
    @DisplayName("Should handle search without title parameter")
    void route_SearchMovies_WithoutTitleParam() {
        when(movieHandler.searchMovies(any()))
            .thenReturn(ServerResponse.ok().bodyValue("[]"));

        webTestClient.get()
            .uri("/movies/search")
            .exchange()
            .expectStatus().isOk();
    }

    @Test
    @DisplayName("Should handle search with special characters in title")
    void route_SearchMovies_SpecialCharacters() {
        when(movieHandler.searchMovies(any()))
            .thenReturn(ServerResponse.ok().bodyValue("[]"));

        webTestClient.get()
            .uri("/movies/search?title=Star+Wars")
            .exchange()
            .expectStatus().isOk();
    }

    // ============ GET /movies/{id} Tests ============

    @Test
    @DisplayName("Should map GET /movies/{id} route")
    void route_GetMovieById_ShouldMap() {
        when(movieHandler.getMovie(any()))
            .thenReturn(ServerResponse.ok().bodyValue("{}"));

        webTestClient.get()
            .uri("/movies/" + VALID_UUID)
            .headers(httpHeaders -> httpHeaders.putAll(HTTP_HEADERS_BASIC.get()))
            .exchange()
            .expectStatus().isOk();
    }

    @Test
    @DisplayName("Should get movie with valid UUID")
    void route_GetMovieById_ValidUUID() {
        when(movieHandler.getMovie(any()))
            .thenReturn(ServerResponse.ok().bodyValue("{}"));

        webTestClient.get()
            .uri("/movies/" + VALID_UUID)
            .exchange()
            .expectStatus().isOk();
    }

    @Test
    @DisplayName("Should return 404 when movie not found")
    void route_GetMovieById_NotFound() {
        when(movieHandler.getMovie(any()))
            .thenReturn(ServerResponse.notFound().build());

        webTestClient.get()
            .uri("/movies/" + VALID_UUID)
            .exchange()
            .expectStatus().isNotFound();
    }

    @Test
    @DisplayName("Should handle invalid UUID format")
    void route_GetMovieById_InvalidUUID() {
        when(movieHandler.getMovie(any()))
            .thenReturn(ServerResponse.badRequest().build());

        webTestClient.get()
            .uri("/movies/invalid-uuid")
            .exchange()
            .expectStatus().isBadRequest();
    }

    @Test
    @DisplayName("Should get movie by different valid UUIDs")
    void route_GetMovieById_DifferentUUIDs() {
        when(movieHandler.getMovie(any()))
            .thenReturn(ServerResponse.ok().bodyValue("{}"));

        String uuid1 = "123e4567-e89b-12d3-a456-426614174000";
        String uuid2 = "987fcdeb-51a2-43d1-9d20-87654321abcd";

        webTestClient.get()
            .uri("/movies/" + uuid1)
            .exchange()
            .expectStatus().isOk();

        webTestClient.get()
            .uri("/movies/" + uuid2)
            .exchange()
            .expectStatus().isOk();
    }

    // ============ GET /movies/{movieId}/shows Tests ============

    @Test
    @DisplayName("Should map GET /movies/{movieId}/shows route with city parameter")
    void route_GetShows_ShouldMap() {
        when(movieHandler.getShows(any()))
            .thenReturn(ServerResponse.ok().bodyValue("[]"));

        webTestClient.get()
            .uri("/movies/" + VALID_UUID + "/shows?city=Paris")
            .headers(httpHeaders -> httpHeaders.putAll(HTTP_HEADERS_BASIC.get()))
            .exchange()
            .expectStatus().isOk();
    }

    @Test
    @DisplayName("Should return shows for valid movie and city")
    void route_GetShows_ValidMovieAndCity() {
        when(movieHandler.getShows(any()))
            .thenReturn(ServerResponse.ok().bodyValue("[]"));

        webTestClient.get()
            .uri("/movies/" + VALID_UUID + "/shows?city=Mumbai")
            .exchange()
            .expectStatus().isOk();
    }

    @Test
    @DisplayName("Should handle get shows with different cities")
    void route_GetShows_DifferentCities() {
        when(movieHandler.getShows(any()))
            .thenReturn(ServerResponse.ok().bodyValue("[]"));

        webTestClient.get()
            .uri("/movies/" + VALID_UUID + "/shows?city=Bangalore")
            .exchange()
            .expectStatus().isOk();

        webTestClient.get()
            .uri("/movies/" + VALID_UUID + "/shows?city=Delhi")
            .exchange()
            .expectStatus().isOk();
    }

    @Test
    @DisplayName("Should handle get shows without city parameter")
    void route_GetShows_WithoutCityParam() {
        when(movieHandler.getShows(any()))
            .thenReturn(ServerResponse.badRequest().build());

        webTestClient.get()
            .uri("/movies/" + VALID_UUID + "/shows")
            .exchange()
            .expectStatus().isBadRequest();
    }

    @Test
    @DisplayName("Should return empty shows list for valid movie")
    void route_GetShows_EmptyResult() {
        when(movieHandler.getShows(any()))
            .thenReturn(ServerResponse.ok().bodyValue("[]"));

        webTestClient.get()
            .uri("/movies/" + VALID_UUID + "/shows?city=UnknownCity")
            .exchange()
            .expectStatus().isOk();
    }

    @Test
    @DisplayName("Should handle invalid movie ID in shows endpoint")
    void route_GetShows_InvalidMovieId() {
        when(movieHandler.getShows(any()))
            .thenReturn(ServerResponse.notFound().build());

        webTestClient.get()
            .uri("/movies/invalid-uuid/shows?city=Mumbai")
            .exchange()
            .expectStatus().isNotFound();
    }

    @Test
    @DisplayName("Should handle shows endpoint with special characters in city")
    void route_GetShows_SpecialCharactersInCity() {
        when(movieHandler.getShows(any()))
            .thenReturn(ServerResponse.ok().bodyValue("[]"));

        webTestClient.get()
            .uri("/movies/" + VALID_UUID + "/shows?city=New+York")
            .exchange()
            .expectStatus().isOk();
    }

    // ============ POST /movies/admin Tests ============

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    @DisplayName("Should map POST /movies/admin route with ADMIN role")
    void route_CreateAdminMovie_ShouldMap() {
        when(movieHandler.create(any()))
            .thenReturn(ServerResponse.ok().bodyValue("{}"));

        webTestClient.post()
            .uri("/movies/admin")
            .headers(httpHeaders -> httpHeaders.putAll(HTTP_HEADERS_BASIC.get()))
            .bodyValue("{\"title\": \"New Movie\", \"active\": true}")
            .exchange()
            .expectStatus().isOk();
    }
//
//    @Test
//    @WithMockUser(username = "admin", roles = {"ADMIN"})
//    @DisplayName("Should create movie with ADMIN role")
//    void route_CreateAdminMovie_WithAdminRole() {
//        when(movieHandler.create(any()))
//            .thenReturn(ServerResponse.created(null).bodyValue("{}").build());
//
//        webTestClient.post()
//            .uri("/movies/admin")
//            .headers(httpHeaders -> httpHeaders.putAll(HTTP_HEADERS_BASIC.get()))
//            .bodyValue("{\"title\": \"Inception\", \"active\": true}")
//            .exchange()
//            .expectStatus().isCreated();
//    }
//
//    @Test
//    @DisplayName("Should return 401 when creating movie without authentication")
//    void route_CreateAdminMovie_WithoutAuthentication() {
//        when(movieHandler.create(any()))
//            .thenReturn(ServerResponse.status(401).build());
//
//        webTestClient.post()
//            .uri("/movies/admin")
//            .bodyValue("{\"title\": \"New Movie\", \"active\": true}")
//            .exchange()
//            .expectStatus().isUnauthorized();
//    }
//
//    @Test
//    @WithMockUser(username = "user", roles = {"USER"})
//    @DisplayName("Should return 403 when creating movie with non-admin role")
//    void route_CreateAdminMovie_WithNonAdminRole() {
//        when(movieHandler.create(any()))
//            .thenReturn(ServerResponse.status(403).build());
//
//        webTestClient.post()
//            .uri("/movies/admin")
//            .headers(httpHeaders -> httpHeaders.putAll(HTTP_HEADERS_BASIC.get()))
//            .bodyValue("{\"title\": \"New Movie\", \"active\": true}")
//            .exchange()
//            .expectStatus().isForbidden();
//    }
//
//    @Test
//    @WithMockUser(username = "admin", roles = {"ADMIN"})
//    @DisplayName("Should return 400 for invalid request body")
//    void route_CreateAdminMovie_InvalidBody() {
//        when(movieHandler.create(any()))
//            .thenReturn(ServerResponse.badRequest().build());
//
//        webTestClient.post()
//            .uri("/movies/admin")
//            .headers(httpHeaders -> httpHeaders.putAll(HTTP_HEADERS_BASIC.get()))
//            .bodyValue("invalid json")
//            .exchange()
//            .expectStatus().isBadRequest();
//    }
//
//    @Test
//    @WithMockUser(username = "admin", roles = {"ADMIN"})
//    @DisplayName("Should handle create movie with missing required fields")
//    void route_CreateAdminMovie_MissingFields() {
//        when(movieHandler.create(any()))
//            .thenReturn(ServerResponse.badRequest().build());
//
//        webTestClient.post()
//            .uri("/movies/admin")
//            .headers(httpHeaders -> httpHeaders.putAll(HTTP_HEADERS_BASIC.get()))
//            .bodyValue("{}")
//            .exchange()
//            .expectStatus().isBadRequest();
//    }
//
//    @Test
//    @WithMockUser(username = "admin", roles = {"ADMIN"})
//    @DisplayName("Should create multiple movies with admin role")
//    void route_CreateAdminMovie_MultipleCreates() {
//        when(movieHandler.create(any()))
//            .thenReturn(ServerResponse.ok().bodyValue("{}").build());
//
//        webTestClient.post()
//            .uri("/movies/admin")
//            .headers(httpHeaders -> httpHeaders.putAll(HTTP_HEADERS_BASIC.get()))
//            .bodyValue("{\"title\": \"Movie 1\", \"active\": true}")
//            .exchange()
//            .expectStatus().isOk();
//
//        webTestClient.post()
//            .uri("/movies/admin")
//            .headers(httpHeaders -> httpHeaders.putAll(HTTP_HEADERS_BASIC.get()))
//            .bodyValue("{\"title\": \"Movie 2\", \"active\": false}")
//            .exchange()
//            .expectStatus().isOk();
//    }
}