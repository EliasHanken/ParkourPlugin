package me.streafe.parkour.parkour;

import me.streafe.parkour.ParkourSystem;
import me.streafe.parkour.parkour.Parkour;
import me.streafe.parkour.utils.CounterRunnable;
import me.streafe.parkour.utils.Utils;
import org.bukkit.*;
import org.bukkit.entity.Item;
import org.bukkit.event.EventHandler;
import org.bukkit.inventory.ItemStack;

import java.io.Serializable;
import java.util.*;

public class SimpleParkour implements Parkour{

    private List<Location> checkpoints;
    private Location finish;
    private Location start;
    private String name;

    private Map<UUID,Double> playersList;
    private Map<UUID,CounterRunnable> counterRunnableMap;

    public SimpleParkour(Location start, List<Location> checkpoints, Location finish, String name){
        this.checkpoints = checkpoints;
        this.start = start;
        this.finish = finish;
        this.counterRunnableMap = new HashMap<>();
        this.playersList = new HashMap<>();
        this.name = name;

    }

    @Override
    public void startParkourChecker(UUID uuid) {
        Bukkit.getPlayer(uuid).sendMessage(Utils.translate("&cSheeesh"));
        if(Bukkit.getPlayer(uuid).getLocation().getBlock().getType() == Material.GOLD_PLATE &&
        Bukkit.getPlayer(uuid).getLocation().equals(start)){
            if(counterRunnableMap.containsKey(uuid)){
                counterRunnableMap.get(uuid).cancel();
                counterRunnableMap.get(uuid).runTaskTimerAsynchronously(ParkourSystem.getInstance(), 0L, 2L);
            }else{
                CounterRunnable counterRunnable = new CounterRunnable();
                counterRunnableMap.put(uuid,counterRunnable);
                counterRunnable.runTaskTimerAsynchronously(ParkourSystem.getInstance(), 0L, 2L);
            }
            Bukkit.getPlayer(uuid).setAllowFlight(false);
            Bukkit.getPlayer(uuid).sendMessage(ChatColor.translateAlternateColorCodes('&', "&aParkour started, RUN!!!"));
            Bukkit.getPlayer(uuid).getWorld().playSound(Bukkit.getPlayer(uuid).getLocation(), Sound.NOTE_STICKS, 2f, 1f);
        }


    }

    @Override
    public Map<UUID,Double> getPlayerList(){
        return this.playersList;
    }

    private void endParkour(UUID uuid) {
        Bukkit.getPlayer(uuid).sendMessage(ChatColor.translateAlternateColorCodes('&',"&cParkour cancelled, returned to the start"));
        Location tempLoc = start;
        //So you dont teleport to the freaking pressureplate and start again.
        tempLoc.setX(start.getX() + 1);
        Bukkit.getPlayer(uuid).teleport(tempLoc);
        Bukkit.getPlayer(uuid).getWorld().playSound(Bukkit.getPlayer(uuid).getLocation(),Sound.ENDERMAN_TELEPORT,1f,1f);

        playersList.remove(uuid);
        counterRunnableMap.get(uuid).cancel();
        counterRunnableMap.remove(uuid);
        removePlayer(uuid);
    }

    @Override
    public void checkpointChecker(UUID uuid) {
        if(Bukkit.getPlayer(uuid).getLocation().getBlock().getType() == Material.GOLD_PLATE){
            if(checkpoints.contains(Bukkit.getPlayer(uuid).getLocation())){
                for(int i = 0; i < checkpoints.size();i++){
                    if(checkpoints.get(i).equals(Bukkit.getPlayer(uuid).getLocation())){
                        Bukkit.getPlayer(uuid).getLocation()
                                .getWorld()
                                .playSound(Bukkit.getPlayer(uuid).getLocation(), Sound.NOTE_PLING,2f,1f);
                        Bukkit.getPlayer(uuid).sendMessage(ChatColor.translateAlternateColorCodes(
                                '&',"&bCheckpoint " +
                                        i + "/" +
                                        checkpoints.size() + " reached!"));
                        break;
                    }
                }
            }else if(Bukkit.getPlayer(uuid).getLocation().equals(finish)){
                finish(uuid);
            }
        }
    }

    @Override
    public void giveItems(UUID uuid) {

    }

    @Override
    public void checkPlayerPosition(UUID uuid) {
        if(Bukkit.getPlayer(uuid).getLocation().getY()-10 < start.getY()){
            endParkour(uuid);
        }
    }

    @Override
    public void saveRun(UUID uuid) {

    }

    @Override
    public void finish(UUID uuid) {
        this.playersList.put(uuid,counterRunnableMap.get(uuid).getTimeAndStop());
        Bukkit.getPlayer(uuid).sendMessage(ChatColor.translateAlternateColorCodes('&',"&aParkour finished in &e" + counterRunnableMap.get(uuid) + " &aseconds!"));
        giveReward(uuid);
    }

    @Override
    public List<Location> getCheckpoints() {
        return this.checkpoints;
    }

    @Override
    public Location getStart() {
        return this.start;
    }

    @Override
    public Location getFinish() {
        return this.finish;
    }

    @Override
    public void setCheckpoints(List<Location> locations) {
        this.checkpoints = locations;
    }

    @Override
    public void setStart(Location location) {
        this.start = location;
    }

    @Override
    public void setFinish(Location location) {
        this.finish = location;
    }


    @Override
    public String getName() {
        return name;
    }

    @Override
    public void setName(String name) {
        this.name = name;
    }

    @Override
    public void giveReward(UUID uuid) {
        Bukkit.getPlayer(uuid).getInventory().addItem(new ItemStack(Material.DIAMOND));
        Bukkit.getPlayer(uuid).sendMessage(Utils.translate("&eReward : &a1 Diamond"));
    }

    @Override
    public void addPlayer(UUID uuid) {
        if(!playersList.containsKey(uuid)){
            getPlayerList().put(uuid,null);
            Bukkit.getPlayer(uuid).sendMessage(Utils.translate("&eYou can now start the parkour :)"));
            Bukkit.getPlayer(uuid).getWorld().playSound(Bukkit.getPlayer(uuid).getLocation(),Sound.NOTE_PLING,1f,1.5f);
        }else{
            Bukkit.getPlayer(uuid).sendMessage(Utils.translate("&cParkour already started"));
            Bukkit.getPlayer(uuid).getWorld().playSound(Bukkit.getPlayer(uuid).getLocation(),Sound.NOTE_PLING,1f,0.5f);
        }
    }

    @Override
    public void removePlayer(UUID uuid) {
        getPlayerList().remove(uuid);
    }
}
