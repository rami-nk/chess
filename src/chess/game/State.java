package chess.game;

import chess.board.Board;
import chess.board.Square;
import chess.pieces.*;

import java.util.ArrayList;
import java.util.stream.Collectors;

public class State {

    private State previous, next;
    private Piece[][] board;
    private boolean whitesTurn;
    private boolean lastMoveLegal = true;
    private boolean checkMate;
    private boolean opponentsKingChecked;
    private boolean enPassant;
    private Move lastMove;
    private ArrayList<Piece> movedPieces;

    public State() {
        Board board = new Board();
        this.board = board.getBoard();
        whitesTurn = board.isWhitesTurn();
        movedPieces = new ArrayList<>();
    }

    public State(Piece[][] board, boolean white) {
        this.board = board;
        whitesTurn = white;
        movedPieces = new ArrayList<>();
    }

    public State(String fen, boolean whitesTurn) {
        Board config = new Board(fen);
        this.board = config.getBoard();
        this.whitesTurn = whitesTurn;
        movedPieces = new ArrayList<>();
    }

    public State copy() {
        State copy = null;
        try {
            copy = this.getClass().getDeclaredConstructor().newInstance();
            copy.previous = previous;
            copy.next = next;
            copy.lastMove = lastMove;
            copy.whitesTurn = whitesTurn;
            copy.lastMoveLegal = false;
            copy.checkMate = checkMate;
            copy.enPassant = false;
            copy.opponentsKingChecked = false;
            copy.movedPieces = (ArrayList<Piece>) movedPieces.clone();

            for (int row=0; row < 8; row++) {
                System.arraycopy(board[row], 0, copy.board[row], 0, 8);
            }

        } catch (Exception e) {
            e.printStackTrace();
            System.exit(1);
        }
        return copy;
    }

    public boolean isMoveLegal(Move move) {
        if (getPiece(move.from) != null) {
            return getPiece(move.from).isValidMove(move);
        }
        return false;
    }

    public Piece getPiece(Square square) {
        return board[square.getRow() - 1][square.getColumn() - 1];
    }

    public Piece getPiece(int row, int column) {
        return board[row][column];
    }

    private boolean isPieceProtected(Piece piece) {
        return getAllByColorOccupiedSquares(piece.isWhite()).contains(getSquareOfPiece(piece));
    }

    public ArrayList<Move> getAllLegalMoves() {
        ArrayList<Move> moves = new ArrayList<>();
        for (Piece piece : getAllPiecesOfOneColor(whitesTurn)) {
            Square pieceSquare = getSquareOfPiece(piece);
            ArrayList<Square> targetSquares = getCandidateMoves(piece);
            for (Square target : targetSquares) {
                moves.add(new Move(pieceSquare, target));
            }

        }

        return moves;
    }

    public ArrayList<Square> getCandidateMoves(Piece piece) {
        ArrayList<Square> pseudoMoves = piece.getPseudoLegalMoves(this);
        if (!pseudoMoves.isEmpty()) {
            ArrayList<Square> candidateMoves = new ArrayList<>(filterIllegalMoves(piece, pseudoMoves));
            // TODO: passt noch nicht
            if (piece instanceof King) {
/*
                candidateMoves.removeIf(square -> getPiece(square) != null && isPieceProtected(piece));
*/
                candidateMoves.addAll(getLegalCastleSquares(piece.isWhite()));
                return candidateMoves;
            }
            if (piece instanceof Pawn) {
                return legalPawnMoves(piece, candidateMoves);
            }
            return candidateMoves;
        }
        return pseudoMoves;
    }

    private ArrayList<Square> legalPawnMoves(Piece piece, ArrayList<Square> candidateMoves) {
        Square pieceSquare = getSquareOfPiece(piece);

        candidateMoves.removeIf(square -> (piece.isMoved())  && Math.abs(square.getRow() - pieceSquare.getRow()) == 2);
        candidateMoves.removeIf(square -> {
            int rowDiff = Math.abs(square.getRow() - pieceSquare.getRow());
            int columnDiff = Math.abs(square.getColumn() - pieceSquare.getColumn());
            boolean straightMove = rowDiff != 0 && columnDiff == 0;
            return getBoard()[square.getRow() - 1][square.getColumn() - 1] != null && notSameColor(piece, square) && straightMove;
        });

        candidateMoves.removeIf(square -> {
            int rowOffset = piece.isWhite() ? 1 : -1;
            Square sqr = new Square(pieceSquare.getRow() + rowOffset, pieceSquare.getColumn());
            return !piece.isMoved() && (getPiece(sqr) != null) && new Move(pieceSquare, square).distance() == 2;
        });

        candidateMoves.removeIf(square -> {
            int rowDiff = Math.abs(square.getRow() - pieceSquare.getRow());
            int columnDiff = Math.abs(square.getColumn() - pieceSquare.getColumn());
            boolean diagonalMove = rowDiff == 1 && columnDiff == 1;
            return diagonalMove && getBoard()[square.getRow() - 1][square.getColumn() - 1] == null;
        });

        // EnPassant Squares
        candidateMoves.addAll(getPseudoLegalEnPassantSquares(piece));
        candidateMoves.removeIf(square -> !isPiecePinnedOrCanPieceBlockCheck(piece, square));

        return candidateMoves;
    }

