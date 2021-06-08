package me.streafe.parkour.parkour;

import me.streafe.parkour.ParkourSystem;
import org.bukkit.Location;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.util.*;

public class LeaderboardObject {

    private Map<String,Double> leaderBoard;
    private Location location;
    private final LeaderBoardUpdater updater;
    private final String parkourName;


    public LeaderboardObject(Location location, String parkourName){
        this.leaderBoard = new HashMap<>();
        this.location = location;
        this.updater = new LeaderBoardUpdater(this);
        this.parkourName = parkourName;

        updater.runTaskTimer(ParkourSystem.getInstance(),0L,20L);
    }


    public Map<String,Double> getLeaderBoard() {
        return leaderBoard;
    }

    public void setLeaderBoard(Map<String,Double> leaderBoard) {
        this.leaderBoard = leaderBoard;
    }

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    public void update(){
        File file = new File(ParkourSystem.getInstance().getPathToPManager());
        YamlConfiguration yaml = YamlConfiguration.loadConfiguration(file);

        for(String s : yaml.getConfigurationSection("parkours." + this.parkourName).getKeys(false)){
            if(yaml.getString("parkours." + s + ".runs") != null){
                for(String players : yaml.getConfigurationSection("parkours." + s + ".runs").getKeys(false)){
                    getLeaderBoard().put(players,0.0);
                    //TODO doesn't display the leaderboard correctly
                }
            }
        }

        ParkourSystem.getInstance().getServer().getPlayer("krilux").sendMessage(getLeaderBoard().toString());
    }
}
