package models;

import java.util.Random;

public class TetronimoFactory {
    private static final Random random = new Random();

    public static Tetronimo getRandomTetronimo() {
        Tetronimo[] shapes = {
                new OShape(),
                new StraightLine(),
                new LShape1(),
                new LShape2(),
                new LightningShape(),
                new LightningShapePt2(),
                new TShape()
        };
        return shapes[random.nextInt(shapes.length)];
    }
}