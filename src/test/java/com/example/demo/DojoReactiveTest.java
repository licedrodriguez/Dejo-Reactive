package com.example.demo;


import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;
import reactor.core.publisher.GroupedFlux;
import reactor.core.publisher.Mono;

import java.util.*;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.stream.Collectors;

public class DojoReactiveTest {

    @Test
    void converterData(){
        Flux<Player> list = Flux.fromIterable(CsvUtilFile.getPlayers());
        Assertions.assertEquals(18207, list.count());
    }



    @Test
    void jugadoresMayoresA35SegunClub(){
        Flux<Player> flux = Flux.fromIterable(CsvUtilFile.getPlayers()).cache();
        var newList = flux.filter(player -> player.getAge() >= 35)
                .groupBy(Player::getClub)
                .distinct();

        newList.subscribe(s -> {
            System.out.println("Club: " + s.key());
            s.map(player -> player).subscribe(System.out::println);
            //s.map(Player::getName).subscribe(System.out::println);
        });

    }

    @Test
    void jugadoresMayoresA35SegunClub1(){
        Flux<Player> flux = Flux.fromIterable(CsvUtilFile.getPlayers()).cache();
        var newList = flux.collect(Collectors.groupingBy(
                        Player::getClub,
                        Collectors.collectingAndThen(
                                Collectors.filtering(player -> player.getAge() >= 35, Collectors.toList()),
                                players -> players.stream().map(player -> "Jugador: " + player.getName() + " - Edad: " + player.getAge()).collect(Collectors.toList())
                        )
                ));

        newList.subscribe(stringListMap -> Mono.just(stringListMap).subscribe(System.out::println));
    }

    @Test
    void mejorJugadorConNacionalidadFrancia(){
        Flux<Player> flux = Flux.fromIterable(CsvUtilFile.getPlayers());
        var playerM = flux.filter(player -> player.getNational().equals("France"))
                .reduce(BinaryOperator.maxBy(Comparator.comparingDouble(value -> (double) value.getWinners() / value.getGames())));

        playerM.subscribe(System.out::println);
    }

    @Test
    void clubsAgrupadosPorNacionalidad(){
        Flux<Player> flux = Flux.fromIterable(CsvUtilFile.getPlayers()).cache();
        var clubsPorNac = flux.distinct()
                .collect(Collectors.groupingBy(Player::getNational,
                        Collectors.mapping(Player::getClub, Collectors.toList())));

        clubsPorNac.subscribe(System.out::println);
    }
    @Test
    void clubsAgrupadosPorNacionalidad1(){
        Flux<Player> flux = Flux.fromIterable(CsvUtilFile.getPlayers()).cache();
        var clubsPorNac = flux.distinct()
                .collectMultimap(Player::getNational, Player::getClub);

        clubsPorNac.subscribe(System.out::println);
    }

    @Test
    void clubConElMejorJugador(){
        Flux<Player> flux = Flux.fromIterable(CsvUtilFile.getPlayers());
        Mono<String> club = flux.collect(
                Collectors.collectingAndThen(
                        Collectors.maxBy(
                                Comparator.comparingDouble(value -> ((double) value.getWinners() / (double) value.getGames()))
                        ),
                        player -> player.get().getClub())
                );

        club.subscribe(System.out::println);
    }
    @Test
    void clubConElMejorJugador1(){
        Flux<Player> flux = Flux.fromIterable(CsvUtilFile.getPlayers());
        Mono<String> club = flux.reduce(BinaryOperator
                .maxBy(Comparator.comparingDouble(value ->
                        (double) value.getWinners() / value.getGames()))).map(player -> player.getClub()
        );

        club.subscribe(System.out::println);
    }

    @Test
    void mejorJugadorSegunNacionalidad(){
        Flux<Player> flux = Flux.fromIterable(CsvUtilFile.getPlayers());
        var newFlux = flux.collect(Collectors.groupingBy(
                Player::getNational,
                Collectors.collectingAndThen(
                        Collectors.maxBy(
                                Comparator.comparingDouble(player ->
                                        ((double) player.getWinners() / player.getGames())
                                )
                        ),
                        player -> "Jugador: " + player.get().getName() + " - Puntaje: " + player.get().getWinners()
                )
        ));
        newFlux.subscribe(System.out::println);
    }

    @Test
    void mejorJugadorSegunNacionalidad2(){
        Flux<Player> flux = Flux.fromIterable(CsvUtilFile.getPlayers());
        var newFlux = flux.collect(Collectors.groupingBy(Player::getNational,
                        Collectors.collectingAndThen(
                                Collectors.reducing(
                                        BinaryOperator.maxBy(
                                                Comparator.comparingDouble(
                                                        value -> (double) value.getWinners() / (double) value.getGames()
                                                )
                                        )
                                ), player -> player.get().getName())
                        ));
        newFlux.subscribe(System.out::println);
    }



}
