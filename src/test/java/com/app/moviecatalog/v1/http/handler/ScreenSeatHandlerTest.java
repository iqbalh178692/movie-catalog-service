package com.app.moviecatalog.v1.http.handler;

import com.app.moviecatalog.v1.domain.ScreenSeat;
import com.app.moviecatalog.v1.service.screen_seat.ScreentSeatService;
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
public class ScreenSeatHandlerTest {

    ScreenSeatTemplateHandler screenSeatTemplateHandler;
    private WebTestClient webTestClient;
    @Mock private ScreentSeatService seatService;

    @BeforeEach
    void setUp() {
        screenSeatTemplateHandler = new ScreenSeatTemplateHandler(seatService);

        // Create router with handler routes
        var router = RouterFunctions.route()
            .GET("/screens/{screenId}/seats", screenSeatTemplateHandler::getByScreen)
            .POST("/screens/{screenId}/seats", screenSeatTemplateHandler::create)
            .build();
        // Create test client bound to router
        webTestClient = WebTestClient.bindToRouterFunction(router).build();
    }

    @Test
    @DisplayName("Should create screen seat template successfully")
    void testCreateSeatTemplate_Success() {
        ScreenSeat inputTemplate = new ScreenSeat();
        inputTemplate.setScreenId(UUID.randomUUID());
        inputTemplate.setSeatType("PREMIUM");
        inputTemplate.setSeatNumber("A1");

        ScreenSeat createdTemplate = new ScreenSeat();
        createdTemplate.setId(UUID.randomUUID());
        createdTemplate.setScreenId(inputTemplate.getScreenId());
        createdTemplate.setSeatType("ECONOMY");
        createdTemplate.setSeatNumber("D1");

        when(seatService.create(any(ScreenSeat.class)))
            .thenReturn(Mono.just(createdTemplate));

        webTestClient.post()
            .uri("/screens/"+inputTemplate.getScreenId()+"/seats")
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(inputTemplate)
            .exchange()
            .expectStatus().isOk()
            .expectBody(ScreenSeat.class)
            .isEqualTo(createdTemplate);
    }

    @Test
    @DisplayName("Should handle service error when creating seat template")
    void testCreateSeatTemplate_ServiceError() {
        ScreenSeat template = new ScreenSeat();
        template.setScreenId(UUID.randomUUID());
        template.setSeatType("PREMIUM");
        template.setSeatNumber("A1");

        when(seatService.create(any(ScreenSeat.class)))
            .thenReturn(Mono.error(new RuntimeException("Service error")));

        webTestClient.post()
            .uri("/screens/"+template.getScreenId()+"/seats")
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(template)
            .exchange()
            .expectStatus().is5xxServerError();
    }

    @Test
    @DisplayName("Should get seat templates by screen ID successfully")
    void testGetByScreen_Success() {
        UUID screenId = UUID.fromString("22222222-2222-2222-2222-222222222222");

        ScreenSeat template1 = new ScreenSeat();
        template1.setId(UUID.randomUUID());
        template1.setScreenId(screenId);
        template1.setSeatType("PREMIUM");
        template1.setSeatNumber("A1");

        ScreenSeat template2 = new ScreenSeat();
        template2.setId(UUID.randomUUID());
        template2.setScreenId(screenId);
        template2.setSeatType("PREMIUM");
        template2.setSeatNumber("A1");

        when(seatService.getByScreen(screenId))
            .thenReturn(Flux.just(template1, template2));

        webTestClient.get()
            .uri("/screens/"+template1.getScreenId()+"/seats")
            .exchange()
            .expectStatus().isOk()
            .expectBodyList(ScreenSeat.class)
            .hasSize(2);
    }

    @Test
    @DisplayName("Should return empty list when screen has no seat templates")
    void testGetByScreen_Empty() {
        UUID screenId = UUID.fromString("22222222-2222-2222-2222-222222222222");

        when(seatService.getByScreen(screenId))
            .thenReturn(Flux.empty());

        webTestClient.get()
            .uri("/screens/"+screenId+"/seats")
            .exchange()
            .expectStatus().isOk()
            .expectBodyList(ScreenSeat.class)
            .hasSize(0);
    }
}
