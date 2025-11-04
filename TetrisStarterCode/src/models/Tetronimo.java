package models;
import java.util.Random;

import wheelsunh.users.Animator;
import wheelsunh.users.Rectangle;
import wheelsunh.users.ShapeGroup;
import java.awt.Color;
import java.awt.Point;


/**
 * Tetronimo.java:
 * An abstract class to model the base capaabilities of a tetronimo
 *
 * @author Professor Rossi
 * @version 1.0 July 24, 2020
 *
 * @see java.awt.Color
 */
public abstract class Tetronimo extends ShapeGroup
{
    /**
     * Constant to represent the size of the tetronimo
     */
    public static final int SIZE= 20;

    protected Rectangle r1;
    protected Rectangle r2;
    protected Rectangle r3;
    protected Rectangle r4;
    protected Color color;
    protected int curRotation = 0;

    /**
     * Generates the four rectangles for the tetronino and puts them on the screen, they are at the default coordinates
     * to start
     */
    public Tetronimo(Color c) {
        color = c;
        r1 = createRect(); r2 = createRect(); r3 = createRect(); r4 = createRect();
    }

    private Rectangle createRect() {
        Rectangle r = new Rectangle();
        r.setSize(SIZE, SIZE);
        r.setColor(color);

        r.setFrameColor(Color.BLACK);
        r.setFrameThickness(1);

        return r;
    }

    protected void init() {
        add(r1);
        add(r2);
        add(r3);
        add(r4);
    }
    /**
     * Increments the rotation of the tetronimo, other classes need to override this to provide the full functionality
     */
    public void rotate()
    {
        curRotation = (curRotation + 1) % 4;

    }

    /**
     * Shifts the tetronimo left one row
     */
    public void shiftLeft()
    {
        setLocation(getXLocation() - SIZE, getYLocation());
    }

    /**
     * Shifts the tetronimo right one row
     */
    public void shiftRight()
    {
        setLocation(getXLocation() + SIZE, getYLocation());
    }

    public void shiftDown()
    {
        setLocation(getXLocation(), getYLocation() + SIZE);
    }

    public void hide() {
        for (Rectangle r : getSquares()) {
            r.setColor(new Color(0, 0, 0, 0));  // fully transparent
        }
    }

    public void show() {
        Color original = getColor();
        for (Rectangle r : getSquares()) {
            r.setColor(original);
        }
    }

    public abstract int getHeight();
    public abstract int getWidth();

    public Rectangle[] getSquares() { return new Rectangle[]{r1, r2, r3, r4}; }
    public Color getColor() { return color; }
}