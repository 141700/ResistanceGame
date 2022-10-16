package resistanceGame.service;

import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import resistanceGame.model.Game;
import resistanceGame.model.Player;

import java.util.HashMap;
import java.util.List;

@Component
@Log4j2
public class TeamService {

    @Autowired
    GameService gameService;

    public boolean isTeamValid(Game game, List<String> team) {
        return team.size() == getRequestedTeamValue(game);
    }

    public void createTeam(int gameId, List<String> team) {
        Game game = gameService.getGame(gameId);
        List<Player> table = game.getTable();
        team.stream()
                .mapToInt(Integer::parseInt)
                .forEach(i -> table.get(i).setInTeam(true));
    }

    public int getRequestedTeamValue(Game game) {
        return game.getConfiguration().get(game.getCurrentMission());
    }

    public void voteForTeam(Player player, boolean vote) {
        Game game = gameService.getGame(player.getInGame());
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
