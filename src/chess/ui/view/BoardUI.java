package chess.ui.view;

import chess.board.Board;
import chess.game.State;
import chess.pieces.*;
import chess.ui.Controller;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;

public class BoardUI {

    private JLayeredPane mainLayeredPane = new JLayeredPane();
    private JPanel boardPanel = new JPanel(new GridLayout(Board.ROWS, Board.COLUMNS));
    private SquareUI[][] grid = new SquareUI[Board.ROWS][Board.COLUMNS];

    private final Dimension SQR_SIZE = new Dimension(64, 64);
    public static final int PIECE_WIDTH = 60;
    public static final Color BLACK_SQUARE_COLOR = new Color(125,135,150);
    public static final Color WHITE_SQUARE_COLOR = new Color(232,235,239);


    public BoardUI(Controller controller) {
        MouseAdapter mouseAdapter = controller.getMouseAdapterInstance(boardPanel, mainLayeredPane, grid);
        initializeBoard(controller.getCurrentState());
        boardPanel.setSize(boardPanel.getPreferredSize());
        boardPanel.setLocation(0, 0);
        mainLayeredPane.add(boardPanel, JLayeredPane.DEFAULT_LAYER);
        mainLayeredPane.setPreferredSize(boardPanel.getPreferredSize());

        mainLayeredPane.addMouseListener(mouseAdapter);
        mainLayeredPane.addMouseMotionListener(mouseAdapter);
    }

    public void render(State state) {
        for (int row=7; row >= 0; row--) {
            for (int column=0; column < Board.COLUMNS; column++) {
                boardPanel.remove(grid[row][column]);

            }
        }
        initializeBoard(state);
    }

    private void initializeBoard(State state) {
        for (int row=7; row >= 0; row--) {
            for (int column=0; column < Board.COLUMNS; column++) {
                Color color = row%2 == column%2 ? WHITE_SQUARE_COLOR : BLACK_SQUARE_COLOR;
                PieceType pieceType = state.getPieceType(row, column);
                grid[row][column] = new SquareUI(row + 1, column + 1, color, pieceType);
                grid[row][column].setPreferredSize(SQR_SIZE);
                boardPanel.add(grid[row][column]);

                if (pieceType != PieceType.NONE) {
                    ImageIcon icon = new ImageIcon("src/chess/ui/pieces/" + getPieceType(pieceType) + ".png");
                    JLabel label = new JLabel(icon/*, SwingConstants.CENTER*/);
                    grid[row][column].add(label);
                }
            }
        }
    }

    public JPanel getBoardPanel() {
        return boardPanel;
    }

    public SquareUI[][] getGrid() {
        return grid;
    }

    private String getPieceType(PieceType pieceType) {
        String type = "";
        switch (pieceType) {
            case BLACK_BISHOP:  type = "bB"; break;
            case BLACK_KING:    type = "bK"; break;
            case BLACK_QUEEN:   type = "bQ"; break;
            case BLACK_PAWN:    type = "bP"; break;
            case BLACK_KNIGHT:  type = "bN"; break;
            case BLACK_ROOK:    type = "bR"; break;
            case WHITE_BISHOP:  type = "wB"; break;
            case WHITE_KING:    type = "wK"; break;
            case WHITE_QUEEN:   type = "wQ"; break;
            case WHITE_PAWN:    type = "wP"; break;
            case WHITE_KNIGHT:  type = "wN"; break;
            case WHITE_ROOK:    type = "wR"; break;
        }
        return type;
    }

    public JComponent getMainComponent() {
        return mainLayeredPane;
    }
}
