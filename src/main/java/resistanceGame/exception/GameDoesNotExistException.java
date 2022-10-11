package resistanceGame.exception;

public class GameDoesNotExistException extends RuntimeException {
    public GameDoesNotExistException(int id) {
        super(String.format("Game with id " + id + " does not exist"));
    }

}
