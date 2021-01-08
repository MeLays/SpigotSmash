package de.melays.smash;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Set;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import de.melays.smash.errors.MError;
import de.melays.smash.logger.LoggerLevel;

public class ArenaManager {

	HashMap<String , Arena> arenas = new HashMap<String , Arena>();
	
	main plugin;
	
	public ArenaManager (main m){
		plugin = m;
		getArenaConfig().options().copyDefaults(true);
		saveArenaConfig();
	}
	
	public void createArena (String name , int min , int max){
		this.getArenaConfig().set(name+".enabled", false);
		this.getArenaConfig().set(name+".min", min);
		this.getArenaConfig().set(name+".max", max);
		
		this.getArenaConfig().set(name+".max_y", 50);
		this.getArenaConfig().set(name+".infinitemode", true);
		
		saveArenaConfig();
	}
	
	public void stopAll() {
		@SuppressWarnings("unchecked")
		HashMap<String , Arena> arenas = (HashMap<String, Arena>) this.arenas.clone();
		for (Arena a : arenas.values()) {
			a.stopComplete();
		}
	}
	
	public Arena remixInfiniteArena(Arena a){
		
		ArrayList<Arena> rndm = new ArrayList<Arena>(arenas.values());
		Collections.shuffle(rndm);
		for (Arena c : rndm){
			if (c.max >= c.getAllMembers().size() + a.getAllMembers().size() && c.state == GameState.LOBBY){
				a = c;
				continue;
			}
		}
		
		return a;
	}
	
	public void loadAll(){
		arenas = new HashMap<String , Arena>();
		Set<String> arenas = this.getArenaConfig().getKeys(false);
		for (String s : arenas){
			plugin.logger.log("Loading arena "+s+" ...", LoggerLevel.INFORMATION);
			MError error = loadArena (s);
			if (error == MError.OKAY){
				plugin.logger.log("Loaded arena "+s+" !", LoggerLevel.INFORMATION);
			}
			else{
				plugin.logger.log("Error loading arena "+s+" ! [Error: " + error.toString() + "]", LoggerLevel.IMPORTANT);
			}
		}
	}
	
	public MError loadArena (String name){
		if (checkArena(name) == MError.OKAY){
			if (arenas.containsKey(name)){
				return MError.ARENA_ALLREADY_LOADED;
			}
			arenas.put(name, new Arena (name , plugin));
			return MError.OKAY;
		}
		else{
			return checkArena(name);
		}
	}
	
	public MError unloadArena (String name){
		if (arenas.containsKey(name)){
			Arena a = getArena(name);
			a.stop();
			return MError.OKAY;
		}
		return MError.ARENA_UNKNOWN;
	}
	
	public boolean isExisting (String name){
		return getArenaConfig().isSet(name+".enabled");
	}
	
	public MError checkArena (String name){
		if (!hasLocation(name , "lobby")){
			return MError.LOBBY_LOC_MISSING;
		}
		if (this.getLocationsCounting(name, "gamelocations").size() == 0){
			return MError.GAME_LOC_MISSING;
		}
		if (this.getLocationsCounting(name, "itemlocations").size() == 0){
			return MError.ITEM_LOC_MISSING;
		}
		if (!hasLocation(name , "back")){
			return MError.BACK_LOC_MISSING;
		}
		if (!hasLocation(name , "spectator")){
			return MError.SPEC_LOC_MISSING;
		}
		if (!hasLocation(name , "corner_1")){
			return MError.CORNER_1_MISSING;
		}
		if (!hasLocation(name , "corner_2")){
			return MError.CORNER_2_MISSING;
		}
		return MError.OKAY;
	}
	
	public void addArenaLocation (String name , String locname , Location loc){
		this.getArenaConfig().set(name+"."+locname+"."+"x", loc.getX());
		this.getArenaConfig().set(name+"."+locname+"."+"y", loc.getY());
		this.getArenaConfig().set(name+"."+locname+"."+"z", loc.getZ());
		this.getArenaConfig().set(name+"."+locname+"."+"yaw", loc.getYaw());
		this.getArenaConfig().set(name+"."+locname+"."+"pitch", loc.getPitch());
		this.getArenaConfig().set(name+"."+locname+"."+"world", loc.getWorld().getName());
		saveArenaConfig();
	}
	
