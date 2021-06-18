package me.streafe.parkour.parkour;

import me.streafe.parkour.ParkourSystem;
import me.streafe.parkour.parkour.Parkour;
import me.streafe.parkour.utils.CounterRunnable;
import me.streafe.parkour.utils.PacketUtils;
import me.streafe.parkour.utils.Utils;
import org.bukkit.*;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.ItemMergeEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitTask;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.util.*;

public class SimpleParkour implements Parkour{

    private List<Location> checkpoints;
    private Location finish;
    private Location start;
    private String name;
    private LeaderboardObject leaderboardObject;
    private Location leaderboardLocation;

    private Map<UUID,Double> playersList;
    private Map<UUID,BukkitTask> counterRunnableMap;
    private Map<UUID,Boolean> hasFlight;
    private Map<UUID,Location> playerCheckpoint;

    private Map<UUID,ItemStack[]> currentItems;

    public SimpleParkour(Location start, List<Location> checkpoints, Location finish, String name, Location leaderboardLoc){
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
        this.playerCheckpoint = new HashMap<>();
        this.name = name;
        this.currentItems = new HashMap<>();
        this.leaderboardObject = new LeaderboardObject(leaderboardLocation,name);
        this.leaderboardLocation = leaderboardLoc;
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

    private void endParkour(UUID uuid,boolean flying) {

        if(flying){
            Bukkit.getPlayer(uuid).sendMessage(ChatColor.translateAlternateColorCodes('&',"&cYou started flying, parkour cancelled."));
        }else{
            Bukkit.getPlayer(uuid).sendMessage(ChatColor.translateAlternateColorCodes('&',"&cParkour cancelled, returned to the start"));
        }

        Location tempLoc = new Location(start.getWorld(),start.getX(),start.getY(),start.getZ(),start.getYaw(),start.getPitch());
        tempLoc.setX(start.getX() + 1);
        tempLoc.setZ(start.getZ() + 1);
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
        if(Bukkit.getPlayer(uuid).getLocation().getBlock().getType() == Material.IRON_PLATE){
            Location playerLoc = Bukkit.getPlayer(uuid).getLocation();
            int var = 0;
            for(Location location : checkpoints){
                if(location.getBlockX() == playerLoc.getBlockX() && location.getBlockY() == playerLoc.getBlockY() && location.getBlockZ() == playerLoc.getBlockZ()){
                    getPlayerCheckpoint().put(uuid,location);
                    Bukkit.getPlayer(uuid).sendMessage(Utils.translate("&aCheckpoint &e" + var + " &areached!"));
                    Bukkit.getPlayer(uuid).playSound(Bukkit.getPlayer(uuid).getLocation(),Sound.NOTE_PLING,1.5f,1f);
                    return;
                }
                var++;
            }
        }else if(Bukkit.getPlayer(uuid).getLocation().getBlock().getType() == Material.GOLD_PLATE){
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
            endParkour(uuid,false);
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



        Bukkit.getPlayer(uuid).setAllowFlight(getHasFlight().get(uuid));
        getHasFlight().remove(uuid);

        if(currentItems.containsKey(uuid)){
            Bukkit.getPlayer(uuid).getInventory().clear();
            Bukkit.getPlayer(uuid).getInventory().setContents(currentItems.get(uuid));
            Bukkit.getPlayer(uuid).updateInventory();
            currentItems.remove(uuid);
        }

        double prevBest;
        File file = new File(ParkourSystem.getInstance().getPathToPManager());
        YamlConfiguration yaml = YamlConfiguration.loadConfiguration(file);

        Player player = Bukkit.getPlayer(uuid);

        if(yaml.get("parkours."+getName() + ".runs." + Bukkit.getPlayer(uuid).getName()) != null){
            prevBest = yaml.getDouble("parkours."+getName() + ".runs." + Bukkit.getPlayer(uuid).getName());
            if(getPlayerList().get(uuid) == 0.0){
                Bukkit.getPlayer(uuid).sendMessage(Utils.translate("&cRun not saved, did you cheat maybe?"));
                playersList.remove(uuid);
                counterRunnableMap.remove(uuid);
                return;
            }
            if(getPlayerList().get(uuid) < prevBest){
                yaml.set("parkours."+this.getName() + ".runs." + Bukkit.getPlayer(uuid).getName(),getPlayerList().get(uuid));
                player.sendMessage(" ");
                player.sendMessage(Utils.translate("&eNew record!"));
                player.sendMessage(" ");
                try {
                    yaml.save(file);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }else{
                player.sendMessage(" ");
                player.sendMessage(Utils.translate("&eYour best is &a" + prevBest));
                player.sendMessage(" ");
            }
        }else{
            if(getPlayerList().get(uuid) == 0.0){
                Bukkit.getPlayer(uuid).sendMessage(Utils.translate("&cRun not saved, did you cheat maybe?"));
                playersList.remove(uuid);
                counterRunnableMap.remove(uuid);
                return;
            }
            yaml.set("parkours."+this.getName() + ".runs." + Bukkit.getPlayer(uuid).getName(),getPlayerList().get(uuid));
            player.sendMessage(" ");
            player.sendMessage(Utils.translate("&eNew record!"));
            player.sendMessage(" ");
            try {
                yaml.save(file);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        playersList.remove(uuid);
        counterRunnableMap.remove(uuid);
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
    public void removePlayer(UUID uuid, boolean flying) {
        endParkour(uuid, flying);
    }

    public Map<UUID, Boolean> getHasFlight() {
        return hasFlight;
    }

    public Map<UUID, Location> getPlayerCheckpoint() {
        return playerCheckpoint;
    }

    @Override
    public void setLeaderboard(Location location) {
        this.leaderboardLocation = location;
    }

    @Override
    public Location getLeaderboardLoc() {
        return this.leaderboardLocation;
    }

    @Override
    public LeaderboardObject getLeaderboardObject() {
        return this.leaderboardObject;
    }

    @Override
    public void setLeaderboardObject(LeaderboardObject leaderboardObject) {
        this.leaderboardObject = leaderboardObject;
    }
}
