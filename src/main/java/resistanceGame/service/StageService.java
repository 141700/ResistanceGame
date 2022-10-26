package resistanceGame.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import resistanceGame.configuration.GameplayConfiguration;
import resistanceGame.dto.Gameplay;
import resistanceGame.form.MainForm;
import resistanceGame.model.Game;
import resistanceGame.model.Player;
import resistanceGame.model.Stage;

import java.util.List;

@Component
@RequiredArgsConstructor
public class StageService {

    private final GameService gameService;

    private final PlayerService playerService;

    private final TeamService teamService;

    private final MissionService missionService;

    private final MessageService messageService;

    public Gameplay handleFinishStage(Game game, Player player) {
        return new Gameplay(game, player, messageService.getCommonMessage(game, player), gameService.isVictory(game));
    }

    public Gameplay handleMissionResultStage(Game game, Player player) {
        boolean context = false;
        if (playerService.isAllPlayersReadyForNextStage(game)) {
            if (!gameService.isGameOver(game)) {
                missionService.riseCurrentMission(game);
                playerService.resetPlayersReadyForNextStage(game);
                game.setStage(Stage.TEAM);
                teamService.resetTeam(game, true);
            } else {
                switchToNextStage(game);
                context = gameService.isVictory(game);
            }
        } else {
            player.setReadyForNextStage(true);
        }
        return new Gameplay(game, player, messageService.getCommonMessage(game, player), context);
    }

    public Gameplay handleMissionStage(Game game, Player player, MainForm form) {
        boolean context = false;
        int currentMission = game.getCurrentMission();
        if (!player.getMissionVote().containsKey(currentMission)) {
            if (form.getVote() != null) {
                missionService.voteForMission(player, currentMission, form.getVote());
                context = true;
                if (missionService.isMissionVoteComplete(game)) {
                    switchToNextStage(game);
                    missionService.implementMissionVotes(game);
                    game.getMissionResults().put(currentMission, missionService.isMissionSucceed(game));
                    player.setReadyForNextStage(true);
                }
            }
        } else {
            context = true;
        }
        return new Gameplay(game, player, messageService.getCommonMessage(game, player), context);
    }

    public Gameplay handleTeamResultStage(Game game, Player player) {
        boolean context = false;
        if (playerService.isAllPlayersReadyForNextStage(game)) {
            playerService.resetPlayersReadyForNextStage(game);
            if (teamService.getResultOfTeamVote(game)) {
                switchToNextStage(game);
                teamService.resetTeamVotes(game);
            } else if (game.getCurrentVote() < GameplayConfiguration.VOTES_COUNT) {
                game.setStage(Stage.TEAM);
                teamService.resetTeam(game, false);
            } else {
                game.setStage(Stage.FINISH);
            }
        } else {
            context = teamService.getResultOfTeamVote(game);
            player.setReadyForNextStage(true);
        }
        return new Gameplay(game, player, messageService.getCommonMessage(game, player), context);
    }

    public Gameplay handleTeamVoteStage(Game game, Player player, MainForm form) {
        boolean context = true;
        if (!player.getTeamVote().containsKey(game.getCurrentVote())) {
            if (form.getVote() != null) {
                teamService.voteForTeam(game, player, form.getVote());
                if (teamService.isTeamVoteComplete(game)) {
                    switchToNextStage(game);
                    context = teamService.getResultOfTeamVote(game);
                    player.setReadyForNextStage(true);
                }
            } else {
                context = false;
            }
        }
        return new Gameplay(game, player, messageService.getCommonMessage(game, player), context);
    }

    public Gameplay handleTeamStage(Game game, Player player, MainForm form) {
        List<String> team = form.getTeam();
        if (team != null && teamService.isTeamValid(game, team) && game.isLeader(player)) {
            teamService.createTeam(game, form.getTeam());
            switchToNextStage(game);
        }
        return new Gameplay(game, player, messageService.getCommonMessage(game, player), false);
    }

    public Gameplay handleStartStage(Game game, Player player, MainForm form) {
        String message;
        if (form.isStart() && game.getSize() >= GameplayConfiguration.MIN_PLAYERS_COUNT) {
            gameService.startGame(game);
            switchToNextStage(game);
            message = messageService.getCommonMessage(game, player);
        } else {
            message = messageService.getStartStageMessage(game, player, form.isStart());
        }
        return new Gameplay(game, player, message, false);
    }

    private void switchToNextStage(Game game) {
        game.setStage(next(game.getStage()));
    }

    private Stage next(Stage stage) {
        return Stage.values()[stage.ordinal() + 1];
    }

}
