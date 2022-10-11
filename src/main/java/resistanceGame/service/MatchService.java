package resistanceGame.service;

import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import resistanceGame.model.Game;
import resistanceGame.model.Player;

import java.util.ArrayList;
import java.util.Collections;
import java.util.stream.IntStream;

@Component
@Log4j2
public class MatchService {

    @Autowired
    MissionService missionService;

    public boolean isVictory(Game game) {
        return missionService.getSucceededMissions(game) == 3;
    }

    public boolean isGameOver(Game game) {
        int succeeded = missionService.getSucceededMissions(game);
        return succeeded == 3 || ((game.getCurrentMission() - succeeded) == 3);
    }

    public boolean isAllPlayersReadyForNextStage(Game game) {
        return game.getSize() == (int) game.getTable().stream()
                .filter(Player::isReadyForNextStage).count();
    }

    public void resetPlayersReadyForNextStage(Game game) {
        game.getTable().forEach(player -> player.setReadyForNextStage(false));
    }

    public String generateIntroToMatch(Game game, Player player) {
        Player.Role role = player.getRole();
//        log.info("---> " + player.getName() + " " + role);
        switch (role) {
            case SPY:
                return "Обнаружен секретный список шпионов!\n" +
                        "В этом списке вы и ваши партнеры:\n" +
                        getSpiesList(game);
            case RESISTANCE:
                return "Поздравляем!\n" +
                        "Вы член Сопротивления! Будьте бдительны!";
        }
        return null;
    }

    private String getSpiesList(Game game) {
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
}
