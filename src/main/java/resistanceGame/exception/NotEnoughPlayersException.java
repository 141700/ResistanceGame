package resistanceGame.exception;

public class NotEnoughPlayersException extends RuntimeException {
    public NotEnoughPlayersException(int id) {
        super(String.format("Not enough players in session with id " + id + "."));
    }

}
