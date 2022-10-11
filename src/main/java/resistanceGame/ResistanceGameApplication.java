package resistanceGame;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.ComponentScan;
//import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

//@EnableJpaRepositories
@EnableCaching
@SpringBootApplication
@ComponentScan
public class ResistanceGameApplication {

    public static void main(String[] args) {
        SpringApplication.run(ResistanceGameApplication.class, args);
    }
}
