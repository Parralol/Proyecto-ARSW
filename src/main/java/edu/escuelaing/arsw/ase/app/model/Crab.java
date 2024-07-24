package edu.escuelaing.arsw.ase.app.model;

import java.util.Random;

/**
 * Public Monster Crab class
 */
public class Crab extends Monster {
    private int consY;

    /**
     * Contructor for the Crab class
     * 
     * @param stage the game stage
     */
    public Crab(Stage stage) {
        super(stage);
        setSpriteNames(new String[] { "Crab.gif", "Crab1.gif" });
        Random rand = new Random();
        setFrameSpeed(35);
        int randint1 = rand.nextInt(150);
        consY = randint1;
    }

    /**
     * The act Crab will execute
     */
    @Override
    public void act() {
        super.act();
        x += vx;
        if (x < 0 || x > Stage.WIDTH)
            vx = -vx;
        super.setY((int) (16 * Math.cos(x / 16)) + consY);
    }

    @Override
    public void collision(Actor a) {
        if (a instanceof Bullet || a instanceof Bomb) {
            remove();
            stage.getPlayers().get(a.getId()).addScore(20);
        }
    }
}
