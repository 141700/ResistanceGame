package resistanceGame.service;

import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import resistanceGame.configuration.GameplayConfiguration;
import resistanceGame.exception.*;
import resistanceGame.model.Player;
import resistanceGame.model.Game;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Component
@Log4j2
public class GameService {

    @Autowired
    GameplayConfiguration configuration;

    @Autowired
    MatchService matchService;

    private final ConcurrentHashMap<Integer, Game> allGames;
    private final ConcurrentHashMap<UUID, Player> allPlayers;

    public GameService() {
        this.allGames = new ConcurrentHashMap<>();
        this.allPlayers = new ConcurrentHashMap<>();
    }

    public int createGame() {
        int gameId = getRandomValue(100000);
        this.allGames.put(gameId, new Game());
        return gameId;
    }

    public UUID createAndAddPlayerToGame(int gameId, String name) {
        Game game = getGame(gameId);
        if (game.getCurrentMission() == 0) {
            ArrayList<Player> table = game.getTable();
            Player player = new Player(name);
            int size = game.getSize();
            if (!table.contains(player)) {
                if (size < 10) {
                    table.add(player);
                    if (size == 0) game.setCreatedBy(player);
                    player.setSeat(size);
                    player.setInGame(gameId);
                    UUID uuid = player.getUuid();
                    this.allPlayers.put(uuid, player);
                    return uuid;
                }
                return null;
            } else {
                throw new PlayerNameIsNotUnique(name); // fixme после переходна авторизацию user теряет смысл
            }
        } else {
            throw new GameIsClosedException(gameId);
        }
    }

    public void startGame(int gameId) {
        Game game = getGame(gameId);
        int size = game.getSize();
        if (size > 4) {
            game.setConfiguration(configuration.getConfiguration(size));
            matchService.setRandomRoles(game);
            game.setLeader(getRandomValue(size));
            game.setCurrentMission(1);
        } else {
            throw new NotEnoughPlayersException(gameId);
        }
    }

    public Game getGame(int gameId) {
        Game game = this.allGames.get(gameId);
        if (game == null) {
            throw new GameDoesNotExistException(gameId);
        }
        return game;
    }

    public Player getPlayer(UUID uuid) {
        Player player = this.allPlayers.get(uuid);
        if (player == null) {
            throw new PlayerNotFoundException(uuid);
        }
        return player;
    }

    public Map<Integer, Game> getOpenGamesList() {
        return this.allGames.entrySet().stream()
                .filter(entry -> entry.getValue().getCurrentMission() < 1)
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }

    public int getRandomValue(int parameter) {
        return (int) (Math.random() * parameter);
    }
}
