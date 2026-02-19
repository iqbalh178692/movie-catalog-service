package com.app.moviecatalog.v1.http.router;


import com.app.moviecatalog.v1.http.handler.MovieHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;

@Configuration
public class MovieRouter {

    @Bean
    public RouterFunction<ServerResponse> routes(MovieHandler handler) {

        return RouterFunctions.route()

            .GET("/movies", handler::getAllMovies)
            .GET("/movies/search", handler::searchMovies)
            .GET("/movies/{id}", handler::getMovie)

            // Admin endpoint
            .POST("/movies/admin", handler::create)

            .build();
    }
}
