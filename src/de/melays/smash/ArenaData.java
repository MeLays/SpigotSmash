package de.melays.smash;

import java.util.ArrayList;
import java.util.HashMap;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class ArenaData {

	Arena a;
	
	public ArenaData (Arena a){
		this.a = a;
	}
	
	ArrayList<AdvancedMaterial> broken_blocks = new ArrayList<AdvancedMaterial>();
	
	HashMap<Player , ItemStack[]> inventory = new HashMap<Player , ItemStack[]>();
	HashMap<Player , ItemStack[]> armor_inventory = new HashMap<Player , ItemStack[]>();
	
	public void saveAndClearInventory(Player p){
		inventory.put(p, p.getInventory().getContents());
		armor_inventory.put(p, p.getInventory().getArmorContents());
		clearInventory(p);
	}
	
	public void clearInventory (Player p){
		try{
			p.getInventory().clear();
			p.getInventory().setArmorContents(null);
		}catch (Exception ex){
			
		}
	}
	
	public void restoreInventory(Player p){
		clearInventory(p);
		if (inventory.containsKey(p)){
			p.getInventory().setContents(inventory.get(p));
		}
		if (armor_inventory.containsKey(p)){
			p.getInventory().setArmorContents(armor_inventory.get(p));
		}
	}
	
}
