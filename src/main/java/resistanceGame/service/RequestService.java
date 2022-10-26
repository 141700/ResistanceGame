package resistanceGame.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import resistanceGame.dto.Gameplay;
import resistanceGame.form.MainForm;
import resistanceGame.model.Game;
import resistanceGame.model.Player;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class RequestService {

    private final GameService gameService;

    private final PlayerService playerService;

    private final StageService stageService;

    public Gameplay handleIncomeRequest(MainForm form) {
        Player player = playerService.getPlayer(form.getUuid());
        Game game = gameService.getGame(player.getInGame());
        switch (game.getStage()) {
            case START:
                return stageService.handleStartStage(game, player, form);
            case TEAM:
                return stageService.handleTeamStage(game, player, form);
            case TEAM_VOTE:
                return stageService.handleTeamVoteStage(game, player, form);
            case TEAM_RESULT:
                return stageService.handleTeamResultStage(game, player);
            case MISSION:
                return stageService.handleMissionStage(game, player, form);
            case MISSION_RESULT:
                return stageService.handleMissionResultStage(game, player);
            case FINISH:
                return stageService.handleFinishStage(game, player);
            default:
                throw new IllegalStateException("Game stage is not valid " + game.getStage());
        }
    }

    public UUID initGame(String name, int gameId) {
        Player player = playerService.createPlayer(name);
        Game game = gameId != 0
                ? gameService.getGame(gameId)
                : gameService.createGame(player);
        return playerService.joinPlayerToGame(game, player);
    }

}
