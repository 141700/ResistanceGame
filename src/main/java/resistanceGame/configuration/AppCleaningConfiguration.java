package resistanceGame.configuration;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Map;
import java.util.stream.Collectors;

@Setter
@Getter
@Component
@EnableScheduling
@ConfigurationProperties(prefix = "cleaning")
public class AppCleaningConfiguration {

    private int notStartedGameLifeHours;

    private int startedGameLifeHours;

}
