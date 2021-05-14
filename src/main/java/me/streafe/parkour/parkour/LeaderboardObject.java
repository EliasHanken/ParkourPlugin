package me.streafe.parkour.parkour;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class LeaderboardObject {

    private List<UUID> leaderBoard;

    public LeaderboardObject(){
        this.leaderBoard = new ArrayList<>();
    }

    public List<UUID> getLeaderBoard() {
        return leaderBoard;
    }


}
