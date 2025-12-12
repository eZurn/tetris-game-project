package Exceptions;

/**
 * GameException.java
 * Custom exception class that covers tetris errors
 * @author Eric Zurn
 */
public class GameException extends Exception {
    private String details;


    /**
     * Constructor with custom message
     *
     * @param message The error message to display
     */
    public GameException(String message) {
        super(message);
    }

    /**
     * Constructor with message and detailed information
     *
     * @param message The error message
     * @param details Additional details about the error
     */
    public GameException(String message, String details) {
        super(message);
        this.details = details;
    }

    /**
     * Provides localized message with details
     *
     * @return The complete error message with details
     */
    @Override
    public String getLocalizedMessage() {
        if (details != null) {
            return super.getMessage() + " - Details: " + details;
        }
        return super.getMessage();
    }

    /**
     * Custom toString for better error reporting
     *
     * @return Formatted error string
     */
    @Override
    public String toString() {
        return "GameException: " + getLocalizedMessage();
    }
}
