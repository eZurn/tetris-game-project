package controllers;

import models.Tetronimo;
import views.TetrisBoard;
import models.TetronimoFactory;
import wheelsunh.users.Rectangle;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

/**
 * All game rules live here.
 */
public class TetrisController {
    private final TetrisBoard board;
    private Tetronimo nextPiece;
    private int score = 0;
    private boolean gameOver = false;

    public TetrisController(TetrisBoard b) {
        this.board = b;
        generateNextPiece();
    }

    /** --------------------------------------------------------------
     *  Return the piece that will fall now and prepare the next one.
     *  -------------------------------------------------------------- */
    public Tetronimo getNextTetromino() {
        Tetronimo current = nextPiece;
        generateNextPiece();

        // start at column 3 (0-based) → x = BOARD_X + 3*SIZE
        current.setLocation(TetrisBoard.BOARD_X + 3 * Tetronimo.SIZE, 0);
        return current;
    }

    private void generateNextPiece() {
        nextPiece = TetronimoFactory.getRandomTetronimo();
    }

    public Tetronimo getPreviewPiece() { return nextPiece; }

    /** --------------------------------------------------------------
     *  Has the falling piece reached the bottom or another piece?
     *  -------------------------------------------------------------- */
    public boolean tetronimoLanded(Tetronimo t) {
        // bottom of board?
        if (t.getYLocation() + t.getHeight() >= TetrisBoard.HEIGHT * Tetronimo.SIZE)
            return true;

        Rectangle[][] field = board.getPlayingField();
        for (Rectangle sq : t.getSquares()) {
            int boardX = (sq.getXLocation() - TetrisBoard.BOARD_X) / Tetronimo.SIZE;
            int boardY = (sq.getYLocation() / Tetronimo.SIZE) + 1;   // one row below

            if (boardY >= TetrisBoard.HEIGHT) continue;
            if (boardX < 0 || boardX >= TetrisBoard.WIDTH) continue;

            if (field[boardX][boardY].getColor() != Color.WHITE)
                return true;
        }
        return false;
    }

    /** --------------------------------------------------------------
     *  Freeze the piece into the board, clear full lines, update score.
     *  -------------------------------------------------------------- */
    public void lockPiece(Tetronimo t) {
        Rectangle[][] field = board.getPlayingField();

        // copy colours into the grid
        for (Rectangle sq : t.getSquares()) {
            int x = (sq.getXLocation() - TetrisBoard.BOARD_X) / Tetronimo.SIZE;
            int y = sq.getYLocation() / Tetronimo.SIZE;
            if (x >= 0 && x < TetrisBoard.WIDTH && y >= 0 && y < TetrisBoard.HEIGHT) {
                field[x][y].setColor(sq.getColor());
            }
        }
        t.hide();

        clearLines();

        // game-over only after the piece is locked and lines cleared
        if (topRowsOccupied()) gameOver = true;
    }

    /** --------------------------------------------------------------
     *  Scan for full rows, award points, drop everything above.
     *  -------------------------------------------------------------- */
    private void clearLines() {
        Rectangle[][] field = board.getPlayingField();
        List<Integer> full = new ArrayList<>();

        for (int y = 0; y < TetrisBoard.HEIGHT; y++) {
            boolean rowFull = true;
            for (int x = 0; x < TetrisBoard.WIDTH; x++) {
                if (field[x][y].getColor() == Color.WHITE) {
                    rowFull = false;
                    break;
                }
            }
            if (rowFull) full.add(y);
        }

        int lines = full.size();
        if (lines == 0) return;

        // ---- scoring (exact spec) ----
        switch (lines) {
            case 1:
                score += 100;
                break;
            case 2:
                score += 300;
                break;
            case 3:
                score += 500;
                break;
            case 4:
                score += 800;  // Tetris!
                break;
            default:
                break;
        }
        board.updateScore(score);

        // ---- drop rows ----
        for (int line : full) {
            for (int x = 0; x < TetrisBoard.WIDTH; x++) {
                for (int y = line; y > 0; y--) {
                    field[x][y].setColor(field[x][y - 1].getColor());
                }
                field[x][0].setColor(Color.WHITE);
            }
        }
    }

    /** --------------------------------------------------------------
     *  Game ends when the two top rows contain any coloured block.
     *  -------------------------------------------------------------- */
    private boolean topRowsOccupied() {
        Rectangle[][] field = board.getPlayingField();
        for (int x = 0; x < TetrisBoard.WIDTH; x++) {
            if (field[x][0].getColor() != Color.WHITE ||
                    field[x][1].getColor() != Color.WHITE) {
                return true;
            }
        }
        return false;
    }

    public boolean isGameOver() { return gameOver; }
    public int getScore() { return score; }
}