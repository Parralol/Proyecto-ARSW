package edu.escuelaing.arsw.ase.app.controller;

import edu.escuelaing.arsw.ase.app.model.InvadersGUI;
import edu.escuelaing.arsw.ase.app.model.Player;

import org.json.JSONObject;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;


import java.awt.event.KeyEvent;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;
import java.util.logging.Logger;


@RestController
public class InvadersController extends TextWebSocketHandler{

    private static InvadersGUI invadersGUI;

    private final Map<String, WebSocketSession> sessions = new ConcurrentHashMap<>();
 
    Logger log = Logger.getLogger(getClass().getName());

    public InvadersController() {
        getInvaders();
        new Thread(() -> {
            invadersGUI.game();      
        }).start();
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

        this.invadersGUI.addPlayer(session.getId(), "HOME_VIEW_COUNT");
        // Initialize player data and store it in the map
        System.out.println(this.invadersGUI.getPlayer(session.getId()));
        session.sendMessage(new TextMessage("Connection established."));
        session.sendMessage(new TextMessage(getGameImage()));
        // Additional setup, if needed
        // Send player ID to the client
        session.sendMessage(new TextMessage("Player ID:" + session.getId()));
        log.log(Level.INFO, () -> session.getId() + " INITIALIZED");
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        String clientId = session.getId();
        String payload = message.getPayload();

        System.out.println("Client " + clientId + " sent: " + payload);

        JSONObject obj = new JSONObject(payload);
        // Retrieve player data based on the session ID
        
        KeyEventDTO press = new KeyEventDTO();
        press.setKeyCode(obj.getInt("keyCode"));
        press.setType(obj.getString("type"));
        handleKeyEvent(press, clientId);

        // Optionally broadcast the updated state to all clients
        broadcastState();
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        sessions.remove(session.getId());
        //players.remove(session.getId());
        // Cleanup, if needed
    }

    private void updatePlayerData(Player player, String input) {
        // Implement your logic to update player data based on the input
        // For example, update the player's position, score, etc.
        System.out.println("Updating player data for " + player.getName());
    }

    private void broadcastState() throws Exception {
        String state = getSharedState(); // Retrieve the current shared state

        for (WebSocketSession s : sessions.values()) {
            s.sendMessage(new TextMessage("Updated state: " + state));
        }
    }

    private String getSharedState() {
        // Retrieve the shared state from your server logic
        return "Current state";
    }

    @GetMapping(value = "/game/image", produces = "image/png")
    public byte[] getGameImage() throws IOException {
        BufferedImage gameImage = invadersGUI.getGameImage();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ImageIO.write(gameImage, "png", baos);
        return baos.toByteArray();
    }


    public void handleKeyEvent(KeyEventDTO keyEventDTO, String id) {
        KeyEvent keyEvent = new KeyEvent(
                invadersGUI,
                keyEventDTO.getType().equals("keydown") ? KeyEvent.KEY_PRESSED : KeyEvent.KEY_RELEASED,
                System.currentTimeMillis(),
                0,
                keyEventDTO.getKeyCode(),
                KeyEvent.CHAR_UNDEFINED);
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