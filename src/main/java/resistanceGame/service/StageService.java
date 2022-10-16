package resistanceGame.service;

import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import resistanceGame.configuration.GameplayConfiguration;
import resistanceGame.model.Game;
import resistanceGame.model.Stage;

@Component
@Log4j2
public class StageService {

    public void switchToNextStage(Game game) {
        game.setStage(next(game.getStage()));
    }

    private Stage next(Stage stage) {
        return Stage.values()[stage.ordinal() + 1];
    }

}