	public int addLocationCounting (String name , String counterpath , Location loc) {
		try {
			ConfigurationSection section = getArenaConfig().getConfigurationSection(name+"."+counterpath);
			Set<String> keys = section.getKeys(false);
			int highest = 0;
			for (String s : keys) {
				try {
					int current = Integer.parseInt(s);
					if (current > highest) {
						highest = current;
					}
				}catch(Exception ex){
					
				}
			}
			int new_loc = highest + 1;
			this.addArenaLocation(name, counterpath+"."+new_loc, loc);
			return new_loc;
		} catch (Exception e) {
			this.addArenaLocation(name, counterpath+"."+1, loc);
			return 1; 
			
		}
	}
	
	public void setLocationCounting (String name , String counterpath , int id , Location loc) {
		this.addArenaLocation(name, counterpath+"."+id, loc);
	}
	
	public void removeLocationCounting (String name , String counterpath , int id) {
		this.getArenaConfig().set(name+"."+counterpath+"."+id, null);
		this.saveArenaConfig();
	}
	
	public ArrayList<Location> getLocationsCounting (String name , String counterpath) {
		ConfigurationSection section = getArenaConfig().getConfigurationSection(name+"."+counterpath);
		ArrayList<Location> locs = new ArrayList<Location>();
		try {
			Set<String> keys = section.getKeys(false);
			for (String s : keys) {
				locs.add(this.getArenaLocation(name, counterpath+"."+s));
			}
			Collections.shuffle(locs);
			return locs;
		} catch (Exception e) {
			return locs;
		}
	}
	
	public Location getArenaLocation (String name , String locname){
		double x = getArenaConfig().getDouble(name+"."+locname+"."+"x");
		double y = getArenaConfig().getDouble(name+"."+locname+"."+"y");
		double z = getArenaConfig().getDouble(name+"."+locname+"."+"z");
		float yaw = (float) getArenaConfig().getDouble(name+"."+locname+"."+"yaw");
		float pitch = (float) getArenaConfig().getDouble(name+"."+locname+"."+"pitch");
		World world = Bukkit.getWorld(getArenaConfig().getString(name+"."+locname+"."+"world"));
		return new Location (world , x , y , z , yaw , pitch);
	}
	
	public boolean hasLocation (String name , String locname){
		if (this.getArenaConfig().isSet(name+"."+locname+".x"))
		return true;
		return false;
	}
	
	public Arena getArena(String n){
		if (arenas.containsKey(n)){
			return arenas.get(n);
		}
		else{
			return null;
		}
	}
	
	public Arena searchPlayer (Player p){
		for (Arena a : arenas.values()){
			if (a.getAllMembers().contains(p)){
				return a;
			}
		}
		return null;
	}
		
	
	
	//Config methods
	
	private FileConfiguration config = null;
	private File configFile = null;
	
	public void reloadArenaConfig() {
	    if (configFile == null) {
	    	configFile = new File(plugin.getDataFolder(), "arenas.yml");
	    }
	    config = YamlConfiguration.loadConfiguration(configFile);

	    InputStream defConfigStream = plugin.getResource("arenas.yml");
	    if (defConfigStream != null) {
	        @SuppressWarnings("deprecation")
			YamlConfiguration defConfig = YamlConfiguration.loadConfiguration(defConfigStream);
	        config.setDefaults(defConfig);
	    }
	}
	
	public FileConfiguration getArenaConfig() {
	    if (config == null) {
	    	reloadArenaConfig();
	    }
	    return config;
	}
	
	public void saveArenaConfig() {
	    if (config == null || configFile == null) {
	    return;
	    }
	    try {
	    	config.save(configFile);
	    } catch (IOException ex) {
	        
	    }
	}
}

