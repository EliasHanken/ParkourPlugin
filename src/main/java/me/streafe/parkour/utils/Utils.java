package me.streafe.parkour.utils;

import org.bukkit.ChatColor;

public class Utils {

    /**
     *
     * @param string the string to be converted to the ChatColor format
     *               translation
     * @return The translated string for the user to be sent in chat.
     */
    public static String translate(String string){
        return ChatColor.translateAlternateColorCodes('&',string);
    }

}
