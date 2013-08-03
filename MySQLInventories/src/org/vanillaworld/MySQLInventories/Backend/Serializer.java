package org.vanillaworld.MySQLInventories.Backend;

import java.io.UnsupportedEncodingException;

import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;

public class Serializer {
	
	public static byte[] Serialize(ItemStack[] items)
	{
		String converted = "";
		YamlConfiguration config = new YamlConfiguration();
		if(items.length > 0)
		{
			int num = 0;
			for(ItemStack item : items)
			{
				if(item != null)
				{
					config.set(Integer.toString(num), item);
				}
				num += 1;
			}
		}
		converted = config.saveToString();
		byte[] compressed;
		try {
			compressed = Compression.compress(converted.getBytes("ASCII"));
			return compressed;
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public static ItemStack[] Deserialize(byte[] data, boolean armor)
	{
		byte[] decompressed = Compression.decompress(data);
		String text = "";
		try {
			text = new String(decompressed, "ASCII");
		} catch (UnsupportedEncodingException e1) {
			e1.printStackTrace();
		}
		YamlConfiguration config = new YamlConfiguration();
		try {
			config.loadFromString(text);
		} catch (InvalidConfigurationException e) {
			e.printStackTrace();
		}
		ItemStack[] items = null;
		if(config.getKeys(false).size() > 0)
		{
			if(armor)
			{
				items = new ItemStack[4];
			}
			else
			{
				items = new ItemStack[36];
			}
		
			for(String key : config.getKeys(false))
			{
				items[Integer.parseInt(key)] = config.getItemStack(key);
			}
		}
		return items;
	}

}
