package chess.board;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class Square{

    private final int row;
    private final int column;
    private final boolean white;

    public Square(int row, int column) {
        this.row = row;
        this.column = column;
        this.white = (row + column)%2 == 0;
    }

    public boolean isWhite() {
        return white;
    }

    public int getRow() {
        return row;
    }

    public int getColumn() {
        return column;
    }

    public String getChessNotation() {
        Map<Integer, String> cols = new HashMap<>();
        cols.put(1, "a");
        cols.put(2, "b");
        cols.put(3, "c");
        cols.put(4, "d");
        cols.put(5, "e");
        cols.put(6, "f");
        cols.put(7, "g");
        cols.put(8, "h");
        return cols.get(this.column) + this.row;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Square square = (Square) o;
        return row == square.row && column == square.column && white == square.white;
    }

    @Override
    public int hashCode() {
        return Objects.hash(row, column, white);
    }

    @Override
    public String toString() {
        return this.getChessNotation();
    }
}