    private ArrayList<Square> getPseudoLegalEnPassantSquares(Piece piece) {
        ArrayList<Square> enPassantSquares = new ArrayList<>();
        Square pieceSquare = getSquareOfPiece(piece);
        int row = pieceSquare.getRow() - 1;
        int column = pieceSquare.getColumn() - 1;
        int offset = 1;
        int colorRowOffset = piece.isWhite() ? 1 : -1;
        int enPassantRow = piece.isWhite() ? 4 : 3;

        for (int i = 0; i < 2; i++) {

            if (row == enPassantRow) {
                if (inBoardRange(row, column + offset)) {
                    if (getBoard()[row][column + offset] != null) {
                        if (inBoardRange(row + colorRowOffset, column + offset)) {
                            if (getBoard()[row + colorRowOffset][column + offset] == null) {
                                Piece opponentsPawn = board[row][column + offset];
                                if (opponentsPawn instanceof Pawn && opponentsPawn.isWhite() != piece.isWhite()) {
                                    // Überprüfen ob erster Bauernzug des Gegners
                                    if (getPrevious() != null && !getPrevious().getMovedPieces().contains(opponentsPawn)) {
                                        enPassantSquares.add( new Square(row + colorRowOffset + 1, column + offset + 1));
                                    }
                                }
                            }
                        }
                    }
                }
            }
            offset *= -1;
        }
        return enPassantSquares;
    }
    public void move(Move move) {
        Piece piece = getPiece(move.from);
        ArrayList<Square> candidateMoves = getCandidateMoves(piece);
        if (!candidateMoves.isEmpty() && candidateMoves.contains(move.to)) {
            getPiece(move.from).move(this, move);
            lastMoveLegal = true;
            setLastMove(move);
            movedPieces.add(piece);
        }
        if (isKingInCheck(!whitesTurn)) opponentsKingChecked = true;

        if (isCheckMate()) checkMate = true;
    }

    public void setPiece(Square square, Piece piece) {
        board[square.getRow() - 1][square.getColumn() - 1] = piece;
    }

    public void print() {
        for (int row=7; row >= 0; row--) {
            System.out.print("(" + (row+1) + ") ");
            for (int column=0; column < 8; column++) {
                String square = board[row][column] != null ?
                        board[row][column].toString() : "\u2003";
                System.out.print("[" + square + "]");
            }
            System.out.println();
        }
        System.out.println();
    }

    public boolean isEnPassant() {
        return enPassant;
    }

    public boolean isLastMoveLegal() {
        return lastMoveLegal;
    }

    public boolean isOpponentsKingChecked() {
        return opponentsKingChecked;
    }

    /** Board Methods **/

    public PieceType getPieceType(int row, int column) {
        Piece piece = this.board[row][column];
        if (piece == null) return PieceType.NONE;
        if (piece instanceof Pawn && piece.isWhite()) return PieceType.WHITE_PAWN;
        if (piece instanceof Pawn && !piece.isWhite()) return PieceType.BLACK_PAWN;
        if (piece instanceof King && piece.isWhite()) return PieceType.WHITE_KING;
        if (piece instanceof King && !piece.isWhite()) return PieceType.BLACK_KING;
        if (piece instanceof Queen && piece.isWhite()) return PieceType.WHITE_QUEEN;
        if (piece instanceof Queen && !piece.isWhite()) return PieceType.BLACK_QUEEN;
        if (piece instanceof Bishop && piece.isWhite()) return PieceType.WHITE_BISHOP;
        if (piece instanceof Bishop && !piece.isWhite()) return PieceType.BLACK_BISHOP;
        if (piece instanceof Rook && piece.isWhite()) return PieceType.WHITE_ROOK;
        if (piece instanceof Rook && !piece.isWhite()) return PieceType.BLACK_ROOK;
        if (piece instanceof Knight && piece.isWhite()) return PieceType.WHITE_KNIGHT;
        if (piece instanceof Knight && !piece.isWhite()) return PieceType.BLACK_KNIGHT;
        return null;
    }

