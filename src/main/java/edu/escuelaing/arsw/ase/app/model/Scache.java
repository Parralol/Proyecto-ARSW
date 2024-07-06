package edu.escuelaing.arsw.ase.app.model;

import java.awt.image.BufferedImage;
import java.io.FileInputStream;
import java.util.HashMap;
import javax.imageio.ImageIO;

public class Scache {
    @SuppressWarnings("rawtypes")
    private HashMap sprites;

    @SuppressWarnings("rawtypes")
    public Scache() {
            sprites = new HashMap();
          }

    private BufferedImage loadImage(String nombre) {
        try {
            return ImageIO.read(new FileInputStream(nombre));
        } catch (Exception e) {
            System.out.println("El error fue : " + e.getClass().getName() + " " + e.getMessage());
            System.exit(0);
            return null;
        }
    }

    @SuppressWarnings("unchecked")
    public BufferedImage getSprite(String nombre) {
        BufferedImage img = (BufferedImage) sprites.get(nombre);
        if (img == null) {
            img = loadImage("resources/" + nombre);
            sprites.put(nombre, img);
        }
        return img;
    }

    @SuppressWarnings("rawtypes")
    public HashMap getSprites() {
        return sprites;
    }
}
