package resistanceGame.service;

import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import resistanceGame.model.Game;
import resistanceGame.model.Player;

import java.util.Map;

@Component
@Log4j2
public class MissionService {

    public int getSucceededMissions(Game game) {
        return (int) game.getMissionResults().entrySet().stream()
                .filter(Map.Entry::getValue)
                .count();
    }

    public void voteForMission(Player player, int currentMission, boolean vote) {
        player.getMissionVote().put(currentMission, vote);
    }

    public boolean isMissionVoteComplete(Game game) {
        int currentMission = game.getCurrentMission();
        return game.getConfiguration().get(currentMission) == (int) game.getTable().stream()
                .filter(player -> player.isInTeam() && player.getMissionVote().get(currentMission) != null)
                .count();
    }

    public void implementMissionVotes(Game game) {
        int currentMission = game.getCurrentMission();
        int failedVotes = (int) game.getTable().stream()
                .filter(player -> player.isInTeam() && !player.getMissionVote().get(currentMission))
                .count();
        game.getMissions().put(currentMission, failedVotes);
    }

    public boolean isMissionSucceed(Game game) {
        int currentMission = game.getCurrentMission();
        int failedVotes = game.getMissions().get(currentMission);
        return failedVotes == 0 || (failedVotes == 1 && currentMission >= 4 && game.getSize() >= 7);
    }

    public void riseCurrentMission(Game game) {
        game.setCurrentMission(game.getCurrentMission() + 1);
    }

}
