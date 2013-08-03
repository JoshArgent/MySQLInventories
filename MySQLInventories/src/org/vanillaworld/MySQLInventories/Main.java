package org.vanillaworld.MySQLInventories;

import java.util.Timer;
import java.util.TimerTask;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.vanillaworld.MySQLInventories.Backend.BackendManager;
import org.vanillaworld.MySQLInventories.Backend.MySQL;
import org.vanillaworld.MySQLInventories.Config.ConfigManager;

public class Main extends JavaPlugin implements Listener {
	
	ConfigManager configManager;
	MySQL connection;
	BackendManager backendManager;
	Timer autoUpdater;
	
	public void onEnable()
	{
		configManager = new ConfigManager(this);
		connection = new MySQL(this, configManager.getMySQLAddress(), configManager.getMySQLPort(), configManager.getMySQLDatabaseName(), "MySQLInventories", configManager.getMySQLUsername(), configManager.getMySQLPassword());
		connection.dbConnect();
		backendManager = new BackendManager(this, connection);
		this.getServer().getPluginManager().registerEvents(this, this);
		autoUpdater = new Timer();
		if(!(configManager.getUpdateDelay() <= 0))
		{
			startAutoUpdater(configManager.getUpdateDelay());
		}
	}
	
	public void onDisable()
	{
		Bukkit.getServer().getScheduler().cancelAllTasks();
		Bukkit.getServer().getScheduler().cancelTasks(this);
		autoUpdater.cancel();
		if(Bukkit.getOnlinePlayers().length > 0)
		{
			for(Player p : Bukkit.getOnlinePlayers())
			{
				backendManager.saveInventory(p);
			}
		}
	}
	
	private void startAutoUpdater(int delay)
	{
		delay = delay * 1000;
		autoUpdater.scheduleAtFixedRate(new TimerTask() {
			  @Override
			  public void run() 
			  {
				  if(connection.isConnected())
				  {
					  if(Bukkit.getOnlinePlayers().length > 0)
					  {
						  for(Player p : Bukkit.getOnlinePlayers())
						  {
							  backendManager.saveInventory(p);
						  }
					  }
				  }
				  else
				  {
					  connection.dbConnect();
				  }
			  }
		}, delay, delay);
	}
	
	@EventHandler
	private void playerQuit(PlayerQuitEvent event)
	{
		if(configManager.getSaveOnQuit())
		{
			backendManager.saveInventory(event.getPlayer());
		}
	}
	
	@EventHandler
	private void playerJoin(PlayerJoinEvent event)
	{
		backendManager.restoreInventory(event.getPlayer());
	}

}
