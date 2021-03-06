package com.example;

import lombok.*;
import lombok.extern.java.Log;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.web.reactive.function.client.ExchangeFilterFunctions;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Date;

@Log
@SpringBootApplication
public class FfsClientApplication {

    @Bean
    WebClient webClient() {
        wh
        // ffs-service.cfapps.io
        return WebClient.create("http://ffs-service.cfapps.io/movies")
                .filter(ExchangeFilterFunctions.basicAuthentication("phopper", "password"));
        // localhost
        //return WebClient.create("http://localhost:8080/movies")
        //        .filter(ExchangeFilterFunctions.basicAuthentication("jlong", "password"));
    }

    @Bean
    CommandLineRunner demo(WebClient client) {
        return strings ->
                client
                        .get()
                        .uri("")
                        .retrieve()
                        .bodyToFlux(Movie.class)
                        .filter(movie -> movie.getTitle().equalsIgnoreCase("aeon flux"))
                        .flatMap(movie ->
                                client.get()
                                        .uri("/{id}/events", movie.getId())
                                        .retrieve()
                                        .bodyToFlux(MovieEvent.class))
                        .subscribe(movieEvent -> log.info(movieEvent.toString()));
    }

    public static void main(String[] args) {
        SpringApplication.run(FfsClientApplication.class, args);
    }
}


@Data
@AllArgsConstructor
class MovieEvent {
    private Movie movie;
    private Date when;
}

@Data
@AllArgsConstructor
@NoArgsConstructor
class Movie {
    @Getter
    @Setter
    private String id;
    @Getter
    @Setter
    private String title;
}