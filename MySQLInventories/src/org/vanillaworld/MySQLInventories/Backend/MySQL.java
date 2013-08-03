package org.vanillaworld.MySQLInventories.Backend;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

public class MySQL {
	
	public MySQL(JavaPlugin plugin, String host, int port, String database, String table, String username, String password)
	{
		strUri = "jdbc:mysql://" + host + ":" + port + "/" + database;
		strUser = username;
		strPassword = password;
		this.plugin = plugin;
		strTable = table;
	}
	 
    public Connection conn = null;
    public Statement stmt = null;
    public boolean dbConnSuccess = false;
   
    public String strUri = "jdbc:mysql://localhost:3300/database";
    public String strUser;
    public String strPassword;
    public String strTable;
    private JavaPlugin plugin;
   
    public void dbConnect(){
		new ConnectTask().runTaskAsynchronously(this.plugin);
    }
    
    public ResultSet dbStm(String s){
    	ResultSet results = null;
        try {
        	results = stmt.executeQuery(s);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return results;
    }
    
    public Connection getConnection()
    {
    	return conn;
    }
    
    public void executeUpdate(String s){
        try {
        	stmt.executeUpdate(s);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
    public void checkConnection()
    {
    	try {
			if(this.conn.isClosed())
			{
				dbConnect();
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
    }
    
    public boolean isConnected()
    {
    	try {
			return !this.conn.isClosed();
		} catch (SQLException e) {
		}
    	return false;
    }
    
    private class ConnectTask extends BukkitRunnable { 
        public void run() {
        	try {
				Class.forName("com.mysql.jdbc.Driver");
			} catch (ClassNotFoundException e) {
				System.out.print("[MySQLInventories] Fatal error connecting to MySQL:");
				e.printStackTrace();
				return;
			} 
            try {
				conn = DriverManager.getConnection(strUri, strUser, strPassword);
			} catch (SQLException e) {
				System.out.print("[MySQLInventories] Fatal error connecting to MySQL:");
				e.printStackTrace();
				return;
			}
            try {
				stmt = conn.createStatement();
			} catch (SQLException e) {
				System.out.print("[MySQLInventories] Fatal error connecting to MySQL:");
				e.printStackTrace();
				return;
			}
            System.out.print("[MySQLInventories] Successfully connected to MySQL.");
            dbConnSuccess = true;
            
            try {
            	DatabaseMetaData metadata = conn.getMetaData();
				ResultSet resultSet;
				resultSet = metadata.getTables(null, null, "MySQLInventories", null);
				if(!resultSet.next())
				{
					System.out.print("[MySQLInventories] Creating new MySQL table.");
					executeUpdate("CREATE TABLE MySQLInventories (Player VARCHAR(16), Inventory BLOB, Armor BLOB);");
				}
				
			} catch (SQLException e) {
				System.out.print("[MySQLInventories] Fatal error creating MySQL table:");
				e.printStackTrace();
			}
            
        }
    }
 
}
