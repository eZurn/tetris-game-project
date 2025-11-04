package models;
import java.awt.*;

public class OShape extends Tetronimo {

        public OShape() {
            super(Color.YELLOW);
            super.r1.setLocation(0, 0);
            super.r2.setLocation(0, Tetronimo.SIZE);
            super.r3.setLocation(Tetronimo.SIZE, 0);
            super.r4.setLocation(Tetronimo.SIZE, Tetronimo.SIZE);

            init();
        }

        public int getHeight() {
            return Tetronimo.SIZE * 2;
        }

        public int getWidth() {
            return Tetronimo.SIZE * 2;
        }
    }
