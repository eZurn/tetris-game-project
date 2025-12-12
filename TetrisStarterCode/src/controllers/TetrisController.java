package controllers;

import models.Tetronimo;
import views.TetrisBoard;
import models.TetronimoFactory;
import wheelsunh.users.Rectangle;
import Exceptions.GameException;
import Exceptions.InvalidMoveException;
import utilis.GameStateManager;



import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

/**
 * TetrisController.java
 * All game rules live here
 *
 * @author Eric Zurn, Adam Smith, Matt Nguyen
 */

public class TetrisController {
    private final TetrisBoard board;
    private Tetronimo nextPiece;
    private int score;
    private boolean gameOver;
    private int linesCleared;
    private int level;
    private GameStateManager<Integer> scoreHistory;


    /**
     * Constructor for TetrisController
     *
     * @param b The TetrisBoard instance to control
     * @throws GameException if board is null
     */
    public TetrisController(TetrisBoard b) throws GameException{
        if (b == null) {
            throw new GameException("TetrisBoard cannot be null", "Controller requires valid board reference");
        }

        this.board = b;
        this.score = 0;
        this.gameOver = false;
        this.linesCleared = 0;
        this.level = 1;

        // Initialize generic score history manager
        this.scoreHistory = new GameStateManager<>(100);

        try {
            generateNextPiece();
        } catch (Exception e) {
            throw new GameException("Failed to generate initial piece", e.getMessage());
        }
    }

    /**
     * Return the piece that will fall now and prepare the next one
     *
     * @return The next Tetronimo to fall
     * @throws GameException if piece generation fails
     */
    public synchronized Tetronimo getNextTetromino() throws GameException {
        try {
            Tetronimo current = nextPiece;
            generateNextPiece();

            // Start at column 3 (0-based)
            current.setLocation(TetrisBoard.BOARD_X + 3 * Tetronimo.SIZE, 0);
            current.show();
            return current;

        } catch (Exception e) {
            throw new GameException("Failed to get next Tetronimo", e.getMessage());
        }
    }

    /**
     * Generate the next piece using the factory
     *
     * @throws GameException if factory fails
     */
    private void generateNextPiece() throws GameException {
        try {
            nextPiece = TetronimoFactory.getRandomTetronimo();
            if (nextPiece == null) {
                throw new GameException("Factory returned null Tetronimo");
            }
        } catch (Exception e) {
            throw new GameException("Failed to generate piece", e.getMessage());
        }
    }

    /**
     * Get the preview piece (next to fall)
     *
     * @return The next Tetronimo in queue
     */
    public Tetronimo getPreviewPiece() {
        return nextPiece;
    }

    /**
     * Check if the falling piece has reached the bottom or another piece
     *
     * @param t The Tetronimo to check
     * @return true if landed, false if can still fall
     * @throws GameException if tetronimo is null
     */
    public boolean tetronimoLanded(Tetronimo t) throws GameException {
        if (t == null) {
            throw new GameException("Cannot check landing for null Tetronimo");
        }

        try {
            // Check bottom of board
            if (t.getYLocation() + t.getHeight() >= TetrisBoard.HEIGHT * Tetronimo.SIZE) {
                return true;
            }

            // Check collision with existing pieces
            Rectangle[][] field = board.getPlayingField();

            for (Rectangle sq : t.getSquares()) {
                int boardX = (sq.getXLocation() - TetrisBoard.BOARD_X) / Tetronimo.SIZE;
                int boardY = (sq.getYLocation() / Tetronimo.SIZE) + 1;   // one row below

                // Skip if outside bounds
                if (boardY >= TetrisBoard.HEIGHT) continue;
                if (boardX < 0 || boardX >= TetrisBoard.WIDTH) continue;

                // Check if space below is occupied
                if (field[boardX][boardY].getColor() != Color.WHITE) {
                    return true;
                }
            }
            return false;

        } catch (ArrayIndexOutOfBoundsException e) {
            throw new GameException("Board access error",
                    "Invalid position during landing check");
        }
    }

    /**
     * Freeze the piece into the board, clear full lines, update score
     *
     * @param t The Tetronimo to lock in place
     * @throws GameException if locking fails
     */
    public synchronized void lockPiece(Tetronimo t) throws GameException {
        if (t == null) {
            throw new GameException("Cannot lock null Tetronimo");
        }

        try {
            Rectangle[][] field = board.getPlayingField();

            Color pieceColor = t.getColor();

            // Copy colors into the grid
            for (Rectangle sq : t.getSquares()) {
                int x = (sq.getXLocation() - TetrisBoard.BOARD_X) / Tetronimo.SIZE;
                int y = sq.getYLocation() / Tetronimo.SIZE;

                // Validate bounds before accessing
                if (x >= 0 && x < TetrisBoard.WIDTH && y >= 0 && y < TetrisBoard.HEIGHT) {
                    field[x][y].setColor(pieceColor);  // ← Uses the stored color
                    field[x][y].setFrameThickness(1);
                } else {
                    throw new GameException("Piece locked outside valid board area",
                            "Position: (" + x + ", " + y + ")");
                }
            }

            // Hide the falling piece
            t.hide();

            // Clear any complete lines
            clearLines();

            // Check game over condition
            if (topRowsOccupied()) {
                gameOver = true;
            }

        } catch (ArrayIndexOutOfBoundsException e) {
            throw new GameException("Array access error during lock", e.getMessage());
        } catch (NullPointerException e) {
            throw new GameException("Null reference during lock", e.getMessage());
        }
    }


