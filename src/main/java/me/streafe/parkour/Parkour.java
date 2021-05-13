package me.streafe.parkour;

import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerMoveEvent;

public interface Parkour {

    void startParkour(PlayerInteractEvent e);
    void endParkour(PlayerInteractEvent e);
    void checkpoint(PlayerInteractEvent e);
    void giveItems();
    void checkPlayerPosition(PlayerMoveEvent e);
    void saveRun();
    Player getPlayer();
}
