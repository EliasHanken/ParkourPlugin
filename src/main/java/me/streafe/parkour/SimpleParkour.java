package me.streafe.parkour;

import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerMoveEvent;

public class SimpleParkour implements Parkour{

    private int checkpoint = 0;
    private final Player player;

    public SimpleParkour(Player player){
        this.player = player;
    }

    @Override
    public void startParkour(PlayerInteractEvent e) {

    }

    @Override
    public void endParkour(PlayerInteractEvent e) {

    }

    @Override
    public void checkpoint(PlayerInteractEvent e) {

    }

    @Override
    public void giveItems() {

    }

    @Override
    public void checkPlayerPosition(PlayerMoveEvent e) {

    }

    @Override
    public void saveRun() {

    }

    @Override
    public Player getPlayer() {
        return this.player;
    }
}
