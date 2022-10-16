package resistanceGame.model;

import lombok.*;

import java.util.ArrayList;
import java.util.HashMap;

@Getter
@Setter
public class Game {

    private ArrayList<Player> table;

//    private final User createdBy;
    private Player createdBy;

    private Stage stage;

    private int leader;

    private ArrayList<Integer> configuration;

    private HashMap<Integer, Integer> missions;

    private HashMap<Integer, Boolean> missionResults;

    private int currentMission;

    private int currentVote;

    public Game() {
        this.table = new ArrayList<>();
        this.missions = new HashMap<>();
        this.missionResults = new HashMap<>();
        this.leader = 0;
        this.currentMission = 0;
        this.currentVote = 1;
        this.stage = Stage.START;
    }

//    public Game(Player player) {
//        this.table = new ArrayList<>();
//        this.table.add(player);
//        this.createdBy = player;
//        this.leader = 0;
//        this.currentMission = 0;
//        this.currentVote = 1;
//    }

    public int getSize() {
        return this.table.size();
    }

    public boolean isLeader(Player player) {
        return this.leader == player.getSeat();
    }

}
