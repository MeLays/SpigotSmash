package de.melays.smash.items;

import java.util.ArrayList;

import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import de.melays.smash.Arena;
import de.melays.smash.ItemManager;
import de.melays.smash.SmashItem;

public class SI_WoodSword extends SmashItem{

	int damage = 10;
	Material m = Material.WOOD_SWORD;
	String name = "Wooden Sword";
	String lore = "";
	
	public SI_WoodSword(Arena a , ItemManager m) {
		super(a , m);
		ConfigurationSection section = m.getItemConfig("wood_sword");
		section.addDefault("damage", 10);
		section.addDefault("material", "WOOD_SWORD");
		section.addDefault("name", "Wooden Sword");
		section.addDefault("lore", "");
		section.addDefault("chance", chance);
		chance = m.getItemConfig().getInt("wood_sword.chance");
		damage = m.getItemConfig().getInt("wood_sword.damage");
		try{
			this.m = Material.getMaterial(m.getItemConfig().getString("wood_sword.material").toUpperCase());
		}catch(Exception ex){
			
		}
		name = m.getItemConfig().getString("wood_sword.name");
		lore = m.getItemConfig().getString("wood_sword.lore");
		m.saveItemConfig();
	}
	
	int chance = 6;
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
