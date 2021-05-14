package me.streafe.parkour.parkour;

import me.streafe.parkour.ParkourSystem;
import me.streafe.parkour.parkour.Parkour;
import me.streafe.parkour.utils.Utils;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.util.*;

public class ParkourManager implements Listener{

    private List<Parkour> parkourList;
    private Map<UUID,Parkour> tempList;

    public ParkourManager(){
        this.parkourList = new ArrayList<>();
        this.tempList = new HashMap<>();
        try{
            addSavedParkours();
        }catch (Exception e){
            e.printStackTrace();
        }
    }


    public List<Parkour> getList(){
        return this.parkourList;
    }

    public void addSavedParkours(){
        File file = new File(ParkourSystem.getInstance().getPathToPManager());
        YamlConfiguration yaml = YamlConfiguration.loadConfiguration(file);
        if(yaml.getConfigurationSection("parkours") != null){
            for(String string : yaml.getConfigurationSection("parkours").getKeys(false)){
                String name = "";
                Location start = null;
                Location finish = null;
                List<Location> checkPoints = new ArrayList<>();
                for(String s : yaml.getConfigurationSection("parkours." + string).getKeys(false)){

                    if(s.equalsIgnoreCase("name")){
                        name = yaml.getString("parkours." + string + "." + s);
                    }
                    if(s.equalsIgnoreCase("startPoint")){
                        start = Utils.readLocFromString(yaml.getString("parkours." + string + "." + s));
                    }
                    if(s.equalsIgnoreCase("finishPoint")){
                        finish = Utils.readLocFromString(yaml.getString("parkours." + string + "." + s));
                    }
                    if(s.equalsIgnoreCase("checkPoints")){
                        for(String checkP : yaml.getConfigurationSection("parkours." + string + "." + s).getKeys(false)){
                            //ParkourSystem.getInstance().getServer().getConsoleSender().sendMessage(Utils.readLocFromString(yaml.getString("parkours." + string + "." + s + "." + checkP)).toString());
                            checkPoints.add(Utils.readLocFromString(yaml.getString("parkours." + string + "." + s + "." + checkP)));
                        }
                    }
                }
                getParkourList().add(new SimpleParkour(start,checkPoints,finish,name));
            }
        }
    }

    public void saveParkours() throws IOException {
        File file = new File(ParkourSystem.getInstance().getPathToPManager());
        YamlConfiguration yaml = YamlConfiguration.loadConfiguration(file);
        for(Parkour parkour : getParkourList()){
            yaml.set("parkours." + parkour.getName() + ".startPoint",Utils.locationToString(parkour.getStart()));
            yaml.set("parkours." + parkour.getName() + ".finishPoint",Utils.locationToString(parkour.getFinish()));
            yaml.set("parkours." + parkour.getName() + ".name",parkour.getName());
            List<String> checkpoints = new ArrayList<>();
            parkour.getCheckpoints().forEach(e ->{
                checkpoints.add(Utils.locationToString(e));
            });
            for(int i = 0; i < checkpoints.size(); i++){
                yaml.set("parkours." + parkour.getName() + ".checkPoints." + i,checkpoints.get(i));
            }
            yaml.options().configuration().save(file);
        }
    }

    public Parkour getParkourByCreator(UUID uuid){
        for(Map.Entry<UUID,Parkour> entry : tempList.entrySet()){
            if(entry.getKey().equals(uuid)){
                return entry.getValue();
            }
        }
        return null;
    }

    public boolean playerTempMode(UUID uuid){
        for(Map.Entry<UUID,Parkour> entry : tempList.entrySet()){
            if(entry.getKey().equals(uuid)){
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
        creator.sendMessage(Utils.translate("&aParkour &b" + parkour.getName() + " &asuccessfully saved."));
        creator.getLocation().getWorld().playSound(creator.getLocation(), Sound.NOTE_PLING,2f,1f);
        tempList.remove(creator.getUniqueId());
    }

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent ev){
        if(parkourList.size() > 0){
            parkourList.forEach(e -> {
                if(e.getPlayerList().containsKey(ev.getPlayer().getUniqueId())){
                    ev.getPlayer().sendMessage(Utils.translate("&cSheeeeesh"));
                    e.startParkourChecker(ev.getPlayer().getUniqueId());
                    e.checkPlayerPosition(ev.getPlayer().getUniqueId());
                    e.checkpointChecker(ev.getPlayer().getUniqueId());
                }
            });
        }
    }

    public List<Parkour> getParkourList(){
        return this.parkourList;
    }

    public Map<UUID, Parkour> getTempList() {
        return tempList;
    }
}
