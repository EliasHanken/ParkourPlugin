package me.streafe.parkour.parkour;

import me.streafe.parkour.ParkourSystem;
import me.streafe.parkour.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;

import java.io.File;
import java.util.*;

public class LeaderboardObject {

    private Map<String,Double> leaderBoard;
    private final Location location;
    private LeaderBoardUpdater updater;
    private String parkourName;
    private ArmorStand armorStand;
    private List<String> text;


    public LeaderboardObject(Location location, String parkourName){
        this.leaderBoard = new HashMap<>();
        this.location = location;
        this.updater = new LeaderBoardUpdater(this);
        this.parkourName = parkourName;
        this.text = new ArrayList<>();

        setupArmorstand();

        updater.runTaskTimer(ParkourSystem.getInstance(),0L,40L);
    }

    public void setupArmorstand(){
        for(Entity entity : location.getWorld().getNearbyEntities(location,1d,10d,1d)){
            if(entity instanceof ArmorStand){
                entity.remove();
                ParkourSystem.getInstance().getServer().getConsoleSender().sendMessage("Old armorstand found & removed");
            }
        }

        this.armorStand = location.getWorld().spawn(location,ArmorStand.class);

        ParkourSystem.getInstance().getServer().getConsoleSender().sendMessage("New armorstand created, initialized as leaderboard");


        this.armorStand.setCustomName(Utils.translate("&aLeaderboard"));
        this.armorStand.setCustomNameVisible(true);
        this.armorStand.setGravity(false);
        this.armorStand.setVisible(false);

        createArmorstand("&7(" + parkourName + ")",location.subtract(0,0.25,0));
        createArmorstand("&7-",location.subtract(0,0.25,0));
    }

    public Map<String,Double> getLeaderBoard() {
        return leaderBoard;
    }

    public void setLeaderBoard(Map<String,Double> leaderBoard) {
        this.leaderBoard = leaderBoard;
    }

    public Location getLocation() {
        return location;
    }

    public void update() {
        File file = new File(ParkourSystem.getInstance().getPathToPManager());
        YamlConfiguration yaml = YamlConfiguration.loadConfiguration(file);

        if (yaml.get("parkours." + this.parkourName + ".runs") != null) {
            for (String player : yaml.getConfigurationSection("parkours." + this.parkourName + ".runs").getKeys(false)) {
                getLeaderBoard().put(player, yaml.getDouble("parkours." + this.parkourName + ".runs." + player));
            }
        }

        text.clear();
        getLeaderBoard().entrySet().stream()
                .sorted(Map.Entry.comparingByValue())
                .forEachOrdered(x -> text.add(Utils.translate("&e" + x.getKey() + " &7- &b" + x.getValue())));
            //ParkourSystem.getInstance().getServer().getPlayer("krilux").sendMessage(this.getLeaderBoard().toString());

        Location loc = new Location(location.getWorld(),location.getX(),location.getY(),location.getZ());
        for (String s : text) {
            createArmorstandFlickerFree(s, loc.subtract(0, 0.25, 0));
        }

        if(text.size() == 0){
            for(Entity entity : this.location.getWorld().getNearbyEntities(this.location,3d,3d,3d)){
                if(entity instanceof ArmorStand){
                    ArmorStand am = (ArmorStand) entity;
                    if(!am.getCustomName().contains("Leaderboard") && !am.getCustomName().contains(parkourName) && !ChatColor.stripColor(am.getCustomName()).equals("-")){
                        entity.remove();
                    }
                }
            }
        }
    }

    public void createArmorstand(String name, Location lc){
        for(Entity entity : this.location.getWorld().getNearbyEntities(this.location,3d,3d,3d)){
            if(entity instanceof ArmorStand){
                if(entity.getLocation().equals(lc)){
                    entity.remove();
                }
            }
        }

        ArmorStand armorStand = lc.getWorld().spawn(lc,ArmorStand.class);

        armorStand.setVisible(false);
        armorStand.setCustomNameVisible(true);
        armorStand.setCustomName(Utils.translate(name));
        armorStand.setGravity(false);
    }

    public void createArmorstandFlickerFree(String name, Location lc){
        ArmorStand armorStand = null;
        for(Entity entity : this.location.getWorld().getNearbyEntities(this.location,1d,1d,1d)){
            if(entity instanceof ArmorStand){
                if(entity.getLocation().equals(lc)){
                    armorStand = (ArmorStand) entity;
                }
            }
        }
        if(armorStand == null){
            armorStand = lc.getWorld().spawn(lc,ArmorStand.class);
        }
        armorStand.setVisible(false);
        armorStand.setCustomName(Utils.translate(name));
        armorStand.setCustomNameVisible(true);
        armorStand.setGravity(false);
    }
}
