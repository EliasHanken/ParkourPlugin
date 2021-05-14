package me.streafe.parkour.utils;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;

import java.io.*;
import java.math.BigDecimal;
import java.math.RoundingMode;

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

    public static double round(double value, int places) {
        if (places < 0) throw new IllegalArgumentException();

        BigDecimal bd = BigDecimal.valueOf(value);
        bd = bd.setScale(places, RoundingMode.HALF_UP);
        return bd.doubleValue();
    }

    public static String locationToString(Location location){
        return location.getWorld().getName()
                + ":" + round(location.getX(),2)
                + ":" + round(location.getY(),2)
                + ":" + round(location.getZ(),2)
                + ":" + round(location.getYaw(),3)
                + ":" + round(location.getPitch(),3);
    }

    public static Location readLocFromString(String string){
        String[] s = string.split(":");

        return new Location(Bukkit.getWorld(s[0]),
                Double.parseDouble(s[1]),
                Double.parseDouble(s[2]),
                Double.parseDouble(s[3]),
                Float.parseFloat(s[4]),
                Float.parseFloat(s[5]));
    }

    /**
     * Save a binary version of the object to the given file.
     * If the file name is not an absolute path, then it is assumed
     * to be relative to the current project folder.
     * @param destinationFile The file where the details are to be saved.
     * @param object The object to be saved in binary version.
     * @throws IOException If the saving process fails for any reason.
     */
    public static void saveToFile(String destinationFile, Object object) throws IOException{
        File dir = new File("tasks");
        if(!dir.exists()){
            dir.mkdir();
        }
        File outputFile = new File(destinationFile);
        ObjectOutputStream os = new ObjectOutputStream(new FileOutputStream(outputFile));
        os.writeObject(object);
        os.close();
    }

    /**
     * Read the binary version of an address book from the given file.
     * If the file name is not an absolute path, then it is assumed
     * to be relative to the current project folder.
     * @param sourceFile The file from where the details are to be read.
     * @return The Task object.
     * @throws IOException If the reading process fails for any reason.
     */
    public static Object readFromFile(String sourceFile) throws IOException, ClassNotFoundException{
        try{
            FileInputStream fi = new FileInputStream(sourceFile);
            ObjectInputStream oi = new ObjectInputStream(fi);

            Object obj = oi.readObject();

            oi.close();
            fi.close();

            return obj;
        }catch (IOException e){
            throw new IOException("Unable to make a valid filename for "+
                    sourceFile);
        }
    }
}
