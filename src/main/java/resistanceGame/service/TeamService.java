package resistanceGame.service;

import org.springframework.stereotype.Component;
import resistanceGame.model.Game;
import resistanceGame.model.Player;

import java.util.HashMap;
import java.util.List;

@Component
public class TeamService {

    public boolean isTeamValid(Game game, List<String> team) {
        return team.size() == getRequestedTeamValue(game);
    }

    public void createTeam(Game game, List<String> team) {
        List<Player> table = game.getTable();
        team.stream()
                .mapToInt(Integer::parseInt)
                .forEach(i -> table.get(i).setInTeam(true));
    }

    public int getRequestedTeamValue(Game game) {
        return game.getConfiguration().get(game.getCurrentMission());
    }

    public void voteForTeam(Game game, Player player, boolean vote) {
        int currentVote = game.getCurrentVote();
        player.getTeamVote().put(currentVote, vote);
    }

    public void resetTeam(Game game, boolean resultTeamVote) {
        game.getTable().forEach(player -> player.setInTeam(false));
        riseCurrentVote(game, resultTeamVote);
        changeLeader(game);
    }

    private void riseCurrentVote(Game game, boolean resultTeamVote) {
        int nextVote;
        if (resultTeamVote) {
            nextVote = 1;
            game.getTable().forEach(player -> player.setMissionVote(new HashMap<>()));
        } else {
            nextVote = game.getCurrentVote() + 1;
        }
        game.setCurrentVote(nextVote);
    }

    private void changeLeader(Game game) {
        game.setLeader((game.getLeader() + 1) % game.getSize());
    }

    public boolean isTeamVoteComplete(Game game) {
        int currentVote = game.getCurrentVote();
        return game.getSize() == (int) game.getTable().stream()
                .filter(player -> player.getTeamVote().get(currentVote) != null)
                .count();
    }

    public boolean getResultOfTeamVote(Game game) {
        int currentVote = game.getCurrentVote();
        int yes = (int) game.getTable().stream()
                .filter(player -> player.getTeamVote().get(currentVote))
                .count();
        return yes > game.getSize() / 2;
    }

    public void resetTeamVotes(Game game) {
        game.getTable().forEach(player -> player.setTeamVote(new HashMap<>()));
    }
}
