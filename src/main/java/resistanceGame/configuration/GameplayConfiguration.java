package resistanceGame.configuration;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Map;
import java.util.stream.Collectors;

@Setter
@Getter
@Component
@ConfigurationProperties(prefix = "gameplay")
public class GameplayConfiguration {

    private Map<String, ArrayList<String>> config;

    private int gameIdRank;

    public final static int VOTES_COUNT = 5;

    public final static int MISSIONS_FOR_END = 3;

    public final static int MIN_PLAYERS_COUNT = 5;

    public final static int MAX_PLAYERS_COUNT = 10;

    public ArrayList<Integer> getConfiguration(int pax) {
        return this.config.get(Integer.toString(pax)).stream()
                .map(Integer::parseInt)
                .collect(Collectors.toCollection(ArrayList::new));
    }
}
