package resistanceGame.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import resistanceGame.configuration.GameplayConfiguration;
import resistanceGame.model.Game;
import resistanceGame.model.Player;
import resistanceGame.model.Stage;

@Component
@RequiredArgsConstructor
public class MessageService {

    private final GameService gameService;

    public String getCommonMessage(Game game, Player player) {
        return game.getStage() == Stage.FINISH ? "ИГРА ОКОНЧЕНА!" : getIntroMessage(game, player);
    }

    private String getIntroMessage(Game game, Player player) {
        return game.getCurrentMission() == 1 && game.getCurrentVote() == 1
                ? generateIntro(game, player)
                : "";
    }

    private String generateIntro(Game game, Player player) {
        Player.Role role = player.getRole();
        switch (role) {
            case SPY:
                return "Обнаружен секретный список шпионов!\n" +
                        "В этом списке вы и ваши партнеры:\n" +
                        gameService.getSpiesList(game);
            case RESISTANCE:
                return "Вы участник Сопротивления! Будьте бдительны!";
            default:
                throw new IllegalStateException("Player's role is not valid " + role);
        }
    }

    public String getStartStageMessage(Game game, Player player, boolean isStart) {
        String message;
        if (player.getSeat() == 0) {
            if (isStart) {
                if (game.getSize() >= GameplayConfiguration.MIN_PLAYERS_COUNT) {
                    message = generateIntro(game, player);
                } else {
                    message = "Для начала игры требуется не менее " + GameplayConfiguration.MIN_PLAYERS_COUNT + " игроков!";
                }
            } else {
                message = "Вы создатель игровой сессии - вам и начинать,\n" +
                        "когда присоединятся все желающие.";
            }
        } else {
            message = "Вы участник игры ID " + player.getInGame() + ", которую создал " + game.getCreatedBy().getName() + "." +
                    "\nВ игре пока " + game.getSize() + " уч. и она открыта для присоединения.";
        }
        return message;
    }
}
