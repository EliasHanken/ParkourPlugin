package me.streafe.parkour.parkour;

import me.streafe.parkour.ParkourSystem;
import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class LeaderBoardUpdater extends BukkitRunnable {

    private final LeaderboardObject leaderboardObject;
    public boolean stop = false;

    public LeaderBoardUpdater(LeaderboardObject leaderboardObject) {
        this.leaderboardObject = leaderboardObject;
    }

    @Override
    public void run() {
        Bukkit.getConsoleSender().sendMessage("Running -> " + this.getTaskId() + " : " +
                ParkourSystem.getInstance().getParkourManager().getParkourByName(this.leaderboardObject.getParkourName()).getName() +"\n");
        if(!stop){
            this.leaderboardObject.update();
        }
    }
}
