package edu.escuelaing.arsw.ase.app.model;

/**
 * Monster class
 */
public class Monster extends Actor {
    protected int vx;
    protected static final double FIRING_FREQUENCY = 0.01;

    /**
     * Constructor for the monster class
     * 
     * @param stage the game stage
     */
    public Monster(Stage stage) {
        super(stage);
        setSpriteNames(new String[] { "bicho.gif", "bicho1.gif" });
        setFrameSpeed(35);
    }

    /**
     * Monster act
     */
    public void act() {
        super.act();
        x += vx;
        if (x < 0 || x > Stage.WIDTH)
            vx = -vx;
        if (Math.random() < FIRING_FREQUENCY)
            fire();
    }

    /**
     * Gets the velocity of the monster
     * 
     * @return
     */
    public int getVx() {
        return vx;
    }

    /**
     * Sets the velocity the monster
     * 
     * @param i the velocity
     */
    public void setVx(int i) {
        vx = i;
    }

    /**
     * Fires a laser
     */
    public void fire() {
        Laser m = new Laser(stage);
        m.setX(x + getWidth() / 2);
        m.setY(y + getHeight());
        stage.addActor(m);
    }

    /**
     * Checks the collision
     * 
     * @param a The given actors that collided
     */
    public void collision(Actor a) {
        if (a instanceof Bullet || a instanceof Bomb) {
            remove();
            stage.getPlayers().get(a.getId()).addScore(20);
        }
    }
}
