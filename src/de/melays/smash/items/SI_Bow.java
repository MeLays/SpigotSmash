package de.melays.smash.items;

import java.util.ArrayList;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Arrow;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import de.melays.smash.Arena;
import de.melays.smash.ItemManager;
import de.melays.smash.SmashItem;

public class SI_Bow extends SmashItem implements Listener{

	int damage = 1;
	Material material = Material.BOW;
	String name = "Bow";
	String lore = "";
	
	public SI_Bow(Arena a , ItemManager m) {
		super(a , m);
		ConfigurationSection section = m.getItemConfig("bow");
		section.addDefault("damage", 1);
		section.addDefault("material", "BOW");
		section.addDefault("name", "Bow");
		section.addDefault("lore", "");
		section.addDefault("chance", chance);
		chance = m.getItemConfig().getInt("bow.chance");
		damage = m.getItemConfig().getInt("bow.damage");
		try{
			this.material = Material.getMaterial(m.getItemConfig().getString("bow.material").toUpperCase());
		}catch(Exception ex){
			
		}
		name = m.getItemConfig().getString("bow.name");
		lore = m.getItemConfig().getString("bow.lore");
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
	
	ArrayList<Arrow> arrow;
	UUID shooter;
	
	int rep;
	
	public void fire (){
		arrow = new ArrayList<Arrow>();
		this.store("scheduler", 25);
		rep = Bukkit.getScheduler().scheduleSyncRepeatingTask(a.plugin, new Runnable(){

			@Override
			public void run() {
				int temp = (int)get("scheduler");
				if (temp > 0){
					store("scheduler", ((int)get("scheduler"))-1);
					Arrow a = p.launchProjectile(Arrow.class);
					a.setVelocity(a.getVelocity().multiply(10));
					arrow.add(a);
				}
				else{
					Bukkit.getScheduler().cancelTask(rep);
				}
			}
			
		}, 0, 2);
	}

	public void cancleActions(){
		Bukkit.getScheduler().cancelTask(rep);
		destroy();
		try {
			for (Arrow a : arrow){
				a.remove();
			}
		} catch (Exception e) {

		}
	}
	
}
