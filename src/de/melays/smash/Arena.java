package de.melays.smash;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;

import de.melays.smash.errors.MError;
import de.melays.smash.logger.LoggerLevel;
import net.md_5.bungee.api.ChatColor;

public class Arena {

	String name;
	public GameState state = GameState.LOBBY;
	
	//Player lists
	public ArrayList<Player> players = new ArrayList<Player>();
	ArrayList<Player> specs = new ArrayList<Player>();
	public HashMap<Player,PlayerData> playerdata = new HashMap<Player,PlayerData>();
	public HashMap<Player,Integer> damagemultiplier = new HashMap<Player,Integer>();
	public HashMap<Player,Integer> reducermultiplier = new HashMap<Player,Integer>();
	
	HashMap<Player, Boolean> isSmashing = new HashMap<Player, Boolean>();
	
	public ArrayList<Player> playersVotedSkip = new ArrayList<Player>();
	
	int max = 0;
	int min = 0; 
	
	ArrayList<Location> game;
	Location spectator;
	Location back;
	Location lobby;
	
	Location smaller;
	Location bigger;
	
	public main plugin;
	
	//Settings
	boolean move = true;
	boolean damage = false;
	boolean inv_click = false;
	boolean drop = false;
	boolean build = false;
	boolean blockbreak = false;
	boolean doublejump = true;
	
	//Groundbreaking Settings
	boolean infinitemode = true;
	int min_y = 30;
	
	//ItemManager
	public ItemManager im;
	
	//ArenaData
	public ArenaData data;
	public ArenaBackup backup;
	
	public Arena (String name , main m){
		this.name = name;
		plugin = m;
		im = new ItemManager(this);
		data = new ArenaData(this);
		backup = new ArenaBackup(this);
		loadData();
		this.gameLoop();
		backup.saveArena();
	}
	
	public ArrayList<Player> getAllMembers (){
		ArrayList<Player> temp = new ArrayList<Player>(players);
		temp.addAll(specs);
		return temp;
	}
	
	public void loadData(){
		game = plugin.am.getLocationsCounting(name, "gamelocations");
		back = plugin.am.getArenaLocation(name, "back");
		smaller = plugin.am.getArenaLocation(name, "corner_1");
		bigger = plugin.am.getArenaLocation(name, "corner_2");
		spectator = plugin.am.getArenaLocation(name, "spectator");
		lobby = plugin.am.getArenaLocation(name, "lobby");
		min = plugin.am.getArenaConfig().getInt(name+"."+"min");
		max = plugin.am.getArenaConfig().getInt(name+"."+"max");
		min_y = plugin.am.getArenaConfig().getInt(name+"."+"max_y");
		infinitemode = plugin.am.getArenaConfig().getBoolean(name+"."+"infinitemode");
	}
	
	public boolean inMap(Location loc){
		if (smaller.getX() <= loc.getX() && smaller.getY() <= loc.getY() && smaller.getZ() <= loc.getZ()){
			if (bigger.getX() >= loc.getX() && bigger.getY() >= loc.getY() && bigger.getZ() >= loc.getZ()){
				return true;
			}
		}
		return false;
	}
	
	public MError join (Player p){
		return join(p , false);
	}
	
	public MError join (Player p , boolean silent){
		if (this.getAllMembers().contains(p)){
			return MError.ALREADY_INGAME;
		}
		if (state == GameState.LOBBY){
			if (this.players.size() < this.max){
				players.add(p);
				p.teleport(lobby);
				p.setGameMode(GameMode.SURVIVAL);
				damagemultiplier.put(p, 100);
				reducermultiplier.put(p, 100);
				isSmashing.put(p, false);
				playerdata.put(p, new PlayerData(p , this));
				if (!silent)
					sendMessage(plugin.mf.getMessage("playerjoin", true).replace("%player%", p.getName()));
				data.saveAndClearInventory(p);
				p.setAllowFlight(false);
				updateBoard();
				return MError.OKAY;
			}
			else{
				return MError.ARENA_FULL;
			}
		}
		else if (state == GameState.LOADING){
			return MError.ARENA_LOADING;
		}
		else{
			addSpectator(p);
			return MError.OKAY_AS_SPEC;
		}
	}
	
