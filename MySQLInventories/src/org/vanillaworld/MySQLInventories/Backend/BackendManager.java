package org.vanillaworld.MySQLInventories.Backend;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

public class BackendManager {
	
	private JavaPlugin plugin;
	private MySQL connection;
	
	public BackendManager(JavaPlugin plugin, MySQL connection)
	{
		this.plugin = plugin;
		this.connection = connection;
	}
	
	public void saveInventory(Player player)
	{
		ItemStack[] inv = player.getInventory().getContents();
		ItemStack[] armor = player.getInventory().getArmorContents();
		saveInventory(player.getName(), inv, armor);
	}
	
	public void saveInventory(String player, ItemStack[] inv, ItemStack[] armor)
	{
		new SaveTask(player, inv, armor).runTaskAsynchronously(this.plugin);
	}
	
	public void restoreInventory(Player p)
	{
		p.getInventory().clear();
		p.getInventory().setArmorContents(null);
		new RestoreTask(p).runTaskAsynchronously(this.plugin);
	}
		
	private class SaveTask extends BukkitRunnable { 
		
		String player;
		ItemStack[] inv;
		ItemStack[] armor;
		
		public SaveTask(String player, ItemStack[] inv, ItemStack[] armor)
		{
			this.player = player;
			this.inv = inv;
			this.armor = armor;
		}
		
        public void run() 
        {
        	byte[] invByte = Serializer.Serialize(inv);
        	byte[] armorByte = Serializer.Serialize(armor);
        	        	
        	try {
				PreparedStatement prest1 = connection.getConnection().prepareStatement("SELECT * FROM MySQLInventories WHERE Player=?");
				prest1.setString(1, player);
				ResultSet result = prest1.executeQuery();
				if(!result.next())
				{
					PreparedStatement prest2 = connection.getConnection().prepareStatement("INSERT INTO MySQLInventories (Player, Inventory, Armor) VALUES (?, ?, ?)");
					prest2.setString(1, player);
					prest2.setBytes(2, invByte);
					prest2.setBytes(3, armorByte);
					prest2.executeUpdate();
				}
				else
				{
					PreparedStatement prest2 = connection.getConnection().prepareStatement("UPDATE MySQLInventories SET Inventory=?, Armor=? WHERE Player=?");
					prest2.setBytes(1, invByte);
					prest2.setBytes(2, armorByte);
					prest2.setString(3, player);
					prest2.executeUpdate();
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
        }
    }
	
	private class RestoreTask extends BukkitRunnable { 
		
		Player player;
		
		public RestoreTask(Player player)
		{
			this.player = player;
		}
		
        public void run() 
        {
        	
        	PreparedStatement prest1;
			try {
				prest1 = connection.getConnection().prepareStatement("SELECT * FROM MySQLInventories WHERE Player=?");
				prest1.setString(1, player.getName());
				ResultSet result = prest1.executeQuery();
				if(result.next())
				{
					final ItemStack[] inv = Serializer.Deserialize(result.getBytes(2), false);
					final ItemStack[] armor = Serializer.Deserialize(result.getBytes(3), true);
					// Restore the ItemStack's using a thread-safe sync task
					plugin.getServer().getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
						@SuppressWarnings("deprecation")
						public void run() {
							player.getInventory().setContents(inv);
							player.getInventory().setArmorContents(armor);
							player.updateInventory();
						}
					});
				}
				
			} catch (SQLException e) {
				e.printStackTrace();
			}
        }
    }

}
