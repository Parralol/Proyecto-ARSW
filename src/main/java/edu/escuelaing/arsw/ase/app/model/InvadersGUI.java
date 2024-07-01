package edu.escuelaing.arsw.ase.app.model;

import java.awt.Canvas;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferStrategy;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Random;

import javax.swing.JFrame;
import javax.swing.JPanel;

public class InvadersGUI extends Canvas implements Stage, KeyListener {

  private BufferStrategy strategy;
  private long usedTime;
  private JFrame ventana;
  private JPanel panel;
  private Scache spriteCache;
  @SuppressWarnings("rawtypes")
  private ArrayList actors;

  private Player player;

  private boolean gameEnded = false;

  public InvadersGUI() {
    prepareElements();
    prepareMethods();
  }

  public Scache getScache() {
    return spriteCache;
  }

  private void prepareElements() {
    spriteCache = new Scache();
    ventana = new JFrame("Invaders");
    panel = (JPanel) ventana.getContentPane();
    setBounds(0, 0, Stage.WIDTH, Stage.HEIGHT);
    panel.setPreferredSize(new Dimension(Stage.WIDTH, Stage.HEIGHT));
    panel.setLayout(null);
    panel.add(this);
    ventana.setBounds(0, 0, Stage.WIDTH, Stage.HEIGHT);
    ventana.setVisible(true);
    ventana.setResizable(false);
    createBufferStrategy(2);
    strategy = getBufferStrategy();
    requestFocus();
  }

  private void prepareMethods() {
    ventana.addWindowListener(new WindowAdapter() {
      public void windowClosing(WindowEvent e) {
        System.exit(0);
      }
    });

    addKeyListener(this);
  }

  @SuppressWarnings({ "unchecked", "rawtypes" })
  public void initWorld() {
    actors = new ArrayList();
    Random rand = new Random();

    for (int i = 0; i < 10; i++) {
      int rand_int1 = rand.nextInt(10);
      int rand_int2 = rand.nextInt(10);
      if(rand_int1 == 5 || rand_int2 == 5){
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
      Actor m = (Actor) actors.get(i);
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
    Graphics2D g = (Graphics2D) strategy.getDrawGraphics();
    g.setColor(Color.black);
    g.fillRect(0, 0, getWidth(), getHeight());
    for (int i = 0; i < actors.size(); i++) {
      Actor m = (Actor) actors.get(i);
      m.paint(g);
    }
    player.paint(g);
    paintStatus(g);
    g.setColor(Color.white);
    strategy.show();
  }

  public Scache getSpriteCache() {
    return spriteCache;
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
      g.drawImage(bomb, xBase + i * bomb.getWidth(), Stage.PLAY_HEIGHT, this);
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
    while (isVisible() && !gameEnded) {

      long startTime = System.currentTimeMillis();
      updateWorld();
      checkCollisions();
      paintWorld();
      usedTime = System.currentTimeMillis() - startTime;
      try {
        Thread.sleep(SPEED);
      } catch (InterruptedException e) {
      }
    }
      paintGameOver();
    

  }

  public void paintGameOver() {
    Graphics2D g = (Graphics2D) strategy.getDrawGraphics();
    g.setColor(Color.white);
    g.setFont(new Font("Arial", Font.BOLD, 20));
    g.drawString("GAME OVER", Stage.WIDTH / 2 - 50, Stage.HEIGHT / 2);
    strategy.show();
  }

  public void checkCollisions() {
    Rectangle playerBounds = player.getBounds();
    for (int i = 0; i < actors.size(); i++) {
      Actor a1 = (Actor) actors.get(i);
      Rectangle r1 = a1.getBounds();
      if (r1.intersects(playerBounds)) {
        player.collision(a1);
        a1.collision(player);
      }
      for (int j = i + 1; j < actors.size(); j++) {
        Actor a2 = (Actor) actors.get(j);
        Rectangle r2 = a2.getBounds();
        if (r1.intersects(r2)) {
          a1.collision(a2);
          a2.collision(a1);
        }
      }
    }
  }

  public Player getPlayer() {
    return player;
  }

  public static void main(String[] args) {
    InvadersGUI inv = new InvadersGUI();
    inv.game();
  }

  public void keyTyped(KeyEvent e) {
    // TODO Auto-generated method stub
  }

  public void keyPressed(KeyEvent e) {
    player.keyPressed(e);

  }

  public void keyReleased(KeyEvent e) {
    player.keyReleased(e);
  }

  @SuppressWarnings("unchecked")
  public void addActor(Actor a) {
    actors.add(a);
  }

}
