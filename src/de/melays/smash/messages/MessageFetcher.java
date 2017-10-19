package de.melays.smash.messages;

import java.io.File;
import java.io.IOException;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import de.melays.smash.main;

import org.bukkit.ChatColor;

public class MessageFetcher {
	FileConfiguration customConfig = null;
	File customConfigurationFile = null;
	main plugin;
	
	public MessageFetcher(main m){
		plugin = m;
		getMessageFetcher().options().copyDefaults(true);
		saveMessageFile();
	}
	
	public void reloadMessageFile() {
	    if (customConfigurationFile == null) {
	    customConfigurationFile = new File(plugin.getDataFolder(), "messages.yml");
	    }
	    customConfig = YamlConfiguration.loadConfiguration(customConfigurationFile);

	    java.io.InputStream defConfigStream = plugin.getResource("messages.yml");
	    if (defConfigStream != null) {
	        @SuppressWarnings("deprecation")
			YamlConfiguration defConfig = YamlConfiguration.loadConfiguration(defConfigStream);
	        customConfig.setDefaults(defConfig);
	    }
	}
	
	public FileConfiguration getMessageFetcher() {
	    if (customConfig == null) {
	    	reloadMessageFile();
	    }
	    return customConfig;
	}
	
	public void saveMessageFile() {
	    if (customConfig == null || customConfigurationFile == null) {
	    return;
	    }
	    try {
	        customConfig.save(customConfigurationFile);
	    } catch (IOException ex) {

	    }
	}
	
	public String getMessage (String id , boolean prefixreplace){
		String msg = getMessageFetcher().getString(id);
		if (msg != null){
			if (prefixreplace){
				msg =  msg.replace("%prefix%", getMessage("prefix" , false));
			}
			msg = msg.replace("[ae]", "ä");
			msg = msg.replace("[ue]", "ü");
			msg = msg.replace("[oe]", "ö");
			msg = msg.replace("[AE]", "Ä");
			msg = msg.replace("[UE]", "Ü");
			msg = msg.replace("[OE]", "Ö");
			return ChatColor.translateAlternateColorCodes('&',msg);
		}
		else{
			return "Your custom messages.yml doesn't contain this key ("+id+")";
		}
	}
}