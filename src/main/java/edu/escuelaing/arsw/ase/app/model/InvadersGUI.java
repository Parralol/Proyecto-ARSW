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
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;
import java.util.Random;
import java.util.concurrent.ConcurrentLinkedQueue;

public class InvadersGUI extends Canvas implements Stage, KeyListener {

    private long usedTime;
    private Scache spriteCache;
    private Queue<Actor> actors;

    private HashMap<String, Player> players;
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

    public Player getPlayer(String id){
        return players.get(id);
    }
    public void addPlayer(String id, String name){
        Player p = new Player(this);
        p.setX(Stage.WIDTH / 2);
        p.setY(Stage.HEIGHT - 2 * p.getHeight());
        p.setVx(5);
        p.addShields(200);
        p.setName(name);
        players.put(id, p);
    }
    private void prepareElements() {
        spriteCache = new Scache();
        players = new HashMap<>();
    }

    private void prepareMethods() {
        addKeyListener(this);
    }

    public void initWorld() {
        actors = new ConcurrentLinkedQueue<>();
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
        Iterator<Actor> iterator = actors.iterator();
        while (iterator.hasNext()) {
            Actor m = iterator.next();
            if (m != null && m.isMarkedForRemoval()) {
                iterator.remove(); // Safe removal
            } else if (m != null) {
                m.act();
            }
        }
    
        // Iterate over players to update their state
        for (Map.Entry<String, Player> entry : players.entrySet()) {
            entry.getValue().act();
        }
    
        // Update the single player (if this is part of your game logic)
        player.act();
    }
    public void paintWorld() {
        offScreenGraphics.setColor(Color.black);
        offScreenGraphics.fillRect(0, 0, Stage.WIDTH, Stage.HEIGHT);
        for (Actor m : actors) {
            if (m != null) {
                m.paint(offScreenGraphics);
            }
        }
        for (Map.Entry<String, Player> entry : players.entrySet()) {
            entry.getValue().paint(offScreenGraphics);
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
        Iterator<Actor> iterator1 = actors.iterator();
        while (iterator1.hasNext()) {
            Actor a1 = iterator1.next();
            if (a1 == null) continue;
            Rectangle r1 = a1.getBounds();
            if (r1.intersects(playerBounds)) {
                player.collision(a1);
                a1.collision(player);
            }

            Iterator<Actor> iterator2 = actors.iterator();
            while (iterator2.hasNext()) {
                Actor a2 = iterator2.next();
                if (a2 == null || a2 == a1) continue;
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
        return new ArrayList(Arrays.asList(actors.toArray()));
    }

    public void setActors(ArrayList<Actor> actors) {
        this.actors = new LinkedList<>(actors);
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
    public Map<String, Player> getPlayers() {
        return new HashMap<>(players);
    }
    public void multiKeyPressed(KeyEvent e, String id){
        this.players.get(id).keyPressed(e);
        if(players.get(id) != null){
            System.out.println(players.get(id) + "<--- ID \n" +players.get(id).getX() +" <-X POS \n" +players.get(id).getY()+ "<- Y POS");
        }
        
    }
    public void multiKeyReleased(KeyEvent e, String id){
        players.get(id).keyReleased(e);
    }
    public void keyPressed(KeyEvent e) {
        System.out.println(e.getKeyCode() + "<---258");
        player.keyPressed(e);
    }

    public void keyReleased(KeyEvent e) {
        player.keyReleased(e);
    }

    public void addActor(Actor a) {
        actors.add(a);
    }
}

