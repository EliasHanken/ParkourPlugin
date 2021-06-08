package me.streafe.parkour.parkour;

import org.bukkit.scheduler.BukkitRunnable;

public class LeaderBoardUpdater extends BukkitRunnable {

    private final LeaderboardObject leaderboardObject;

    public LeaderBoardUpdater(LeaderboardObject leaderboardObject) {
        this.leaderboardObject = leaderboardObject;
    }

    @Override
    public void run() {
        this.leaderboardObject.update();
    }
}
