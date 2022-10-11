package resistanceGame.configuration;

import lombok.Setter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.ArrayList;

@Setter
@Component
public class GameplayConfigurtation {

    @Value("#{'${gameplay.players.five}'.split(',')}")
    private ArrayList<Integer> five;
    @Value("#{'${gameplay.players.six}'.split(',')}")
    private ArrayList<Integer> six;
    @Value("#{'${gameplay.players.seven}'.split(',')}")
    private ArrayList<Integer> seven;
    @Value("#{'${gameplay.players.eight}'.split(',')}")
    private ArrayList<Integer> eight;
    @Value("#{'${gameplay.players.nine}'.split(',')}")
    private ArrayList<Integer> nine;
    @Value("#{'${gameplay.players.ten}'.split(',')}")
    private ArrayList<Integer> ten;

    public ArrayList<Integer> getConfiguration(int pax) {
        switch (pax) {
            case 5:
                return this.five;
            case 6:
                return this.six;
            case 7:
                return this.seven;
            case 8:
                return this.eight;
            case 9:
                return this.nine;
            case 10:
                return this.ten;
        }
        return null;
    }
}
