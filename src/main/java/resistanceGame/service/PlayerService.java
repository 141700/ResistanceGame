package resistanceGame.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import resistanceGame.exception.PlayerNotFoundException;
import resistanceGame.model.Game;
import resistanceGame.model.Player;

import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Component
@RequiredArgsConstructor
public class PlayerService {

    private final GameService gameService;

    private final ConcurrentHashMap<UUID, Player> allPlayers = new ConcurrentHashMap<>();

    public Player createPlayer(String name) {
        return new Player(name);
    }

    public Player getPlayer(UUID uuid) {
        Player player = this.allPlayers.get(uuid);
        if (player == null) {
            throw new PlayerNotFoundException(uuid);
        }
        return player;
    }

    public UUID joinPlayerToGame(Game game, Player player) {
        player.setSeat(gameService.addPlayer(game, player));
        player.setInGame(game.getId());
        UUID uuid = player.getUuid();
        this.allPlayers.put(uuid, player);
        return uuid;
    }

    public boolean isAllPlayersReadyForNextStage(Game game) {
        return game.getTable().stream()
                .allMatch(Player::isReadyForNextStage);
    }

    public void resetPlayersReadyForNextStage(Game game) {
        game.getTable().forEach(player -> player.setReadyForNextStage(false));
    }

}
