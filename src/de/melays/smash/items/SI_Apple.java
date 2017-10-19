package de.melays.smash.items;

import java.util.ArrayList;

import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import de.melays.smash.Arena;
import de.melays.smash.ItemManager;
import de.melays.smash.SmashItem;
import de.melays.smash.SoundDebugger;

public class SI_Apple extends SmashItem{

	int damage = 1;
	Material m = Material.APPLE;
	String name = "Apple";
	String lore = "";
	
	int refill = -10;
	
	public SI_Apple(Arena a , ItemManager m) {
		super(a , m);
		ConfigurationSection section = m.getItemConfig("apple");
		section.addDefault("damage", damage);
		section.addDefault("material", this.m.toString());
		section.addDefault("name", name);
		section.addDefault("lore", "");
		section.addDefault("refill", refill);
		damage = m.getItemConfig().getInt("apple.damage");
		refill = m.getItemConfig().getInt("apple.refill");
		section.addDefault("chance", chance);
		chance = m.getItemConfig().getInt("apple.chance");
		try{
			this.m = Material.getMaterial(m.getItemConfig().getString("apple.material").toUpperCase());
		}catch(Exception ex){
			
		}
		name = m.getItemConfig().getString("apple.name");
		lore = m.getItemConfig().getString("apple.lore");
		m.saveItemConfig();
	}
	
	int chance = 5;
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
	
	public void destroy(){
		p.getInventory().setItem(0 , new ItemStack(Material.AIR));
		super.m.a.data.clearInventory(p);
		SoundDebugger.playSound(p, "EAT", "ENTITY_ITEM_BREAK");
		super.m.playeritems.put(p, null);
	}
	
	public void onLeftclick(){
		super.a.playerdata.get(super.p).addDamage(refill);
		destroy();
	}
	
	public void onRightclick(){
		super.a.playerdata.get(super.p).addDamage(refill);
		destroy();
	}

}
