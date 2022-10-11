package resistanceGame.service;

import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import resistanceGame.configuration.GameplayConfigurtation;
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
    GameplayConfigurtation configuration;

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
        if (this.allGames.containsKey(gameId)) {
            return this.allGames.get(gameId);
        } else {
            throw new GameDoesNotExistException(gameId);
        }
    }

    public Player getPlayer(UUID uuid) {
        if (this.allPlayers.containsKey(uuid)) {
            return this.allPlayers.get(uuid);
        } else {
            throw new PlayerNotFoundException(uuid);
        }
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
