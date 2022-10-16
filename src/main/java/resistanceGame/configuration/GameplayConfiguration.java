package resistanceGame.configuration;

import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Map;
import java.util.stream.Collectors;

@Setter
@Component
@ConfigurationProperties(prefix = "gameplay")
public class GameplayConfiguration {

    private Map<String, ArrayList<String>> config;

    public ArrayList<Integer> getConfiguration(int pax) {
        return this.config.get(Integer.toString(pax)).stream()
                .map(Integer::parseInt)
                .collect(Collectors.toCollection(ArrayList::new));
    }
}