    public ArrayList<Square> getAllByColorOccupiedSquares(boolean white) {
        ArrayList<Square> squares = new ArrayList<>();
        ArrayList<Piece> pieces = getAllPiecesOfOneColor(white);
        for (Piece piece : pieces) {
            ArrayList<Square>  pseudoPieceMoves = piece.getPseudoLegalMoves(this);
            if (!pseudoPieceMoves.isEmpty()) {
                pseudoPieceMoves.removeIf(square -> {
                    boolean isPawn = piece instanceof Pawn;
                    int columnDiff = Math.abs(getSquareOfPiece(piece).getColumn() - square.getColumn());
                    return isPawn && columnDiff == 0;
                });
                squares.addAll(pseudoPieceMoves);
            }
        }
        return squares;
    }

    public boolean inBoardRange(int row, int column) {
        return (row <= (Board.ROWS - 1) && row >= 0) && (column <= (Board.COLUMNS - 1) && column >= 0);
    }

    public Square getSquareOfPiece(Piece piece) {
        for (int row=0; row < 8; row++) {
            for (int column=0; column < 8; column++) {
                if (board[row][column] == piece) {
                    return new Square(row + 1, column + 1);
                }
            }
        }
        return null;
    }

    public ArrayList<Square> getSquaresInDirection(Piece piece, int rowIncrement, int columnIncrement) {
        ArrayList<Square> squares = new ArrayList<>();
        Square pieceSquare = getSquareOfPiece(piece);
        int row = pieceSquare.getRow() - 1 + rowIncrement;
        int column = pieceSquare.getColumn() - 1 + columnIncrement;
        while (inBoardRange(row, column)) {
            Square square = new Square(row + 1, column + 1);

            if (board[row][column] != null) {
                squares.add(square);
                return squares;
            }

            squares.add(square);
            row += rowIncrement;
            column += columnIncrement;
        }
        return squares;
    }

    public ArrayList<Piece> getAllPieces() {
        ArrayList<Piece> pieces = new ArrayList<>();
        for (int row=0; row < 8; row++) {
            for (int column=0; column < 8; column++) {
                Piece piece = board[row][column];
                if (piece != null) pieces.add(piece);
            }
        }
        return pieces;
    }

    public ArrayList<Piece> getAllPiecesOfOneColor(boolean white) {
        return (ArrayList<Piece>) getAllPieces()
                .stream().filter(piece -> piece.isWhite() == white)
                .collect(Collectors.toList());
    }

    public Piece getKing(boolean white) {
        for (int row=0; row < 8; row++) {
            for (int column=0; column < 8; column++) {
                Piece piece = board[row][column];
                if (piece instanceof King && piece.isWhite() == white) return piece;
            }
        }
        return null;
    }

    public ArrayList<Square> getAllValidSquaresDiagonal(Piece piece) {
        ArrayList<Square> occupiedSquares = new ArrayList<>();
        occupiedSquares.addAll(getSquaresInDirection(piece, 1, 1));
        occupiedSquares.addAll(getSquaresInDirection(piece, -1, 1));
        occupiedSquares.addAll(getSquaresInDirection(piece, 1, -1));
        occupiedSquares.addAll(getSquaresInDirection(piece, -1, -1));

        return occupiedSquares;
    }

    public ArrayList<Square> getAllValidSquaresCross(Piece piece) {
        ArrayList<Square> occupiedSquares = new ArrayList<>();
        occupiedSquares.addAll(getSquaresInDirection(piece, -1, 0));
        occupiedSquares.addAll(getSquaresInDirection(piece, 0, -1));
        occupiedSquares.addAll(getSquaresInDirection(piece, 1, 0));
        occupiedSquares.addAll(getSquaresInDirection(piece, 0, 1));

        return occupiedSquares;
    }

    public boolean notSameColor(Piece piece, Square square) {
        return piece.isWhite() != board[square.getRow() - 1][square.getColumn() - 1].isWhite();
    }

    private boolean isPieceMoved(Piece piece) {
        return movedPieces.contains(piece);
    }

