package chess.exceptions;

public class CheckMateException extends Exception {

    public CheckMateException(String msg) {
        super(msg);
    }

    public CheckMateException() {
        super();
    }
}
