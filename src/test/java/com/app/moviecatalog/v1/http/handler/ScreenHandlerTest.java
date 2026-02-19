package com.app.moviecatalog.v1.http.handler;

import com.app.moviecatalog.v1.domain.Screen;
import com.app.moviecatalog.v1.service.screen.ScreenService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.reactive.function.server.RouterFunctions;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ScreenHandlerTest {

    private  ScreenHandler screenHandler;
    private WebTestClient webTestClient;

    @Mock  private ScreenService screenService;

    @BeforeEach
    void setUp() {
        screenHandler = new ScreenHandler(screenService);

        // Create router with handler routes
        var router = RouterFunctions.route()
            .GET("/screens/{theatreId}", screenHandler::getByTheatre)
            .POST("/screens", screenHandler::create)
            .build();
        // Create test client bound to router
        webTestClient = WebTestClient.bindToRouterFunction(router).build();
    }

    @Test
    @DisplayName("Should create screen successfully")
    void testCreateScreen_Success() {
        Screen inputScreen = new Screen();
        inputScreen.setTheatreId(UUID.randomUUID());
        inputScreen.setName("some name 1");

        Screen createdScreen = new Screen();
        createdScreen.setId(UUID.randomUUID());
        createdScreen.setTheatreId(inputScreen.getTheatreId());
        inputScreen.setName("some name 2");

        when(screenService.create(any(Screen.class)))
            .thenReturn(Mono.just(createdScreen));

        webTestClient.post()
            .uri("/screens")
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(inputScreen)
            .exchange()
            .expectStatus().isOk()
            .expectBody(Screen.class)
            .isEqualTo(createdScreen);
    }

    @Test
    @DisplayName("Should handle service error when creating screen")
    void testCreateScreen_ServiceError() {
        Screen screen = new Screen();
        screen.setTheatreId(UUID.randomUUID());
        screen.setName("some name 2");

        when(screenService.create(any(Screen.class)))
            .thenReturn(Mono.error(new RuntimeException("Service error")));

        webTestClient.post()
            .uri("/screens")
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(screen)
            .exchange()
            .expectStatus().is5xxServerError();
    }

    @Test
    @DisplayName("Should get screens by theatre ID successfully")
    void testGetByTheatre_Success() {
        UUID theatreId = UUID.fromString("33333333-3333-3333-3333-333333333333");

        Screen screen1 = new Screen();
        screen1.setId(UUID.randomUUID());
        screen1.setTheatreId(theatreId);
        screen1.setName("SOME_VALID1");

        Screen screen2 = new Screen();
        screen2.setId(UUID.randomUUID());
        screen2.setTheatreId(theatreId);
        screen1.setName("SOME_VALID2");

        when(screenService.getByTheatre(theatreId))
            .thenReturn(Flux.just(screen1, screen2));

        webTestClient.get()
            .uri("/screens/" + theatreId)
            .exchange()
            .expectStatus().isOk()
            .expectBodyList(Screen.class)
            .hasSize(2);
    }

    @Test
    @DisplayName("Should return empty list when theatre has no screens")
    void testGetByTheatre_Empty() {
        UUID theatreId = UUID.randomUUID();

        when(screenService.getByTheatre(theatreId))
            .thenReturn(Flux.empty());

        webTestClient.get()
            .uri("/screens/" + theatreId)
            .exchange()
            .expectStatus().isOk()
            .expectBodyList(Screen.class)
            .hasSize(0);
    }

}
