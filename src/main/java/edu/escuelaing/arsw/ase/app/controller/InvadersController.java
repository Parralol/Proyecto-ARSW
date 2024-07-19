package edu.escuelaing.arsw.ase.app.controller;

import edu.escuelaing.arsw.ase.app.model.InvadersGUI;
import edu.escuelaing.arsw.ase.app.model.Player;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
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

    private final String HOME_VIEW_COUNT = "HOME_VIEW_COUNT";

    private final InvadersGUI invadersGUI;

    private final Map<String, WebSocketSession> sessions = new ConcurrentHashMap<>();
    private final Map<String, Player> players = new ConcurrentHashMap<>(); // Store player data
 
    Logger log;
    public InvadersController() {
        this.invadersGUI = new InvadersGUI();
        new Thread(() -> {
            invadersGUI.game();
            
        }).start();
    }

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        sessions.put(session.getId(), session);

        invadersGUI.addPlayer(session.getId(), "HOME_VIEW_COUNT");
        // Initialize player data and store it in the map
        
        session.sendMessage(new TextMessage("Connection established."));
        
        // Additional setup, if needed

        log.log(Level.INFO, () -> session.getId() + " INITIALIZED");
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        String clientId = session.getId();
        String payload = message.getPayload();

        System.out.println("Client " + clientId + " sent: " + payload);

        // Retrieve player data based on the session ID
        Player player = players.get(clientId);
        if (player != null) {
            updatePlayerData(player, payload);
        }

        // Optionally broadcast the updated state to all clients
        broadcastState();
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        sessions.remove(session.getId());
        players.remove(session.getId());
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
/** 
    @GetMapping("/")
    public String home(Principal principal, HttpSession session) {
        incrementCount(session, HOME_VIEW_COUNT);
        return "hello, " + principal.getName();
    }

    @GetMapping("/count")
    public String count(HttpSession session) {
        return "HOME VIEW COUNT: " + session.getAttribute(HOME_VIEW_COUNT);
    }

    private void incrementCount(HttpSession session, String attr) {
        var homeViewCount = session.getAttribute(attr) == null ? 0 : (Integer) session.getAttribute(attr);
        session.setAttribute(attr, homeViewCount += 1);
    }
*/
    @GetMapping(value = "/game/image", produces = "image/png")
    public byte[] getGameImage() throws IOException {
        BufferedImage gameImage = invadersGUI.getGameImage();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ImageIO.write(gameImage, "png", baos);
        return baos.toByteArray();
    }

    @PostMapping("/game/key")
    @CrossOrigin
    public void handleKeyEvent(@RequestBody KeyEventDTO keyEventDTO) {
        KeyEvent keyEvent = new KeyEvent(
                invadersGUI,
                keyEventDTO.getType().equals("keydown") ? KeyEvent.KEY_PRESSED : KeyEvent.KEY_RELEASED,
                System.currentTimeMillis(),
                0,
                keyEventDTO.getKeyCode(),
                KeyEvent.CHAR_UNDEFINED);
        if (keyEventDTO.getType().equals("keydown")) {
            invadersGUI.keyPressed(keyEvent);
        } else {
            invadersGUI.keyReleased(keyEvent);
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