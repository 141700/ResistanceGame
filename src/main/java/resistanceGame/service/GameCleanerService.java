package resistanceGame.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import resistanceGame.configuration.AppCleaningConfiguration;
import resistanceGame.model.Game;

import java.time.Duration;
import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
@Slf4j
public class GameCleanerService {

    private final GameService gameService;

    private final AppCleaningConfiguration configuration;


    @Scheduled(cron = "${cleaning.cronInterval}")
    public void removeOldGames() {
        gameService.getAllGames().entrySet()
                .removeIf(entry -> isGameOld(entry.getValue()));
    }

    private boolean isGameOld(Game game) {
        long gameDuration = Duration.between(game.getCreated(), LocalDateTime.now()).toHours();
        int gameLife = game.getCurrentMission() == 0 ?
                configuration.getNotStartedGameLifeHours() : configuration.getStartedGameLifeHours();
        return gameDuration >= gameLife;
    }
}
