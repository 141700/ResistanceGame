package resistanceGame.exception;

import java.util.UUID;

public class PlayerNotFoundException extends RuntimeException {
    public PlayerNotFoundException(UUID id) {
        super("Player with uuid " + id + " not found.");
    }

}
