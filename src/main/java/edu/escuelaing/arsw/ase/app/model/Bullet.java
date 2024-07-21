package edu.escuelaing.arsw.ase.app.model;

public class Bullet extends Actor {
    protected static final int BULLET_SPEED = 10;
    public Bullet(Stage stage, String id) {
        super(stage);
        this.id = id;
        setSpriteNames(new String[] { "misil.gif" });
        setFrameSpeed(35);
    }

    public void act() {
        super.act();
        y -= BULLET_SPEED;
        if (y < 0)
            remove();
    }
}
