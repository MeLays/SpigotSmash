package de.melays.smash;

import org.bukkit.entity.Player;

import net.md_5.bungee.api.ChatColor;

public class PlayerData {
	
	Player p;
	Arena a;
	
	public PlayerData(Player p , Arena a) {
		this.p = p;
		this.a = a;
	}
	
	int damage = 0;
	int lives = 3;
	int max_lives = 3;
	
	public boolean smashing = false;
	public double velocity_last;
	
	public void updateLifes() {
		p.setMaxHealth(lives*2);
		p.setHealth(lives*2);
		updateTab();
	}
	
	public void updateTab(){
		String prefix = ChatColor.DARK_RED+"";
		for (int i = 0 ; i < lives ; i ++){
			prefix += "❤";
		}
		prefix += ChatColor.DARK_GRAY;
		for (int i = 0 ; i < max_lives-lives ; i ++){
			prefix += "❤";
		}
		ChatColor damage = ChatColor.GREEN;
		if (this.damage > 100){
			damage = ChatColor.YELLOW;
		}
		if (this.damage > 300){
			damage = ChatColor.GOLD;
		}
		if (this.damage > 500){
			damage = ChatColor.RED;
		}
		if (this.damage > 800){
			damage = ChatColor.DARK_RED;
		}
		
		ColorTabAPI.setTabStyle(p, prefix+ChatColor.GRAY+" | "+damage , ChatColor.GRAY+" | "+damage+this.damage+"%", a.getAllMembers().indexOf(p), a.getAllMembers());
	}
	
	public void updateDamage() {
		p.setExp(0);
		p.setLevel(damage);
		updateTab();
	}
	
	public boolean death() {
		lives -= 1;
		if (lives <= 0) {
			return false;
		}
		resetDamage();
		return true;
	}
	
	public void addDamage(int a) {
		if (this.a.damagemultiplier.containsKey(p) && a >= 0){
			a = (int) (a * ((double)this.a.damagemultiplier.get(p) / 100));
		}
		damage += a;
		if (damage < 0)
			damage = 0;
		updateDamage();
	}
	
	public void resetDamage() {
		damage = 0;
	}
	
	public double getMultiplikator(){
		
		double r = ((double) damage) / 100.0;
		
		if (this.a.reducermultiplier.containsKey(p)){
			r = (int) (r * ((double)this.a.reducermultiplier.get(p) / 100));
		}
		
		return r;
	}
}
