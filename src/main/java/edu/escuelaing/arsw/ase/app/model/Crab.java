package edu.escuelaing.arsw.ase.app.model;

import java.util.Random;

public class Crab extends Monster{
    public int consY;

    public Crab(Stage stage) {
        super(stage);
        setSpriteNames(new String[] { "Crab.gif", "Crab1.gif" });
        Random rand = new Random();
        setFrameSpeed(35);
        int rand_int1 = rand.nextInt(150);
        consY = rand_int1;
    }

    @Override
    public void act() {
        super.act();
        x += vx;
        if (x < 0 || x > Stage.WIDTH)
            vx = -vx;
        super.setY((int)(16* Math.cos( x/16))+consY);
    }

    public int getVx() {
        return vx;
    }

    public void setVx(int i) {
        vx = i;
    }

    @Override
    public void collision(Actor a) {
        if (a instanceof Bullet || a instanceof Bomb){
            remove();
            spawn();
            stage.getPlayer().addScore(40);
        }
    }
}
