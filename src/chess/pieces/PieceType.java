package chess.pieces;

public enum PieceType {
    BLACK_PAWN(false), BLACK_ROOK(false), BLACK_KING(false), BLACK_QUEEN(false), BLACK_BISHOP(false), BLACK_KNIGHT(false),
    WHITE_PAWN(true), WHITE_ROOK(true), WHITE_KING(true), WHITE_QUEEN(true), WHITE_BISHOP(true), WHITE_KNIGHT(true),
    NONE(false);

    private boolean white;

    PieceType (boolean white) {
        this.white = white;
    }

    public boolean isWhite() {
        return white;
    }
}
