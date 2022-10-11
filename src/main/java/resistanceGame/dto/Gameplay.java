package resistanceGame.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import resistanceGame.model.Game;
import resistanceGame.model.Player;

@Getter
@Setter
@AllArgsConstructor
public class Gameplay {

    private Game game;

    private Player player;

    private String headerMessage;

    private String context;

}
