import views.TetrisBoard;

import wheelsunh.users.Frame;

/**
 * Tetris.java:
 * Main class for tetris, the program starts from here
 *
 * @author Professor Rossi
 * @version 1.0 July 24, 2020
 */
public class Tetris
{
    /**
     * Function main begins with program execution
     *
     * @param args The command line args (not used in this program)
     */
    public static void main( String[] args )
    {
        Frame f = new Frame();

        new TetrisBoard( f );
    }
}
