package de.melays.smash.items;

import java.util.ArrayList;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.FallingBlock;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.util.Vector;

import de.melays.smash.Arena;
import de.melays.smash.ItemManager;
import de.melays.smash.SmashItem;
import de.melays.smash.Tools;

public class SI_Claymore extends SmashItem{

	int damage = 1;
	Material m = Material.STONE_PLATE;
	String name = "Claymore";
	String lore = "";
	
	public SI_Claymore(Arena a , ItemManager m) {
		super(a , m);
		ConfigurationSection section = m.getItemConfig("claymore");
		section.addDefault("damage", damage);
		section.addDefault("material", this.m.toString());
		section.addDefault("name", name);
		section.addDefault("lore", "");
		section.addDefault("chance", chance);
		chance = m.getItemConfig().getInt("claymore.chance");
		damage = m.getItemConfig().getInt("claymore.damage");
		try{
			this.m = Material.getMaterial(m.getItemConfig().getString("claymore.material").toUpperCase());
		}catch(Exception ex){
			
		}
		name = m.getItemConfig().getString("claymore.name");
		lore = m.getItemConfig().getString("claymore.lore");
		try {
			
		}catch(Exception ex) {
			
		}
	}
	
	int chance = 3;
	public SmashItem setDefaultAmount(ItemManager im){
		this.addToDefault(chance, im);
		return this;
	}
	
	public ItemStack getItemStack(){
		ItemStack stack = new ItemStack (m);
		ItemMeta m = stack.getItemMeta();
		m.setDisplayName(name);
		ArrayList<String> lore = new ArrayList<String>();
		lore.add(this.lore);
		m.setLore(lore);
		stack.setItemMeta(m);
		return stack;
	}
	
	public SmashItem registerEvents(){
		Bukkit.getPluginManager().registerEvents(this, a.plugin);
		return this;
	}
	
	@EventHandler
	public void onMove(PlayerInteractEvent e){
		if(e.getAction().equals(Action.PHYSICAL)){
			if(e.getClickedBlock().getType() == Material.STONE_PLATE){
				if (e.getClickedBlock().getLocation().equals(loc)){
					onTrigger();
				}
			}
		}
	}
	
	Location loc;
	
	public void onBlockPlace(Location loc){
		destroy();
		this.loc = loc;
		loc.getBlock().setType(Material.STONE_PLATE);
	}
	
	@SuppressWarnings("deprecation")
	public void onTrigger(){
		for (Block b : Tools.getBlocks(loc.getBlock(), 2)){
			if (a.inMap(b.getLocation())){
				if (Tools.randInt(1, 2) == 2){
	    		      float x = -3.0F + (float)(Math.random() * 7.0D);
	    		      float y = -4.0F + (float)(Math.random() * 9.0D);
	    		      float z = -3.0F + (float)(Math.random() * 7.0D);
	    		      
	    		      FallingBlock fallingBlock = b.getWorld().spawnFallingBlock(b.getLocation(), b.getType(), b.getData());
	    		      fallingBlock.setDropItem(false);
	    		      fallingBlock.setVelocity(new Vector(x, y, z));
	    		      b.setType(Material.AIR);
				}
			}
		}
		loc.getWorld().createExplosion(loc.getX(), loc.getY() , loc.getZ(), 7F, false , false);
	}
	
	public void cancleActions(){
		try {
			
		} catch (Exception e) {

		}
	}
	
	public int getHitDamage(){
		return damage;
	}
	

}
