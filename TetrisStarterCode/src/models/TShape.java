package models;

import java.awt.*;

public class TShape extends Tetronimo {
    public TShape() {
        super(Color.MAGENTA);

        r1.setLocation(0, SIZE);
        r2.setLocation(SIZE, SIZE);
        r3.setLocation(SIZE*2, SIZE);
        r4.setLocation(SIZE, 0);  // top of T

        init();
    }

    @Override
    public void rotate() {
        super.rotate();
        Point loc = getLocation();
        setLocation(0, 0);

        switch (curRotation) {
            case 0: // pointing up
                r1.setLocation(SIZE, 0);
                r2.setLocation(0, SIZE);
                r3.setLocation(SIZE, SIZE);
                r4.setLocation(SIZE*2, SIZE);
                break;
            case 1: // pointing right
                r1.setLocation(0, 0);
                r2.setLocation(0, SIZE);
                r3.setLocation(0, SIZE*2);
                r4.setLocation(SIZE, SIZE);
                break;
            case 2: // pointing down
                r1.setLocation(0, SIZE);
                r2.setLocation(SIZE, SIZE);
                r3.setLocation(SIZE*2, SIZE);
                r4.setLocation(SIZE, SIZE*2);
                break;
            case 3: // pointing left
                r1.setLocation(SIZE, 0);
                r2.setLocation(SIZE, SIZE);
                r3.setLocation(SIZE, SIZE*2);
                r4.setLocation(0, SIZE);
                break;
        }
        setLocation(loc);
    }

    @Override
    public int getHeight() {
        return (curRotation % 2 == 0) ? SIZE * 2 : SIZE * 3;
    }

    @Override
    public int getWidth() {
        return (curRotation % 2 == 0) ? SIZE * 3 : SIZE * 2;
    }
}