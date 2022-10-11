package resistanceGame.service;

import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import resistanceGame.dto.Gameplay;
import resistanceGame.form.MainForm;
import resistanceGame.model.Game;
import resistanceGame.model.Player;

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

    public Gameplay analyzeUserRequest(MainForm form) {
        Player player = gameService.getPlayer(form.getUuid());
        Game game = gameService.getGame(player.getInGame());
        switch (game.getStage()) {
            case START:
                return handleStartStage(game, player, form);
//            case INTRO:
//                return handleIntroStage(game, player, form);
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
            case VICTORY:
            case DEFEAT:
                return handleFinishStage(game, player);
        }
        return null;
    }

    private Gameplay handleFinishStage(Game game, Player player) {
        return new Gameplay(game, player, "ИГРА ОКОНЧЕНА!", "finish");
    }

    private Gameplay handleMissionResultStage(Game game, Player player) {
        String message = "";
        String context = "mission-result";
        if (matchService.isAllPlayersReadyForNextStage(game)) {
            if (!matchService.isGameOver(game)) {
                missionService.riseCurrentMission(game);
                matchService.resetPlayersReadyForNextStage(game);
                game.setStage(Game.Stage.TEAM);
                teamService.resetTeam(game, true);
                context = "team";
            } else {
                game.setStage(matchService.isVictory(game) ? Game.Stage.VICTORY : Game.Stage.DEFEAT);
                message = "ИГРА ОКОНЧЕНА!";
                context = "finish";
            }
        } else {
            player.setReadyForNextStage(true);
        }
        return new Gameplay(game, player, message, context);
    }

    private Gameplay handleMissionStage(Game game, Player player, MainForm form) {
        String context = "mission";
        int currentMission = game.getCurrentMission();
        if (!player.getMissionVote().containsKey(currentMission)) {
            if (form.getVote() != null) {
                missionService.voteForMission(player, currentMission, form.getVote());
                context = "voted";
                if (missionService.isMissionVoteComplete(game)) {
                    game.setStage(Game.Stage.MISSION_RESULT);
                    missionService.implementMissionVotes(game);
                    game.getMissionResults().put(currentMission, missionService.isMissionSucceed(game));
                    context = "mission-result";
                    player.setReadyForNextStage(true);
                }
            }
        } else {
            context = "voted";
        }
        return new Gameplay(game, player, "", context);
    }

    private Gameplay handleTeamResultStage(Game game, Player player) {
        String context;
        if (matchService.isAllPlayersReadyForNextStage(game)) {
            matchService.resetPlayersReadyForNextStage(game);
            if (teamService.getResultOfTeamVote(game)) {
                game.setStage(Game.Stage.MISSION);
                teamService.resetTeamVotes(game);
                context = "mission";
            } else if (game.getCurrentVote() < 5) {
                game.setStage(Game.Stage.TEAM);
                teamService.resetTeam(game, false);
                context = "team";
            } else {
                game.setStage(Game.Stage.DEFEAT);
                context = "finish";
            }
        } else {
            context = teamService.getResultOfTeamVote(game) ? "team-yes" : "team-no";
            player.setReadyForNextStage(true);
        }
        return new Gameplay(game, player, "", context);
    }

    private Gameplay handleTeamVoteStage(Game game, Player player, MainForm form) {
        String message = "";
        if (game.getCurrentMission() == 1 && game.getCurrentVote() == 1) {
            message = matchService.generateIntroToMatch(game, player);
        }
        String context = "voted";
        if (!player.getTeamVote().containsKey(game.getCurrentVote())) {
            if (form.getVote() != null) {
                teamService.voteForTeam(player, form.getVote());
                if (teamService.isTeamVoteComplete(game)) {
                    game.setStage(Game.Stage.TEAM_RESULT);
                    context = teamService.getResultOfTeamVote(game) ? "team-yes" : "team-no";
                    player.setReadyForNextStage(true);
                }
            } else {
                context = "team-vote";
            }
        }
        return new Gameplay(game, player, message, context);
    }

    private Gameplay handleTeamStage(Game game, Player player, MainForm form) {
        String message = "";
        String context = "team";
        if (game.getCurrentMission() == 1 && game.getCurrentVote() == 1) {
            message = matchService.generateIntroToMatch(game, player);
        }
        List<String> team = form.getTeam();
        if (team != null && teamService.isTeamValid(game, team) && game.isLeader(player)) {
            teamService.createTeam(player.getInGame(), form.getTeam());
            context = "team-vote";
        }
        return new Gameplay(game, player, message, context);
    }

//    private Gameplay handleIntroStage(Game game, Player player, MainForm form) {
//        String message = matchService.generateIntroToMatch(game, player);
//        String context = "intro";
//        if (matchService.isAllPlayersReadyForNextStage(game)) {
//            matchService.resetPlayersReadyForNextStage(game);
//            game.setStage(Game.Stage.TEAM);
//            context = "team";
//        } else {
//            player.setReadyForNextStage(true);
//        }
//        return new Gameplay(game, player, message, context);
//    }

    private Gameplay handleStartStage(Game game, Player player, MainForm form) {
        String message;
        String context = "start";
        if (player.getSeat() == 0) {
            if (form.isStart()) {
                if (game.getSize() > 4) {
                    gameService.startGame(player.getInGame());
                    game.setStage(Game.Stage.TEAM);
                    message = matchService.generateIntroToMatch(game, player);
                    context = "team";
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
        return new Gameplay(game, player, message, context);
    }
}
