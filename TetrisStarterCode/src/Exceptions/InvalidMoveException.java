package Exceptions;

/**
 * InvalidMoveException.java
 * A more specific error class in relation to invalid moves
 * @author Eric Zurn
 */
public class InvalidMoveException extends RuntimeException {
    private int attemptedX;
    private int attemptedY;
    private String moveType;

    /**
     * Constructor with move details
     *
     * @param moveType The type of move attempted (LEFT, RIGHT, DOWN, ROTATE)
     * @param x The attempted X position
     * @param y The attempted Y position
     */
    public InvalidMoveException(String moveType, int x, int y) {
        super("Invalid move: " + moveType + "Attempted position: (" + x + ", " + y + ")");
        this.moveType = moveType;
        this.attemptedX = x;
        this.attemptedY = y;
    }


}
