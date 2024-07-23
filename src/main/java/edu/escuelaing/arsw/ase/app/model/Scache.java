package edu.escuelaing.arsw.ase.app.model;

import java.awt.image.BufferedImage;
import java.io.FileInputStream;
import java.util.HashMap;
import javax.imageio.ImageIO;

/**
 * Public logic class Scache for image processing and handling
 */
@SuppressWarnings({ "rawtypes", "unchecked" })

public class Scache {

    private HashMap sprites;

    /**
     * public Scache constructor
     */
    public Scache() {
        sprites = new HashMap();
    }

    /**
     * Public method to load the image
     * 
     * @param nombre image name
     * @return BufferedImage
     */
    private BufferedImage loadImage(String nombre) {
        try {
            return ImageIO.read(new FileInputStream(nombre));
        } catch (Exception e) {
            System.out.println("El error fue : " + e.getClass().getName() + " " + e.getMessage());
            System.exit(0);
            return null;
        }
    }

    /**
     * Public method to get a sprite
     * 
     * @param nombre returns a sprite given the sprite name
     * @return BufferedImage
     */
    public BufferedImage getSprite(String nombre) {
        BufferedImage img = (BufferedImage) sprites.get(nombre);
        if (img == null) {
            img = loadImage("resources/" + nombre);
            sprites.put(nombre, img);
        }
        return img;
    }

    /**
     * Public method to obtain all sprites
     * 
     * @return HashMap containing the sprites
     */
    public HashMap getSprites() {
        return sprites;
    }
}
