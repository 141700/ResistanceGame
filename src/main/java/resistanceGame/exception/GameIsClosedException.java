package resistanceGame.exception;

public class GameIsClosedException extends RuntimeException {
    public GameIsClosedException(int id) {
        super(String.format("Session with id " + id + " is already closed for new players."));
    }

}
