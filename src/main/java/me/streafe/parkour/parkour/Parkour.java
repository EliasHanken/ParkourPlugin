package me.streafe.parkour.parkour;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerMoveEvent;

import java.util.List;
import java.util.Map;
import java.util.UUID;

public interface Parkour {

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

}
