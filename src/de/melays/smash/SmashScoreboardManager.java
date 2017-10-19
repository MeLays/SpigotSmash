package de.melays.smash;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Score;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.ScoreboardManager;

import de.melays.smash.logger.LoggerLevel;

public class SmashScoreboardManager {
	
	ScoreboardManager manager;
	
	main plugin;
	
	public SmashScoreboardManager (main a){
		this.plugin = a;
		this.getScoreboardConfig().options().copyDefaults(true);
		saveScoreboardConfig();
		manager = Bukkit.getScoreboardManager();
		
		for (String s : this.getScoreboardConfig().getKeys(false)){
			
			loadScoreboard(s);
			
		}
		
	}
	
	HashMap<String , ArrayList<String>> scoreboards = new HashMap<String , ArrayList<String>>();
	
	public boolean loadScoreboard (String s){
		ArrayList<String> scoreboard = (ArrayList<String>) getScoreboardConfig().getStringList(s);
		if (scoreboard != null){
			if (scoreboard.size() != 0){
				scoreboards.put(s, scoreboard);
				plugin.logger.log("Loaded scoreboard " + s, LoggerLevel.INFORMATION);
				return true;
			}
		}
		return false;
	}
	
	HashMap<Player , Scoreboard> p_sb = new HashMap<Player , Scoreboard>();
	
	public void setScoreboard (Player p , String sb , HashMap<String , String> keys){
		
		if (!p_sb.containsKey(p)){
			p_sb.put(p, manager.getNewScoreboard());
		}
		
		Scoreboard board = p_sb.get(p);
		
		@SuppressWarnings("unchecked")
		ArrayList<String> board_strings = (ArrayList<String>) scoreboards.get(sb).clone();
		String title = board_strings.get(0);
		board_strings.remove(title);
		
		Objective objective = board.registerNewObjective(UUID.randomUUID().toString().substring(0, 15), "dummy");
		objective.setDisplaySlot(DisplaySlot.SIDEBAR);
		objective.setDisplayName(ChatColor.translateAlternateColorCodes('&', title));
		
		int count = 9;
		for (String s : board_strings){
			String st = ChatColor.translateAlternateColorCodes('&', s);
			for (String sr : keys.keySet()){
				st = st.replaceAll("%"+sr+"%", keys.get(sr));
			}
			
			Score score = objective.getScore(ChatColor.translateAlternateColorCodes('&', st));
			score.setScore(count);
			count--;
		}
		p_sb.put(p, board);
		p.setScoreboard(board);
	}
	
	public void removeScoreboard(Player p){
		p.setScoreboard(manager.getNewScoreboard());
	}
	
	//Config
	
	private FileConfiguration config = null;
	private File configFile = null;
	
	public void reloadScoreboardConfig() {
	    if (configFile == null) {
	    	configFile = new File(plugin.getDataFolder(), "scoreboards.yml");
	    }
	    config = YamlConfiguration.loadConfiguration(configFile);

	    InputStream defConfigStream = plugin.getResource("scoreboards.yml");
	    if (defConfigStream != null) {
	        @SuppressWarnings("deprecation")
			YamlConfiguration defConfig = YamlConfiguration.loadConfiguration(defConfigStream);
	        config.setDefaults(defConfig);
	    }
	}
	
	public FileConfiguration getScoreboardConfig() {
	    if (config == null) {
	    	reloadScoreboardConfig();
	    }
	    return config;
	}
	
	public void saveScoreboardConfig() {
	    if (config == null || configFile == null) {
	    return;
	    }
	    try {
	    	config.save(configFile);
	    } catch (IOException ex) {
	        
	    }
	}

}
