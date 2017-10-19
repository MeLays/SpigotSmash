package de.melays.smash.items;

import java.util.ArrayList;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Entity;
import org.bukkit.entity.FallingBlock;
import org.bukkit.entity.Fireball;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.util.Vector;

import de.melays.smash.Arena;
import de.melays.smash.GameState;
import de.melays.smash.ItemManager;
import de.melays.smash.SmashItem;
import de.melays.smash.Tools;

public class SI_RocketLauncher extends SmashItem implements Listener{

	int damage = 1;
	Material material = Material.STICK;
	String name = "Rocket Launcher";
	String lore = "";
	
	public SI_RocketLauncher(Arena a , ItemManager m) {
		super(a , m);
		ConfigurationSection section = m.getItemConfig("rocket_launcher");
		section.addDefault("damage", 1);
		section.addDefault("material", "STICK");
		section.addDefault("name", "Rocket Launcher");
		section.addDefault("lore", "");
		section.addDefault("chance", chance);
		chance = m.getItemConfig().getInt("rocket_laucher.chance");
		damage = m.getItemConfig().getInt("rocket_launcher.damage");
		try{
			this.material = Material.getMaterial(m.getItemConfig().getString("rocket_launcher.material").toUpperCase());
		}catch(Exception ex){
			
		}
		name = m.getItemConfig().getString("rocket_launcher.name");
		lore = m.getItemConfig().getString("rocket_launcher.lore");
		m.saveItemConfig();
		try {
			
		}catch(Exception ex) {
			
		}
	}
	
	int chance = 3;
	public SmashItem setDefaultAmount(ItemManager im){
		this.addToDefault(chance, im);
		return this;
	}
	
	public SmashItem registerEvents(){
		Bukkit.getPluginManager().registerEvents(this, a.plugin);
		return this;
	}
	
	public ItemStack getItemStack(){
		ItemStack stack = new ItemStack (material);
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
	
	public void onRightclick(){
		fire();
		destroy();
	}
	
	public void onLeftclick(){
		fire();
		destroy();
	}
	
	@SuppressWarnings("deprecation")
	@EventHandler
	public void onEntityExplode(EntityExplodeEvent e) {
		if (a.state == GameState.INGAME) {
		    Entity ent = e.getEntity();
		    if (ent instanceof Fireball) {
		    	Fireball f = (Fireball) ent;
		    	if (f.getShooter() instanceof Player){
		    		Player p = (Player)f.getShooter();
		    		if (!p.getUniqueId().equals(shooter)){
		    			return;
		    		}
		    		e.setCancelled(true);
		    		if (!a.inMap(f.getLocation()))return;
		    		Location l = e.getLocation();
		    		l.getWorld().createExplosion(l.getX(), l.getY() , l.getZ(), 3F, false , false);
		    		for (Block b : Tools.getBlocks(e.getLocation().getBlock(), 2)){
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
		    }
		}
	}
	
	Fireball fireball;
	UUID shooter;
	public void fire (){
		shooter = this.p.getUniqueId();
		fireball = this.p.launchProjectile(Fireball.class);
	}

	public void cancleActions(){
		try {
			fireball.remove();
		} catch (Exception e) {

		}
	}
	
}
