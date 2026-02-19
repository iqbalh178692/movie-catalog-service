package com.app.moviecatalog.v1.http.router;

import com.app.moviecatalog.v1.http.handler.ScreenHandler;
import com.app.moviecatalog.v1.http.handler.ScreenSeatTemplateHandler;
import com.app.moviecatalog.v1.http.handler.ShowHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;

@Configuration
public class ScreenRouter {

    @Bean
    public RouterFunction<ServerResponse> screenRoutes(
        ScreenHandler screenHandler,
        ScreenSeatTemplateHandler seatHandler, ShowHandler showHandler) {

        return RouterFunctions.route()

            // Screens
            .POST("/screens/admin", screenHandler::create)
            .GET("/screens/theatre/{theatreId}",
                screenHandler::getByTheatre)

            // Seat Templates // Admin endpoint
            .POST("/screens/admin/{screenId}/seats",
                seatHandler::create)

            .GET("/screens/{screenId}/seats",
                seatHandler::getByScreen)
            // Admin endpoint
            .POST("/shows/admin", showHandler::create)
            .build();
    }
}
