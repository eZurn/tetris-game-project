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
public class LShape1 extends Tetronimo
{
    /**
     * Creates the tetronimo and puts it in the vertical orientation
     */
    public LShape1()
    {
        super(Color.BLUE);
        super.r1.setLocation( 0, 0 );
        super.r2.setLocation( 0, Tetronimo.SIZE );
        super.r3.setLocation( Tetronimo.SIZE, Tetronimo.SIZE);
        super.r4.setLocation( Tetronimo.SIZE*2, Tetronimo.SIZE);

        init();
    }

    /**
     * Rotates the tetronimo
     */
    @Override
    public void rotate()
    {
        super.rotate();

        Point curLoc = super.getLocation();
        super.setLocation( 0, 0 );

        switch( this.curRotation % 4){
            case 0:
                super.r1.setLocation( 0, 0);
                super.r2.setLocation( 0, Tetronimo.SIZE );
                super.r3.setLocation( Tetronimo.SIZE, Tetronimo.SIZE );
                super.r4.setLocation( Tetronimo.SIZE*2, Tetronimo.SIZE);
                break;
            case 1:
                super.r1.setLocation( Tetronimo.SIZE, 0);
                super.r2.setLocation( Tetronimo.SIZE, Tetronimo.SIZE);
                super.r3.setLocation( Tetronimo.SIZE, Tetronimo.SIZE*2);
                super.r4.setLocation( 0, Tetronimo.SIZE*2);
                break;
            case 2:
                super.r1.setLocation( 0, 0);
                super.r2.setLocation( Tetronimo.SIZE, 0 );
                super.r3.setLocation( Tetronimo.SIZE*2, 0);
                super.r4.setLocation( Tetronimo.SIZE*2, Tetronimo.SIZE);
                break;
            case 3:
                super.r1.setLocation( Tetronimo.SIZE, 0);
                super.r2.setLocation( 0, 0);
                super.r3.setLocation( 0, Tetronimo.SIZE);
                super.r4.setLocation( 0, Tetronimo.SIZE*2);
                break;
        }

        super.setLocation( curLoc );
    }

    /**
     * Gets the height of the tetronimo based on the orientation
     *
     * @return The height of the tetronimo
     */
    @Override
    public int getHeight()
    {
        if (this.curRotation % 4 == 1 || this.curRotation % 4 == 3) {
            return Tetronimo.SIZE * 3;
        }
        else {
            return Tetronimo.SIZE * 2;
        }
    }

    /**
     * Gets the width of the tetronimo based on the orientation
     *
     * @return The width of the tetronimo
     */
    @Override
    public int getWidth()
    {
        if (this.curRotation % 4 == 1 || this.curRotation % 4 == 3) {
            return Tetronimo.SIZE * 2;
        }
        else {
            return Tetronimo.SIZE * 3;
        }
    }
}
