package com.example.demo;


import org.junit.jupiter.api.Test;
import java.util.*;
import java.util.function.BiConsumer;
import java.util.stream.Collectors;

public class DojoStreamTest {

    @Test
    void converterData(){
        List<Player> list = CsvUtilFile.getPlayers();
        assert list.size() == 18207;
    }

    @Test
    void jugadoresMayoresA35SegunClub(){
        List<Player> list = CsvUtilFile.getPlayers();
        var newList = list.parallelStream().filter(player -> player.age >= 35)
                .flatMap(player1 -> list.parallelStream()
                        .filter(player2 -> player1.club.equals(player2.club))
                )
                .distinct()
                .collect(Collectors.groupingBy(Player::getClub));

         newList.forEach((s, players) -> {
             System.out.println("Club: " + s);
             System.out.println(players);
         });
    }

    @Test
    void jugadoresMayoresA35SegunClub1(){
        List<Player> list = CsvUtilFile.getPlayers();
        Map<String, List<String>> jugadoresPorClub = list.parallelStream()
                .collect(Collectors.groupingBy(
                        Player::getClub,
                        Collectors.collectingAndThen(
                                Collectors.filtering(player -> player.age >= 35, Collectors.toList()),
                                players -> players.stream().map(player -> player.getName()).collect(Collectors.toList()))
                        ));
        jugadoresPorClub.forEach((s, strings) -> {
            System.out.println("Club: " + s);
            System.out.println(strings);
        });
    }

    @Test
    void mejorJugadorConNacionalidadFrancia(){
        List<Player> list = CsvUtilFile.getPlayers();
        var newList = list.parallelStream().filter(player -> player.national.equals("France"))
                .max(Comparator.comparingDouble(value -> ((double) value.getWinners() / value.getGames())))
                        .orElse(null);

        System.out.println(newList);
    }

    @Test
    void clubsAgrupadosPorNacionalidad(){
        List<Player> list = CsvUtilFile.getPlayers();
        Map<String, List<String>> clubsPorNac = list.parallelStream()
                .collect(Collectors.groupingBy(Player::getNational,
                        Collectors.mapping(Player::getClub, Collectors.toList())));

        System.out.println(clubsPorNac);
    }

    @Test
    void clubConElMejorJugador(){
        List<Player> list = CsvUtilFile.getPlayers();
        String club = list.parallelStream()
                .max(Comparator.comparingDouble(value -> ((double) value.getWinners() / value.getGames())))
                .map(player -> player.getClub())
                .orElse(null);

        System.out.println(club);
    }

    @Test
    void mejorJugadorSegunNacionalidad(){
        List<Player> list = CsvUtilFile.getPlayers();
        Map<String, String> playerNac = list.parallelStream()
                .collect(Collectors.groupingBy(
                        Player::getNational,
                        Collectors.collectingAndThen(
                                Collectors.maxBy(
                                        Comparator.comparingDouble(player ->
                                                ((double) player.getWinners() / player.getGames())
                                        )
                                ),
                                //Optional::get
                                player -> "Jugador: " + player.get().getName() + " - Puntaje: " + player.get().getWinners()
                        )
                ));

        playerNac.forEach((s, player) -> {
            System.out.println("Nacionalidad: " + s);
            System.out.println(player);
        });
    }


}
