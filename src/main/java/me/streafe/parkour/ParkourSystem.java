package me.streafe.parkour;

import me.streafe.parkour.parkour.ParkourCommand;
import me.streafe.parkour.parkour.ParkourManager;
import org.bukkit.plugin.java.JavaPlugin;

public class ParkourSystem extends JavaPlugin {

    private static ParkourSystem instance;
    private ParkourManager parkourManager;

    @Override
    public void onEnable(){
        //initialize instance & fix config
        instance = this;
        getConfig().options().copyDefaults(true);
        saveConfig();
        this.parkourManager = new ParkourManager();

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


}
