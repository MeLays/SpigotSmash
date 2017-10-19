package de.melays.smash.items;

import java.util.ArrayList;

import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import de.melays.smash.Arena;
import de.melays.smash.ItemManager;
import de.melays.smash.SmashItem;

public class SI_IronSword extends SmashItem{

	int damage = 50;
	Material m = Material.IRON_SWORD;
	String name = "Iron Sword";
	String lore = "";
	
	public SI_IronSword(Arena a , ItemManager m) {
		super(a , m);
		ConfigurationSection section = m.getItemConfig("iron_sword");
		section.addDefault("damage", 50);
		section.addDefault("material", "IRON_SWORD");
		section.addDefault("name", "Iron Sword");
		section.addDefault("lore", "");
		section.addDefault("chance", chance);
		chance = m.getItemConfig().getInt("iron_sword.chance");
		damage = m.getItemConfig().getInt("iron_sword.damage");
		try{
			this.m = Material.getMaterial(m.getItemConfig().getString("iron_sword.material").toUpperCase());
		}catch(Exception ex){
			
		}
		name = m.getItemConfig().getString("iron_sword.name");
		lore = m.getItemConfig().getString("iron_sword.lore");
		try {
			
		}catch(Exception ex) {
			
		}
	}
	
	int chance = 4;
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
		destroy();
		return damage;
	}
	

}
