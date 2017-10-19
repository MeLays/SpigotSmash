package de.melays.smash.items;

import java.util.ArrayList;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.FallingBlock;
import org.bukkit.entity.Item;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.util.Vector;

import de.melays.smash.Arena;
import de.melays.smash.GameState;
import de.melays.smash.ItemManager;
import de.melays.smash.SmashItem;
import de.melays.smash.Tools;

public class SI_Airstrike extends SmashItem{

	int damage = 1;
	Material m = Material.REDSTONE_TORCH_ON;
	String name = "Airstrike";
	String lore = "";
	
	public SI_Airstrike(Arena a , ItemManager m) {
		super(a , m);
		ConfigurationSection section = m.getItemConfig("airstrike");
		section.addDefault("damage", damage);
		section.addDefault("material", this.m.toString());
		section.addDefault("name", name);
		section.addDefault("lore", "");
		section.addDefault("chance", chance);
		chance = m.getItemConfig().getInt("airstrike.chance");
		damage = m.getItemConfig().getInt("airstrike.damage");
		try{
			this.m = Material.getMaterial(m.getItemConfig().getString("airstrike.material").toUpperCase());
		}catch(Exception ex){
			
		}
		name = m.getItemConfig().getString("airstrike.name");
		lore = m.getItemConfig().getString("airstrike.lore");
		m.saveItemConfig();
	}
	
	int chance = 1;
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
	
	public int getHitDamage(){
		return damage;
	}
	
	public void cancleActions(){
		try {
			item.remove();
		} catch (Exception e) {

		}
		for (int i : scheudler){
			try {
				Bukkit.getScheduler().cancelTask(i);
			} catch (Exception e) {

			}
		}
	}
	Item item;
	ArrayList<Integer> scheudler = new ArrayList<Integer>();
	public void throwThis(){
		destroy();
		ItemStack stack = getItemStack();
		item = p.getWorld().dropItem(p.getEyeLocation(), stack);
		item.setVelocity(p.getLocation().getDirection());
		scheudler.add(Bukkit.getScheduler().scheduleSyncDelayedTask(a.plugin , new Runnable(){

			@Override
			public void run() {
				if (a.state == GameState.INGAME) {
					item.remove();
					Location loc = item.getLocation();
					ArrayList<Location> blocks = new ArrayList<Location>();
					for (int i = 0 ; i < Tools.randInt(12, 27) ; i++){
						blocks.add(loc.clone().add(Tools.randInt(1,15),Tools.randInt(1,15) , Tools.randInt(1,15)));
					}
		    		blocks.add(loc);
		    		for (Location l : blocks){
		    			explode(l);
		    		}
				}
			}
			
		}, 60));
	}
	
	public void explode (Location l){
		if (!a.inMap(l))return;
		scheudler.add(Bukkit.getScheduler().scheduleSyncDelayedTask(a.plugin , new Runnable(){

			@SuppressWarnings("deprecation")
			@Override
			public void run() {
				
				if (a.state == GameState.INGAME) {
	    			l.getWorld().strikeLightning(l);
	    			l.getWorld().createExplosion(l.getX(), l.getY() , l.getZ(), 5F, false , false);
		    		for (Block b : Tools.getBlocks(l.getBlock(), 2)){
		    			if (Tools.randInt(1, 4) == 2){
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
				
			}
			
		}, Tools.randInt(1, 30)));
	}
	
	public void onRightclick(){
		throwThis();
		destroy();
	}
	
	public void onLeftclick(){
		throwThis();
		destroy();
	}
	
	

}
