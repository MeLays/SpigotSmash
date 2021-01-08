package de.melays.smash.items;

import java.util.ArrayList;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Entity;
import org.bukkit.entity.TNTPrimed;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import de.melays.smash.Arena;
import de.melays.smash.ItemManager;
import de.melays.smash.SmashItem;

public class SI_TNTWalk extends SmashItem implements Listener{

	int damage = 1;
	Material material = Material.BOW;
	String name = "Bow";
	String lore = "";
	
	public SI_TNTWalk(Arena a , ItemManager m) {
		super(a , m);
		ConfigurationSection section = m.getItemConfig("tntwalk");
		section.addDefault("damage", 1);
		section.addDefault("material", "TNT");
		section.addDefault("name", "TNT Walk");
		section.addDefault("lore", "");
		section.addDefault("chance", chance);
		chance = m.getItemConfig().getInt("tntwalk.chance");
		damage = m.getItemConfig().getInt("tntwalk.damage");
		try{
			this.material = Material.getMaterial(m.getItemConfig().getString("tntwalk.material").toUpperCase());
		}catch(Exception ex){
			
		}
		name = m.getItemConfig().getString("tntwalk.name");
		lore = m.getItemConfig().getString("tntwalk.lore");
		m.saveItemConfig();
		try {
			
		}catch(Exception ex) {
			
		}
	}
	
	int chance = 4;
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
	
	ArrayList<Entity> tnts;
	UUID shooter;
	
	int rep;
	
	public void fire (){
		tnts = new ArrayList<Entity>();
		this.store("scheduler", 5);
		rep = Bukkit.getScheduler().scheduleSyncRepeatingTask(a.plugin, new Runnable(){

			@Override
			public void run() {
				int temp = (int)get("scheduler");
				if (temp > 0){
					store("scheduler", ((int)get("scheduler"))-1);
					Entity tnt = p.getWorld().spawn(p.getLocation(), TNTPrimed.class);
			        ((TNTPrimed)tnt).setFuseTicks(50);
			        tnts.add(tnt);
				}
				else{
					Bukkit.getScheduler().cancelTask(rep);
				}
			}
			
		}, 0, 20);
	}

	public void cancleActions(){
		Bukkit.getScheduler().cancelTask(rep);
		destroy();
		try {
			for (Entity a : tnts){
				a.remove();
			}
		} catch (Exception e) {

		}
	}
	
}
