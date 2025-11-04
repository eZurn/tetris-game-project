package views;

import controllers.TetrisController;
import models.Tetronimo;
import wheelsunh.users.*;
import wheelsunh.users.Frame;
import wheelsunh.users.Rectangle;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

/**
 * The visual board + main game loop.
 */
public class TetrisBoard implements KeyListener {
    public static final int WIDTH  = 10;
    public static final int HEIGHT = 24;          // visible rows (24 total)

    private final TetrisController controller;
    private Tetronimo current;
    private Rectangle[][] field;
    private Rectangle[] previewSquares = new Rectangle[4];
    private TextBox scoreDisplay;
    private ShapeGroup nextPreview;
    private final Frame frame;
    public static final int BOARD_X = 40;               // left margin of the board

    public TetrisBoard(Frame frame) {
        this.frame = frame;
        frame.addKeyListener(this);
        controller = new TetrisController(this);
        buildBoard();
        buildGUI();
        createGridlines();
        run();
    }

    /** --------------------------------------------------------------------
     *  Build the empty 10×24 grid of white rectangles.
     *  -------------------------------------------------------------------- */
    private void buildBoard() {
        field = new Rectangle[WIDTH][HEIGHT];
        for (int x = 0; x < WIDTH; x++) {
            for (int y = 0; y < HEIGHT; y++) {
                field[x][y] = new Rectangle();
                field[x][y].setLocation(BOARD_X + x * Tetronimo.SIZE,
                        y * Tetronimo.SIZE);
                field[x][y].setSize(Tetronimo.SIZE, Tetronimo.SIZE);
                field[x][y].setColor(Color.WHITE);          // empty cell

                field[x][y].setFrameColor(new Color(200,200,200));
                field[x][y].setFrameThickness(1);
            }
        }

        Rectangle border = new Rectangle();
        border.setLocation(BOARD_X - 2, -2);
        border.setSize(WIDTH * Tetronimo.SIZE + 4,
                HEIGHT * Tetronimo.SIZE + 4);
        //border.setColor(Color.WHITE);
        border.setColor(new Color(0, 0, 0, 0));// transparent inside
        border.setFrameColor(Color.BLACK);
        border.setFrameThickness(3);
    }


    private void buildGUI() {
        scoreDisplay = new TextBox("Score: 0");
        scoreDisplay.setLocation(BOARD_X + WIDTH * Tetronimo.SIZE + 20, 50);
        scoreDisplay.setSize(120, 30);

        TextBox nextLabel = new TextBox("Next:");
        nextLabel.setLocation(BOARD_X + WIDTH * Tetronimo.SIZE + 20, 100);

        nextPreview = new ShapeGroup();
        nextPreview.setLocation(BOARD_X + WIDTH * Tetronimo.SIZE + 20, 130);

        // Create 4 permanent preview squares
        for (int i = 0; i < 4; i++) {
            previewSquares[i] = new Rectangle();
            previewSquares[i].setSize(Tetronimo.SIZE, Tetronimo.SIZE);
            previewSquares[i].setFrameColor(Color.BLACK);
            previewSquares[i].hide();  // start hidden
            nextPreview.add(previewSquares[i]);
        }
    }

    public void updateScore(int score) {
        scoreDisplay.setText("Score: " + score);
    }

    /** --------------------------------------------------------------------
     *  Refresh the “next piece” preview panel.
     *  -------------------------------------------------------------------- */
    public void updatePreview(Tetronimo next) {
        Rectangle[] src = next.getSquares();

        for (int i = 0; i < 4; i++) {
            Rectangle srcRect = src[i];
            Rectangle previewRect = previewSquares[i];

            // Compute relative offset from the piece's origin
            int relX = srcRect.getXLocation() - next.getXLocation();
            int relY = srcRect.getYLocation() - next.getYLocation();

            // Center in preview box (add 20px padding)
            previewRect.setLocation(20 + relX, 20 + relY);
            previewRect.setColor(srcRect.getColor());
            previewRect.show();
        }
    }

