package me.streafe.parkour.playermanagement;

import me.streafe.parkour.parkour.Parkour;
import me.streafe.parkour.utils.Utils;

import java.sql.*;

public class SQL {

    private String host,user,password,database;
    private int port;
    private Connection connection = null;
    private Statement statement;

    /**
     *
     * SQL class initializer, sets the necessary variables.
     *
     * @param host sets the host example: localhost
     * @param user sets the login user
     * @param password sets the login user password
     * @param database sets the database
     */
    public SQL(String host, String user, String password, String database, int port){
        this.host = host;
        this.user = user;
        this.password = password;
        this.database = database;
        this.port = port;
    }

    /**
     * Connects to the sql, throws error if something happens.
     */
    public void connect(){
        try{
            Class.forName("com.mysql.jdbc.Driver");
            this.connection = DriverManager.getConnection("jdbc:mysql://" + this.host + ":" + this.port + "/" + database + "?characterEncoding=utf8",user,password);
        }catch (SQLException | ClassNotFoundException e){
            e.printStackTrace();
        }
    }

    /**
     *
     * @return Connection.
     */
    public Connection getConnection(){
        return this.connection;
    }

    public void createDefaultTables(){
        try{
            statement = connection.createStatement();
            statement.executeUpdate("CREATE TABLE IF NOT EXISTS `parkour` (`name` varchar(24), `startPos` varchar(255), `finishPos` varchar(255), `checkpoints` TEXT)");
            statement.executeUpdate("CREATE TABLE IF NOT EXISTS `parkour_runs` (`parkour_name` varchar(36), `uuid` varchar(36), `time` varchar(255), `date` varchar(255))");
        }catch (SQLException e){
            e.printStackTrace();
        }
    }

    public void insertParkour(Parkour parkour){
        try{
            PreparedStatement statement = connection.prepareStatement("SELECT * FROM `parkour`");
            ResultSet rs = statement.executeQuery();
            while(!rs.next()){
                statement.executeUpdate("INSERT INTO `parkour` (`name`, `startPos`, `finishPos`, `checkpoints`) VALUES " +
                        "('"+parkour.getName()+"','"+ Utils.locationToString(parkour.getStart())+"','"+Utils.locationToString(parkour.getFinish())+"','"+Utils.locationToString(parkour.getCheckpoints())+"')");
            }
        }catch (SQLException e){
            e.printStackTrace();
        }
    }

}
