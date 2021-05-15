package me.streafe.parkour.parkour;

import me.streafe.parkour.ParkourSystem;
import me.streafe.parkour.utils.Utils;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;

public class ParkourManager implements Listener{

    public List<Parkour> parkourList;
    public Map<UUID,Parkour> tempList;

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
        for(Map.Entry<UUID,Parkour> entry : ParkourSystem.getInstance().getParkourManager().tempList.entrySet()){
            if(entry.getKey().equals(uuid)){
                return entry.getValue();
            }
        }
        return null;
    }

    public boolean playerTempMode(UUID uuid){
        for(Map.Entry<UUID,Parkour> entry : ParkourSystem.getInstance().getParkourManager().tempList.entrySet()){
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
        ParkourSystem.getInstance().getParkourManager().parkourList.add(parkour);
        creator.sendMessage(Utils.translate("&aParkour &b" + parkour.getName() + " &asuccessfully saved."));
        creator.getLocation().getWorld().playSound(creator.getLocation(), Sound.NOTE_PLING,2f,1f);
        ParkourSystem.getInstance().getParkourManager().tempList.remove(creator.getUniqueId());
    }

    @EventHandler
    public void onPlayerMoveStart(PlayerInteractEvent ev){
        if(ev.getAction().equals(Action.PHYSICAL)){
            if(ev.getClickedBlock().getType() == Material.IRON_PLATE){
                if(ParkourSystem.getInstance().getParkourManager().getParkourList().size() > 0){
                    ParkourSystem.getInstance().getParkourManager().getParkourList().forEach(e -> {
                        if(e.getPlayerList().containsKey(ev.getPlayer().getUniqueId())){
                            e.startParkourChecker(ev.getPlayer().getUniqueId());
                            e.checkpointChecker(ev.getPlayer().getUniqueId());
                        }
                    });
                }
            }
        }
    }

    /*
    @EventHandler
    public void onPlayerMoveFinish(PlayerInteractEvent ev){
        if(ev.getAction().equals(Action.PHYSICAL)){
            if(ev.getClickedBlock().getType() == Material.GOLD_PLATE){
                if(ParkourSystem.getInstance().getParkourManager().getParkourList().size() > 0){
                    ParkourSystem.getInstance().getParkourManager().getParkourList().forEach(e -> {
                        if(e.getPlayerList().containsKey(ev.getPlayer().getUniqueId())){
                            e.checkpointChecker(ev.getPlayer().getUniqueId());
                        }
                    });
                }
            }
        }
    }

     */

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent e){
        Player player = e.getPlayer();
        if(e.getAction() == Action.RIGHT_CLICK_AIR || e.getAction() == Action.RIGHT_CLICK_BLOCK){
            if(player.getItemInHand() != null){
                if(player.getItemInHand().hasItemMeta()){
                    if(e.getItem().getItemMeta().getDisplayName().contains("Exit parkour")){
                        e.setCancelled(true);
                        //getParkourByUUID(player.getUniqueId()).removePlayer(player.getUniqueId());
                        if(getParkourByPlayerUUID(player.getUniqueId()) != null){
                            getParkourByPlayerUUID(player.getUniqueId()).removePlayer(player.getUniqueId());
                        }
                    }else if(e.getItem().getItemMeta().getDisplayName().contains("Last checkpoint")){
                        e.setCancelled(true);
                        player.sendMessage(Utils.translate("&cCheckpoints are under development."));
                    }
                }
            }
        }
    }

    public List<Parkour> getParkourList(){
        return this.parkourList;
    }

    public Map<UUID, Parkour> getTempList() {
        return tempList;
    }

    public boolean isInParkour(UUID uuid){
        AtomicBoolean isInParkour = new AtomicBoolean(false);
        ParkourSystem.getInstance().getParkourManager().getParkourList().stream().forEach(e ->{
            if(e.getPlayerList().containsKey(uuid)){
                isInParkour.set(true);
            }
        });
        return isInParkour.get();
    }

    public Parkour getParkourByPlayerUUID(UUID uuid){
        for(Parkour parkour : ParkourSystem.getInstance().getParkourManager().getParkourList()){
            if(parkour.getPlayerList().containsKey(uuid)){
                return parkour;
            }
        }
        return null;
    }

    public Parkour getParkourByName(String name){
        for(Parkour parkour : ParkourSystem.getInstance().getParkourManager().getParkourList()){
            if(parkour.getName().equalsIgnoreCase(name)){
                return parkour;
            }
        }
        return null;
    }

    public boolean deleteParkour(String name){
        Iterator<Parkour> iterator = getParkourList().iterator();
        while(iterator.hasNext()){
            Parkour parkour = iterator.next();
            if(parkour.getName().equalsIgnoreCase(name)){
                iterator.remove();
                return true;
            }
        }
        return false;
    }
}