	public void addSpectator (Player p){
		specs.add(p);
		p.teleport(spectator);
		data.saveAndClearInventory(p);
		plugin.sbm.setScoreboard(p, "spectator", getSBKeys(p));
		p.setGameMode(GameMode.SPECTATOR);
		p.setAllowFlight(true);
	}
	
	public void voteSkip(Player p) {
		if (!this.playersVotedSkip.contains(p)) {
			this.playersVotedSkip.add(p);
			p.sendMessage(plugin.mf.getMessage("skip", true));
		}
		else {
			p.sendMessage(plugin.mf.getMessage("noskip", true));
		}
		
		int c = 0;
		for (Player p2 : this.players) {
			if (this.playersVotedSkip.contains(p2)) c++;
		}
		
		if (c > (this.players.size() / 2) +1) {
			p.sendMessage(plugin.mf.getMessage("skipped", true));
			this.restart();
		}
	}
	
	public void leave (Player p){
		ColorTabAPI.clearTabStyle(p, players);
		if (players.contains(p)){
			players.remove(p);
			sendMessage(plugin.mf.getMessage("playerleave", true).replace("%player%", p.getName()));
		}
		if (specs.contains(p)){
			specs.remove(p);
		}
		p.setGameMode(GameMode.SURVIVAL);
		p.setMaxHealth(20);
		p.setAllowFlight(false);
		p.setHealth(20);
		p.setLevel(0);
		plugin.sbm.removeScoreboard(p);
		data.restoreInventory(p);
		p.teleport(back);
		if (state != GameState.LOBBY && state != GameState.ENDING)
		this.checkWin();
	}
	
	public void stop(){
		for (Player p : this.getAllMembers()){
			leave(p);
		}
		im.restore();
		backup.restoreArena();
		Bukkit.getScheduler().cancelTask(loop);
		plugin.am.arenas.remove(this.name);
		plugin.am.loadArena(name);
	}
	
	public void fixSpawn(Location loc){
		if (loc.getBlock().getRelative(BlockFace.DOWN).getType() == Material.AIR){
			loc.getBlock().getRelative(BlockFace.DOWN).setType(Material.SANDSTONE);
		}
	}
	
