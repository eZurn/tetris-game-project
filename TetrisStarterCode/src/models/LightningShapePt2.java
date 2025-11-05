package models;

import java.awt.*;

/**
 * StraightLine.java:
 * Creates a straight line tetronimo
 *
 * @author Professor Rossi
 * @version 1.0 July 24, 2020
 *
 * @see java.awt.Point
 */
public class LightningShapePt2 extends Tetronimo {
    /**
     * Creates the tetronimo and puts it in the vertical orientation
     */
    public LightningShapePt2() {
        super(Color.RED);
        super.r1.setLocation(0, Tetronimo.SIZE);
        super.r2.setLocation(Tetronimo.SIZE, Tetronimo.SIZE);
        super.r3.setLocation(Tetronimo.SIZE, 0);
        super.r4.setLocation(Tetronimo.SIZE * 2, 0);

        init();
    }

    /**
     * Rotates the tetronimo
     */
    @Override
    public void rotate() {
        super.rotate(); // updates curRotation (probably mod 4 in Tetronimo)

        Point curLoc = super.getLocation();
        super.setLocation(0, 0);

        final int S = Tetronimo.SIZE;

        switch (super.curRotation % 2) {
            case 0:
                r1.setLocation(0, S);
                r2.setLocation(S, S);
                r3.setLocation(S, 0);
                r4.setLocation(2 * S, 0);
                break;

            case 1:
                r1.setLocation(0, 0);
                r2.setLocation(0, S);
                r3.setLocation(S, S);
                r4.setLocation(S, 2*S);
                break;

            default:
                break;
        }

        super.setLocation(curLoc);
    }

    /**
     * Gets the height of the tetronimo based on the orientation
     *
     * @return The height of the tetronimo
     */
    @Override
    public int getHeight() {
        // 0° and 180° orientations: 2 blocks tall
        // 90° and 270° orientations: 3 blocks tall
        int rot = this.curRotation % 4;
        if (rot == 0 || rot == 2) {
            return Tetronimo.SIZE * 2;
        } else {
            return Tetronimo.SIZE * 3;
        }
    }

    /**
     * Gets the width of the tetronimo based on the orientation
     *
     * @return The width of the tetronimo
     */
    @Override
    public int getWidth() {
        // 0° and 180° orientations: 3 blocks wide
        // 90° and 270° orientations: 2 blocks wide
        int rot = this.curRotation % 4;
        if (rot == 0 || rot == 2) {
            return Tetronimo.SIZE * 3;
        } else {
            return Tetronimo.SIZE * 2;
        }
    }
}

