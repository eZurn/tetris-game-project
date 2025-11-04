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
public class LightningShape extends Tetronimo
{
    /**
     * Creates the tetronimo and puts it in the vertical orientation
     */
    public LightningShape()
    {
        super(Color.GREEN);
        super.r1.setLocation( 0, 0 );
        super.r2.setLocation( Tetronimo.SIZE , 0);
        super.r3.setLocation( Tetronimo.SIZE , Tetronimo.SIZE );
        super.r4.setLocation( Tetronimo.SIZE * 2, Tetronimo.SIZE  );
        init();
    }

    /**
     * Rotates the tetronimo
     */
    @Override
    public void rotate()
    {
        super.rotate(); // updates curRotation (probably mod 4 in Tetronimo)

        Point curLoc = super.getLocation();
        super.setLocation(0, 0);

        final int S = Tetronimo.SIZE;

        switch (super.curRotation % 4)
        {
            case 0:
                r1.setLocation(0,     0);
                r2.setLocation(S,   0);
                r3.setLocation(S, S);
                r4.setLocation(2*S, S);
                break;

            case 1:
                r1.setLocation(S,   0);
                r2.setLocation(0,   S);
                r3.setLocation(S, S);
                r4.setLocation(0,   2*S);
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
    public int getHeight()
    {
        // 0/2 => 2 tall; 1/3 => 3 tall
        return (this.curRotation % 4 == 0 || this.curRotation % 4 == 2)
                ? Tetronimo.SIZE * 2
                : Tetronimo.SIZE * 3;
    }

    @Override
    public int getWidth()
    {
        // 0/2 => 3 wide; 1/3 => 2 wide
        return (this.curRotation % 4 == 0 || this.curRotation % 4 == 2)
                ? Tetronimo.SIZE * 3
                : Tetronimo.SIZE * 2;
    }
}

