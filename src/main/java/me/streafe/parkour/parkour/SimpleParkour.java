package me.streafe.parkour.parkour;

import me.streafe.parkour.ParkourSystem;
import me.streafe.parkour.parkour.Parkour;
import me.streafe.parkour.utils.CounterRunnable;
import me.streafe.parkour.utils.PacketUtils;
import me.streafe.parkour.utils.Utils;
import org.bukkit.*;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.ItemMergeEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitTask;

import java.io.Serializable;
import java.util.*;

public class SimpleParkour implements Parkour{

    private List<Location> checkpoints;
    private Location finish;
    private Location start;
    private String name;

    private Map<UUID,Double> playersList;
    private Map<UUID,BukkitTask> counterRunnableMap;
    private Map<UUID,Boolean> hasFlight;

    private Map<UUID,ItemStack[]> currentItems;

    public SimpleParkour(Location start, List<Location> checkpoints, Location finish, String name){
        if(checkpoints == null){
            this.checkpoints = new ArrayList<>();
        }else{
            this.checkpoints = checkpoints;
        }
        this.start = start;
        this.finish = finish;
        this.counterRunnableMap = new HashMap<>();
        this.playersList = new HashMap<>();
        this.hasFlight = new HashMap<>();
        this.name = name;
        this.currentItems = new HashMap<>();
    }

    @Override
    public void startParkourChecker(UUID uuid) {
        if (!counterRunnableMap.containsKey(uuid)) {
            BukkitTask counterRunnable = new CounterRunnable(Bukkit.getPlayer(uuid)).runTaskTimer(ParkourSystem.getInstance(),0L,2L);
            counterRunnableMap.put(uuid, counterRunnable);
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
        Location tempLoc = new Location(start.getWorld(),start.getX(),start.getY(),start.getZ(),start.getYaw(),start.getPitch());
        tempLoc.setX(tempLoc.getX() + 1);
        tempLoc.setZ(tempLoc.getX() + 1);
        //So you dont teleport to the freaking pressureplate and start again.
        Bukkit.getPlayer(uuid).teleport(tempLoc);
        Bukkit.getPlayer(uuid).getWorld().playSound(Bukkit.getPlayer(uuid).getLocation(),Sound.ENDERMAN_TELEPORT,1f,1f);

        playersList.remove(uuid);
        if(counterRunnableMap.get(uuid) != null){
            counterRunnableMap.get(uuid).cancel();
        }
        counterRunnableMap.remove(uuid);

        Bukkit.getPlayer(uuid).setAllowFlight(getHasFlight().get(uuid));
        getHasFlight().remove(uuid);

        if(currentItems.containsKey(uuid)){
            Bukkit.getPlayer(uuid).getInventory().clear();
            Bukkit.getPlayer(uuid).getInventory().setContents(currentItems.get(uuid));
            Bukkit.getPlayer(uuid).updateInventory();
            currentItems.remove(uuid);
        }

    }

    @Override
    public void checkpointChecker(UUID uuid) {
        if(Bukkit.getPlayer(uuid).getLocation().getBlock().getType() == Material.GOLD_PLATE){
            /*
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

             */
            //TODO fix check of locations.
            finish(uuid);
        }
    }

    @Override
    public void giveItems(UUID uuid) {

        ItemStack doorItem = new ItemStack(Material.WOOD_DOOR);
        ItemMeta doorMeta = doorItem.getItemMeta();
        doorMeta.setDisplayName(Utils.translate("&cExit parkour"));
        doorItem.setItemMeta(doorMeta);

        ItemStack checkpoint = new ItemStack(Material.PAPER);
        ItemMeta checkMeta = checkpoint.getItemMeta();
        checkMeta.setDisplayName(Utils.translate("&bLast checkpoint"));
        checkpoint.setItemMeta(checkMeta);

        Bukkit.getPlayer(uuid).getInventory().setItem(4,doorItem);
        Bukkit.getPlayer(uuid).getInventory().setItem(0,checkpoint);
    }

    @Override
    public void checkPlayerPosition(UUID uuid) {
        if(Bukkit.getPlayer(uuid).getLocation().getY()-10 > start.getY()){
            endParkour(uuid);
        }
    }

    @Override
    public void saveRun(UUID uuid) {

    }

    @Override
    public void finish(UUID uuid) {
        this.getCounterRunnableMap().get(uuid).cancel();
        PacketUtils.sendActionBarMessage(Bukkit.getPlayer(uuid),Utils.translate("&aFinished in &e" + getPlayerList().get(uuid)));
        Bukkit.getPlayer(uuid).sendMessage(ChatColor.translateAlternateColorCodes('&',"&aParkour finished in &e" + getPlayerList().get(uuid) + " &aseconds!"));
        giveReward(uuid);

        Location tempLoc = start;
        //So you dont teleport to the freaking pressureplate and start again.
        tempLoc.setX(start.getX() + 1);
        tempLoc.setZ(start.getZ() + 1);
        Bukkit.getPlayer(uuid).teleport(tempLoc);
        Bukkit.getPlayer(uuid).getWorld().playSound(Bukkit.getPlayer(uuid).getLocation(),Sound.ENDERMAN_TELEPORT,1f,1f);

        playersList.remove(uuid);
        counterRunnableMap.remove(uuid);

        Bukkit.getPlayer(uuid).setAllowFlight(getHasFlight().get(uuid));
        getHasFlight().remove(uuid);

        if(currentItems.containsKey(uuid)){
            Bukkit.getPlayer(uuid).getInventory().clear();
            Bukkit.getPlayer(uuid).getInventory().setContents(currentItems.get(uuid));
            Bukkit.getPlayer(uuid).updateInventory();
            currentItems.remove(uuid);
        }
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

    private Map<UUID,BukkitTask> getCounterRunnableMap(){
        return this.counterRunnableMap;
    }

    @Override
    public void addPlayer(UUID uuid) {
        if(!playersList.containsKey(uuid)){
            getPlayerList().put(uuid,0.0);
            Bukkit.getPlayer(uuid).sendMessage(Utils.translate("&eYou can now start the parkour :)"));
            Bukkit.getPlayer(uuid).getWorld().playSound(Bukkit.getPlayer(uuid).getLocation(),Sound.NOTE_PLING,1f,1.5f);
            this.currentItems.put(uuid,Bukkit.getPlayer(uuid).getInventory().getContents());
            Bukkit.getPlayer(uuid).getInventory().clear();
            giveItems(uuid);
        }else{
            Bukkit.getPlayer(uuid).sendMessage(Utils.translate("&cParkour already started"));
            Bukkit.getPlayer(uuid).getWorld().playSound(Bukkit.getPlayer(uuid).getLocation(),Sound.NOTE_PLING,1f,0.5f);
        }

        Player player = Bukkit.getPlayer(uuid);
        getHasFlight().put(uuid,player.getAllowFlight());
        player.setAllowFlight(false);
    }

    @Override
    public void removePlayer(UUID uuid) {
        endParkour(uuid);
    }

    public Map<UUID, Boolean> getHasFlight() {
        return hasFlight;
    }
}
