package edu.escuelaing.arsw.ase.app.model;

/**
 * Bomb class
 */
public class Bomb extends Actor {
    public static final int UP_LEFT = 0;
    public static final int UP = 1;
    public static final int UP_RIGHT = 2;
    public static final int LEFT = 3;
    public static final int RIGHT = 4;
    public static final int DOWN_LEFT = 5;
    public static final int DOWN = 6;
    public static final int DOWN_RIGHT = 7;

    protected static final int BOMB_SPEED = 5;

    protected int vx;
    protected int vy;

    /**
     * Bomb constructor
     * 
     * @param stage the game stage
     * @param heading the direction of the bullet
     * @param x x position
     * @param y y position
     * @param id the player Id that show the bomb
     */
    public Bomb(Stage stage, int heading, int x, int y, String id) {
        super(stage);
        this.x = x;
        this.y = y;
        String sprite = "";
        this.id = id;
        switch (heading) {
            case UP_LEFT:
                vx = -BOMB_SPEED;
                vy = -BOMB_SPEED;
                sprite = "bombUL.gif";
                break;
            case UP:
                vx = 0;
                vy = -BOMB_SPEED;
                sprite = "bombU.gif";
                break;
            case UP_RIGHT:
                vx = BOMB_SPEED;
                vy = -BOMB_SPEED;
                sprite = "bombUR.gif";
                break;
            case LEFT:
                vx = -BOMB_SPEED;
                vy = 0;
                sprite = "bombL.gif";
                break;
            case RIGHT:
                vx = BOMB_SPEED;
                vy = 0;
                sprite = "bombR.gif";
                break;
            case DOWN_LEFT:
                vx = -BOMB_SPEED;
                vy = BOMB_SPEED;
                sprite = "bombDL.gif";
                break;
            case DOWN:
                vx = 0;
                vy = BOMB_SPEED;
                sprite = "bombD.gif";
                break;
            case DOWN_RIGHT:
                vx = BOMB_SPEED;
                vy = BOMB_SPEED;
                sprite = "bombDR.gif";
                break;
        }
        setSpriteNames(new String[] { sprite });
        setFrameSpeed(35);
    }

    public void act() {
        super.act();
        y += vy;
        x += vx;
        if (y < 0 || y > Stage.HEIGHT || x < 0 || x > Stage.WIDTH)
            remove();
    }
}