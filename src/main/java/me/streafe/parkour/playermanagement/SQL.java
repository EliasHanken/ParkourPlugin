package me.streafe.parkour.playermanagement;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class SQL {

    private String host,user,password,database;
    private int port;
    private Connection connection = null;

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

}
