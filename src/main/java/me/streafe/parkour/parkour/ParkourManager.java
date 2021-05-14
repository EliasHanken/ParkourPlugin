package me.streafe.parkour.parkour;

import me.streafe.parkour.parkour.Parkour;
import me.streafe.parkour.utils.Utils;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;

import java.util.*;

public class ParkourManager implements Listener {

    private List<Parkour> parkourList;
    private Map<UUID,Parkour> tempList;

    public ParkourManager(){
        this.parkourList = new ArrayList<>();
    }

    public List<Parkour> getList(){
        return this.parkourList;
    }

    public boolean contains(UUID uuid){
        for(Parkour parkour : getList()){
            if(parkour.getPlayerList().containsKey(uuid)){
                return true;
            }
        }
        return false;
    }

    public void addParkour(Parkour parkour, Player creator){
        if(parkour.getCheckpoints() == null){
            creator.sendMessage(Utils.translate("&cNo checkpoints added, add them before saving."));
            return;
        }else if(parkour.getStart() == null){
            creator.sendMessage(Utils.translate("&cNo start location found, add it before saving."));
            return;
        }else if(parkour.getName() == null){
            creator.sendMessage(Utils.translate("&cNo name found, name the parkour before saving."));
            return;
        }else if(parkour.getFinish() == null){
            creator.sendMessage(Utils.translate("&cNo finish location found! Set one before saving."));
            return;
        }
        for(Parkour parkour1 : parkourList){
            if(parkour1.getName().equalsIgnoreCase(parkour.getName())){
                creator.sendMessage(Utils.translate("&cA parkour with that name already exists."));
                return;
            }
        }
        this.parkourList.add(parkour);
        creator.sendMessage(Utils.translate("&cParkour successfully created!"));
        creator.getLocation().getWorld().playSound(creator.getLocation(), Sound.NOTE_PLING,2f,1f);
    }

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent ev){
        if(parkourList.size() > 0){
            parkourList.stream().forEach(e -> {
                if(e.getPlayerList().containsKey(ev.getPlayer().getUniqueId())){
                    e.startParkourChecker(ev.getPlayer().getUniqueId());
                    e.checkPlayerPosition(ev.getPlayer().getUniqueId());
                    e.checkpointChecker(ev.getPlayer().getUniqueId());
                }
            });
        }
    }

    public Map<UUID, Parkour> getTempList() {
        return tempList;
    }
}
