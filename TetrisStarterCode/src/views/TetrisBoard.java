package views;

import controllers.TetrisController;
import models.Tetronimo;
import threads.GameLoopThread;
import Exceptions.GameException;
import Exceptions.InvalidMoveException;
import wheelsunh.users.*;
import wheelsunh.users.Frame;
import wheelsunh.users.Rectangle;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

/**
 * TetrisBoard.java
 * The visual board and main game loop with multithreading
 *
 * @author Eric Zurn, Adam Smith, Matt Nguyen
 */
public class TetrisBoard implements KeyListener {
    public static final int WIDTH  = 10;
    public static final int HEIGHT = 24;
    public static final int BOARD_X = 40;

    private TetrisController controller;
    private Rectangle[][] field;
    private Rectangle[] previewSquares;
    private Rectangle[][] gridlines;
    private final Frame frame;

    private TextBox scoreDisplay;
    private TextBox levelDisplay;
    private TextBox linesDisplay;
    private ShapeGroup nextPreview;

    private GameLoopThread gameThread;
    private Thread thread;               // left margin of the board

    /**
     * Constructor for TetrisBoard
     *
     * @param frame The Frame to display the game in
     */
    public TetrisBoard(Frame frame) {
        this.frame = frame;

        try {
            frame.addKeyListener(this);
            controller = new TetrisController(this);

            previewSquares = new Rectangle[4];

            buildBoard();
            buildGUI();
            startGameThread();
            createGridlines();



        } catch (GameException e) {
            System.err.println("Failed to initialize game: " + e.getLocalizedMessage());
            showError("Initialization Error", e.getMessage());
        } catch (Exception e) {
            System.err.println("Unexpected error: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Build the empty 10×24 grid of white rectangles
     */
    private void buildBoard() {
        try {
            field = new Rectangle[WIDTH][HEIGHT];

            for (int x = 0; x < WIDTH; x++) {
                for (int y = 0; y < HEIGHT; y++) {
                    field[x][y] = new Rectangle();
                    field[x][y].setLocation(BOARD_X + x * Tetronimo.SIZE,
                            y * Tetronimo.SIZE);
                    field[x][y].setSize(Tetronimo.SIZE, Tetronimo.SIZE);
                    field[x][y].setColor(Color.WHITE);
                    field[x][y].setFrameColor(new Color(0,0,0));
                    field[x][y].setFrameThickness(0);
                }
            }

            // Create border
            Rectangle border = new Rectangle();
            border.setLocation(BOARD_X - 2, -2);
            border.setSize(WIDTH * Tetronimo.SIZE + 4,
                    HEIGHT * Tetronimo.SIZE + 4);
            border.setColor(new Color(0, 0, 0, 0));
            border.setFrameColor(Color.BLACK);
            border.setFrameThickness(3);

        } catch (Exception e) {
            System.err.println("Error building board: " + e.getMessage());
        }
    }


    /**
     * Build GUI elements (score, level, lines, preview)
     */
    private void buildGUI() {
        try {
            int panelX = BOARD_X + WIDTH * Tetronimo.SIZE + 20;

            // Score display
            scoreDisplay = new TextBox("Score: 0");
            scoreDisplay.setLocation(panelX, 30);
            scoreDisplay.setSize(120, 30);
            scoreDisplay.setFrameThickness(2);

            // Level display
            levelDisplay = new TextBox("Level: 1");
            levelDisplay.setLocation(panelX, 70);
            levelDisplay.setSize(120, 30);
            levelDisplay.setFrameThickness(2);

            // Lines cleared display
            linesDisplay = new TextBox("Lines: 0");
            linesDisplay.setLocation(panelX, 110);
            linesDisplay.setSize(120, 30);
            linesDisplay.setFrameThickness(2);

            // Next piece label
            TextBox nextLabel = new TextBox("Next:");
            nextLabel.setLocation(panelX, 160);
            nextLabel.setSize(140, 120);
            nextLabel.setColor(new Color(0, 0, 0, 0));
            nextLabel.setFrameColor(Color.BLACK);
            nextLabel.setFrameThickness(2);

            // Preview group
            nextPreview = new ShapeGroup();
            nextPreview.setLocation(panelX, 190);

            // Create preview squares
            for (int i = 0; i < 4; i++) {
                previewSquares[i] = new Rectangle();
                previewSquares[i].setSize(Tetronimo.SIZE, Tetronimo.SIZE);
                previewSquares[i].setFrameColor(Color.BLACK);
                previewSquares[i].hide();
                nextPreview.add(previewSquares[i]);
            }

            // Instructions
            TextBox instructions = new TextBox("↑: Rotate\n←→: Move\n↓: Drop\nP: Pause");
            instructions.setLocation(panelX, 310);
            instructions.setSize(140, 80);
            instructions.setColor(new Color(240, 240, 240));
            instructions.setFrameThickness(1);

        } catch (Exception e) {
            System.err.println("Error building GUI: " + e.getMessage());
        }
    }

    /**
     * Update score display
     *
     * @param score The new score to display
     */
    public synchronized void updateScore(int score) {
        try {
            scoreDisplay.setText("Score: " + score);
        } catch (Exception e) {
            System.err.println("Error updating score: " + e.getMessage());
        }
    }

    /**
     * Update level and lines display
     *
     * @param lines Total lines cleared
     * @param level Current level
     */
    public synchronized void updateStats(int lines, int level) {
        try {
            linesDisplay.setText("Lines: " + lines);
            levelDisplay.setText("Level: " + level);
        } catch (Exception e) {
            System.err.println("Error updating stats: " + e.getMessage());
        }
    }

    /**
     * Called when player levels up
     *
     * @param newLevel The new level reached
     */
    public void onLevelUp(int newLevel) {
        if (gameThread != null) {
            gameThread.increaseSpeed(30);  // Speed up by 30ms per level
        }
    }

    /**
     * Refresh the "next piece" preview panel
     *
     * @param next The next Tetronimo to display
     */
    public void updatePreview(Tetronimo next) {
        if (next == null) return;

        Rectangle[] src = next.getSquares();

        // Normalize positions so preview is compact (top-left based)
        int minX = Integer.MAX_VALUE;
        int minY = Integer.MAX_VALUE;

        for (Rectangle r : src) {
            if (r.getXLocation() < minX) minX = r.getXLocation();
            if (r.getYLocation() < minY) minY = r.getYLocation();
        }

        // Draw preview without using absolute screen coordinates from the real piece
        for (int i = 0; i < 4; i++) {

            Rectangle srcRect = src[i];
            Rectangle previewRect = previewSquares[i];

            int relX = (srcRect.getXLocation() - minX);
            int relY = (srcRect.getYLocation() - minY);

            previewRect.setLocation(300 + relX, 190 + relY);

            // THIS is the important fix:
            previewRect.setColor(next.getColor());
            previewRect.setFrameColor(Color.BLACK);

            previewRect.show();
        }
    }

    /**
     * Start the game loop on a separate thread
     */
    private void startGameThread() {
        try {
            // Create game loop thread (implements Runnable - Week 13)
            gameThread = new GameLoopThread(this, controller, 400);

            // Create and start the thread
            thread = new Thread(gameThread);
            thread.start();

            System.out.println("Game thread started successfully");

        } catch (Exception e) {
            System.err.println("Failed to start game thread: " + e.getMessage());
        }
    }

    /**
     * Show game over message
     * Called from game thread when game ends
     */
    public void showGameOver() {
        try {
            TextBox gameOver = new TextBox("    GAME OVER    ");
            gameOver.setLocation(50, 180);
            gameOver.setSize(180, 80);
            gameOver.setColor(Color.RED);
            gameOver.setFrameColor(Color.BLACK);
            gameOver.setFrameThickness(3);

            // Show final stats
            TextBox finalStats = new TextBox(
                    "Final Score: " + controller.getScore() + "\n" +
                            "Level: " + controller.getLevel() + "\n" +
                            "Lines: " + controller.getLinesCleared()
            );
            int panelX = BOARD_X + WIDTH * Tetronimo.SIZE + 20;  // Calculate right panel X position
            finalStats.setLocation(panelX, 400);  // Move to RIGHT panel, lower position
            finalStats.setSize(140, 90);  // Narrower to fit panel, taller for 4 lines
            finalStats.setColor(new Color(255, 255, 200)); // Light yellow background
            finalStats.setFrameColor(Color.BLACK);
            finalStats.setFrameThickness(2);

        } catch (Exception e) {
            System.err.println("Error showing game over: " + e.getMessage());
        }
    }

    /**
     * Show error message to user
     *
     * @param title Error title
     * @param message Error message
     */
    private void showError(String title, String message) {
        TextBox error = new TextBox(title + "\n" + message);
        error.setLocation(50, 200);
        error.setSize(280, 100);
        error.setColor(Color.YELLOW);
        error.setFrameColor(Color.RED);
        error.setFrameThickness(3);
    }

    /**
     * Create permanent gridline overlay
     */
    private void createGridlines() {
        try {
            for (int x = 0; x < WIDTH; x++) {
                for (int y = 0; y < HEIGHT; y++) {
                    Rectangle gridCell = new Rectangle();
                    gridCell.setLocation(BOARD_X + x * Tetronimo.SIZE,
                            y * Tetronimo.SIZE);
                    gridCell.setSize(Tetronimo.SIZE, Tetronimo.SIZE);
                    gridCell.setColor(new Color(0, 0, 0, 0));
                    gridCell.setFrameColor(new Color(180, 180, 180));
                    gridCell.setFrameThickness(1);
                }
            }
        } catch (Exception e) {
            System.err.println("Error creating gridlines: " + e.getMessage());
        }
    }

    /* --------------------------------------------------------------------
       KEY HANDLING
       -------------------------------------------------------------------- */
    /**
     * Handle key press events
     *
     * @param e The KeyEvent
     */
    @Override
    public void keyPressed(KeyEvent e) {
        // Don't process if game over or no current piece
        if (controller.isGameOver() || gameThread == null) return;

        Tetronimo current = gameThread.getCurrentPiece();
        if (current == null) return;

        int key = e.getKeyCode();

        try {
            switch (key) {
                case KeyEvent.VK_UP:  // Rotate
                    handleRotate(current);
                    break;

                case KeyEvent.VK_LEFT:  // Move left
                    handleMoveLeft(current);
                    break;

                case KeyEvent.VK_RIGHT:  // Move right
                    handleMoveRight(current);
                    break;

                case KeyEvent.VK_DOWN:  // Drop faster
                    handleMoveDown(current);
                    break;

                case KeyEvent.VK_P:  // Pause/Resume
                    handlePause();
                    break;

                case KeyEvent.VK_SPACE:  // Hard drop
                    handleHardDrop(current);
                    break;
            }

        } catch (InvalidMoveException ime) {
            // Move was invalid - already handled by reverting
            // Could play error sound here

        } catch (GameException ge) {
            System.err.println("Game error: " + ge.getLocalizedMessage());

        } catch (Exception ex) {
            System.err.println("Unexpected error: " + ex.getMessage());
        }
    }

    /**
     * Handle rotation with exception handling
     *
     * @param current The current Tetronimo
     * @throws InvalidMoveException if rotation is invalid
     */
    private void handleRotate(Tetronimo current) throws InvalidMoveException, GameException {
        synchronized(current) {
            current.rotate();

            if (outOfBounds(current) || collision(current)) {
                current.rotate();  // Undo (rotate 3 more times)
                current.rotate();
                current.rotate();
                throw new InvalidMoveException("ROTATE",
                        current.getXLocation(),
                        current.getYLocation());
            }
        }
    }

    /**
     * Handle left movement with exception handling
     *
     * @param current The current Tetronimo
     * @throws InvalidMoveException if move is invalid
     */
    private void handleMoveLeft(Tetronimo current) throws InvalidMoveException, GameException {
        synchronized(current) {
            current.shiftLeft();

            if (outOfBounds(current) || collision(current)) {
                current.shiftRight();  // Undo
                throw new InvalidMoveException("LEFT",
                        current.getXLocation(),
                        current.getYLocation());
            }
        }
    }

    /**
     * Handle right movement with exception handling
     *
     * @param current The current Tetronimo
     * @throws InvalidMoveException if move is invalid
     */
    private void handleMoveRight(Tetronimo current) throws InvalidMoveException, GameException {
        synchronized(current) {
            current.shiftRight();

            if (outOfBounds(current) || collision(current)) {
                current.shiftLeft();  // Undo
                throw new InvalidMoveException("RIGHT",
                        current.getXLocation(),
                        current.getYLocation());
            }
        }
    }

    /**
     * Handle downward movement
     *
     * @param current The current Tetronimo
     * @throws GameException if landing check fails
     */
    private void handleMoveDown(Tetronimo current) throws GameException {
        synchronized(current) {
            if (!controller.tetronimoLanded(current)) {
                current.shiftDown();
            }
        }
    }

    /**
     * Handle hard drop (instant drop to bottom)
     *
     * @param current The current Tetronimo
     * @throws GameException if drop fails
     */
    private void handleHardDrop(Tetronimo current) throws GameException {
        synchronized(current) {
            while (!controller.tetronimoLanded(current)) {
                current.shiftDown();
            }
        }
    }

    /**
     * Handle pause/resume toggle
     */
    private void handlePause() {
        if (gameThread != null) {
            if (gameThread.isPaused()) {
                gameThread.resume();
                System.out.println("Game resumed");
            } else {
                gameThread.pause();
                System.out.println("Game paused");
            }
        }
    }

    /**
     * Check if any square lies outside the 10×24 board
     *
     * @param t The Tetronimo to check
     * @return true if out of bounds, false otherwise
     */
    private boolean outOfBounds(Tetronimo t) {
        try {
            for (Rectangle r : t.getSquares()) {
                int bx = (r.getXLocation() - BOARD_X) / Tetronimo.SIZE;
                int by = r.getYLocation() / Tetronimo.SIZE;

                if (bx < 0 || bx >= WIDTH || by >= HEIGHT) {
                    return true;
                }
            }
            return false;

        } catch (Exception e) {
            System.err.println("Error checking bounds: " + e.getMessage());
            return true;  // Fail safe
        }
    }

    /**
     * Check if the piece overlaps a locked square
     *
     * @param t The Tetronimo to check
     * @return true if collision detected, false otherwise
     */
    private boolean collision(Tetronimo t) {
        try {
            for (Rectangle r : t.getSquares()) {
                int bx = (r.getXLocation() - BOARD_X) / Tetronimo.SIZE;
                int by = r.getYLocation() / Tetronimo.SIZE;

                if (bx >= 0 && bx < WIDTH && by >= 0 && by < HEIGHT) {
                    if (field[bx][by].getColor() != Color.WHITE) {
                        return true;
                    }
                }
            }
            return false;

        } catch (Exception e) {
            System.err.println("Error checking collision: " + e.getMessage());
            return true;  // Fail safe
        }
    }

    /**
     * Get the playing field
     *
     * @return 2D array of Rectangle objects representing the board
     */
    public Rectangle[][] getPlayingField() { return field; }

    @Override public void keyTyped(KeyEvent e) {}
    @Override public void keyReleased(KeyEvent e) {}
}