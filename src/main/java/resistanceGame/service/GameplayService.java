package resistanceGame.service;

import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import resistanceGame.dto.Gameplay;
import resistanceGame.form.MainForm;
import resistanceGame.model.Game;
import resistanceGame.model.Player;
import resistanceGame.model.Stage;

import java.util.List;

@Component
@Log4j2
public class GameplayService {

    @Autowired
    GameService gameService;

    @Autowired
    MatchService matchService;

    @Autowired
    TeamService teamService;

    @Autowired
    MissionService missionService;

    @Autowired
    StageService stageService;

    public Gameplay analyzeUserRequest(MainForm form) {
        Player player = gameService.getPlayer(form.getUuid());
        Game game = gameService.getGame(player.getInGame());
        switch (game.getStage()) {
            case START:
                return handleStartStage(game, player, form);
            case TEAM:
                return handleTeamStage(game, player, form);
            case TEAM_VOTE:
                return handleTeamVoteStage(game, player, form);
            case TEAM_RESULT:
                return handleTeamResultStage(game, player);
            case MISSION:
                return handleMissionStage(game, player, form);
            case MISSION_RESULT:
                return handleMissionResultStage(game, player);
            case FINISH:
                return handleFinishStage(game, player);
            default:
                throw new IllegalStateException("Game stage is not valid " + game.getStage());
        }
    }

    private Gameplay handleFinishStage(Game game, Player player) {
        return new Gameplay(game, player, "ИГРА ОКОНЧЕНА!", matchService.isVictory(game));
    }

    private Gameplay handleMissionResultStage(Game game, Player player) {
        String message = "";
        boolean context = false;
        if (matchService.isAllPlayersReadyForNextStage(game)) {
            if (!matchService.isGameOver(game)) {
                missionService.riseCurrentMission(game);
                matchService.resetPlayersReadyForNextStage(game);
                game.setStage(Stage.TEAM);
                teamService.resetTeam(game, true);
            } else {
                stageService.switchToNextStage(game);
                context = matchService.isVictory(game);
                message = "ИГРА ОКОНЧЕНА!";
            }
        } else {
            player.setReadyForNextStage(true);
        }
        return new Gameplay(game, player, message, context);
    }

    private Gameplay handleMissionStage(Game game, Player player, MainForm form) {
        boolean context = false;
        int currentMission = game.getCurrentMission();
        if (!player.getMissionVote().containsKey(currentMission)) {
            if (form.getVote() != null) {
                missionService.voteForMission(player, currentMission, form.getVote());
                context = true;
                if (missionService.isMissionVoteComplete(game)) {
                    stageService.switchToNextStage(game);
                    missionService.implementMissionVotes(game);
                    game.getMissionResults().put(currentMission, missionService.isMissionSucceed(game));
                    player.setReadyForNextStage(true);
                }
            }
        } else {
            context = true;
        }
        return new Gameplay(game, player, "", context);
    }

    private Gameplay handleTeamResultStage(Game game, Player player) {
        boolean context = false;
        if (matchService.isAllPlayersReadyForNextStage(game)) {
            matchService.resetPlayersReadyForNextStage(game);
            if (teamService.getResultOfTeamVote(game)) {
                stageService.switchToNextStage(game);
                teamService.resetTeamVotes(game);
            } else if (game.getCurrentVote() < 5) {
                game.setStage(Stage.TEAM);
                teamService.resetTeam(game, false);
            } else {
                game.setStage(Stage.FINISH);
            }
        } else {
            context = teamService.getResultOfTeamVote(game);
            player.setReadyForNextStage(true);
        }
        return new Gameplay(game, player, "", context);
    }

    private Gameplay handleTeamVoteStage(Game game, Player player, MainForm form) {
        String message = "";
        if (game.getCurrentMission() == 1 && game.getCurrentVote() == 1) {
            message = matchService.generateIntroToMatch(game, player);
        }
        boolean context = true;
        if (!player.getTeamVote().containsKey(game.getCurrentVote())) {
            if (form.getVote() != null) {
                teamService.voteForTeam(player, form.getVote());
                if (teamService.isTeamVoteComplete(game)) {
                    stageService.switchToNextStage(game);
                    context = teamService.getResultOfTeamVote(game);
                    player.setReadyForNextStage(true);
                }
            } else {
                context = false;
            }
        }
        return new Gameplay(game, player, message, context);
    }

    private Gameplay handleTeamStage(Game game, Player player, MainForm form) {
        String message = "";
        if (game.getCurrentMission() == 1 && game.getCurrentVote() == 1) {
            message = matchService.generateIntroToMatch(game, player);
        }
        List<String> team = form.getTeam();
        if (team != null && teamService.isTeamValid(game, team) && game.isLeader(player)) {
            teamService.createTeam(player.getInGame(), form.getTeam());
            stageService.switchToNextStage(game);
        }
        return new Gameplay(game, player, message, false);
    }

    private Gameplay handleStartStage(Game game, Player player, MainForm form) {
        String message;
        if (player.getSeat() == 0) {
            if (form.isStart()) {
                if (game.getSize() > 4) {
                    gameService.startGame(player.getInGame());
                    stageService.switchToNextStage(game);
                    message = matchService.generateIntroToMatch(game, player);
                } else {
                    message = "Для начала игры требуется не менее 5 игроков!";
                }
            } else {
                message = "Вы создатель игровой сессии - вам и начинать, когда присоединяться все желающие.";
            }
        } else {
            message = "Вы участник игры ID " + player.getInGame() + ", которую создал " + game.getCreatedBy().getName() + "."
                    + "\nВ игре пока " + game.getSize() + " уч. и она открыта для присоединения.";
        }
        return new Gameplay(game, player, message, false);
    }
}
