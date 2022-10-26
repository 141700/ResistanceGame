package resistanceGame.model;

import lombok.*;

import java.util.ArrayList;
import java.util.HashMap;

@Getter
@Setter
public class Game {

    private int id;

    private ArrayList<Player> table;

    private Player createdBy;

    private Stage stage;

    private int leader;

    private ArrayList<Integer> configuration;

    private HashMap<Integer, Integer> missions;

    private HashMap<Integer, Boolean> missionResults;

    private int currentMission;

    private int currentVote;

    public Game(int gameId, Player creator) {
        this.id = gameId;
        this.createdBy = creator;
        this.table = new ArrayList<>();
        this.missions = new HashMap<>();
        this.missionResults = new HashMap<>();
        this.leader = 0;
        this.currentMission = 0;
        this.currentVote = 1;
        this.stage = Stage.START;
    }

    public int getSize() {
        return this.table.size();
    }

    public boolean isLeader(Player player) {
        return this.leader == player.getSeat();
    }

}