	public void restart(){
		if (plugin.infiniteremix && infinitemode){
			Arena a = plugin.am.remixInfiniteArena(this);
			if (!a.name.equals(this.name)){
				plugin.logger.log("Infiniteremix map found! ("+a.name+")", LoggerLevel.INFORMATION);
				for (Player p : this.getAllMembers()){
					leave(p);
					a.join(p , true);
				}
			}
			plugin.logger.log("No Infiniteremix map found! :/", LoggerLevel.INFORMATION);
		}
		if (!infinitemode){
			for (Player p : this.getAllMembers()){
				leave(p);
			}
		}
		playersVotedSkip = new ArrayList<Player>();
		
		im.restore();
		im = new ItemManager(this);
		data = new ArenaData(this);
		state = GameState.LOADING;
		backup.restoreArena();
		this.counter = this.defaultcounter_lobby;
		if (infinitemode){
			@SuppressWarnings("unchecked")
			ArrayList<Player> save = (ArrayList<Player>) this.getAllMembers().clone();
			specs = new ArrayList<Player>();
			players = new ArrayList<Player>();
			this.updateBoard();
			for (Player p : save){
				players.add(p);
				p.teleport(lobby);
				p.setGameMode(GameMode.SURVIVAL);
				p.setFlying(false);
				p.setAllowFlight(false);
				playerdata.put(p, new PlayerData(p , this));
			}
			plugin.logger.log("Restarting arena "+name+" in infinitemode!", LoggerLevel.INFORMATION);
		}
		loadData();
		Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, new Runnable(){

			@Override
			public void run() {
				
				state = GameState.LOBBY;
				backup.restoreArena();
				
			}
			
		}, 200);
	}

	public void stopComplete(){
		for (Player p : this.getAllMembers()){
			leave(p);
		}
		im.restore();
		Bukkit.getScheduler().cancelTask(loop);
		this.backup.restoreArena();
		plugin.am.arenas.remove(this.name);
	}
	
	public void sendMessage (String msg){
		for (Player p : this.getAllMembers()) {
			p.sendMessage(ChatColor.translateAlternateColorCodes('&', msg));
		}
	}
	
	public void sendSpecMessage (String msg){
		for (Player p : specs) {
			p.sendMessage(ChatColor.translateAlternateColorCodes('&', msg));
		}
	}
	
	public void setGlobalPlayerLevel(int lvl) {
		for (Player p : getAllMembers()) {
			p.setExp(0);
			p.setLevel(lvl);
		}
	}
	
	public void death(Player p){
		if (players.contains(p)){
			if (playerdata.get(p).death()){
				p.sendMessage(plugin.mf.getMessage("lostlife", true).replaceAll("%life%", playerdata.get(p).lives+""));
				playerdata.get(p).updateDamage();
				playerdata.get(p).updateLifes();
				im.playerDrop(p);
				playerdata.get(p).smashing = false;
				isSmashing.put(p, false);
				p.setAllowFlight(true);
				playerdata.get(p).velocity_last = 0.0;
				p.getInventory().clear();
				this.teleport(p);
				sendMessage(plugin.mf.getMessage("playerdied", true).replaceAll("%life%", playerdata.get(p).lives+"").replaceAll("%player%", p.getName()));
			}
			else{
				moveToSpec(p);
				sendMessage(plugin.mf.getMessage("playerdied_out", true).replaceAll("%player%", p.getName()));
			}
			checkWin();
		}
	}
	
	public void moveToSpec (Player p){
		players.remove(p);
		specs.add(p);
		p.setMaxHealth(20);
		p.setHealth(20);
		p.setGameMode(GameMode.SPECTATOR);
		data.clearInventory(p);
		im.playerDrop(p);
		p.setAllowFlight(true);
		playerdata.get(p).smashing = false;
		playerdata.get(p).velocity_last = 0.0;
		updateBoard();
		ColorTabAPI.clearTabStyle(p, this.getAllMembers());
		p.teleport(spectator);
	}
	
	public void checkWin(){
		if (players.size() == 1){
			Player  p = players.get(0);
			this.switchToEnding();
			moveToSpec(p);
			winner = p.getName();
			this.updateBoard();
			sendMessage(plugin.mf.getMessage("won", true).replaceAll("%player%", p.getName()).replaceAll("%life%", playerdata.get(p).lives+""));
		}
		else if (players.size() < 1){
			this.restart();
		}
	}
	
	public void updateBoard(){
		for (Player p : players){
			if (state == GameState.LOBBY)
				plugin.sbm.setScoreboard(p, "lobby", getSBKeys(p));
			if (state == GameState.STARTING)
				plugin.sbm.setScoreboard(p, "starting", getSBKeys(p));
			if (state == GameState.INGAME)
				plugin.sbm.setScoreboard(p, "ingame", getSBKeys(p));
			if (state == GameState.ENDING)
				plugin.sbm.setScoreboard(p, "ending", getSBKeys(p));
		}
		for (Player p : specs){
			if (state == GameState.ENDING){
				plugin.sbm.setScoreboard(p, "ending", getSBKeys(p));
			}
			else{
				plugin.sbm.setScoreboard(p, "spectator", getSBKeys(p));
			}
		}
	}
	
	//Arena Loop
	
	int defaultcounter_lobby = 10;
	int defaultcounter_starting = 5;
	int defaultcounter_ingame = 3000;
	int defaultcounter_ending = 15;
	int counter = defaultcounter_lobby;
	
	int loop;
	
	public void gameLoop() {
		loop = Bukkit.getScheduler().scheduleSyncRepeatingTask(plugin, new Runnable() {

			@Override
			public void run() {
				if (state == GameState.LOBBY) {
					if (getAllMembers().size() < min && counter != defaultcounter_lobby) {
						counter = defaultcounter_lobby;
						sendMessage(plugin.mf.getMessage("notenoughplayers", true));
					}
					else if (getAllMembers().size() < min && counter == defaultcounter_lobby){
						
					}
					else if (counter != 0){
						if (counter % 30 == 0 || counter == 10 || counter <= 5) {
							sendMessage(plugin.mf.getMessage("lobbycountdown", true).replace("%s%", counter + ""));
						}
						setGlobalPlayerLevel(counter);
						counter -= 1;
					}
					else {
						switchToStarting();
					}
				}
				else if (state == GameState.STARTING) {
					if (counter == 0) {
						switchToIngame();
						return;
					}
					im.spawnRandomItem();
					counter -= 1;
				}
				else if (state == GameState.INGAME) {
					for (Player p : players) {
						playerdata.get(p).updateDamage();
						playerdata.get(p).updateLifes();
					}
					if (Tools.randInt(1, 3) == 2){
						im.spawnRandomItem();
					}
					if (counter != 0){
						if ((counter % 30 == 0 && counter <= 90) || counter == 10 || counter <= 5) {
							sendMessage(plugin.mf.getMessage("gameendcountdown", true).replace("%s%", counter + ""));
						}
					}else {
						switchToEnding();
					}
					counter -= 1;
				}
				else if (state == GameState.ENDING) {
					if (counter != 0){
						if (counter % 30 == 0 || counter == 10 || counter <= 5) {
							if (infinitemode)
								sendMessage(plugin.mf.getMessage("gamerestartcountdown", true).replace("%s%", counter + ""));
							else
								sendMessage(plugin.mf.getMessage("gamestopcountdown", true).replace("%s%", counter + ""));
						}
						setGlobalPlayerLevel(counter);
					}else {
						restart();
					}
					counter -= 1;
				}
				
			}
			
		} , 0 , 20);
	}
	
	String winner = "Nobody";
	
	public HashMap<String , String> getSBKeys(Player p){
		HashMap<String , String> map = new HashMap<String , String>();
		try {
			map.put("players", players.size()+"");
		} catch (Exception e) {

		}
		try {
			map.put("damage", playerdata.get(p).damage+"");
		} catch (Exception e) {

		}
		try {
			map.put("lifes", playerdata.get(p).lives+"");
		} catch (Exception e) {

		}
		map.put("arena", name);
		map.put("min", min+"");
		map.put("max", max+"");
		map.put("winner", winner+"");
		return map;
	}
	
	//Statecontrol
	
	public void switchToStarting() {
		state = GameState.STARTING;
		for (Player p : players){
			p.setAllowFlight(true);
		}
		updateBoard();
		counter = this.defaultcounter_starting;
		move = false;
		sendMessage(plugin.mf.getMessage("getready", true));
		teleportAll();
		for (Entity e : smaller.getWorld().getEntities()){
			if (e instanceof Item){
				if (inMap(e.getLocation())){
					e.remove();
				}
			}
		}
	}
	public void switchToIngame() {
		state = GameState.INGAME;
		for (Player p : players){
			p.setAllowFlight(true);
			for (Player pp : players){
				pp.hidePlayer(p);
				pp.showPlayer(p);
			}
		}
		updateBoard();
		counter = this.defaultcounter_ingame;
		move = true;
		drop = true;
		damage = true;
		this.backup.restoreArena();
	}
	public void switchToEnding() {
		state = GameState.ENDING;
		for (Player p : players){
			p.setAllowFlight(false);
		}
		updateBoard();
		counter = this.defaultcounter_ending;
		move = true;
		drop = false;
		inv_click = false;
		damage = false;
		for (Entity e : smaller.getWorld().getEntities()){
			if (e instanceof Item){
				if (inMap(e.getLocation())){
					e.remove();
				}
			}
		}
	}
	public void teleportAll() {
		int current = 0;
		Collections.shuffle(game);
		for (Player p : players) {
			if (game.size() <= current) {
				current = 0;
			}
			p.teleport(game.get(current));
			current += 1;
		}
	}
	public void teleport (Player p) {
		Collections.shuffle(game);
		Location loc = game.get(0);
		fixSpawn(loc);
		p.teleport(loc);
	}
	
	
	
	
}