    /**
     * Scan for full rows, award points, drop everything above
     *
     * @throws GameException if line clearing fails
     */
    private void clearLines() throws GameException {
        try {
            Rectangle[][] field = board.getPlayingField();

            // Generic ArrayList to store full row indices (Week 15 - Generics)
            ArrayList<Integer> fullRows = new ArrayList<>();

            // Find all full rows
            for (int y = 0; y < TetrisBoard.HEIGHT; y++) {
                boolean rowFull = true;
                for (int x = 0; x < TetrisBoard.WIDTH; x++) {
                    if (field[x][y].getColor() == Color.WHITE) {
                        rowFull = false;
                        break;
                    }
                }
                if (rowFull) {
                    fullRows.add(y);  // Type-safe addition (no casting needed)
                }
            }

            int lines = fullRows.size();
            if (lines == 0) return;

            // Update lines cleared counter
            linesCleared += lines;

            // Increase level every 10 lines
            int oldLevel = level;
            level = (linesCleared / 10) + 1;
            if (level > oldLevel) {
                board.onLevelUp(level);
            }

            // Award score based on lines cleared (standard Tetris scoring)
            int pointsEarned = 0;
            switch (lines) {
                case 1:
                    pointsEarned = 100 * level;
                    break;
                case 2:
                    pointsEarned = 300 * level;
                    break;
                case 3:
                    pointsEarned = 500 * level;
                    break;
                case 4:
                    pointsEarned = 800 * level;  // Tetris!
                    break;
                default:
                    pointsEarned = 1000 * level;  // Epic clear!
                    break;
            }

            score += pointsEarned;

            // Store score in history (demonstrates generic usage)
            scoreHistory.addToHistory(score);

            // Update display
            board.updateScore(score);
            board.updateStats(linesCleared, level);

            // Drop rows down (iterate through each full row)
            for (Integer lineIndex : fullRows) {  // Type-safe iteration
                int line = lineIndex;  // Auto-unboxing from Integer to int

                for (int x = 0; x < TetrisBoard.WIDTH; x++) {
                    // Shift all rows above down by one
                    for (int y = line; y > 0; y--) {

                        field[x][y].setColor(field[x][y - 1].getColor());
                    }
                    // Clear top row
                    field[x][0].setColor(Color.WHITE);

                }
            }

        } catch (Exception e) {
            throw new GameException("Error clearing lines", e.getMessage());
        }
    }

    /**
     * Check if game is over (top two rows contain any colored block)
     *
     * @return true if game over, false otherwise
     */
    private boolean topRowsOccupied() {
        try {
            Rectangle[][] field = board.getPlayingField();

            for (int x = 0; x < TetrisBoard.WIDTH; x++) {
                if (field[x][0].getColor() != Color.WHITE ||
                        field[x][1].getColor() != Color.WHITE) {
                    return true;
                }
            }
            return false;

        } catch (Exception e) {
            System.out.println("Error checking top rows: " + e.getMessage());
            return true;
        }
    }

    /**
     * Validate if a move is legal
     *
     * @param t The Tetronimo to validate
     * @param moveType The type of move being attempted
     * @throws InvalidMoveException if move is illegal
     */
    public void validateMove(Tetronimo t, String moveType) throws InvalidMoveException {
        if (t == null) {
            throw new InvalidMoveException(moveType, -1, -1);
        }

        // Check bounds and collisions
        for (Rectangle sq : t.getSquares()) {
            int x = (sq.getXLocation() - TetrisBoard.BOARD_X) / Tetronimo.SIZE;
            int y = sq.getYLocation() / Tetronimo.SIZE;

            // Out of bounds check
            if (x < 0 || x >= TetrisBoard.WIDTH || y >= TetrisBoard.HEIGHT) {
                throw new InvalidMoveException(moveType, x, y);
            }
        }
    }
    /**
     * Check if game is over
     *
     * @return true if game over, false otherwise
     */
    public boolean isGameOver() { return gameOver; }

    /**
     * Get current score
     *
     * @return The current score
     */
    public int getScore() {return score;}

    /**
     * Get current level
     *
     * @return The current level
     */
    public int getLevel() {return level;}

    /**
     * Get total lines cleared
     *
     * @return Number of lines cleared
     */
    public int getLinesCleared() {return linesCleared;}

    /**
     * Get score history (demonstrates generic return type - Week 15)
     *
     * @return ArrayList of historical scores
     */
    public ArrayList<Integer> getScoreHistory() {return scoreHistory.getAllHistory();}

    /**
     * Reset game state for new game
     *
     * @throws GameException if reset fails
     */
    public void reset() throws GameException {
        score = 0;
        linesCleared = 0;
        level = 1;
        gameOver = false;
        scoreHistory.clearHistory();
        generateNextPiece();
    }
}