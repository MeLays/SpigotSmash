package de.melays.smash.items;

import java.util.ArrayList;

import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import de.melays.smash.Arena;
import de.melays.smash.ItemManager;
import de.melays.smash.SmashItem;

public class SI_DiamondSword extends SmashItem{

	int damage = 100;
	Material m = Material.DIAMOND_SWORD;
	String name = "Stone Sword";
	String lore = "";
	
	public SI_DiamondSword(Arena a , ItemManager m) {
		super(a , m);
		ConfigurationSection section = m.getItemConfig("diamond_sword");
		section.addDefault("damage", 100);
		section.addDefault("material", "DIAMOND_SWORD");
		section.addDefault("name", "Diamond Sword");
		section.addDefault("lore", "");
		section.addDefault("chance", chance);
		chance = m.getItemConfig().getInt("diamond_sword.chance");
		damage = m.getItemConfig().getInt("diamond_sword.damage");
		try{
			this.m = Material.getMaterial(m.getItemConfig().getString("diamond_sword.material").toUpperCase());
		}catch(Exception ex){
			
		}
		name = m.getItemConfig().getString("diamond_sword.name");
		lore = m.getItemConfig().getString("diamond_sword.lore");
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
