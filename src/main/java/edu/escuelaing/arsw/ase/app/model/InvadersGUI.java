package edu.escuelaing.arsw.ase.app.model;

import java.awt.Canvas;
import java.awt.Rectangle;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;
import java.util.Random;
import java.util.concurrent.ConcurrentLinkedQueue;

@SuppressWarnings({ "rawtypes", "unchecked" })
public class InvadersGUI extends Canvas implements Stage, KeyListener {

    private long usedTime;
    private Scache spriteCache;
    private Queue<Actor> actors;

    private HashMap<String, Player> players;

    private boolean gameEnded = false;

    public InvadersGUI() {
        prepareElements();
        prepareMethods();
    }

    public Scache getScache() {
        return spriteCache;
    }

    public Player getPlayer(String id) {
        return players.get(id);
    }

    public void addPlayer(String id, String name) {
        Player p = new Player(this);
        p.setX(Stage.WIDTH / 2);
        p.setY(Stage.HEIGHT - 2 * p.getHeight());
        p.setVx(5);
        p.addShields(200);
        p.setName(name);
        p.setId(id);
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
        for (int i = 0; i < 5; i++) {
            int randInt1 = rand.nextInt(5);
            int randInt2 = rand.nextInt(5);
            if (randInt1 == 1 || randInt2 == 3) {
                Crab m = new Crab(this);
                m.setX((int) (Math.random() * Stage.WIDTH));
                m.setY(i * 20);
                m.setVx((int) (Math.random() * 20 - 10));
                actors.add(m);
            } else {
                Monster m = new Monster(this);
                m.setX((int) (Math.random() * Stage.WIDTH));
                m.setY(i * 20);
                m.setVx((int) (Math.random() * 20 - 10));
                actors.add(m);
            }
        }
    }

    private void generateMonster(int i){
        Random rand = new Random();
        int randInt1 = rand.nextInt(5);
        int randInt2 = rand.nextInt(5);
        if (randInt1 == 1 || randInt2 == 3) {
                Crab m = new Crab(this);
                m.setX((int) (Math.random() * Stage.WIDTH));
                m.setY(i );
                m.setVx((int) (Math.random() * 20 - 10));
                actors.add(m);
        } else {
                Monster m = new Monster(this);
                m.setX((int) (Math.random() * Stage.WIDTH));
                m.setY( i);
                m.setVx((int) (Math.random() * 20 - 10));
                actors.add(m);
            }
        }
    
    public void updateWorld() {
        Iterator<Actor> iterator = actors.iterator();
        while (iterator.hasNext()) {
            Actor m = iterator.next();
            if (m.isMarkedForRemoval()) {
                iterator.remove();
                if(m instanceof Monster){
                    generateMonster(m.getY());
                }
            } else{
                m.act();
            }
        }

        // Iterate over players to update their state
        for (Map.Entry<String, Player> entry : players.entrySet()) {
            entry.getValue().act();
        }
    }

    public void gameOver() {
        gameEnded = true;
    }

    public void game() {
        usedTime = 1000;
        initWorld();
        while (!gameEnded) {
            long startTime = System.currentTimeMillis();
            updateWorld();
            checkCollisions();
            usedTime = System.currentTimeMillis() - startTime;
            try {
                Thread.sleep(SPEED);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
        // paintGameOver();
    }

    public void checkCollisions() {
        for (Map.Entry<String, Player> entry : players.entrySet()) {
            Player player = entry.getValue();
            Rectangle playerBounds = player.getBounds();

            for (Actor actor : actors) {
                if (actor == null)
                    continue;
                Rectangle actorBounds = actor.getBounds();

                if (playerBounds.intersects(actorBounds)) {
                    player.collision(actor);
                    actor.collision(player);
                }
            }
        }
        Iterator<Actor> iterator1 = actors.iterator();
        while (iterator1.hasNext()) {
            Actor a1 = iterator1.next();
            if (a1 == null)
                continue;
            Rectangle r1 = a1.getBounds();
            Iterator<Actor> iterator2 = actors.iterator();
            while (iterator2.hasNext()) {
                Actor a2 = iterator2.next();
                if (a2 == null || a1 == a2)
                    continue;
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

    public void addActor(Actor a) {
        actors.add(a);
    }

    public boolean isGameEnded() {
        return gameEnded;
    }

    public void changePlayerName(String id, String name) {
        getPlayer(id).setName(name);
    }

    public void setGameEnded(boolean gameEnded) {
        this.gameEnded = gameEnded;
    }

    public void keyTyped(KeyEvent e) {
        // No implementation needed
    }

    public Map<String, Player> getPlayers() {
        return new HashMap<>(players);
    }

    public void multiKeyPressed(KeyEvent e, String id) {
        this.players.get(id).keyPressed(e);
    }

    public void multiKeyReleased(KeyEvent e, String id) {
        players.get(id).keyReleased(e);
    }

    public void keyPressed(KeyEvent e) {


    }

    public void keyReleased(KeyEvent e) {

    }

}
