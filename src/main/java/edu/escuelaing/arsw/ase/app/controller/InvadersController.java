package edu.escuelaing.arsw.ase.app.controller;

import edu.escuelaing.arsw.ase.app.entity.User;
import edu.escuelaing.arsw.ase.app.model.Actor;
import edu.escuelaing.arsw.ase.app.model.InvadersGUI;
import edu.escuelaing.arsw.ase.app.model.Player;
import edu.escuelaing.arsw.ase.app.service.UserService;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.awt.event.KeyEvent;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;


@SuppressWarnings("static-access")
@RestController
public class InvadersController extends TextWebSocketHandler {

    private static InvadersGUI invadersGUI;
    private final Map<String, WebSocketSession> sessions = new ConcurrentHashMap<>();
    private final Map<String, Boolean> playerUpdateSent = new ConcurrentHashMap<>();
    
    private static final String KEY_DOWN = "keydown";
    
    UserService scores;

    Logger log = Logger.getLogger(getClass().getName());
    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
    
    @Autowired
    public InvadersController(UserService scores){
        this.scores = scores;
    }

    public InvadersController() {
        getInvaders();
        new Thread(() -> invadersGUI.game()).start();
        scheduler.scheduleAtFixedRate(() -> {
            try {
                broadcastState();

            } catch (Exception e) {
                e.printStackTrace();
            }
        }, 0, 40, TimeUnit.MILLISECONDS);
    }

    public static InvadersGUI getInvaders() {
        if (invadersGUI == null) {
            invadersGUI = new InvadersGUI();
        }
        return invadersGUI;
    }

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        sessions.put(session.getId(), session);
        this.invadersGUI.addPlayer(session.getId(), session.getId());
        session.sendMessage(new TextMessage("Connection established."));
        session.sendMessage(new TextMessage("Player ID:" + session.getId()));
        log.log(Level.INFO, () -> session.getId() + " INITIALIZED");
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        String clientId = session.getId();
        String payload = message.getPayload();

        try {
            JSONObject obj = new JSONObject(payload);
            String type = obj.getString("type");

            switch (type) {
                case "keyup", KEY_DOWN:
                    KeyEventDTO press = new KeyEventDTO();
                    press.setKeyCode(obj.getInt("keyCode"));
                    press.setType(type);
                    handleKeyEvent(press, clientId);
                    break;

                case "nameChange":
                    String newName = obj.getString("name");
                    handleNameChange(clientId, newName);
                    break;

                default:
                    log.log(Level.WARNING,() ->"Unknown message type: " + type);
                    break;
            }
        } catch (Exception e) {
            log.log(Level.SEVERE,() ->"Error processing message: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status){
        sessions.remove(session.getId());
    }

    private void broadcastState() throws Exception {
        Map<String, Player> players = this.invadersGUI.getPlayers();
        for (WebSocketSession session : sessions.values()) {
            try {
                String state = getPlayerState(players);
                session.sendMessage(new TextMessage(state));
                state = getActorsState(this.invadersGUI.getActors());
                session.sendMessage(new TextMessage(state));
            } catch (IOException e) {
                log.log(Level.WARNING, () -> "Error broadcasting state to session " + session.getId() + "  ERROR:\n " + e.getMessage());
            }
        }
    }

    private String getActorsState(ArrayList<Actor> actors) {
        JSONObject json = new JSONObject();
        int count = 0;
        for (Actor a : actors) {
            JSONObject actorJson = new JSONObject();
            actorJson.put("type", a.getClass().getSimpleName());
            actorJson.put("x", a.getX());
            actorJson.put("y", a.getY());
            actorJson.put("deletion", a.isMarkedForRemoval());
            json.put(Integer.toString(count++), actorJson);
        }

        return json.toString();
    }

    private String getPlayerState(Map<String, Player> players) {
        JSONObject json = new JSONObject();

        for (Map.Entry<String, Player> entry : players.entrySet()) {
            Player player = entry.getValue();
            JSONObject playerJson = new JSONObject();
            playerJson.put("name", player.getName());
            playerJson.put("x", player.getX());
            playerJson.put("y", player.getY());
            playerJson.put("life", player.getShields());
            playerJson.put("loose", player.isLoose());
            json.put(entry.getKey(), playerJson);
        }

        return json.toString();
    }

    private void handleNameChange(String clientId, String newName) {
        Player player = this.invadersGUI.getPlayer(clientId);
        if (player != null) {
            player.setName(newName);
            log.log(Level.INFO,() ->"Player " + clientId + " changed name to " + newName);
        } else {
            log.log(Level.WARNING,() ->"Player with ID " + clientId + " not found.");
        }
    }

    public void handleKeyEvent(KeyEventDTO keyEventDTO, String id) {
        KeyEvent keyEvent = new KeyEvent(
                invadersGUI,
                keyEventDTO.getType().equals(KEY_DOWN) ? KeyEvent.KEY_PRESSED : KeyEvent.KEY_RELEASED,
                System.currentTimeMillis(),
                0,
                keyEventDTO.getKeyCode(),
                KeyEvent.CHAR_UNDEFINED);
        if (keyEventDTO.getType().equals(KEY_DOWN)) {
            invadersGUI.multiKeyPressed(keyEvent, id);
        } else {
            invadersGUI.multiKeyReleased(keyEvent, id);
        }

    }

    public static class KeyEventDTO {
        private String type;
        private int keyCode;

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public int getKeyCode() {
            return keyCode;
        }

        public void setKeyCode(int keyCode) {
            this.keyCode = keyCode;
        }
    }

    @GetMapping("/send")
    @CrossOrigin
    public void dbTest() {
        Map<String, Player> players = this.invadersGUI.getPlayers();
        for (Map.Entry<String, Player> player : players.entrySet()) {
            Boolean xd = !playerUpdateSent.getOrDefault(player.getValue().getId(), false);
            if (player != null && player.getValue().isLoose() && Boolean.TRUE.equals(xd)) {
                scores.save(new User(player.getValue().getId(), Integer.toString(player.getValue().getScore()) , player.getValue().getName()));
                playerUpdateSent.put(player.getValue().getId(), true);
            }
        }
       log.log(Level.INFO,()->"REQUEST SENT");
    }

    @GetMapping("/scores")
    @CrossOrigin
    public List<User> getScores(){
        return scores.findAllUsersOrderByScoreDesc(Sort.by(Direction.DESC, "score"));
    }
}
