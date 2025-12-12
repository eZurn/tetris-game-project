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

    /**
     * Gets the type of move that failed
     *
     * @return The move type (LEFT, RIGHT, DOWN, ROTATE)
     */
    public String getMoveType() {
        return moveType;
    }

    /**
     * Gets the attempted X coordinate
     *
     * @return X coordinate
     */
    public int getAttemptedX() {
        return attemptedX;
    }

    /**
     * Gets the attempted Y coordinate
     *
     * @return Y coordinate
     */
    public int getAttemptedY() {
        return attemptedY;
    }
}
