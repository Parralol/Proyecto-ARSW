package edu.escuelaing.arsw.ase.app.controller;

import edu.escuelaing.arsw.ase.app.model.Actor;
import edu.escuelaing.arsw.ase.app.model.InvadersGUI;
import edu.escuelaing.arsw.ase.app.model.Player;

import org.json.JSONObject;

import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;


import java.awt.event.KeyEvent;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

@SuppressWarnings("static-access")
public class InvadersController extends TextWebSocketHandler{

    private static InvadersGUI invadersGUI;

    private final Map<String, WebSocketSession> sessions = new ConcurrentHashMap<>();
 
    Logger log = Logger.getLogger(getClass().getName());
    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

    public InvadersController() {
        getInvaders();
        new Thread(() -> {
            invadersGUI.game();      
        }).start();
         scheduler.scheduleAtFixedRate(() -> {
            try {
                broadcastState();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }, 0, 40, TimeUnit.MILLISECONDS);
    }

    public static InvadersGUI getInvaders(){
        if(invadersGUI == null){
            invadersGUI = new InvadersGUI();
        }
        return invadersGUI;
    }

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        sessions.put(session.getId(), session);

        this.invadersGUI.addPlayer(session.getId(), session.getId());
        // Initialize player data and store it in the map

        session.sendMessage(new TextMessage("Connection established."));
        // Send player ID to the client
        session.sendMessage(new TextMessage("Player ID:" + session.getId()));
        log.log(Level.INFO, () -> session.getId() + " INITIALIZED");
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
            String clientId = session.getId();
            String payload = message.getPayload();
        
            System.out.println("Client " + clientId + " sent: " + payload);
        
            try {
                JSONObject obj = new JSONObject(payload);
                String type = obj.getString("type");
        
                switch (type) {
                    case "keydown":
                    case "keyup":
                        // Handle key events
                        KeyEventDTO press = new KeyEventDTO();
                        press.setKeyCode(obj.getInt("keyCode"));
                        press.setType(type);
                        handleKeyEvent(press, clientId);
                        break;
        
                    case "nameChange":
                        // Handle name change
                        String newName = obj.getString("name");
                        handleNameChange(clientId, newName);
                        break;
        
                    default:
                        System.err.println("Unknown message type: " + type);
                        break;
                }
            } catch (Exception e) {
                System.err.println("Error processing message: " + e.getMessage());
                e.printStackTrace();
            }
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        sessions.remove(session.getId());
        //players.remove(session.getId());
        // Cleanup, if needed
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
                log.log(Level.WARNING, "Error broadcasting state to session " + session.getId(), e);
            }
        }
    }

    private String getActorsState(ArrayList<Actor> actors){
        JSONObject json = new JSONObject();
        int count = 0;
        for (Actor a : actors) {
            JSONObject actorJson = new JSONObject();
            actorJson.put("type", a.getClass().getSimpleName());
            actorJson.put("x", a.getX());
            actorJson.put("y", a.getY());
            actorJson.put("deletion", a.isMarkedForRemoval());
            json.put( Integer.toString(count++), actorJson);
        }
        
        return json.toString();
    }
    private String getPlayerState(Map<String, Player> players) {
        // Convert player data to JSON string
        JSONObject json = new JSONObject();
        
        for (Map.Entry<String, Player> entry : players.entrySet()) {
            Player player = entry.getValue();
            JSONObject playerJson = new JSONObject();
            playerJson.put("name", player.getName());
            playerJson.put("x", player.getX());
            playerJson.put("y", player.getY());
            playerJson.put("life", player.getShields());
            playerJson.put("score", player.getScore());
            playerJson.put("id", entry.getKey()); 
            json.put(entry.getKey(), playerJson);
        }
        
        return json.toString();
    }
    @SuppressWarnings("unused")
    private String getSharedState() {

        return "Current state";
    }

    private void handleNameChange(String clientId, String newName) {
        // Update the player name
        Player player = this.invadersGUI.getPlayer(clientId);
        if (player != null) {
            player.setName(newName);
            System.out.println("Player " + clientId + " changed name to " + newName);
        } else {
            System.err.println("Player with ID " + clientId + " not found.");
        }
    }
    public void handleKeyEvent(KeyEventDTO keyEventDTO, String id) {
        KeyEvent keyEvent = new KeyEvent(
                invadersGUI,
                keyEventDTO.getType().equals("keydown") ? KeyEvent.KEY_PRESSED : KeyEvent.KEY_RELEASED,
                System.currentTimeMillis(),
                0,
                keyEventDTO.getKeyCode(),
                KeyEvent.CHAR_UNDEFINED);
        Map<String, Player> xd = this.invadersGUI.getPlayers();
        //xd.entrySet().forEach(System.out::println);
        System.out.println(getPlayerState(xd));
        //System.out.println(getActorsState(this.invadersGUI.getActors()));
        if (keyEventDTO.getType().equals("keydown")) {
            invadersGUI.multiKeyPressed(keyEvent, id);
            //invadersGUI.keyPressed(keyEvent);
        } else {
            invadersGUI.multiKeyReleased(keyEvent, id);
            //invadersGUI.keyReleased(keyEvent);
        }
    }

    public static class KeyEventDTO {
        private String type;
        private int keyCode;

        // Getters and setters
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
}