package edu.escuelaing.arsw.ase.app.controller;

import edu.escuelaing.arsw.ase.app.model.InvadersGUI;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

@RestController
public class InvadersController {

    private final InvadersGUI invadersGUI;

    public InvadersController() {
        this.invadersGUI = new InvadersGUI();
        new Thread(() -> {
            invadersGUI.game();
        }).start();
    }

    @GetMapping(value = "/game/image", produces = "image/png")
    public byte[] getGameImage() throws IOException {
        BufferedImage gameImage = invadersGUI.getGameImage();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ImageIO.write(gameImage, "png", baos);
        return baos.toByteArray();
    }
}