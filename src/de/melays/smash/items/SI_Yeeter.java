package de.melays.smash.items;

import java.util.ArrayList;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Arrow;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import de.melays.smash.Arena;
import de.melays.smash.ItemManager;
import de.melays.smash.SmashItem;

public class SI_Yeeter extends SmashItem implements Listener{

	Material material = Material.BOW;
	String name = "Bow";
	String lore = "";
	
	public SI_Yeeter(Arena a , ItemManager m) {
		super(a , m);
		ConfigurationSection section = m.getItemConfig("yeeter");
		section.addDefault("damage", 1);
		section.addDefault("material", "FIREWORK");
		section.addDefault("name", "Yeeter");
		section.addDefault("lore", "");
		section.addDefault("chance", chance);
		chance = m.getItemConfig().getInt("yeeter.chance");
		try{
			this.material = Material.getMaterial(m.getItemConfig().getString("yeeter.material").toUpperCase());
		}catch(Exception ex){
			
		}
		name = m.getItemConfig().getString("yeeter.name");
		lore = m.getItemConfig().getString("yeeter.lore");
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
		p.setVelocity(p.getLocation().getDirection().multiply(150));
		p.teleport(p.getLocation().setDirection(p.getLocation().getDirection().multiply(150)));
		p.getLocation().getWorld().playSound(p.getLocation(), Sound.FIREWORK_BLAST, 1, 1);
	}

	public void cancleActions(){

	}
	
}
