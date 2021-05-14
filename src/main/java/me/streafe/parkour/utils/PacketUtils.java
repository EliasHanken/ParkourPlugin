package me.streafe.parkour.utils;

import net.minecraft.server.v1_8_R3.*;
import org.bukkit.ChatColor;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;

public class PacketUtils {

    public static void sendPacket(Player player, Packet<?> packet){
        ((CraftPlayer)player).getHandle().playerConnection.sendPacket(packet);
    }

    public static void sendScoreBoard(Player player){
        Scoreboard board = new Scoreboard();
        ScoreboardObjective obj = board.registerObjective("Test", IScoreboardCriteria.b);

        PacketPlayOutScoreboardObjective removepacket = new PacketPlayOutScoreboardObjective(obj,1);
        PacketPlayOutScoreboardObjective createpacket = new PacketPlayOutScoreboardObjective(obj,0);
        PacketPlayOutScoreboardDisplayObjective display = new PacketPlayOutScoreboardDisplayObjective(1,obj);

        obj.setDisplayName("Test");

    }

    public static void sendTitle(Player player, String text, ChatColor color){
        IChatBaseComponent chatTitle = IChatBaseComponent.ChatSerializer.a("{\"text\": \"" + text + "\",color:" + color.name().toLowerCase() + "}");

        PacketPlayOutTitle title = new PacketPlayOutTitle(PacketPlayOutTitle.EnumTitleAction.TITLE,chatTitle);
        PacketPlayOutTitle length = new PacketPlayOutTitle(5,20,5);

        sendPacket(player,title);
        sendPacket(player,length);

    }

    public static void sendActionBarMessage(Player player, String message){
        PacketPlayOutChat packet = new PacketPlayOutChat(IChatBaseComponent.ChatSerializer.a("{\"text\":\"" + Utils.translate(message) + "\"}"), (byte) 2);
        ((CraftPlayer)player).getHandle().playerConnection.sendPacket(packet);
    }

    public static void sendTitle(Player player, int fadeIn, int duration, int fadeOut, String message){
        IChatBaseComponent titleJson = IChatBaseComponent.ChatSerializer.a("{\"text\":\"" + Utils.translate(message) +"\"}");
        PacketPlayOutTitle title = new PacketPlayOutTitle(PacketPlayOutTitle.EnumTitleAction.TITLE,titleJson,fadeIn,duration,fadeOut);
        ((CraftPlayer)player).getHandle().playerConnection.sendPacket(title);
    }

    public static void sendSubtitle(Player player, int fadeIn, int duration, int fadeOut, String message){
        IChatBaseComponent titleJson = IChatBaseComponent.ChatSerializer.a("{\"text\":\"" + Utils.translate(message) +"\"}");
        PacketPlayOutTitle title = new PacketPlayOutTitle(PacketPlayOutTitle.EnumTitleAction.SUBTITLE,titleJson,fadeIn,duration,fadeOut);
        ((CraftPlayer)player).getHandle().playerConnection.sendPacket(title);
    }

    public static void playSound(Player player, String sound, float volume, float pitch){
        PacketPlayOutNamedSoundEffect packet = new PacketPlayOutNamedSoundEffect(sound,player.getLocation().getX(),player.getLocation().getY(),player.getLocation().getZ(),volume,pitch);
        ((CraftPlayer)player).getHandle().playerConnection.sendPacket(packet);
    }



}
