package models;

import java.util.Random;

/**
 * TetronimoFactory.java
 * @author Eric Zurn
 * Picks a random tetronimo
 */
public class TetronimoFactory {
    private static final Random random = new Random();

    /**
     * Generates and returns the tetronimo
     * @return the tetronimo
     */
    public static Tetronimo getRandomTetronimo() {
        switch (random.nextInt(7)) {
            case 0:
                return new OShape();
            case 1:
                return new StraightLine();
            case 2:
                return new LightningShape();
            case 3:
                return new LightningShapePt2();
            case 4:
                return new LShape1();
            case 5:
                return new LShape2();
            case 6:
                return new TShape();
            default:
                return new OShape(); // fallback
        }
    }
}