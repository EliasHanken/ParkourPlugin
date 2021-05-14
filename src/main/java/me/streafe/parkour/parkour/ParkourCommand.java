package me.streafe.parkour.parkour;

import me.streafe.parkour.ParkourSystem;
import me.streafe.parkour.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

import java.io.IOException;

public class ParkourCommand implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String s, String[] args) {
        if(sender instanceof Player){
            Player player = (Player) sender;
            ConfigurationSection conf = ParkourSystem.getInstance().getConfig();
            ParkourManager parkourManager = ParkourSystem.getInstance().getParkourManager();

            if(cmd.getName().equalsIgnoreCase("parkour")){
                if(args.length == 2){
                    if(args[0].equalsIgnoreCase("create")){
                        if(parkourManager.getTempList().size() > 0){
                            for(Parkour parkours : ParkourSystem.getInstance().getParkourManager().getTempList().values()){
                                if(parkours.getName().equalsIgnoreCase(args[1])){
                                    player.sendMessage(Utils.translate("&cThere is already a parkour in progress with this name"));
                                    return true;
                                }
                            }
                        }

                        parkourManager.getTempList().put(player.getUniqueId(),new SimpleParkour(null,null,null,args[1]));
                        player.sendMessage(Utils.translate("&aParkour added to temp list"));
                        return true;
                    }

                    else if(args[0].equalsIgnoreCase("setStart")){
                        Parkour parkour = parkourManager.getTempList().get(player.getUniqueId());
                        if(parkourManager.playerTempMode(player.getUniqueId()) && parkourManager.getParkourByCreator(player.getUniqueId()).getName().equalsIgnoreCase(args[1])){
                            parkourManager.getTempList().get(player.getUniqueId()).setStart(player.getLocation());
                            player.sendMessage(Utils.translate("&aStarting point set for the parkour named " + parkour.getName()));
                        }else{
                            player.sendMessage(Utils.translate("&cYou currently don't have any ongoing parkours with that name to be changed."));
                        }
                        return true;
                    }

                    else if(args[0].equalsIgnoreCase("setFinish")){
                        Parkour parkour = parkourManager.getTempList().get(player.getUniqueId());
                        if(parkourManager.playerTempMode(player.getUniqueId()) && parkourManager.getParkourByCreator(player.getUniqueId()).getName().equalsIgnoreCase(args[1])){
                            parkourManager.getTempList().get(player.getUniqueId()).setFinish(player.getLocation());
                            player.sendMessage(Utils.translate("&aFinishing point set for the parkour named " + parkour.getName()));
                        }else{
                            player.sendMessage(Utils.translate("&cYou currently don't have any ongoing parkours with that name to be changed."));
                        }
                        return true;
                    }

                    else if(args[0].equalsIgnoreCase("save")){
                        Parkour parkour = parkourManager.getTempList().get(player.getUniqueId());
                        if(parkourManager.playerTempMode(player.getUniqueId()) && parkourManager.getParkourByCreator(player.getUniqueId()).getName().equalsIgnoreCase(args[1])){
                            parkourManager.addParkour(parkour,player);
                            try {
                                parkourManager.saveParkours();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }else{
                            player.sendMessage(Utils.translate("&cYou currently don't have any ongoing parkours to be saved."));
                        }
                        return true;
                    }

                    else if(args[0].equalsIgnoreCase("setCheckPoint")){
                        player.sendMessage(parkourManager.getTempList().toString());
                        Parkour parkour = parkourManager.getTempList().get(player.getUniqueId());
                        if(parkourManager.playerTempMode(player.getUniqueId()) && parkourManager.getParkourByCreator(player.getUniqueId()).getName().equalsIgnoreCase(args[1])){
                            parkour.getCheckpoints().add(player.getLocation());
                            player.sendMessage(Utils.translate("&aCheckpoint " + parkour.getCheckpoints().size() + " was set!"));
                        }else{
                            player.sendMessage(Utils.translate("&cYou currently don't have any ongoing parkours with that name to be changed."));
                        }
                        return true;
                    }

                    else if(args[0].equalsIgnoreCase("join")){
                        final boolean[] foundParkour = new boolean[1];
                        parkourManager.getParkourList().stream().forEach(e -> {
                            if(e.getName().equalsIgnoreCase(args[1])){
                                e.addPlayer(player.getUniqueId());
                                foundParkour[0] = true;
                            }
                        });
                        if(!foundParkour[0]){
                            player.sendMessage(Utils.translate("&cParkour &b"+args[1]+"&c under maintenance"));
                        }
                        return true;
                    }


                }else if(args.length == 1){
                    if(args[0].equalsIgnoreCase("list")){
                        if(parkourManager.getParkourList().size() <= 0){
                            player.sendMessage(Utils.translate("&cNo operational parkours to be found."));
                        }else{
                            player.sendMessage(Utils.translate("&7--[&aParkours&7]--"));
                            parkourManager.getParkourList().stream().forEach(e -> {
                                player.sendMessage(Utils.translate("&7Name: &e"+e.getName()
                                        + Utils.translate("\n   &7Players: &e" + e.getPlayerList()))
                                        + Utils.translate("\n   &7Checkpoints: &e" + e.getCheckpoints().size())
                                        + Utils.translate("\n   &7Type: &e" + e.getClass().getSimpleName())
                                        + "\n");
                            });
                        }
                    }
                    return true;
                }
                player.sendMessage(ChatColor.translateAlternateColorCodes('&',"&c" +
                        "Invalid command:\n" +
                        "&eTry /parkour \n&7(&e1&7)&e<setStart>/<setCheckpoint>/\n&7(&e1&7)&e<setFinish>/<create>/\n&7(&e1&7)&e<delete>/<save>\n&7(&e1&7)&e<list>\n&7(&e2&7)&e<name>"));

            }
        }
        return true;
    }
}