    private ArrayList<Square> getLegalCastleSquares(boolean white) {
        ArrayList<Square> castleSquares = new ArrayList<>();
        Square kingsSquare = getKingsSquare(white);
        Square kingsRookSquare = new Square(kingsSquare.getRow(), 8);
        Piece kingsRook = getPiece(kingsRookSquare);
        Square queensRookSquare = new Square(kingsSquare.getRow(), 1);
        Piece queensRook = getPiece(queensRookSquare);

        Square kingsLeft = new Square(kingsSquare.getRow(), kingsSquare.getColumn() - 1);
        Square kingsRight = new Square(kingsSquare.getRow(), kingsSquare.getColumn() + 1);


        if (!isPieceMoved(getKing(white)) && !isKingInCheck(white)) {
            if (kingsRook instanceof Rook && kingsRook.isWhite() == white && (!isPieceMoved(kingsRook))) {
                if (!isSquareAttacked(kingsRight, !white) && getPiece(kingsRight) == null) {
                    // shortCastling-Squares
                    castleSquares.add(new Square(kingsSquare.getRow(), 7));
                }
            }

            if (queensRook instanceof Rook && queensRook.isWhite() == white && (!isPieceMoved(queensRook))) {
                if (!isSquareAttacked(kingsLeft, !white) && getPiece(kingsLeft) == null) {
                    // longCastling-Squares
                    castleSquares.add(new Square(kingsSquare.getRow(), 3));
                }
            }
        }

        castleSquares.removeIf(square -> getPiece(square) != null);
        castleSquares.removeIf(square -> getAllByColorOccupiedSquares(!white).contains(square));

        return castleSquares;
    }

    public ArrayList<Square> filterIllegalMoves(Piece piece, ArrayList<Square> pseudoLegalMoves) {

        pseudoLegalMoves.removeIf(square -> {
            Piece tmp = getPiece(square);
                    return tmp != null && tmp.isWhite() == piece.isWhite();
                }
                );

        pseudoLegalMoves.removeIf(square -> !isPiecePinnedOrCanPieceBlockCheck(piece, square));
        return pseudoLegalMoves;
    }

    public boolean isKingInCheck(boolean white) {
        return isSquareAttacked(getKingsSquare(white), !white);
    }

    private boolean isPiecePinnedOrCanPieceBlockCheck(Piece piece, Square square) {
        // TODO: moveToSquare berücksichtigt nicht castle- oder enPassant-Moves.
        Board config = new Board();
        config.setBoard(board);
        String fen = config.boardToFen();

        State state = new State(fen, whitesTurn);
        Square pieceSquare = getSquareOfPiece(piece);

        Piece statePiece = state.getPiece(pieceSquare);
        statePiece.move(state, new Move(pieceSquare, square));

        return !state.isSquareAttacked(state.getKingsSquare(state.whitesTurn), !state.whitesTurn);
    }

    public Square getKingsSquare(boolean white) {
        Square kingsSquare = null;
        for (int i=0; i < 8; i++) {
            for (int j=0; j < 8; j++) {
                if (board[i][j] instanceof King && board[i][j].isWhite() == white) {
                    kingsSquare = new Square(i+1, j+1);
                }
            }
        }
        return kingsSquare;
    }

    private boolean isSquareAttacked(Square square, boolean white) {
        return getAllByColorOccupiedSquares(white).contains(square);
    }

    public boolean isCheckMate() {
        return getAllLegalMoves().size() == 0 && isKingInCheck(isWhitesTurn());
    }

    /** Board Methods **/


    public int evaluate() {
        return PositionEvaluator.evaluatePosition(this);
    }

    /** Setter **/
    public void setPrevious(State previous) {
        this.previous = previous;
    }

    public void setNext(State next) {
        this.next = next;
    }

    public void setWhitesTurn(boolean whitesTurn) {
        this.whitesTurn = whitesTurn;
    }

    public void setLastMove(Move lastMove) {
        this.lastMove = lastMove;
    }

    /** Getter **/
    public State getPrevious() {
        return previous;
    }

    public State getNext() {
        return next;
    }

    public boolean isWhitesTurn() {
        return whitesTurn;
    }

    public Move getLastMove() {
        return lastMove;
    }

    public Piece[][] getBoard() {
        return board;
    }

    public void setBoard(Piece[][] board) {
        this.board = board;
    }

    public ArrayList<Piece> getMovedPieces() {
        return movedPieces;
    }

    public boolean getCheckmate() {
        return checkMate;
    }
}