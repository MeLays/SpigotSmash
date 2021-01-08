package de.melays.smash;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;

import de.melays.smash.items.SI_Airstrike;
import de.melays.smash.items.SI_Apple;
import de.melays.smash.items.SI_Bow;
import de.melays.smash.items.SI_Bread;
import de.melays.smash.items.SI_Claymore;
import de.melays.smash.items.SI_DiamondSword;
import de.melays.smash.items.SI_FragGrenade;
import de.melays.smash.items.SI_GoldenCarrot;
import de.melays.smash.items.SI_IronSword;
import de.melays.smash.items.SI_ProtectionPotion;
import de.melays.smash.items.SI_ReducerPotion;
import de.melays.smash.items.SI_RocketLauncher;
import de.melays.smash.items.SI_StoneSword;
import de.melays.smash.items.SI_Storm;
import de.melays.smash.items.SI_TNTWalk;
import de.melays.smash.items.SI_WoodSword;
import de.melays.smash.items.SI_Yeeter;

public class ItemManager {
	
	public Arena a;
	
	public ArrayList<SmashItem> items = new ArrayList<SmashItem>();
	public HashMap<Location,SmashItem> locations = new HashMap<Location,SmashItem>();
	public HashMap<Item,Location> itemlocator = new HashMap<Item,Location>();
	
	public HashMap<Player , SmashItem> playeritems = new HashMap<Player,SmashItem>();
	
	public HashMap<SmashItem , HashMap<String,Object>> itemstorage = new HashMap<SmashItem , HashMap<String,Object>>();
	
	ArrayList<SmashItem> used = new ArrayList<SmashItem>();
	
	public ItemManager(Arena m) {
		this.a = m;
		items.add(new SI_Bow(m , this).setDefaultAmount(this));
		items.add(new SI_StoneSword(m , this).setDefaultAmount(this));
		items.add(new SI_WoodSword(m , this).setDefaultAmount(this));
		items.add(new SI_IronSword(m , this).setDefaultAmount(this));
		items.add(new SI_DiamondSword(m , this).setDefaultAmount(this));
		items.add(new SI_RocketLauncher(m , this).setDefaultAmount(this));
		items.add(new SI_Bread(m , this).setDefaultAmount(this));
		items.add(new SI_Apple(m , this).setDefaultAmount(this));
		items.add(new SI_GoldenCarrot(m , this).setDefaultAmount(this));
		items.add(new SI_ProtectionPotion(m , this).setDefaultAmount(this));
		items.add(new SI_Claymore(m , this).setDefaultAmount(this));
		items.add(new SI_Airstrike(m , this).setDefaultAmount(this));
		items.add(new SI_Storm(m , this).setDefaultAmount(this));
		items.add(new SI_FragGrenade(m , this).setDefaultAmount(this));
		items.add(new SI_ReducerPotion(m , this).setDefaultAmount(this));
		items.add(new SI_Yeeter(m , this).setDefaultAmount(this));
		items.add(new SI_TNTWalk(m , this).setDefaultAmount(this));
		
		this.getItemConfig().options().copyDefaults(true);
		this.saveItemConfig();
		
		loadLocations();
	}

	public void loadLocations(){
		for (Location loc : a.plugin.am.getLocationsCounting(a.name , "itemlocations")){
			locations.put(loc, null);
		}
	}
	
	public void cancle(){
		for (SmashItem i : used){
			i.cancleActions();
		}
	}
	
	public void shuffleLocations(){
	    List<Location> list = new ArrayList<Location>(locations.keySet());
	    Collections.shuffle(list);

	    HashMap<Location,SmashItem> shuffleMap = new HashMap<Location,SmashItem>();
	    list.forEach(k->shuffleMap.put(k, locations.get(k)));
	    locations = shuffleMap;
	}
	
	public void restore(){
		for (Item i : itemlocator.keySet()){
			i.remove();
		}
	}
	
	public boolean playerDrop(Player p){
		if (playeritems.containsKey(p)){
			try {
				playeritems.get(p).destroy();
				p.getInventory().clear();
				p.updateInventory();
				return true;
			} catch (Exception e) {

			}
		}
		return false;
	}
	
	public void callRightclick(Player p){
		if (playeritems.containsKey(p)){
			try {
				playeritems.get(p).onRightclick();
			} catch (Exception e) {

			}
		}
	}
	
	public void callLeftclick(Player p){
		if (playeritems.containsKey(p)){
			try {
				playeritems.get(p).onLeftclick();
			} catch (Exception e) {

			}
		}
	}
	
	public void callBlockPlace(Player p , Location loc){
		if (playeritems.containsKey(p)){
			try {
				playeritems.get(p).onBlockPlace(loc);
			} catch (Exception e) {
				
			}
		}
	}
	
	public void setPlayerItem(Player p , Item i){
		SmashItem si = locations.get(itemlocator.get(i)).registerEvents();
		items.add(si);
		playeritems.put(p , si);
		si.setPlayer(p);
		locations.put(itemlocator.get(i) , null);
		itemlocator.remove(i);
		p.getInventory().setItem(0 , si.getItemStack());
	}
	
	public void pickUp (Player p , Item i){
		if (itemlocator.containsKey(i)){
			if (!playeritems.containsKey(p)){
				setPlayerItem(p , i);
				i.remove();
			}
			else{
				if (playeritems.get(p) == null){
					setPlayerItem(p , i);
					i.remove();
				}
			}
		}
	}
	
	public void spawnRandomItem () {
		shuffleLocations();
		Collections.shuffle(items);
		for (Location loc : locations.keySet()){
			if (locations.get(loc) == null){
				try {
					SmashItem si = (SmashItem)items.get(Tools.randInt(0 , items.size()-1)).clone();
					locations.put(loc, si);
					Item i = loc.getWorld().dropItem(loc, si.getItemStack());
					itemlocator.put(i, loc);
				} catch (CloneNotSupportedException e) {
					e.printStackTrace();
				}
				return;
			}
		}
		
	}
	
	public ConfigurationSection getItemConfig(String name) {
		ConfigurationSection r = null;
		try {
			r = this.getItemConfig().getConfigurationSection(name);
			if (r == null){
				this.getItemConfig().createSection(name);
				r = this.getItemConfig().getConfigurationSection(name);
			}
		}catch (Exception ex) {
			this.getItemConfig().createSection(name);
			r = this.getItemConfig().getConfigurationSection(name);
		}
		return r;
	}
	
	public int getPlayerHit(Player p){
		if (playeritems.containsKey(p)){
			if (playeritems.get(p) == null){
				return 1;
			}
			return playeritems.get(p).getHitDamage();
		}
		return 1;
	}
	
	//Config
	
	private FileConfiguration config = null;
	private File configFile = null;
	
	public void reloadItemConfig() {
	    if (configFile == null) {
	    	configFile = new File(a.plugin.getDataFolder(), "items.yml");
	    }
	    config = YamlConfiguration.loadConfiguration(configFile);

	    InputStream defConfigStream = a.plugin.getResource("items.yml");
	    if (defConfigStream != null) {
	        @SuppressWarnings("deprecation")
			YamlConfiguration defConfig = YamlConfiguration.loadConfiguration(defConfigStream);
	        config.setDefaults(defConfig);
	    }
	}
	
	public FileConfiguration getItemConfig() {
	    if (config == null) {
	    	reloadItemConfig();
	    }
	    return config;
	}
	
	public void saveItemConfig() {
	    if (config == null || configFile == null) {
	    return;
	    }
	    try {
	    	config.save(configFile);
	    } catch (IOException ex) {
	        
	    }
	}
	
}
