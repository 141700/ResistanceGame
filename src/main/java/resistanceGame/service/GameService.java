package resistanceGame.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import resistanceGame.configuration.GameplayConfiguration;
import resistanceGame.exception.*;
import resistanceGame.model.Player;
import resistanceGame.model.Game;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;
import java.util.stream.IntStream;


@Component
@RequiredArgsConstructor
public class GameService {

    private final GameplayConfiguration configuration;

    private final MissionService missionService;

    private final ConcurrentHashMap<Integer, Game> allGames = new ConcurrentHashMap<>();


    public Game createGame(Player creator) {
        int gameId;
        do {
            gameId = generateGameId();
        }
        while (this.allGames.containsKey(gameId));
        Game game = new Game(gameId, creator);
        this.allGames.put(gameId, game);
        return game;
    }

    private int generateGameId() {
        return Utils.getRandomValue(configuration.getGameIdRank());
    }

    public int addPlayer(Game game, Player player) {
        Optional.of(game.getCurrentMission())
                .filter(entry -> entry == 0)
                .orElseThrow(() -> new GameIsClosedException(game.getId()));
        int size = game.getSize();
        if (size >= GameplayConfiguration.MAX_PLAYERS_COUNT) {
            throw new IllegalStateException("The game is overloaded.");
        }
        if (game.getTable().contains(player)) {
            throw new PlayerNameIsNotUnique(player.getName());
        }
        game.getTable().add(player);
        return size;
    }

    public void startGame(Game game) {
        int size = game.getSize();
        game.setConfiguration(configuration.getConfiguration(size));
        setRandomRoles(game);
        game.setLeader(Utils.getRandomValue(size));
        missionService.riseCurrentMission(game);
    }

    public String getSpiesList(Game game) {
        StringBuilder list = new StringBuilder();
        game.getTable().stream()
                .filter(p -> p.getRole() == Player.Role.SPY)
                .forEach(p -> list.append(p.getName()).append("\n"));
        return list.toString();
    }

    public void setRandomRoles(Game game) {
        ArrayList<Player> table = game.getTable();
        Collections.shuffle(table);
        int spies = game.getConfiguration().get(0);
        table.stream().limit(spies).forEach(p -> p.setRole(Player.Role.SPY));
        Collections.shuffle(table);
        IntStream.range(0, table.size()).forEach(i -> table.get(i).setSeat(i));
    }

    public Game getGame(int gameId) {
        Game game = this.allGames.get(gameId);
        if (game == null) {
            throw new GameDoesNotExistException(gameId);
        }
        return game;
    }

    public boolean isVictory(Game game) {
        return missionService.getSucceededMissions(game) == GameplayConfiguration.MISSIONS_FOR_END;
    }

    public boolean isGameOver(Game game) {
        int succeeded = missionService.getSucceededMissions(game);
        return succeeded == GameplayConfiguration.MISSIONS_FOR_END
                || ((game.getCurrentMission() - succeeded) == GameplayConfiguration.MISSIONS_FOR_END);
    }

    public Map<Integer, Game> getOpenGamesList() {
        return this.allGames.entrySet().stream()
                .filter(entry -> entry.getValue().getCurrentMission() == 0)
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }

}
