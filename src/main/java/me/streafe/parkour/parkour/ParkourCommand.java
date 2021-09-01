package me.streafe.parkour.parkour;

import me.streafe.parkour.ParkourSystem;
import me.streafe.parkour.utils.Utils;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

import java.io.IOException;
import java.util.Iterator;
import java.util.UUID;

public class ParkourCommand implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String s, String[] args) {
        if(sender instanceof Player){
            Player player = (Player) sender;
            ConfigurationSection conf = ParkourSystem.getInstance().getConfig();
            ParkourManager parkourManager = ParkourSystem.getInstance().getParkourManager();

            if(cmd.getName().equalsIgnoreCase("parkour")){
                if(args.length == 2) {
                    if (args[0].equalsIgnoreCase("create")) {
                        if (parkourManager.getTempList().size() > 0) {
                            for (Parkour parkours : ParkourSystem.getInstance().getParkourManager().getTempList().values()) {
                                if (parkours.getName().equalsIgnoreCase(args[1])) {
                                    player.sendMessage(Utils.translate("&cThere is already a parkour in progress with this name"));
                                    return true;
                                }
                            }
                        }
                        parkourManager.getTempList().put(player.getUniqueId(), new SimpleParkour(null, null, null, args[1], null));
                        player.sendMessage(Utils.translate("&aParkour added to temp list"));
                        return true;
                    } else if (args[0].equalsIgnoreCase("setStart")) {
                        Parkour parkour = parkourManager.getTempList().get(player.getUniqueId());
                        if (parkourManager.playerTempMode(player.getUniqueId()) && parkourManager.getParkourByCreator(player.getUniqueId()).getName().equalsIgnoreCase(args[1])) {
                            parkourManager.getTempList().get(player.getUniqueId()).setStart(player.getLocation());
                            player.sendMessage(Utils.translate("&aStarting point set for the parkour named " + parkour.getName()));

                            Location leaderboardLoc = new Location(parkour.getStart().getWorld(), parkour.getStart().getX() + 2, parkour.getStart().getY() + 1.5, parkour.getStart().getZ() + 2);
                            parkour.setLeaderboardObj(new LeaderboardObject(leaderboardLoc, parkour.getName()));
                            player.sendMessage(Utils.translate("&aDefault lb location set. Can be changed."));
                        } else {
                            player.sendMessage(Utils.translate("&cYou currently don't have any ongoing parkours with that name to be changed."));
                        }
                        return true;
                    } else if (args[0].equalsIgnoreCase("setLb")) {
                        Parkour parkour = parkourManager.getTempList().get(player.getUniqueId());
                        if (parkourManager.playerTempMode(player.getUniqueId()) && parkourManager.getParkourByCreator(player.getUniqueId()).getName().equalsIgnoreCase(args[1])) {
                            /*
                            if(parkourManager.getParkourByName(parkour.getName()) != null){
                                File file = new File(ParkourSystem.getInstance().getPathToPManager());
                                YamlConfiguration yaml = YamlConfiguration.loadConfiguration(file);
                                yaml.set("parkours." + parkour.getName() + ".leaderboard",Utils.locationToString(player.getLocation()));
                                try {
                                    yaml.save(file);
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            }
                             */
                            parkour.getLeaderboardObj().getUpdater().cancel();
                            for (Entity entity : parkour.getLeaderboardObj().getLocation().getWorld().getNearbyEntities(parkour.getLeaderboardLoc(), 1d, 5d, 1d)) {
                                if (entity instanceof ArmorStand) {
                                    entity.remove();
                                }
                            }
                            parkourManager.getTempList().get(player.getUniqueId()).setLeaderboardObj(null);
                            parkourManager.getTempList().get(player.getUniqueId()).setLeaderboardObj(new LeaderboardObject(player.getLocation(), args[1]));
                            player.sendMessage(Utils.translate("&aLeaderboard set for the parkour named " + parkour.getName()));
                        } else {
                            player.sendMessage(Utils.translate("&cYou currently don't have any ongoing parkours with that name to be changed."));
                        }
                        return true;
                    } else if (args[0].equalsIgnoreCase("setFinish")) {
                        Parkour parkour = parkourManager.getTempList().get(player.getUniqueId());
                        if (parkourManager.playerTempMode(player.getUniqueId()) && parkourManager.getParkourByCreator(player.getUniqueId()).getName().equalsIgnoreCase(args[1])) {
                            parkourManager.getTempList().get(player.getUniqueId()).setFinish(player.getLocation());
                            player.sendMessage(Utils.translate("&aFinishing point set for the parkour named " + parkour.getName()));
                        } else {
                            player.sendMessage(Utils.translate("&cYou currently don't have any ongoing parkours with that name to be changed."));
                        }
                        return true;
                    } else if(args[0].equals("tpto")){
                        if(parkourManager.getParkourByName(args[1]) != null){
                            player.teleport(parkourManager.getParkourByName(args[1]).getStart().add(0,1,1));
                            return true;
                        }
                    }else if(args[0].equals("reset")){
                        if(parkourManager.getParkourByName(args[1]) != null){
                            parkourManager.getParkourByName(args[1]).parkourUpdate();
                            return true;
                        }
                    }else if (args[0].equalsIgnoreCase("save")) {

                        Parkour parkour = parkourManager.getTempList().get(player.getUniqueId());

                        if (parkourManager.playerTempMode(player.getUniqueId()) && parkourManager.getParkourByCreator(player.getUniqueId()).getName().equalsIgnoreCase(args[1])) {
                            boolean contains = false;
                            for(Parkour parkours : parkourManager.getParkourList()){
                                if(parkours.getName().equals(parkour.getName())){
                                    contains = true;
                                }
                            }
                            if(contains){
                                for(Parkour p : parkourManager.getParkourList()){
                                    if(p.getName().equals(parkour.getName())){
                                        p.setName(parkour.getName());
                                        p.setLeaderboard(parkour.getLeaderboardLoc());
                                        p.setFinish(parkour.getFinish());
                                        p.setStart(parkour.getStart());
                                        p.setCheckpoints(parkour.getCheckpoints());
                                        try {
                                            parkourManager.saveParkours();
                                        } catch (IOException e) {
                                            e.printStackTrace();
                                        }
                                        player.sendMessage(Utils.translate("&aParkour &e'&b"+parkour.getName() + "&e' &awas updated!"));
                                        p.parkourUpdate();
                                        if(parkourManager.removeTempEditor(player.getUniqueId())){
                                            player.sendMessage(Utils.translate("&cYou were removed from the temp editor."));
                                        }
                                    }
                                }
                            }else{
                                parkourManager.addParkour(parkour, player);
                                try {
                                    parkourManager.saveParkours();
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            }
                        } else {
                            player.sendMessage(Utils.translate("&cYou currently don't have any ongoing parkours to be saved."));
                        }
                        return true;
                    }




                    else if(args[0].equalsIgnoreCase("setCheckPoint")){
                        Parkour parkour = parkourManager.getTempList().get(player.getUniqueId());
                        if(parkourManager.playerTempMode(player.getUniqueId()) && parkourManager.getParkourByCreator(player.getUniqueId()).getName().equalsIgnoreCase(args[1])){
                            parkour.getCheckpoints().add(player.getLocation());
                            player.sendMessage(Utils.translate("&aCheckpoint " + parkour.getCheckpoints().size() + " was set!"));
                        }else{
                            player.sendMessage(Utils.translate("&cYou currently don't have any ongoing parkours with that name to be changed."));
                        }
                        return true;
                    }

                    else if(args[0].equalsIgnoreCase("select")){
                        if(parkourManager.getTempList().containsKey(player.getUniqueId())){
                            player.sendMessage(Utils.translate("&cYou already have a parkour in temp mode.\nFinish that before continuing"));
                            return true;
                        }else{
                            for(Parkour parkour : parkourManager.getParkourList()){
                                if(parkour.getName().equalsIgnoreCase(args[1])){
                                    player.sendMessage(Utils.translate("&eParkour '&a"+args[1] + "&e' selected."));
                                    parkourManager.getTempList().put(player.getUniqueId(),parkourManager.getParkourByName(args[1]));
                                    player.sendMessage(Utils.translate("&aParkour added to temp list."));
                                    return true;
                                }
                            }
                        }

                        if(parkourManager.getTempList().size() > 0){
                            for(Parkour parkours : ParkourSystem.getInstance().getParkourManager().getTempList().values()){
                                if(parkours.getName().equalsIgnoreCase(args[1])){
                                    player.sendMessage(Utils.translate("&eParkour '&a"+args[1] + "&e' selected."));
                                    parkourManager.getTempList().put(player.getUniqueId(),ParkourSystem.getInstance().getParkourManager().getParkourByName(args[1]));
                                    player.sendMessage(Utils.translate("&aParkour added to temp list."));
                                    return true;
                                }
                            }
                        }

                        player.sendMessage(Utils.translate("&cNo parkours found with that name to be selected."));
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
                            player.sendMessage(Utils.translate("&cParkour &b"+args[1]+"&c is under maintenance or doesn't exist."));
                        }
                        return true;
                    }

                    else if(args[0].equalsIgnoreCase("delete")) {
                        if(parkourManager.playerTempMode(player.getUniqueId())){
                            Iterator<UUID> it = parkourManager.getTempList().keySet().iterator();
                            if(it.hasNext()){
                                if(it.next().equals(player.getUniqueId())){
                                    it.remove();
                                    player.sendMessage(Utils.translate("&cTemp parkour deleted -> &b" + args[1]));
                                    return true;
                                }
                            }
                        }
                        if (parkourManager.deleteParkour(args[1])) {
                            player.sendMessage(Utils.translate("&cParkour deleted -> &b" + args[1]));
                        } else {
                            player.sendMessage(Utils.translate("&cParkour not deleted -> &b" + args[1] + "\n&cCould not find a parkour with that name."));
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
                player.sendMessage(Utils.translate("&7--[&b&lParkour&r&7]--\n" +
                        "&e/parkour create (name) &7creates a temporary parkour instance.\n" +
                        "&e/parkour setStart (name) &7sets the start point for the parkour.\n" +
                        "&e/parkour setCheckPoint (name) &7sets the checkpoints the parkour. Multiple locations is possible.\n" +
                        "&e/parkour setFinish (name) &7sets the parkour finish point.\n" +
                        "&e/parkour save &e(name) &7saves a parkour.\n" +
                        "&e/parkour &cdelete &e(name) &7deletes a parkour.\n" +
                        "&e/parkour &aselect &e(name) &7selects a parkour for changes.\n" +
                        "&e/parkour &ajoin &e(name) &7joins a parkour by name.\n" +
                        "&e/parkour &7list &7shows the operational parkours.\n" +
                        "&e/parkour &7reset &7resets the scores and updates the parkour.\n" +
                        "&e/parkour setLb &7sets the leaderboard for the top scores.\n"));

            }
        }
        return true;
    }
}
