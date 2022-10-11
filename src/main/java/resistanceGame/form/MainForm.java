package resistanceGame.form;

import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.UUID;

@Setter
@Getter
public class MainForm {

    private UUID uuid;

    private String name;

    private int joinedGameId;

    private boolean start;

    private List<String> team;

    private Boolean vote;

    public boolean isNameValid() {
        return this.name.length() > 1 && this.name.length() < 31;
    }

}
