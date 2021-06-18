package me.streafe.parkour.utils;

import me.streafe.parkour.ParkourSystem;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

public class CounterRunnable extends BukkitRunnable {

    private double timeD = 0.0;
    private Player player;

    public CounterRunnable(Player player){
        this.player = player;
    }

    /**
     * This function has to be run 1/10 of a second in order to work
     * properly.
     */
    @Override
    public void run() {
        if(timeD == 0.0){
            ParkourSystem.getInstance().getParkourManager().getParkourByPlayerUUID(player.getUniqueId()).getLongValues().put(player.getUniqueId(),System.nanoTime());
        }
        timeD = Utils.round(timeD+0.1,2);

        /*
            String value;

            try{
                value = (System.nanoTime() - timeMili) / 1000000000 + "." + String.valueOf((System.nanoTime() - timeMili) / 100000).substring(1,4);
            }catch (Exception e){
                value = 0.0+"";
            }



            ParkourSystem.getInstance().getParkourManager().getParkourByPlayerUUID(player.getUniqueId()).getPlayerList().put(player.getUniqueId(),Utils.round(Double.parseDouble(value),3));
            player.sendMessage(value);
            */

            if(player.getLocation().getBlock().getType() == Material.GOLD_PLATE){
                if(ParkourSystem.getInstance().getParkourManager().getParkourList().size() > 0){
                    ParkourSystem.getInstance().getParkourManager().getParkourByPlayerUUID(player.getUniqueId()).checkpointChecker(player.getUniqueId());
                }
            }


        PacketUtils.sendActionBarMessage(player,Utils.translate("&e" + Utils.round(timeD,2)));
    }

}
