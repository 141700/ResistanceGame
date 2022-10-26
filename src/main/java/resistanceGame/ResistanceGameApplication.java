package resistanceGame;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan
public class ResistanceGameApplication {

    public static void main(String[] args) {
        SpringApplication.run(ResistanceGameApplication.class, args);
    }
}
