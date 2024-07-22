package edu.escuelaing.arsw.ase.app.model;

import java.awt.event.KeyEvent;

public class Player extends Actor {

    protected static final int PLAYER_SPEED = 4;
    public static final int MAX_SHIELDS = 200;

    protected int vx;
    protected int vy;
    private boolean up, down, left, right;
    private int clusterBombs;

    private int score;
    private int shields;

    private String name;

    /**
     * Player Constructor
     * @param stage the game stage
     */
    public Player(Stage stage) {
        super(stage);
        setSpriteNames(new String[] { "ship.gif" });
        setFrameSpeed(35);
        clusterBombs = 5;
    }

    /**
     * Sets the player name
     * @param name String the player name
     */
    public void setName(String name){
        this.name = name;
    }

    /**
     * Returns the player name
     * @return String the player name
     */
    public String getName(){
        return name;
    }
   
    /**
     * Returns the player Score
     * @return Integer the player score
     */
    public int getScore() {
        return score;
    }

    /**
     * Sets the game score
     * @param i sets the player score
     */
    public void setScore(int i) {
        score = i;
    }

    /**
     * Returns the player shields
     * @return Integer shields
     */
    public int getShields() {
        return shields;
    }

    /**
     * Sets the player shields
     * @param i Integer the player Shields
     */
    public void setShields(int i) {
        shields = i;
    }

    /**
     * Returns the velocity of the player
     * @return Integer the velocitiy of the Player
     */
    public int getVx() {
        return vx;
    }

    /**
     * Sets the velocity of the player
     * @param i
     */
    public void setVx(int i) {
        vx = i;
    }

    /**
     * Returns the velocity of the Y axis 
     * @return  Integer velocity
     */
    public int getVy() {
        return vy;
    }

    /**
     * Sets the Y axis velocity
     * @param i Integer the Y velocity
     */
    public void setVy(int i) {
        vy = i;
    }

    public void addScore(int i) {
        score += i;
    }

    public void addShields(int i) {
        shields += i;
        if (shields > MAX_SHIELDS)
            shields = MAX_SHIELDS;
    }

    public int getClusterBombs() {
        return clusterBombs;
    }


    protected void updateSpeed() {
        vx = 0;
        vy = 0;
        if (down)
            vy = PLAYER_SPEED;
        if (up)
            vy = -PLAYER_SPEED;
        if (left)
            vx = -PLAYER_SPEED;
        if (right)
            vx = PLAYER_SPEED;
    }

    public void keyReleased(KeyEvent e) {
        switch (e.getKeyCode()) {
            case KeyEvent.VK_DOWN:
                down = false;
                break;
            case KeyEvent.VK_UP:
                up = false;
                break;
            case KeyEvent.VK_LEFT:
                left = false;
                break;
            case KeyEvent.VK_RIGHT:
                right = false;
                break;
            case KeyEvent.VK_SPACE:
                fire();
                break;
        }
        updateSpeed();
    }

    public void keyPressed(KeyEvent e) {
        switch (e.getKeyCode()) {
            case KeyEvent.VK_UP:
                up = true;
                break;
            case KeyEvent.VK_LEFT:
                left = true;
                break;
            case KeyEvent.VK_RIGHT:
                right = true;
                break;
            case KeyEvent.VK_DOWN:
                down = true;
                break;
            case KeyEvent.VK_B:
                fireCluster();
                break;
        }
        updateSpeed();
        
    }

    public void collision(Actor a) {
        if (a instanceof Laser) {
            a.remove();
            addShields(-10);
            //if (getShields() < 0) stage.gameOver();
            
        }
        if (a instanceof Monster) {
            addShields(-40);
        }
        if (getShields() < 0) stage.gameOver();
    }

    public void act() {
        super.act();
        x += vx;
        y += vy;
        if (x < 0)
            x = 0;
        if (x > Stage.WIDTH - getWidth())
            x = Stage.WIDTH - getWidth();
        if (y < 0)
            y = 0;
        if (y > Stage.PLAY_HEIGHT - getHeight())
            y = Stage.PLAY_HEIGHT - getHeight();
    }

    public void fire() {
        Bullet b = new Bullet(stage, id);
        b.setX(x);
        b.setY(y - b.getHeight());
        stage.addActor(b);
    }

    public void fireCluster() {
        if (clusterBombs == 0)
            return;

        clusterBombs--;
        stage.addActor(new Bomb(stage, Bomb.UP_LEFT, x, y, id));
        stage.addActor(new Bomb(stage, Bomb.UP, x, y, id));
        stage.addActor(new Bomb(stage, Bomb.UP_RIGHT, x, y, id));
        stage.addActor(new Bomb(stage, Bomb.LEFT, x, y, id));
        stage.addActor(new Bomb(stage, Bomb.RIGHT, x, y, id));
        stage.addActor(new Bomb(stage, Bomb.DOWN_LEFT, x, y, id));
        stage.addActor(new Bomb(stage, Bomb.DOWN, x, y, id));
        stage.addActor(new Bomb(stage, Bomb.DOWN_RIGHT, x, y, id));
    }

    @Override
    public String toString() {
        return "Player [x=" + x + ", y=" + y + ", score=" + score + ", shields=" + shields + ", name=" + name + "]";
    }
}
