package chess.ui.view;

import chess.pieces.PieceType;

import javax.swing.*;
import java.awt.*;

public class SquareUI extends JPanel {

    private int row;
    private int column;
    private JLabel Piece = null;
    private PieceType pieceType;

    public SquareUI(int row, int column, Color color, PieceType pieceType) {
        this.row = row;
        this.column = column;
        this.pieceType = pieceType;
        setBackground(color);
        setLayout(new GridBagLayout());
    }

    public int getRow() {
        return row;
    }

    public int getColumn() {
        return column;
    }

    public JLabel getPiece() {
        return Piece;
    }

    public PieceType getPieceType() {
        return pieceType;
    }

    @Override
    public Component add(Component comp) {
        Piece = (JLabel) comp;
        return super.add(comp);
    }

    @Override
    public void remove(Component comp) {
        Piece = null;
        pieceType = PieceType.NONE;
        super.remove(comp);
    }
}