    /** --------------------------------------------------------------------
     *  Main game loop – runs on the same thread that created the Frame.
     *  -------------------------------------------------------------------- */
    public void run() {
        current = controller.getNextTetromino();
        updatePreview(controller.getPreviewPiece());

        while (!controller.isGameOver()) {
            if (controller.tetronimoLanded(current)) {
                controller.lockPiece(current);
                if (controller.isGameOver()) break;
                current = controller.getNextTetromino();
                updatePreview(controller.getPreviewPiece());
            } else {
                current.shiftDown();
            }
            Utilities.sleep(400);
        }

        // ----- GAME OVER -----
        TextBox gameOver = new TextBox("    GAME OVER    ");
        gameOver.setLocation(50, 180);
        gameOver.setSize(280, 80);
        gameOver.setColor(Color.RED);
        gameOver.setFrameColor(Color.BLACK);
        gameOver.setFrameThickness(3);
    }

    /* --------------------------------------------------------------------
       KEY HANDLING
       -------------------------------------------------------------------- */
    @Override
    public void keyPressed(KeyEvent e) {
        if (current == null || controller.isGameOver()) return;

        int key = e.getKeyCode();

        switch (key) {
            case KeyEvent.VK_UP:                     // rotate
                current.rotate();
                if (outOfBounds(current) || collision(current))
                    current.rotate();                // undo illegal rotate
                break;

            case KeyEvent.VK_LEFT:
                current.shiftLeft();
                if (outOfBounds(current) || collision(current))
                    current.shiftRight();            // undo
                break;

            case KeyEvent.VK_RIGHT:
                current.shiftRight();
                if (outOfBounds(current) || collision(current))
                    current.shiftLeft();             // undo
                break;

            case KeyEvent.VK_DOWN:
                if (!controller.tetronimoLanded(current))
                    current.shiftDown();
                break;
        }
    }

    private void createGridlines() {
        // Create permanent gridline overlay
        for (int x = 0; x < WIDTH; x++) {
            for (int y = 0; y < HEIGHT; y++) {
                Rectangle gridCell = new Rectangle();
                gridCell.setLocation(BOARD_X + x * Tetronimo.SIZE, y * Tetronimo.SIZE);
                gridCell.setSize(Tetronimo.SIZE, Tetronimo.SIZE);
                gridCell.setColor(new Color(0, 0, 0, 0));  // fully transparent fill
                gridCell.setFrameColor(new Color(180, 180, 180));  // gray gridlines
                gridCell.setFrameThickness(1);
            }
        }
    }
    /** --------------------------------------------------------------------
     *  Helper: does any square lie outside the 10×24 board?
     *  -------------------------------------------------------------------- */
    private boolean outOfBounds(Tetronimo t) {
        for (Rectangle r : t.getSquares()) {
            int bx = (r.getXLocation() - BOARD_X) / Tetronimo.SIZE;
            int by = r.getYLocation() / Tetronimo.SIZE;
            if (bx < 0 || bx >= WIDTH || by >= HEIGHT) return true;
        }
        return false;
    }

    /** --------------------------------------------------------------------
     *  Helper: does the piece overlap a locked square?
     *  -------------------------------------------------------------------- */
    private boolean collision(Tetronimo t) {
        for (Rectangle r : t.getSquares()) {
            int bx = (r.getXLocation() - BOARD_X) / Tetronimo.SIZE;
            int by = r.getYLocation() / Tetronimo.SIZE;
            if (bx >= 0 && bx < WIDTH && by >= 0 && by < HEIGHT) {
                if (field[bx][by].getColor() != Color.WHITE) return true;
            }
        }
        return false;
    }

    public Rectangle[][] getPlayingField() { return field; }

    @Override public void keyTyped(KeyEvent e) {}
    @Override public void keyReleased(KeyEvent e) {}
}