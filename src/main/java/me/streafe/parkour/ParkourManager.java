package me.streafe.parkour;

import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class ParkourManager {

    private Map<UUID,Parkour> parkourHashMap;

    public ParkourManager(){
        this.parkourHashMap = new HashMap<>();
    }

    public void startNewParkour(Parkour parkour){
        this.parkourHashMap.put(parkour.getPlayer().getUniqueId(),parkour);
    }
}
