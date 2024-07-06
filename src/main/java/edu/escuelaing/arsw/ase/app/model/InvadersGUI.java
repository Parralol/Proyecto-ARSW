package edu.escuelaing.arsw.ase.app.model;

import java.awt.Canvas;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Random;

public class InvadersGUI extends Canvas implements Stage, KeyListener {

    private long usedTime;
    private Scache spriteCache;
    private ArrayList<Actor> actors;

    private Player player;

    private boolean gameEnded = false;

    private BufferedImage offScreenImage;
    private Graphics2D offScreenGraphics;

    public InvadersGUI() {
        prepareElements();
        prepareMethods();
        offScreenImage = new BufferedImage(Stage.WIDTH, Stage.HEIGHT, BufferedImage.TYPE_INT_ARGB);
        offScreenGraphics = offScreenImage.createGraphics();
    }

    public Scache getScache() {
        return spriteCache;
    }

    private void prepareElements() {
        spriteCache = new Scache();
    }

    private void prepareMethods() {
        addKeyListener(this);
    }

    public void initWorld() {
        actors = new ArrayList<>();
        Random rand = new Random();

        for (int i = 0; i < 10; i++) {
            int randInt1 = rand.nextInt(10);
            int randInt2 = rand.nextInt(10);
            if(randInt1 == 5 || randInt2 == 5){
                Crab m = new Crab(this);
                m.setX((int) (Math.random() * Stage.WIDTH));
                m.setY(i * 20);
                m.setVx((int) (Math.random() * 20 - 10));
                actors.add(m);
            }else{
                Monster m = new Monster(this);
                m.setX((int) (Math.random() * Stage.WIDTH));
                m.setY(i * 20);
                m.setVx((int) (Math.random() * 20 - 10));
                actors.add(m);
            }
        }
        player = new Player(this);
        player.setX(Stage.WIDTH / 2);
        player.setY(Stage.HEIGHT - 2 * player.getHeight());
        player.setVx(5);
        player.addShields(200);
    }

    public void updateWorld() {
        int i = 0;
        while (i < actors.size()) {
            Actor m = actors.get(i);
            if (m.isMarkedForRemoval()) {
                actors.remove(i);
            } else {
                m.act();
                i++;
            }
        }
        player.act();
    }

    public void paintWorld() {
        offScreenGraphics.setColor(Color.black);
        offScreenGraphics.fillRect(0, 0, Stage.WIDTH, Stage.HEIGHT);
        for (int i = 0; i < actors.size(); i++) {
            Actor m = actors.get(i);
            m.paint(offScreenGraphics);
        }
        player.paint(offScreenGraphics);
        paintStatus(offScreenGraphics);
        offScreenGraphics.setColor(Color.white);
    }

    public BufferedImage getGameImage() {
        return offScreenImage;
    }

    public void paintShields(Graphics2D g) {
        g.setPaint(Color.red);
        g.fillRect(280, Stage.PLAY_HEIGHT, Player.MAX_SHIELDS, 30);
        g.setPaint(Color.blue);
        g.fillRect(280 + Player.MAX_SHIELDS - player.getShields(), Stage.PLAY_HEIGHT, player.getShields(), 30);
        g.setFont(new Font("Arial", Font.BOLD, 20));
        g.setPaint(Color.green);
        g.drawString("Shields", 170, Stage.PLAY_HEIGHT + 20);
    }

    public void gameOver() {
        gameEnded = true;
    }

    public void paintScore(Graphics2D g) {
        g.setFont(new Font("Arial", Font.BOLD, 20));
        g.setPaint(Color.green);
        g.drawString("Score:", 20, Stage.PLAY_HEIGHT + 20);
        g.setPaint(Color.red);
        g.drawString(player.getScore() + "", 100, Stage.PLAY_HEIGHT + 20);
    }

    public void paintAmmo(Graphics2D g) {
        int xBase = 280 + Player.MAX_SHIELDS + 10;
        for (int i = 0; i < player.getClusterBombs(); i++) {
            BufferedImage bomb = spriteCache.getSprite("bombUL.gif");
            g.drawImage(bomb, xBase + i * bomb.getWidth(), Stage.PLAY_HEIGHT, null);
        }
    }

    public void paintfps(Graphics2D g) {
        g.setFont(new Font("Arial", Font.BOLD, 12));
        g.setColor(Color.white);
        if (usedTime > 0)
            g.drawString(String.valueOf(1000 / usedTime) + " fps", Stage.WIDTH - 50, Stage.PLAY_HEIGHT);
        else
            g.drawString("--- fps", Stage.WIDTH - 50, Stage.PLAY_HEIGHT);
    }

    public void paintStatus(Graphics2D g) {
        paintScore(g);
        paintShields(g);
        paintAmmo(g);
        paintfps(g);
    }

    public void game() {
        usedTime = 1000;
        initWorld();
        while (!gameEnded) {
            long startTime = System.currentTimeMillis();
            updateWorld();
            checkCollisions();
            paintWorld();
            usedTime = System.currentTimeMillis() - startTime;
            try {
                Thread.sleep(SPEED);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
        paintGameOver();
    }

    public void paintGameOver() {
        Graphics2D g = offScreenGraphics;
        g.setColor(Color.white);
        g.setFont(new Font("Arial", Font.BOLD, 20));
        g.drawString("GAME OVER", Stage.WIDTH / 2 - 50, Stage.HEIGHT / 2);
    }

    public void checkCollisions() {
        Rectangle playerBounds = player.getBounds();
        for (int i = 0; i < actors.size(); i++) {
            Actor a1 = actors.get(i);
            Rectangle r1 = a1.getBounds();
            if (r1.intersects(playerBounds)) {
                player.collision(a1);
                a1.collision(player);
            }
            for (int j = i + 1; j < actors.size(); j++) {
                Actor a2 = actors.get(j);
                Rectangle r2 = a2.getBounds();
                if (r1.intersects(r2)) {
                    a1.collision(a2);
                    a2.collision(a1);
                }
            }
        }
    }

    public long getUsedTime() {
        return usedTime;
    }

    public void setUsedTime(long usedTime) {
        this.usedTime = usedTime;
    }

    public Scache getSpriteCache() {
        return spriteCache;
    }

    public void setSpriteCache(Scache spriteCache) {
        this.spriteCache = spriteCache;
    }

    public ArrayList<Actor> getActors() {
        return actors;
    }

    public void setActors(ArrayList<Actor> actors) {
        this.actors = actors;
    }

    public void setPlayer(Player player) {
        this.player = player;
    }

    public boolean isGameEnded() {
        return gameEnded;
    }

    public void setGameEnded(boolean gameEnded) {
        this.gameEnded = gameEnded;
    }

    public Player getPlayer() {
        return player;
    }

    public void keyTyped(KeyEvent e) {
        // No implementation needed
    }

    public void keyPressed(KeyEvent e) {
        player.keyPressed(e);
    }

    public void keyReleased(KeyEvent e) {
        player.keyReleased(e);
    }

    public void addActor(Actor a) {
        actors.add(a);
    }
}

