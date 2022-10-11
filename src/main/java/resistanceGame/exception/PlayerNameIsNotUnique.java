package resistanceGame.exception;

public class PlayerNameIsNotUnique extends RuntimeException {
    public PlayerNameIsNotUnique(String name) {
        super("Player with name " + name + " is already joined to the game.");
    }

}
