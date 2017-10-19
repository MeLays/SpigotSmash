package de.melays.smash;

import java.util.ArrayList;
import java.util.HashMap;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import de.melays.commands.Commands;
import de.melays.smash.logger.Logger;
import de.melays.smash.messages.MessageFetcher;

import org.bukkit.event.Listener;

public class main extends JavaPlugin implements Listener, CommandExecutor{
	
	public ArenaManager am;
	public Logger logger;
	public MessageFetcher mf;
	public String logger_prefix = "[SMASH] ";
	
	public SmashScoreboardManager sbm;
	
	String autojoin;
	String bungeeserver;
	
	ChatHook ch;
	
	public HashMap<Player , LocationMarker> markers = new HashMap<Player , LocationMarker>();
	
	boolean infiniteremix;
	
	public void onEnable(){
		
		logger = new Logger(this);
		
		//Register Events
		Bukkit.getPluginManager().registerEvents(this, this);
		Bukkit.getPluginManager().registerEvents(new EventListener(this), this);
		
		//Config save
		getConfig().options().copyDefaults(true);
		saveConfig();
		
		main plugin = this;
		
		new BukkitRunnable(){

			@Override
			public void run() {
				//Create Managers
				am = new ArenaManager(plugin);
				mf = new MessageFetcher(plugin);
				sbm = new SmashScoreboardManager(plugin);
				ch = new ChatHook(plugin);
				
				am.loadAll();
				
				infiniteremix = getConfig().getBoolean("infinitemode_remix");
				
				if (!getConfig().getString("bungeeserver").equals("none"))
					bungeeserver = getConfig().getString("bungeeserver");
				if (getConfig().isSet("autojoin")){
					String autojoin  = getConfig().getString("autojoin");
					if (!autojoin.equals("none") && !autojoin.equals("random")){
						for (Player p : Bukkit.getOnlinePlayers()){
							am.getArena(autojoin).join(p);
						}
					}
					if (autojoin.equals("random")){
						if (plugin.autojoin == null)
							plugin.autojoin = (new ArrayList<Arena> (am.arenas.values())).get(Tools.randInt(0, am.arenas.values().size()-1)).name;
						for (Player p : Bukkit.getOnlinePlayers()){
							am.getArena(plugin.autojoin).join(p);
						}
					}
				}
			}
			
		}.runTask(this);
		
	    this.getServer().getMessenger().registerOutgoingPluginChannel(this, "BungeeCord");	 
	}
	
	public void onDisable() {
		am.stopAll();
	}
	
    @Override
    public boolean onCommand(CommandSender sender, Command command, String commandName, String[] args) {
    	try{
    		return Commands.runCommand(sender , command , commandName , args , this);
    	}catch (Exception ex){
    		ex.printStackTrace();
    		sender.sendMessage(mf.getMessage("error", true));
    		return true;
    	}
    }

}
