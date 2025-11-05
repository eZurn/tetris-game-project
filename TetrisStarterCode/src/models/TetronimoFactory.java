package models;

import java.util.Random;

public class TetronimoFactory {
    private static final Random random = new Random();

    public static Tetronimo getRandomTetronimo() {
        Tetronimo[] shapes = {
                new OShape(),
                new StraightLine(),
                new LightningShape(),
                new LightningShapePt2(),
                new LShape1(),
                new LShape2(),
                new TShape()


                // Add more shapes here when you create them, e.g., new LShape(), new JShape(), etc.
        };
        return shapes[random.nextInt(shapes.length)];
    }
}