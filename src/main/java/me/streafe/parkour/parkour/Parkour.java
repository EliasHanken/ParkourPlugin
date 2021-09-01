package me.streafe.parkour.parkour;

import org.bukkit.Location;

import java.util.List;
import java.util.Map;
import java.util.UUID;

public interface Parkour{

    void startParkourChecker(UUID uuid);
    void checkpointChecker(UUID uuid);
    void giveItems(UUID uuid);
    void checkPlayerPosition(UUID uuid);
    void saveRun(UUID uuid);
    Map<UUID,Double> getPlayerList();
    void finish(UUID uuid);
    List<Location> getCheckpoints();
    Location getStart();
    Location getFinish();
    void setCheckpoints(List<Location> locations);
    void setStart(Location location);
    void setFinish(Location location);
    String getName();
    void setName(String name);
    void giveReward(UUID uuid);
    void addPlayer(UUID uuid);
    void removePlayer(UUID uuid,boolean flying);
    Map<UUID,Location> getPlayerCheckpoint();
    void setLeaderboard(Location location);
    Location getLeaderboardLoc();
    void setLeaderboardObj(LeaderboardObject leaderboardObject);
    LeaderboardObject getLeaderboardObj();
    void parkourUpdate();
    Map<UUID,Long> getLongValues();
}
