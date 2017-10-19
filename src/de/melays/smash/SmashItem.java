package de.melays.smash;

import java.util.HashMap;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;

public class SmashItem implements Cloneable, Listener{
	
	public Arena a;
	public ItemManager m;
	
	public SmashItem (Arena a , ItemManager m) {
		this.a = a;
		this.m = m;
	}
	
	public SmashItem setDefaultAmount (ItemManager im){
		return this;
	}
	
	public void addToDefault (int a , ItemManager im){
		for (int i = 0 ; i < a ; i++){
			im.items.add(this);
		}
	}
	
	public Player p;
	
	public SmashItem setPlayer(Player p){
		this.p = p;
		return this;
	}
	
	public ItemStack getItemStack() {
		return new ItemStack(Material.WOOD_SWORD);
	}
	
	public int getHitDamage() {
		destroy();
		return 10;
	}
	
	public void onRightclick() {
		
	}
	
	public void onLeftclick() {
		
	}
	
	public void onBlockPlace(Location loc){
		
	}
	
	public void cancleActions() {
		//Nothing.
		//This function is made to cancle all schedulers on Items using
		//Repeating Tasks
	}
	
	public void store (String key , Object o){
		HashMap<String , Object> storage = new HashMap<String , Object>();
		if (m.itemstorage.containsKey(this)){
			storage = m.itemstorage.get(this);
		}
		storage.put(key, o);
		m.itemstorage.put(this, storage);
	}
	
	public Object get (String key){
		if (!m.itemstorage.containsKey(this))
			return null;
		HashMap<String , Object> storage = m.itemstorage.get(this);
		if (!storage.containsKey(key))
			return null;
		return storage.get(key);
	}
	
	public SmashItem registerEvents(){
		return this;
	}
	
	public void destroy(){
		p.getInventory().setItem(0 , new ItemStack(Material.AIR));
		m.a.data.clearInventory(p);
		SoundDebugger.playSound(p, "ITEM_BREAK", "ENTITY_ITEM_BREAK");
		m.playeritems.put(p, null);
		p.getInventory().clear();
	}

    protected Object clone() throws CloneNotSupportedException {
        return super.clone();
    }
	
}
