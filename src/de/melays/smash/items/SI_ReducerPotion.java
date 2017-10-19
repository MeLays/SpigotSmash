package de.melays.smash.items;

import java.util.ArrayList;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import de.melays.smash.Arena;
import de.melays.smash.ItemManager;
import de.melays.smash.SmashItem;
import de.melays.smash.SoundDebugger;

public class SI_ReducerPotion extends SmashItem{

	int damage = 1;
	Material m = Material.POTION;
	String name = "Reducer Potion";
	String lore = "";
	
	public SI_ReducerPotion(Arena a , ItemManager m) {
		super(a , m);
		ConfigurationSection section = m.getItemConfig("reducer-potion");
		section.addDefault("damage", damage);
		section.addDefault("material", this.m.toString());
		section.addDefault("name", name);
		section.addDefault("lore", "");
		section.addDefault("chance", chance);
		chance = m.getItemConfig().getInt("reducer-potion.chance");
		damage = m.getItemConfig().getInt("reducer-potion.damage");
		try{
			this.m = Material.getMaterial(m.getItemConfig().getString("reducer-potion.material").toUpperCase());
		}catch(Exception ex){
			
		}
		name = m.getItemConfig().getString("reducer-potion.name");
		lore = m.getItemConfig().getString("reducer-potion.lore");
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
		
		PotionMeta meta = (PotionMeta) stack.getItemMeta();
		meta.addCustomEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 5*20, 1), true);
		stack.setItemMeta(meta);
		
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
		a.reducermultiplier.put(p, a.reducermultiplier.get(p)-50);
		p.sendMessage(a.plugin.mf.getMessage("reduce", true).replaceAll("%p%", a.reducermultiplier.get(p)+""));
		key = Bukkit.getScheduler().scheduleSyncDelayedTask(a.plugin, new Runnable(){
			
			@Override
			public void run() {
				
				a.reducermultiplier.put(p, a.reducermultiplier.get(p)+50);
				p.sendMessage(a.plugin.mf.getMessage("reduce_over", true).replaceAll("%p%", a.reducermultiplier.get(p)+""));
				
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
