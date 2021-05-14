package me.streafe.parkour.utils;

import me.streafe.parkour.ParkourSystem;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

public class CounterRunnable extends BukkitRunnable {

    private double timeD = 0.0;
    private Player player;

    public CounterRunnable(Player player){
        this.player = player;
    }

    /**
     * This function has to be run 1/10 of a second in order to work
     * properly.
     */
    @Override
    public void run() {
        timeD = Utils.round(timeD+0.1,2);
            PacketUtils.sendActionBarMessage(player,Utils.translate("&a" + Utils.round(timeD,2)));
            ParkourSystem.getInstance().getParkourManager().getParkourByUUID(player.getUniqueId()).getPlayerList().put(player.getUniqueId(),timeD);

    }

}
