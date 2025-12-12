package utilis;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;

/**
 * GameStateManager.java
 * Generic class for managing game state history and upcoming pieces
 *
 * Uses type parameters to create type-safe collections without casting
 *
 * @author Eric Zurn
 *
 * @param <T> The type of game state to manage
 */
public class GameStateManager <T>{
    private ArrayList<T> history;      // Generic ArrayList for history
    private Queue<T> upcoming;         // Generic Queue for upcoming items
    private int maxHistorySize;

    /**
     * Constructor with default history size
     */
    public GameStateManager() {
        this(50);  // Default max 50 history items
    }

    /**
     * Constructor with specified history size
     *
     * @param maxHistorySize Maximum number of history items to keep
     */
    public GameStateManager(int maxHistorySize) {
        this.history = new ArrayList<>();
        this.upcoming = new LinkedList<>();
        this.maxHistorySize = maxHistorySize;
    }

    /**
     * Add item to history
     *
     * @param item The item to add to history
     */
    public void addToHistory(T item) {
        // Add to history
        history.add(item);

        if (history.size() > maxHistorySize) {
            history.remove(0);
        }
    }


    /**
     * Clear all history
     */
    public void clearHistory() {
        history.clear();
    }


    /**
     * Get all history items
     *
     * @return ArrayList of all history items
     */
    public ArrayList<T> getAllHistory() {
        return new ArrayList<>(history);  // Return copy to prevent external modification
    }

}
