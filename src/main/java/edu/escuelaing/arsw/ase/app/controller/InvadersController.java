package edu.escuelaing.arsw.ase.app.controller;

import edu.escuelaing.arsw.ase.app.model.InvadersGUI;
import jakarta.servlet.http.HttpSession;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.awt.event.KeyEvent;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.security.Principal;

@RestController
public class InvadersController {

    private final String HOME_VIEW_COUNT = "HOME_VIEW_COUNT";

    private final InvadersGUI invadersGUI;

    public InvadersController() {
        this.invadersGUI = new InvadersGUI();
        new Thread(() -> {
            invadersGUI.game();
        }).start();
    }

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