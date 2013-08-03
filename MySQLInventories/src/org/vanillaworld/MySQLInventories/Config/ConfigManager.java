package org.vanillaworld.MySQLInventories.Config;

import java.io.File;
import java.io.IOException;

import org.bukkit.plugin.java.JavaPlugin;

public class ConfigManager {
	
	private JavaPlugin plugin;
	private File configFile;
	
	public ConfigManager(JavaPlugin plugin)
	{
		this.plugin = plugin;
		if(!plugin.getDataFolder().exists())
		{
			plugin.getDataFolder().mkdir();
		}
		configFile = new File(plugin.getDataFolder() + "/config.yml");
		if(!configFile.exists())
		{
			try {
				configFile.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
			}
			setDefaults();
			saveConfig();
		}
	}
	
	public void saveConfig()
	{
		try {
			this.plugin.getConfig().save(configFile);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void setDefaults()
	{
		setMySQLAddress("localhost");
		setMySQLPort(3306);
		setMySQLDatabaseName("inventories");
		setMySQLUsername("root");
		setMySQLPassword("password");
		setSaveOnQuit(true);
		setUpdateDelay(300);
	}
	
	public void setMySQLAddress(String value)
	{
		this.plugin.getConfig().set("mysql.address", value);
	}
	
	public void setMySQLPort(int value)
	{
		this.plugin.getConfig().set("mysql.port", value);
	}
	
	public void setMySQLDatabaseName(String value)
	{
		this.plugin.getConfig().set("mysql.database", value);
	}
	
	public void setMySQLUsername(String value)
	{
		this.plugin.getConfig().set("mysql.username", value);
	}
	
	public void setMySQLPassword(String value)
	{
		this.plugin.getConfig().set("mysql.password", value);
	}
	
	public void setSaveOnQuit(boolean value)
	{
		this.plugin.getConfig().set("save-on-quit", value);
	}
	
	public void setUpdateDelay(int value)
	{
		this.plugin.getConfig().set("auto-upload-delay", value);
	}
	
	public String getMySQLAddress()
	{
		return this.plugin.getConfig().getString("mysql.address");
	}
	
	public int getMySQLPort()
	{
		return this.plugin.getConfig().getInt("mysql.port");
	}
	
	public String getMySQLDatabaseName()
	{
		return this.plugin.getConfig().getString("mysql.database");
	}
	
	public String getMySQLUsername()
	{
		return this.plugin.getConfig().getString("mysql.username");
	}
	
	public String getMySQLPassword()
	{
		return this.plugin.getConfig().getString("mysql.password");
	}
	
	public boolean getSaveOnQuit()
	{
		return this.plugin.getConfig().getBoolean("save-on-quit");
	}
	
	public int getUpdateDelay()
	{
		return this.plugin.getConfig().getInt("auto-upload-delay");
	}

}
