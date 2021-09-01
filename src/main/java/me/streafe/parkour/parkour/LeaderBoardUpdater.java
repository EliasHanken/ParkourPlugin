package me.streafe.parkour.parkour;

import org.bukkit.scheduler.BukkitRunnable;

public class LeaderBoardUpdater extends BukkitRunnable {

    private final LeaderboardObject leaderboardObject;
    public boolean stop = false;

    public LeaderBoardUpdater(LeaderboardObject leaderboardObject) {
        this.leaderboardObject = leaderboardObject;
    }

    @Override
    public void run() {
        //Bukkit.getConsoleSender().sendMessage("Running -> " + this.getTaskId() + " : " +
        //        ParkourSystem.getInstance().getParkourManager().getParkourByName(this.leaderboardObject.getParkourName()).getName() +"\n");
        if(!stop){
            this.leaderboardObject.update();
        }
    }
}
