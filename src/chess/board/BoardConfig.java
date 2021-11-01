package chess.board;

public class BoardConfig {

    private String fen;
    private boolean whitesTurn;
    private String position;

    public BoardConfig() {
        fen = "rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1";
        String[] fenSplit = fen.split(" ");
        position = fenSplit[0];
        whitesTurn = fenSplit[1].equals("w");
    }

    public String getFen() {
        return fen;
    }

    public boolean getWhitesTurn() {
        return whitesTurn;
    }

    public String getPosition() {
        return position;
    }
}
