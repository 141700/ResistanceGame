package resistanceGame.model;

import lombok.*;

import java.util.HashMap;
import java.util.UUID;

@Getter
@Setter
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Player {

    private final UUID uuid;

    //    private User user;
    @EqualsAndHashCode.Include
    private String name;

    private int inGame;
    private Role role;

    private int seat;

    private boolean inTeam;

    private HashMap<Integer, Boolean> teamVote;

    private boolean readyForNextStage;

    private HashMap<Integer, Boolean> missionVote;

    public enum Role {RESISTANCE, SPY}


    //    public Player(User user) {
//        this.user = user;
//    }
    public Player(String name) {
        this.uuid = UUID.randomUUID();
        this.name = name;
        this.role = Role.RESISTANCE;
        this.teamVote = new HashMap<>();
        this.missionVote = new HashMap<>();
    }
}
