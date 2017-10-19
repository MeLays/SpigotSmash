package de.melays.smash;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;

import de.melays.itembuilder.ItemBuilder;

public class LocationMarker implements Listener {
	
	Location locl;
	Location locr;
	Player p;
	
	main plugin;
	
	public LocationMarker(Player ps , main plugin){
		p = ps;
		this.plugin = plugin;
		Bukkit.getPluginManager().registerEvents(this, plugin);
	}
	
	public void setLeft (Location l){
		locl = l;
	}
	
	public void setRight (Location l){
		locr = l;
	}
	
	public boolean complete (){
		if (locr != null && locl != null){
			return true;
		}
		return false;
	}
	
	public void giveSetupItem(){
		p.getInventory().addItem(new ItemBuilder(Material.RED_MUSHROOM).setName(ChatColor.RED+"LocationMarker (SMASH)").toItemStack());
	}
	
	@EventHandler
	public void onInteract(PlayerInteractEvent e){
		Player p = e.getPlayer();
		if (p == this.p){
			if (!p.getItemInHand().getType().equals(Material.RED_MUSHROOM)){
				return;
			}
			if (!p.getItemInHand().getItemMeta().getDisplayName().equals(ChatColor.RED+"LocationMarker (SMASH)")){
				return;
			}
			if (e.getAction() == Action.RIGHT_CLICK_BLOCK){
				setRight(e.getClickedBlock().getLocation());
				p.sendMessage(plugin.mf.getMessage("prefix", false) + " Set the 2nd location (rightclick)");
				e.setCancelled(true);
			}
			else if (e.getAction() == Action.LEFT_CLICK_BLOCK){
				setLeft(e.getClickedBlock().getLocation());
				p.sendMessage(plugin.mf.getMessage("prefix", false) + " Set the 1st location (leftclick)");
				e.setCancelled(true);
			}
		}
	}
	
	public void generatePositions (String arena){
		Location l1 = locl;
		Location l2 = locr;
		double xpos1;
		double ypos1;
		double zpos1;
		double xpos2;
		double ypos2;
		double zpos2;
		if (l1.getX() <= l2.getX()){	
			xpos1 = l1.getX();
			xpos2 = l2.getX();	
		}
		else{	
			xpos1 = l2.getX();
			xpos2 = l1.getX();	
		}
		if (l1.getY() <= l2.getY()){	
			ypos1 = l1.getY();
			ypos2 = l2.getY();	
		}
		else{
			ypos1 = l2.getY();
			ypos2 = l1.getY();
		}
		if (l1.getZ() <= l2.getZ()){
			
			zpos1 = l1.getZ();
			zpos2 = l2.getZ();
		}
		else{	
			zpos1 = l2.getZ();
			zpos2 = l1.getZ();	
		}
		plugin.am.addArenaLocation(arena, "corner_1", new Location (l1.getWorld() , xpos1 , ypos1 , zpos1));
		plugin.am.addArenaLocation(arena, "corner_2", new Location (l1.getWorld() , xpos2 , ypos2 , zpos2));
		p.sendMessage(plugin.mf.getMessage("prefix", false) + " The Corners of the map have been calculated and saved.");
	}
	
}
