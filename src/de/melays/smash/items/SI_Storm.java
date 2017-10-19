package de.melays.smash.items;

import java.util.ArrayList;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.FallingBlock;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;

import de.melays.smash.Arena;
import de.melays.smash.GameState;
import de.melays.smash.ItemManager;
import de.melays.smash.SmashItem;
import de.melays.smash.Tools;

public class SI_Storm extends SmashItem{

	int damage = 1;
	Material m = Material.FIREWORK_CHARGE;
	String name = "Stormgrenade";
	String lore = "";
	
	public SI_Storm(Arena a , ItemManager m) {
		super(a , m);
		ConfigurationSection section = m.getItemConfig("stormgrenade");
		section.addDefault("damage", damage);
		section.addDefault("material", this.m.toString());
		section.addDefault("name", name);
		section.addDefault("lore", "");
		section.addDefault("chance", chance);
		chance = m.getItemConfig().getInt("stormgrenade.chance");
		damage = m.getItemConfig().getInt("stormgrenade.damage");
		try{
			this.m = Material.getMaterial(m.getItemConfig().getString("stormgrenade.material").toUpperCase());
		}catch(Exception ex){
			
		}
		name = m.getItemConfig().getString("stormgrenade.name");
		lore = m.getItemConfig().getString("stormgrenade.lore");
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
					storm(item.getLocation());
					item.remove();
				}
				else {
					item.remove();
				}
			}
			
		}, 60));
	}
	
	public void storm (Location loc){
		if (!a.inMap(loc))
			return;
		ArrayList<Block> blocks = new ArrayList<Block>();
		for (int i = 0 ; i < Tools.randInt(12, 27) ; i++){
			blocks.add(loc.clone().add(Tools.randInt(1,15),Tools.randInt(1,15) , Tools.randInt(1,15)).getBlock());
		}
		for (int i = 0 ; i < Tools.randInt(15, 20) ; i++){
			try {
				Block flow = blocks.remove(Tools.randInt(0,blocks.size()-1));
				if (a.inMap(flow.getLocation())){
					Vector v = loc.toVector().subtract(flow.getLocation().toVector());
					@SuppressWarnings("deprecation")
					FallingBlock fallingBlock = flow.getWorld().spawnFallingBlock(flow.getLocation(), flow.getType(), flow.getData());
					fallingBlock.setDropItem(false);
					fallingBlock.setVelocity(v);
					flow.setType(Material.AIR);
				}
			} catch (Exception e) {

			}
		}
	    for (Player p : a.players){
	    	if (p.getLocation().distance(loc) <= 10){
	    		Vector v = loc.toVector().subtract(p.getLocation().toVector());
	    		p.setVelocity(new Vector());
	    		p.setVelocity(v);
	    	}
	    }
		scheudler.add(Bukkit.getScheduler().scheduleSyncDelayedTask(a.plugin , new Runnable(){

			@Override
			public void run() {
				if (a.state == GameState.INGAME) {
				    for (Player p : a.players){
				    	if (p.getLocation().distance(loc) <= 5){
				    		p.addPotionEffect(new PotionEffect(PotionEffectType.SLOW , 5 , 7));
				    	}
				    }
					loc.getWorld().createExplosion(loc.getX(), loc.getY() , loc.getZ(), 3F, false , false);
				}
			}
			
		}, 10));
		
		scheudler.add(Bukkit.getScheduler().scheduleSyncDelayedTask(a.plugin , new Runnable(){

			@Override
			public void run() {
				if (a.state == GameState.INGAME) {
		    		loc.getWorld().createExplosion(loc.getX(), loc.getY() , loc.getZ(), 13F, false , false);
		    		for (Block b : Tools.getBlocks(loc.getBlock(), 5)){
		    			if (Tools.randInt(1, 3) == 2){
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
			
		}, 50));

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
