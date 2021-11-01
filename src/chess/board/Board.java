package chess.board;

import chess.pieces.*;

public class Board {

    public static final int ROWS = 8;
    public static final int COLUMNS = 8;
    private Piece[][] board = new Piece[ROWS][COLUMNS];
    private boolean whitesTurn;

    public Board() {
        BoardConfig boardConfig = new BoardConfig();
        this.buildPosition(boardConfig.getPosition());
        this.whitesTurn = boardConfig.getWhitesTurn();
    }

    public Board(String fen) {
        this.buildPosition(fen);
    }

    public void print() {
        for (int row=ROWS-1; row >= 0; row--) {
            System.out.print("(" + (row+1) + ") ");
            for (int column=0; column < COLUMNS; column++) {
                String square = board[row][column] != null ?
                        board[row][column].toString() : "\u2003";
                System.out.print("[" + square + "]");
            }
            System.out.println();
        }
        System.out.println();
    }

    public Piece getPieceByPosition(int row, int column) {
        return board[row][column];
    }

    public String boardToFen() {
        StringBuilder fen = new StringBuilder();
        int counter = 0;
        for (int row=7; row >= 0; row--) {
            for (int column=0; column < 8; column++) {
                if (board[row][column] == null) {
                    counter++;
                } else {
                    if (counter != 0) {
                        fen.append(counter);
                        counter = 0;
                    }
                    fen.append(getSymbol(board[row][column]));
                }
            }
            if (counter != 0) {
                fen.append(counter);
                counter = 0;
            }
            fen.append("/");
        }
        return fen.toString();
    }

    private String getSymbol(Piece piece) {
        String symbol = "";
        if (piece instanceof Pawn) symbol = "p";
        if (piece instanceof King) symbol = "k";
        if (piece instanceof Queen) symbol = "q";
        if (piece instanceof Bishop) symbol = "b";
        if (piece instanceof Rook) symbol = "r";
        if (piece instanceof Knight) symbol = "n";
        if (piece.isWhite()) symbol = symbol.toUpperCase();
        return symbol;
    }

    void buildPosition(String fen) {
        int column = 0;
        int row = ROWS - 1;

        // rnbqkbnr/pppp1ppp/8/8/2P1p3/5N2/PP1PPPPP/RNBQKB1R

        for (char symbol : fen.toCharArray()) {
            if (symbol == '/') {
                row -= 1;
                column = 0;
            } else {
                if (symbol <= '9' && symbol >= '0') {
                    column += (symbol - '0');
                } else {
                    String previousSymbol = String.valueOf(symbol);
                    boolean white = !previousSymbol.equals(previousSymbol.toLowerCase());
                    Piece piece = this.getPiece(String.valueOf(symbol).toLowerCase(),
                            white);
                    board[row][column] = piece;
                    column += 1;
                }
            }
        }
    }

    private Piece getPiece(String symbol, boolean white) {
        Piece piece = null;
        switch (symbol) {
            case "n":
                piece = new Knight(white);
                break;
            case "k":
                piece = new King(white);
                break;
            case "q":
                piece = new Queen(white);
                break;
            case "p":
                piece = new Pawn(white);
                break;
            case "r":
                piece = new Rook(white);
                break;
            case "b":
                piece = new Bishop(white);
                break;
        }
        return piece;
    }

    public void setBoard(Piece[][] board) {
        this.board = board;
    }

    public boolean isWhitesTurn() {
        return whitesTurn;
    }

    public Piece[][] getBoard() {
        return this.board;
    }

}
