package me.streafe.parkour;

import me.streafe.parkour.parkour.ParkourCommand;
import me.streafe.parkour.parkour.ParkourManager;
import me.streafe.parkour.utils.Utils;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;

public class ParkourSystem extends JavaPlugin {

    private static ParkourSystem instance;
    private ParkourManager parkourManager;

    @Override
    public void onEnable(){
        //initialize instance & fix config
        instance = this;

        this.parkourManager = new ParkourManager();
        getConfig().options().copyDefaults(true);
        saveConfig();

        File dir = new File(getDataFolder(),"Parkour");
        File file = new File(getDataFolder() + "/Parkour/ParkourManager.yml");
        if(!dir.exists()){
            dir.mkdirs();
        }
        if(!file.exists()){
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        getServer().getPluginManager().registerEvents(new ParkourManager(),this);
        getCommand("parkour").setExecutor(new ParkourCommand());


    }

    @Override
    public void onDisable() {
    }

    /**
     *
     * @return class instance which returns from the ParkourSystem class whom extends JavaPlugin.
     */
    public static ParkourSystem getInstance(){
        return instance;
    }


    public ParkourManager getParkourManager() {
        return parkourManager;
    }

    public String getPathToPManager(){
        return getDataFolder() + "/Parkour/ParkourManager.yml";
    }


}
