package edu.escuelaing.arsw.ase.app.model;

import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.io.Serializable;
import java.util.Arrays;

/**
 * Actor class
 */
public class Actor implements Serializable{
  protected int x;
  protected int y;
  protected int width;
  protected int height;
  protected String[] spriteNames;
  protected int currentFrame;
  protected Stage stage;
  protected Scache spriteCache;

  protected String id;
  protected int frameSpeed;
  protected int t;
  protected boolean markedForRemoval;

  /**
   * Actor Constructor
   * @param stage the game stage
   */
  public Actor(Stage stage) {
    this.stage = stage;
    spriteCache = stage.getScache();
  }

  /**
   * Remove itself
   */
  public void remove() {
    markedForRemoval = true;
  }

  /**
   * Gets the bounds of the Actor
   * @return Rectangle object
   */
  public Rectangle getBounds() {
    return new Rectangle(x, y, width, height);
  }

  /**
   * Checks the collision of the actor
   * @param a actor that collided
   */
  public void collision(Actor a) {
    //NO COMPLETED DUE TO OVERRIDE NECESSITY
  }

  /**
   * Is it marked for removal?
   * @return returns true if marked for removal, false otherwise
   */
  public boolean isMarkedForRemoval() {
    return markedForRemoval;
  }

  /**
   * returns the X coordinate
   * @return integer 
   */
  public int getX() {
    return x;
  }

  /**
   * Sets actor id
   * 
   * @param id id of the actor
   */
  public void setId(String id){
    this.id = id;
  }

  /**
   * Gets actor Id
   * @return String id
   */
  public String getId(){
    return id;
  }

  /**
   * Set x coordinate
   * @param i
   */
  public void setX(int i) {
    x = i;
  }

  /**
   * gets the Y coordinate
   * @return
   */
  public int getY() {
    return y;
  }

  /**
   * Sets the Y coordinate
   * @param i the Y coordinate
   */
  public void setY(int i) {
    y = i;
  }

  /**
   * Sets sprites names
   * @param names List of names
   */
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

  /**
   * Returns the actor Height
   * @return integer height
   */
  public int getHeight() {
    return height;
  }

  /**
   * Returns the actor Height
   * @return integer width
   */
  public int getWidth() {
    return width;
  }

  /**
   * Sets Height for the actor
   * @param i integer hight
   */
  public void setHeight(int i) {
    height = i;
  }

  /**
   * Sets Height for the actor
   * @param i integer width
   */
  public void setWidth(int i) {
    width = i;
  }

  /**
   * Sets the frameSpeed
   * @return Integer framespeed
   */
  public int getFrameSpeed() {
    return frameSpeed;
  }

  /**
   * Sets the framespeed
   * @param frameSpeed The integer framespeed
   */
  public void setFrameSpeed(int frameSpeed) {
    this.frameSpeed = frameSpeed;
  }

  /**
   * the actor act
   */
  public void act() {
    t++;
    if (t % frameSpeed == 0) {
      t = 0;
      currentFrame = (currentFrame + 1) % spriteNames.length;
    }
  }

  @Override
  public String toString() {
    return "Actor [x=" + x + ", y=" + y + ", width=" + width + ", height=" + height + ", spriteNames="
        + Arrays.toString(spriteNames) + ", currentFrame=" + currentFrame + ", stage=" + stage + ", spriteCache="
        + spriteCache + ", id=" + id + ", frameSpeed=" + frameSpeed + ", t=" + t + ", markedForRemoval="
        + markedForRemoval + "]";
  }

  
}
