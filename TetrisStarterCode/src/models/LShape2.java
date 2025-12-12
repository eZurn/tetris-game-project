package models;

import java.awt.*;

/**
 * LShape2.java:
 * Creates an L shape tetronimo
 *
 * @author Adam Smith
 */
public class LShape2 extends Tetronimo
{
    /**
     * Creates the tetronimo and puts it in the vertical orientation
     */
    public LShape2()
    {
        super(Color.ORANGE);
        super.r1.setLocation( 0, Tetronimo.SIZE );
        super.r2.setLocation( Tetronimo.SIZE, Tetronimo.SIZE );
        super.r3.setLocation( Tetronimo.SIZE*2, Tetronimo.SIZE);
        super.r4.setLocation( Tetronimo.SIZE*2, 0);

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
                super.r1.setLocation( 0, Tetronimo.SIZE );
                super.r2.setLocation( Tetronimo.SIZE, Tetronimo.SIZE );
                super.r3.setLocation( Tetronimo.SIZE*2, Tetronimo.SIZE);
                super.r4.setLocation( Tetronimo.SIZE*2, 0);
                break;
            case 1:
                super.r1.setLocation( 0, 0 );
                super.r2.setLocation( Tetronimo.SIZE, 0 );
                super.r3.setLocation( Tetronimo.SIZE, Tetronimo.SIZE);
                super.r4.setLocation( Tetronimo.SIZE, Tetronimo.SIZE*2);
                break;
            case 2:
                super.r1.setLocation( 0, 0);
                super.r2.setLocation( Tetronimo.SIZE, 0 );
                super.r3.setLocation( Tetronimo.SIZE*2, 0);
                super.r4.setLocation( 0, Tetronimo.SIZE);
                break;
            case 3:
                super.r1.setLocation( 0, 0);
                super.r2.setLocation( 0, Tetronimo.SIZE );
                super.r3.setLocation( 0, Tetronimo.SIZE*2);
                super.r4.setLocation( Tetronimo.SIZE, Tetronimo.SIZE*2);
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
        } else {
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
        } else {
            return Tetronimo.SIZE * 3;
        }
        }
}
