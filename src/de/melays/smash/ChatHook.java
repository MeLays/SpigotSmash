package de.melays.smash;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.plugin.RegisteredServiceProvider;

import de.melays.smash.logger.LoggerLevel;
import net.milkbowl.vault.chat.Chat;

public class ChatHook {
	
	public static Chat chat = null;
	
	main main;
	
	public ChatHook (main main) {
		this.main = main;
		load();
	}
	
	boolean vault = false;
	
	public void load() {
		if (Bukkit.getPluginManager().isPluginEnabled("Vault")) {
			vault = setupChat();
			main.logger.log("Vault chat hooked. Using Vault prefixes and suffixes!" , LoggerLevel.INFORMATION);
		}
		else {
			main.logger.log("No Vault found :C" , LoggerLevel.INFORMATION);
		}
	}
	
    private boolean setupChat()
    {
        RegisteredServiceProvider<Chat> chatProvider = Bukkit.getServer().getServicesManager().getRegistration(net.milkbowl.vault.chat.Chat.class);
        if (chatProvider != null) {
            chat = chatProvider.getProvider();
        }

        return (chat != null);
    }
    
    public String getPrefix(Player p) {
    	if (!vault) {
    		return "";
    	}
    	return ChatColor.translateAlternateColorCodes('&', chat.getPlayerPrefix(p));
    }
    
    public String getSuffix(Player p) {
    	if (!vault) {
    		return "";
    	}
    	return ChatColor.translateAlternateColorCodes('&', chat.getPlayerSuffix(p));
    }
	
}