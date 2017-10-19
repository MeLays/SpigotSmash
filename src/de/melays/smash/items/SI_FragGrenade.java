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

public class SI_FragGrenade extends SmashItem{

	int damage = 1;
	Material m = Material.INK_SACK;
	String name = "Fraggrenade";
	String lore = "";
	
	public SI_FragGrenade(Arena a , ItemManager m) {
		super(a , m);
		ConfigurationSection section = m.getItemConfig("fraggrenade");
		section.addDefault("damage", damage);
		section.addDefault("material", this.m.toString());
		section.addDefault("name", name);
		section.addDefault("lore", "");
		section.addDefault("chance", chance);
		chance = m.getItemConfig().getInt("fraggrenade.chance");
		damage = m.getItemConfig().getInt("fraggrenade.damage");
		try{
			this.m = Material.getMaterial(m.getItemConfig().getString("fraggrenade.material").toUpperCase());
		}catch(Exception ex){
			
		}
		name = m.getItemConfig().getString("fraggrenade.name");
		lore = m.getItemConfig().getString("fraggrenade.lore");
		m.saveItemConfig();
	}
	
	int chance = 2;
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
		try {
			for (Item i : frags)
				i.remove();
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
					exlode(item.getLocation());
					item.remove();
				}
				else {
					item.remove();
				}
			}
			
		}, 60));
	}
	
	ArrayList<Item> frags = new ArrayList<Item>();
	
	public void exlode (Location loc){
		if (!a.inMap(loc))
			return;
		
		for (int i = 0 ; i < Tools.randInt(3, 7) ; i++){
			float x = -3.0F + (float)(Math.random() * 7.0D);
		    float y = -4.0F + (float)(Math.random() * 9.0D);
		    float z = -3.0F + (float)(Math.random() * 7.0D);
		    Vector v = new Vector(x, y, z);
		    Item item = loc.getWorld().dropItem(loc, new ItemStack(Material.BAKED_POTATO));
		    item.setVelocity(v.multiply(0.6));
		    explode(item);
		    frags.add(item);
		}
	}
	
	public void explode (Item i){
		scheudler.add(Bukkit.getScheduler().scheduleSyncDelayedTask(a.plugin , new Runnable(){

			@Override
			public void run() {
				if (a.state == GameState.INGAME) {
					if (!a.inMap(i.getLocation()))
						return;
					Location loc = i.getLocation();
					i.remove();
		    		loc.getWorld().createExplosion(loc.getX(), loc.getY() , loc.getZ(), 4F, false , false);
		    		for (Block b : Tools.getBlocks(loc.getBlock(), 2)){
		    			if (Tools.randInt(1, 4) == 2){
		    				if (a.inMap(b.getLocation())){
			    		      float x = -3.0F + (float)(Math.random() * 7.0D);
			    		      float y = -4.0F + (float)(Math.random() * 9.0D);
			    		      float z = -3.0F + (float)(Math.random() * 7.0D);
			    		      
			    		      @SuppressWarnings("deprecation")
			    		      FallingBlock fallingBlock = b.getWorld().spawnFallingBlock(b.getLocation(), b.getType(), b.getData());
			    		      fallingBlock.setDropItem(false);
			    		      fallingBlock.setVelocity(new Vector(x, y, z));
			    		      b.setType(Material.AIR);
		    				}
		    			}
		    		}
				}
			}
			
		}, Tools.randInt(3, 35)));
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
