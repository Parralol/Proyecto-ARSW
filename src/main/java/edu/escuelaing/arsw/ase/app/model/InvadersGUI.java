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



/**
 * InvadersGUI class
 */
@SuppressWarnings({ "rawtypes", "unchecked" })
public class InvadersGUI extends Canvas implements Stage, KeyListener {

    private long usedTime;
    private Scache spriteCache;
    private Queue<Actor> actors;
    private HashMap<String, Player> players;
    


    private boolean gameEnded = false;

    /**
     * Public InvadersGUI constructor
     */
    public InvadersGUI() {
        prepareElements();
        prepareMethods();
    }


    /**
     * returns the sprite in a generated cache
     */
    public Scache getScache() {
        return spriteCache;
    }

    /**
     * Returns the player with a given Id
     * 
     * @param id session Id
     * @return the player with the given ID
     */
    public Player getPlayer(String id) {
        return players.get(id);
    }

    /**
     * Creates a players and adds it to the game
     * 
     * @param id   Session id
     * @param name Player name
     */
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

    /**
     * Prepares the InvadersGUI elements
     */
    private void prepareElements() {
        spriteCache = new Scache();
        players = new HashMap<>();
    }

    /**
     * Prepares the game methods
     */
    private void prepareMethods() {
        addKeyListener(this);
    }

    /**
     * Constructs the world
     */
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

    /**
     * Generates a monster randomly
     * 
     * @param i the previous monster positon
     */
    private void generateMonster(int i) {
        Random rand = new Random();
        int randInt1 = rand.nextInt(5);
        int randInt2 = rand.nextInt(5);
        if (randInt1 == 1 || randInt2 == 3) {
            Crab m = new Crab(this);
            m.setX((int) (Math.random() * Stage.WIDTH));
            m.setY(i);
            m.setVx((int) (Math.random() * 20 - 10));
            actors.add(m);
        } else {
            Monster m = new Monster(this);
            m.setX((int) (Math.random() * Stage.WIDTH));
            m.setY(i);
            m.setVx((int) (Math.random() * 20 - 10));
            actors.add(m);
        }
    }

    /**
     * Updates the InvadersGUI
     */
    public void updateWorld() {
        Iterator<Actor> iterator = actors.iterator();
        while (iterator.hasNext()) {
            Actor m = iterator.next();
            if (m.isMarkedForRemoval()) {
                iterator.remove();
                if (m instanceof Monster) {
                    generateMonster(m.getY());
                }
            } else {
                m.act();
            }
        }

        // Iterate over players to update their state
        for (Map.Entry<String, Player> entry : players.entrySet()) {
            if(entry.getValue().isLoose()){
                
            }else{
                entry.getValue().act();
            }
        }
    }

    /**
     * Initiates the gameover
     */
    public void gameOver() {
        gameEnded = true;
    }

    /**
     * Game Method
     */
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

    /**
     * Checks collision between game actors
     */
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

    /**
     * returns the use time
     * 
     * @return the update
     */
    public long getUsedTime() {
        return usedTime;
    }

    /**
     * Set use time
     * 
     * @param usedTime the use time
     */
    public void setUsedTime(long usedTime) {
        this.usedTime = usedTime;
    }

    /**
     * Returns the spriteCache
     * 
     * @return sprite cache
     */
    public Scache getSpriteCache() {
        return spriteCache;
    }

    /**
     * Sets the spritecache
     * 
     * @param spriteCache the sprite cache you will use
     */
    public void setSpriteCache(Scache spriteCache) {
        this.spriteCache = spriteCache;
    }

    /**
     * returns actors
     * 
     * @return ArrayList<Actor> the actors
     */
    public ArrayList<Actor> getActors() {
        return new ArrayList(Arrays.asList(actors.toArray()));
    }

    /**
     * Sets the Actors
     * 
     * @param actors the actors that are going to be used
     */
    public void setActors(ArrayList<Actor> actors) {
        this.actors = new LinkedList<>(actors);
    }

    /**
     * adds a actor
     */
    public void addActor(Actor a) {
        actors.add(a);
    }

    /**
     * Checks if the game has ended
     * 
     * @return true if the game has ended, false otherwise
     */
    public boolean isGameEnded() {
        return gameEnded;
    }

    /**
     * Change a player name given the id
     * 
     * @param id   Player id
     * @param name Player new name
     */
    public void changePlayerName(String id, String name) {
        getPlayer(id).setName(name);
    }

    /**
     * Sets the game end
     * 
     * @param gameEnded true if the game has ended, false if otherwise
     */
    public void setGameEnded(boolean gameEnded) {
        this.gameEnded = gameEnded;
    }

    public void keyTyped(KeyEvent e) {
        // No implementation needed
    }

    /**
     * Returns the players in the game
     */
    public Map<String, Player> getPlayers() {
        return new HashMap<>(players);
    }

    /**
     * Key entered by any player
     * 
     * @param e  the given key event
     * @param id the player id
     */
    public void multiKeyPressed(KeyEvent e, String id) {
        if(!this.players.get(id).isLoose()){
            this.players.get(id).keyPressed(e);
        }
    }

    /**
     * Key released by any player
     * 
     * @param e  the given key event
     * @param id the player id
     */
    public void multiKeyReleased(KeyEvent e, String id) {
        if(!this.players.get(id).isLoose()){
            players.get(id).keyReleased(e);
        }
    }

    public void keyPressed(KeyEvent e) {

    }

    public void keyReleased(KeyEvent e) {

    }

}
