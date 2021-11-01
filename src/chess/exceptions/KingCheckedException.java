package chess.exceptions;

public class KingCheckedException extends Exception {

    public KingCheckedException(String msg) {
        super(msg);
    }

    public KingCheckedException() {
        super();
    }
}
