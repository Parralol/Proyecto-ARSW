package edu.escuelaing.arsw.ase.app.model;

import java.util.Random;

public class Ship extends Monster {

    public int consY;
    public int shields;

    /**
     * Contructor for the Crab class
     * 
     * @param stage the game stage
     */
    public Ship(Stage stage) {
        super(stage);
        setSpriteNames(new String[] { "Aship.gif", "Aship.gif" });
        Random rand = new Random();
        setFrameSpeed(35);
        int rand_int1 = rand.nextInt(150);
        consY = rand_int1;
        shields = 30;
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
            shields -= 10;
            if(shields <= 0 ){
                stage.getPlayers().get(a.getId()).addScore(50);
                remove();
            } 
            
        }
    }
}

