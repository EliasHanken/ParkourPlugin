package me.streafe.parkour.parkour;

import me.streafe.parkour.ParkourSystem;
import me.streafe.parkour.utils.Utils;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

public class ParkourCommand implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String s, String[] args) {
        if(sender instanceof Player){
            Player player = (Player) sender;
            ConfigurationSection conf = ParkourSystem.getInstance().getConfig();
            ParkourManager parkourManager = ParkourSystem.getInstance().getParkourManager();

            if(args[0].equalsIgnoreCase("parkour")){
                Parkour parkour = new SimpleParkour(null,null,null,null);
                if(args.length == 3){
                    if(args[1].equalsIgnoreCase("create")){
                        for(Parkour parkours : ParkourSystem.getInstance().getParkourManager().getTempList().values()){
                            if(parkours.getName().equalsIgnoreCase(args[2])){
                                player.sendMessage(Utils.translate("&cThere is already a parkour in progress with this name"));
                                return true;
                            }
                        }
                        parkour.setName(args[2]);
                        parkourManager.getTempList().put(player.getUniqueId(),parkour);
                        player.sendMessage(Utils.translate("&aParkour added to temp list"));
                    }

                    else if(args[1].equalsIgnoreCase("setStart")){
                        if(parkourManager.contains(player.getUniqueId())){
                            parkourManager.getTempList().get(player.getUniqueId()).setStart(player.getLocation());
                            player.sendMessage(Utils.translate("&aStarting point set for the parkour named " + parkour.getName()));
                        }else{
                            player.sendMessage(Utils.translate("&cYou currently don't have any ongoing parkours to be changed."));
                        }
                    }

                    else if(args[1].equalsIgnoreCase("setFinish")){
                        if(parkourManager.contains(player.getUniqueId())){
                            parkourManager.getTempList().get(player.getUniqueId()).setFinish(player.getLocation());
                            player.sendMessage(Utils.translate("&aFinishing point set for the parkour named " + parkour.getName()));
                        }else{
                            player.sendMessage(Utils.translate("&cYou currently don't have any ongoing parkours to be changed."));
                        }
                    }

                    else if(args[1].equalsIgnoreCase("save")){
                        if(parkourManager.contains(player.getUniqueId())){
                            parkourManager.addParkour(parkour,player);
                            player.sendMessage(Utils.translate("&aParkour &b" + parkour.getName() + " &asuccessfully saved."));
                        }else{
                            player.sendMessage(Utils.translate("&cYou currently don't have any ongoing parkours to be saved."));
                        }
                    }
                }else{
                    player.sendMessage(ChatColor.translateAlternateColorCodes('&',"&c" +
                            "Invalid command:\n" +
                            "Try /parkour <setStart>/<setCheckpoint>/<setFinish>/<create>/<delete>/<save> <name>"));
                }
            }
        }
        return true;
    }
}
