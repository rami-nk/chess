package chess.exceptions;

public class EnPassantException extends Exception {

    public EnPassantException(String msg) {
        super(msg);
    }

    public EnPassantException() {
        super();
    }
}
