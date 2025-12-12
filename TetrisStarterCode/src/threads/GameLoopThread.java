package threads;

import models.Tetronimo;
import controllers.TetrisController;
import views.TetrisBoard;

/**
 * GameLoopThread.java
 * Manages the game loop on a separate thread for better responsiveness
 *
 * @author Eric Zurn
 */

public class GameLoopThread implements Runnable{
    private final TetrisBoard board;
    private final TetrisController controller;
    private volatile boolean running;  // volatile ensures visibility across threads
    private volatile boolean paused;
    private Tetronimo currentPiece;
    private int dropSpeed;

    /**
     * Constructor for the game loop thread
     * @param board the tetris board
     * @param controller how to control piece
     * @param initialSpeed the speed of the blocks
     */
    public GameLoopThread(TetrisBoard board, TetrisController controller, int initialSpeed) {
        this.board = board;
        this.controller = controller;
        this.dropSpeed = initialSpeed;
        this.running = true;
        this.paused = false;
    }

    /**
     * Main game loop - runs on separate thread
     */
    @Override
    public void run() {
        try {
            currentPiece = controller.getNextTetromino();
            board.updatePreview(controller.getPreviewPiece());
        } catch (Exception e) {
            System.err.println("Failed to initialize first piece: " + e.getMessage());
            running = false;
            return;
        }

        // Main game loop
        while (running && !controller.isGameOver()) {
            try {
                // Check if paused
                synchronized(this) {
                    while (paused && running) {
                        this.wait();  // Wait until notified (Week 13 - wait/notify)
                    }
                }

                // Check if piece has landed
                if (controller.tetronimoLanded(currentPiece)) {
                    // Synchronize access to shared resource (board)
                    synchronized(board) {
                        controller.lockPiece(currentPiece);
                    }

                    // Check game over condition
                    if (controller.isGameOver()) {
                        break;
                    }

                    // Get next piece
                    currentPiece = controller.getNextTetromino();
                    board.updatePreview(controller.getPreviewPiece());
                } else {
                    // Move piece down
                    synchronized(currentPiece) {
                        currentPiece.shiftDown();
                    }
                }

                // Sleep for drop interval
                Thread.sleep(dropSpeed);

            } catch (InterruptedException e) {
                System.out.println("Game loop interrupted: " + e.getMessage());
                running = false;
            } catch (Exception e) {
                System.out.println("Unexpected error in game loop: " + e.getMessage());
                e.printStackTrace();
                running = false;
            }
        }

        // Game over - show message
        if (controller.isGameOver()) {
            board.showGameOver();
        }
    }

    /**
     * Pauses the game loop
     */
    public synchronized void pause() {
        paused = true;
    }

    /**
     * Resume the game loop
     */
    public synchronized void resume() {
        paused = false;
        this.notify();
    }

    /**
     * Stop the game loop completely
     */
    public void stop() {
        running = false;
        synchronized (this) {
            this.notify();
        }
    }

    /**
     * Increase game speed
     *
     * @param amount to decrease from drop speed
     */
    public synchronized void increaseSpeed(int amount) {
        dropSpeed = Math.max(50, dropSpeed - amount);  // Minimum 50ms
    }

    /**
     * Get current piece
     *
     * @return Current falling Tetronimo
     */
    public synchronized Tetronimo getCurrentPiece() {
        return currentPiece;
    }

    /**
     * A function to check whether the loop is running or not
     * @return if it is running or not
     */
    public boolean isRunning() {
        return running;
    }

    /**
     * Check if game is paused
     *
     * @return true if paused, false otherwise
     */
    public boolean isPaused() {
        return paused;
    }
}
