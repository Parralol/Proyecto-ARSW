package edu.escuelaing.arsw.ase.app;

import edu.escuelaing.arsw.ase.app.model.InvadersGUI;
import edu.escuelaing.arsw.ase.app.model.Player;
import edu.escuelaing.arsw.ase.app.model.Monster;
import edu.escuelaing.arsw.ase.app.model.Laser;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

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
        // Assume Player has a method getId() to retrieve its ID
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

    @Test
    void testPlayerMovement() {
        String playerId = "player1";
        String playerName = "Parralol";
        game.addPlayer(playerId, playerName);

        Player player = game.getPlayer(playerId);
        int initialX = player.getX();

        player.setVx(5);
        player.act();

        assertEquals(initialX + 5, player.getX(), "Player X position should have increased by 5");
    
    }

    @Test
    void testPlayerCollisionWithLaser() {
        String playerId = "player1";
        String playerName = "Parralol";
        game.addPlayer(playerId, playerName);

        Player player = game.getPlayer(playerId);
        player.setX(0);
        player.setY(0);
        int initialShields = player.getShields();

        Laser laser = new Laser(game);
        laser.setX(0);
        laser.setY(0);
        player.collision(laser);

        assertEquals(initialShields - 10, player.getShields(), "Player shields should decrease by 10 after collision with Laser");
    }

    @Test
    void testPlayerCollisionWithMonster() {
        String playerId = "player1";
        String playerName = "Parralol";
        game.addPlayer(playerId, playerName);

        Player player = game.getPlayer(playerId);
        player.setX(0);
        player.setY(0);
        int initialShields = player.getShields();

        Monster monster = new Monster(game);
        monster.setX(0);
        monster.setY(0);
        player.collision(monster);

        assertEquals(initialShields - 40, player.getShields(), "Player shields should decrease by 40 after collision with Monster");
    }

    @Test
    void testPlayerFire() {
        String playerId = "player1";
        String playerName = "Parralol";
        game.addPlayer(playerId, playerName);
        game.initWorld();

        Player player = game.getPlayer(playerId);
        int initialActorsCount = game.getActors().size();

        player.fire();
        assertEquals(initialActorsCount + 1, game.getActors().size(), "A bullet should be added to the actors list when player fires");
    }

    @Test
    void testPlayerFireCluster() {
        String playerId = "player1";
        String playerName = "Parralol";
        game.addPlayer(playerId, playerName);
        game.initWorld();
        Player player = game.getPlayer(playerId);
        int initialActorsCount = game.getActors().size();
        int initialClusterBombs = player.getClusterBombs();

        player.fireCluster();
        assertEquals(initialActorsCount + 8, game.getActors().size(), "Eight bombs should be added to the actors list when player fires a cluster bomb");
        assertEquals(initialClusterBombs - 1, player.getClusterBombs(), "Cluster bombs count should decrease by one after firing");
    }

    @Test
    void testPlayerLoseCondition() {
        String playerId = "player1";
        String playerName = "Parralol";
        game.addPlayer(playerId, playerName);

        Player player = game.getPlayer(playerId);
        player.addShields(-10000);

        player.act();
        assertTrue(player.isLoose(), "Player should lose if shields drop below zero");
    }

}
