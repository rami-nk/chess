package chess.ui;

import chess.board.Square;
import chess.game.Game;
import chess.game.Move;
import chess.game.State;
import chess.pieces.Piece;
import chess.pieces.PieceType;
import chess.ui.view.BoardUI;
import chess.ui.view.SquareUI;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Arrays;

public class Controller extends MouseAdapter {

    private Game game;
    private BoardUI boardUI;

    public Controller(Game game) {
        this.game = game;
    }

    public MyMouseAdapter getMouseAdapterInstance(JPanel boardPanel, JLayeredPane mainLayeredPane, SquareUI[][] grid) {
        return new MyMouseAdapter(boardPanel, mainLayeredPane, grid);
    }

     class MyMouseAdapter extends MouseAdapter {
        private JLabel piece = null;
        private Point delta = null;

        private final JPanel boardPanel;
        private final JLayeredPane mainLayeredPane;
        private final SquareUI[][] grid;

        private SquareUI selectedUISquare;
        private Piece selectedPiece;

        public MyMouseAdapter(JPanel boardPanel, JLayeredPane mainLayeredPane, SquareUI[][] grid) {
            this.boardPanel = boardPanel;
            this.mainLayeredPane = mainLayeredPane;
            this.grid = grid;
        }

        @Override
        public void mousePressed(MouseEvent e) {
            Point point = e.getPoint();
            Component c = boardPanel.getComponentAt(point);

            for (int row=0; row < grid.length; row++) {
                for (int column=0; column < grid[row].length; column++) {
                    if (grid[row][column] == c) {
                        if (grid[row][column].getPieceType() != PieceType.NONE || grid[row][column].getPiece() != null) {
                            if (game.isWhitesTurn() == grid[row][column].getPieceType().isWhite()) {
                                selectedUISquare = grid[row][column];
                                selectedUISquare.setBackground(grid[row][column].getBackground().darker());
                                piece = selectedUISquare.getPiece();
                                selectedPiece = getCurrentState().getPiece(row, column);
                                selectedUISquare.remove(piece);
                                markLegalSquares(getCurrentState().getCandidateMoves(selectedPiece));
                                mainLayeredPane.add(piece, JLayeredPane.DRAG_LAYER);
                                int x = point.x - BoardUI.PIECE_WIDTH / 2;
                                int y = point.y - BoardUI.PIECE_WIDTH / 2;
                                piece.setLocation(x,y);
                                delta = new Point(point.x - x, point.y - y);
                                boardPanel.revalidate();
                                mainLayeredPane.repaint();
                                return;
                            }
                        }
                    }
                }
            }
        }

        @Override
        public void mouseDragged(MouseEvent e) {
            if (piece != null) {
                Point p = e.getPoint();
                int x = p.x - delta.x;
                int y = p.y - delta.y;
                piece.setLocation(x, y);
                mainLayeredPane.revalidate();
                mainLayeredPane.repaint();
            }
        }

        @Override
        public void mouseReleased(MouseEvent e) {
            SquareUI square = (SquareUI) boardPanel.getComponentAt(e.getPoint());
            if (piece != null && square != null) {
                mainLayeredPane.remove(piece);
                move(square);

                selectedUISquare.setBackground(selectedUISquare.getBackground().brighter());

                piece = null;
                delta = null;
                selectedUISquare = null;
                selectedPiece = null;

                boardPanel.revalidate();
                mainLayeredPane.repaint();
            }
        }

        private void markLegalSquares(ArrayList<Square> legalMoves) {
            for (Square square : legalMoves) {
                SquareUI squareUI = grid[square.getRow() - 1][square.getColumn() - 1];
                squareUI.setBackground(squareUI.getBackground().darker().darker());
            }
        }

        private void move(SquareUI squareUI) {
            Square from =  new Square(selectedUISquare.getRow(), selectedUISquare.getColumn());
            Square to = new Square(squareUI.getRow(), squareUI.getColumn());

            game.move(new Move(from, to));
            System.out.println(getCurrentState().getAllLegalMoves().size());

            getBoardUI().render(getCurrentState());

            if (getCurrentState().isOpponentsKingChecked()) {
                Square kingsSquare = getCurrentState().getKingsSquare(getCurrentState().isWhitesTurn());
                grid[kingsSquare.getRow() - 1][kingsSquare.getColumn() - 1].setBackground(Color.red);
            }
            if (getCurrentState().isCheckMate()) {
                System.out.println("Checkmate!");
                System.out.println((!getCurrentState().isWhitesTurn() ? "White" : "Black") + " won!");
            }
        }
    }


    private class RedoAction extends AbstractAction {

        /**
         * Invoked when an action occurs.
         *
         * @param e the event to be processed
         */
        @Override
        public void actionPerformed(ActionEvent e) {
            redo();
        }
    }

    public void redo() {
        game.redo();
        getBoardUI().render(game.getCurrentState());
        getBoardUI().getBoardPanel().revalidate();
        getBoardUI().getMainComponent().repaint();
    }

    public RedoAction getRedoActionInstance() {
        return new RedoAction();
    }

    private class UndoAction extends AbstractAction {

        /**
         * Invoked when an action occurs.
         *
         * @param e the event to be processed
         */
        @Override
        public void actionPerformed(ActionEvent e) {
            undo();
        }
    }

    public void undo() {
        game.undo();
        getBoardUI().render(game.getCurrentState());
        getBoardUI().getBoardPanel().revalidate();
        getBoardUI().getMainComponent().repaint();
    }

    public UndoAction getUndoActionInstance() {
        return new UndoAction();
    }

    public State getCurrentState() {
        return game.getCurrentState();
    }

    public BoardUI getBoardUI() {
        return boardUI;
    }

    public void setBoardUI(BoardUI boardUI) {
        this.boardUI = boardUI;
    }

    public Game getGame() {
        return game;
    }
}