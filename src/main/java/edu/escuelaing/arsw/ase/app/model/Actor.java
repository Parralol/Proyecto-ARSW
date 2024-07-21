package edu.escuelaing.arsw.ase.app.model;

import java.awt.Rectangle;
import java.awt.image.BufferedImage;

public class Actor {
  protected int x, y;
  protected int width, height;
  protected String[] spriteNames;
  protected int currentFrame;
  protected Stage stage;
  protected Scache spriteCache;

  protected int frameSpeed;
  protected int t;
  protected boolean markedForRemoval = false;

  public Actor(Stage stage) {
    this.stage = stage;
    spriteCache = stage.getScache();
  }

  public void remove() {
    markedForRemoval = true;
  }

  public Rectangle getBounds() {
    return new Rectangle(x, y, width, height);
  }

  public void collision(Actor a) {
  }

  public boolean isMarkedForRemoval() {
    return markedForRemoval;
  }

  public int getX() {
    return x;
  }

  public void setX(int i) {
    x = i;
  }

  public int getY() {
    return y;
  }

  public void setY(int i) {
    y = i;
  }

  public void setSpriteNames(String[] names) {
    spriteNames = names;
    height = 0;
    width = 0;
    for (int i = 0; i < names.length; i++) {
      BufferedImage image = spriteCache.getSprite(spriteNames[i]);
      height = Math.max(height, image.getHeight());
      width = Math.max(width, image.getWidth());
    }
  }

  public int getHeight() {
    return height;
  }

  public int getWidth() {
    return width;
  }

  public void setHeight(int i) {
    height = i;
  }

  public void setWidth(int i) {
    width = i;
  }

  public int getFrameSpeed() {
    return frameSpeed;
  }

  public void setFrameSpeed(int frameSpeed) {
    this.frameSpeed = frameSpeed;
  }

  public void act() {
    t++;
    if (t % frameSpeed == 0) {
      t = 0;
      currentFrame = (currentFrame + 1) % spriteNames.length;
    }
  }
}
