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

    public Player(Stage stage) {
        super(stage);
        setSpriteNames(new String[] { "ship.gif" });
        setFrameSpeed(35);
        clusterBombs = 5;
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
        Bullet b = new Bullet(stage);
        b.setX(x);
        b.setY(y - b.getHeight());
        stage.addActor(b);
    }

    public int getClusterBombs() {
        return clusterBombs;
    }

    public void fireCluster() {
        if (clusterBombs == 0)
            return;

        clusterBombs--;
        stage.addActor(new Bomb(stage, Bomb.UP_LEFT, x, y));
        stage.addActor(new Bomb(stage, Bomb.UP, x, y));
        stage.addActor(new Bomb(stage, Bomb.UP_RIGHT, x, y));
        stage.addActor(new Bomb(stage, Bomb.LEFT, x, y));
        stage.addActor(new Bomb(stage, Bomb.RIGHT, x, y));
        stage.addActor(new Bomb(stage, Bomb.DOWN_LEFT, x, y));
        stage.addActor(new Bomb(stage, Bomb.DOWN, x, y));
        stage.addActor(new Bomb(stage, Bomb.DOWN_RIGHT, x, y));
    }

    public int getScore() {
        return score;
    }

    public void setScore(int i) {
        score = i;
    }

    public int getShields() {
        return shields;
    }

    public void setShields(int i) {
        shields = i;
    }

    public int getVx() {
        return vx;
    }

    public void setVx(int i) {
        vx = i;
    }

    public int getVy() {
        return vy;
    }

    public void setVy(int i) {
        vy = i;
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

    public void addShields(int i) {
        shields += i;
        if (shields > MAX_SHIELDS)
            shields = MAX_SHIELDS;
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

    public void addScore(int i) {
        score += i;
    }

    public void collision(Actor a) {
        if (a instanceof Laser) {
            a.remove();
            addShields(-10);

        }
        if (a instanceof Monster) {
            addShields(-40);
            if (getShields() < 0)
                stage.gameOver();
        }
    }
}
