package edu.escuelaing.arsw.ase.app.model;

import java.util.Random;

public class Monster extends Actor {
    protected int vx;
    protected static final double FIRING_FREQUENCY = 0.05;

    public Monster(Stage stage) {
        super(stage);
        setSpriteNames(new String[] { "bicho.gif", "bicho1.gif" });
        setFrameSpeed(35);
    }

    public void act() {
        super.act();
        x += vx;
        if (x < 0 || x > Stage.WIDTH)
            vx = -vx;
        if (Math.random() < FIRING_FREQUENCY)
            fire();
    }

    public int getVx() {
        return vx;
    }

    public void setVx(int i) {
        vx = i;
    }

    public void fire() {
        Laser m = new Laser(stage);
        m.setX(x + getWidth() / 2);
        m.setY(y + getHeight());
        stage.addActor(m);
    }

    public void spawn() {
        Random rand = new Random();
        Monster m;
        int rand_int1 = rand.nextInt(2);
        switch(rand_int1){
            case 0:
                m = new Crab(stage);
                m.setX((int) (Math.random() * Stage.WIDTH));
                m.setY((int) (Math.random() * Stage.PLAY_HEIGHT / 2));
                m.setVx((int) (Math.random() * 20 - 10));
            default:
                m = new Monster(stage);
                m.setX((int) (Math.random() * Stage.WIDTH));
                m.setY((int) (Math.random() * Stage.PLAY_HEIGHT / 2));
                m.setVx((int) (Math.random() * 20 - 10));
        }
        
        stage.addActor(m);
    }

    public void collision(Actor a) {
        if (a instanceof Bullet || a instanceof Bomb) {
            remove();
            spawn();
            stage.getPlayer().addScore(20);
        }
    }
}
