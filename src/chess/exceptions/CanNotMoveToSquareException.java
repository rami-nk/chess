package chess.exceptions;

public class CanNotMoveToSquareException extends Exception {

    public CanNotMoveToSquareException(String msg) {
        super(msg);
    }

    public CanNotMoveToSquareException() {
        super();
    }
}
