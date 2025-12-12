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
     * Get item from history at specific index
     *
     * @param index The index to retrieve
     * @return The item at that index, or null if out of bounds
     */
    public T getFromHistory(int index) {
        try {
            return history.get(index);
        } catch (IndexOutOfBoundsException e) {
            System.out.println("History index out of bounds: " + e.getMessage());
            return null;
        }
    }

    /**
     * Get the most recent history item
     *
     * @return The most recent item, or null if history is empty
     */
    public T getLatestHistory() {
        if (history.isEmpty()) {
            return null;
        }
        return history.get(history.size() - 1);
    }

    /**
     * Add item to upcoming queue
     *
     * @param item The item to queue
     */
    public void queueUpcoming(T item) {
        upcoming.offer(item);
    }

    /**
     * Get and remove next upcoming item
     *
     * @return The next upcoming item, or null if queue is empty
     */
    public T getNextUpcoming() {
        return upcoming.poll();
    }

    /**
     * Peek at next upcoming item without removing
     *
     * @return The next upcoming item, or null if queue is empty
     */
    public T peekNextUpcoming() {
        return upcoming.peek();
    }

    /**
     * Clear all history
     */
    public void clearHistory() {
        history.clear();
    }

    /**
     * Clear upcoming queue
     */
    public void clearUpcoming() {
        upcoming.clear();
    }

    /**
     * Get history size
     *
     * @return Number of items in history
     */
    public int getHistorySize() {
        return history.size();
    }

    /**
     * Get upcoming queue size
     *
     * @return Number of items in upcoming queue
     */
    public int getUpcomingSize() {
        return upcoming.size();
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
