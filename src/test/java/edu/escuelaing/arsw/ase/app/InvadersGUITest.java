package edu.escuelaing.arsw.ase.app;

import edu.escuelaing.arsw.ase.app.model.InvadersGUI;
import edu.escuelaing.arsw.ase.app.model.Actor;
import edu.escuelaing.arsw.ase.app.model.Player;
import edu.escuelaing.arsw.ase.app.model.Monster;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.awt.event.KeyEvent;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class InvadersGUITest {

    private InvadersGUI game;

    @BeforeEach
    void setUp() {
        game = new InvadersGUI();
    }

    @Test
    void testAddPlayer() {
        String playerId = "player1";
        String playerName = "Parralol";
        game.addPlayer(playerId, playerName);

        Player player = game.getPlayer(playerId);
        assertNotNull(player, "Player should be added");
        assertEquals(playerName, player.getName(), "Player name should be set correctly");
        assertEquals(playerId, player.getId(), "Player ID should be set correctly");
    }

    @Test
    void testChangePlayerName() {
        String playerId = "player1";
        String playerName = "Parralol";
        game.addPlayer(playerId, playerName);

        String newPlayerName = "Parraol";
        game.changePlayerName(playerId, newPlayerName);

        Player player = game.getPlayer(playerId);
        assertEquals(newPlayerName, player.getName(), "Player name should be updated");
    }

    @Test
    void testInitWorld() {
        game.initWorld();
        assertFalse(game.getActors().isEmpty(), "World should have actors initialized");
    }

    @Test
    void testUpdateWorld() {
        game.initWorld();
        int initialNumberOfActors = game.getActors().size();

        game.updateWorld();
        assertEquals(initialNumberOfActors, game.getActors().size(), "Number of actors should remain the same or change based on logic");
    }

    @Test
    void testGameOver() {
        assertFalse(game.isGameEnded(), "Game should not be ended initially");
        game.gameOver();
        assertTrue(game.isGameEnded(), "Game should be ended after gameOver is called");
    }




    @Test
    void testGetPlayers() {
        String playerId = "player1";
        String playerName = "Parralol";
        game.addPlayer(playerId, playerName);

        Map<String, Player> players = game.getPlayers();
        assertEquals(1, players.size(), "There should be one player in the game");
        assertTrue(players.containsKey(playerId), "The player ID should be present in the game");
    }

    // Add more tests as necessary
}
