package de.melays.smash.items;

import java.util.ArrayList;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import de.melays.smash.Arena;
import de.melays.smash.ItemManager;
import de.melays.smash.SmashItem;
import de.melays.smash.SoundDebugger;

public class SI_ProtectionPotion extends SmashItem{

	int damage = 1;
	Material m = Material.POTION;
	String name = "Protection Potion";
	String lore = "";
	
	public SI_ProtectionPotion(Arena a , ItemManager m) {
		super(a , m);
		ConfigurationSection section = m.getItemConfig("protection-potion");
		section.addDefault("damage", damage);
		section.addDefault("material", this.m.toString());
		section.addDefault("name", name);
		section.addDefault("lore", "");
		section.addDefault("chance", chance);
		chance = m.getItemConfig().getInt("protection-potion.chance");
		damage = m.getItemConfig().getInt("protection-potion.damage");
		try{
			this.m = Material.getMaterial(m.getItemConfig().getString("protection-potion.material").toUpperCase());
		}catch(Exception ex){
			
		}
		name = m.getItemConfig().getString("protection-potion.name");
		lore = m.getItemConfig().getString("protection-potion.lore");
		m.saveItemConfig();
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
		return damage;
	}
	
	public void destroy(){
		p.getInventory().setItem(0 , new ItemStack(Material.AIR));
		super.m.a.data.clearInventory(p);
		SoundDebugger.playSound(p, "DRINK", "ENTITY_GENERIC_DRINK");
		super.m.playeritems.put(p, null);
	}
	
	int key = 0;
	
	public void start(){
		a.damagemultiplier.put(p, a.damagemultiplier.get(p)-25);
		p.sendMessage(a.plugin.mf.getMessage("resistance", true).replaceAll("%p%", a.damagemultiplier.get(p)+""));
		key = Bukkit.getScheduler().scheduleSyncDelayedTask(a.plugin, new Runnable(){
			
			@Override
			public void run() {
				
				a.damagemultiplier.put(p, a.damagemultiplier.get(p)+25);
				p.sendMessage(a.plugin.mf.getMessage("resistance_over", true).replaceAll("%p%", a.damagemultiplier.get(p)+""));
				
			}
			
		}, 200);
	}
	
	public void onLeftclick(){
		start();
		destroy();
	}
	
	public void onRightclick(){
		start();
		destroy();
	}
	
	public void cancleActions(){
		try {
			Bukkit.getScheduler().cancelTask(key);
			
		} catch (Exception e) {

		}
	}

}
