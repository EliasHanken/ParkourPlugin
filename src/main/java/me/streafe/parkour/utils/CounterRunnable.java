package me.streafe.parkour.utils;

import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitRunnable;

public class CounterRunnable extends BukkitRunnable {

    private double timeD = 0.0;

    /**
     * This function has to be run 1/10 of a second in order to work
     * properly.
     */
    @Override
    public void run() {
        timeD += 0.1;
    }

    public double getTimeAndStop(){
        cancel();
        return timeD;
    }
}
