package de.melays.smash;

import java.util.ArrayList;
import java.util.HashMap;

import org.bukkit.Bukkit;
import org.bukkit.Effect;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Fireball;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPhysicsEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityChangeBlockEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.entity.EntityRegainHealthEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerToggleFlightEvent;
import org.bukkit.event.player.PlayerToggleSneakEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

public class EventListener implements Listener{

	main plugin;
	public EventListener(main m){
		this.plugin = m;
	}
	
	
	HashMap<Player, Boolean> cooldown = new HashMap<Player, Boolean>();
	
	@EventHandler
	public void onSneak(PlayerToggleSneakEvent e){
		Player p = e.getPlayer();
	    Arena a = plugin.am.searchPlayer(p);
	    if (a != null){
	    	if (!a.doublejump || a.state != GameState.INGAME || !a.players.contains(p)){
	    		return;
	    	}
	    	if ((this.cooldown.get(p) != null) && (((Boolean)this.cooldown.get(p)).booleanValue())) {
	    		p.setVelocity(new Vector(0, -5, 0));
	    	}
	    }
	}
	
	@EventHandler
	public void onFly(PlayerToggleFlightEvent e){
	    Player p = e.getPlayer();
	    Arena a = plugin.am.searchPlayer(p);
	    if (a != null){
	    	if (!a.doublejump || a.state != GameState.INGAME || !a.players.contains(p)){
	    		if (a.state == GameState.STARTING)
	    			e.setCancelled(true);
	    		return;
	    	}
	    	if (this.cooldown.get(p) == null){
	    		this.cooldown.put(p, false);
	    	}
	    	if (this.cooldown.get(p) != null || (!((Boolean)this.cooldown.get(p)).booleanValue())){
		        this.cooldown.put(p, Boolean.valueOf(true));
		        double vm = 1.6;
		        double vy = 1.0;
		        p.setVelocity(p.getLocation().getDirection().multiply(vm).setY(vy));
		        World w = p.getWorld();
		        w.playEffect(p.getLocation(), Effect.MOBSPAWNER_FLAMES, 2000);
		        p.setAllowFlight(false);
		        e.setCancelled(true);
		        new BukkitRunnable(){

					@Override
					public void run() {
						
						p.setAllowFlight(true);
						cooldown.put(p, Boolean.valueOf(false));
						
					}
		        }.runTaskLater(a.plugin, 60);
	    	}
	    }
	}
	
	@EventHandler
    public void onBlockFall(EntityChangeBlockEvent e) {
        if ((e.getEntityType() == EntityType.FALLING_BLOCK)) {
            e.setCancelled(true);
        }
    }
		
	@EventHandler
	public void onDamage(EntityDamageEvent e){
		if (e.getEntity() instanceof Player){
			Player p = (Player) e.getEntity();
			Arena a = plugin.am.searchPlayer(p);
			if (a != null) {
				if (!a.damage || a.specs.contains(p)){
					e.setCancelled(true);
				}
				else if (e.getCause() == DamageCause.FALL) {
					e.setCancelled(true);
				}
				else if (a.state == GameState.INGAME){
					if (e.getCause() == DamageCause.BLOCK_EXPLOSION || e.getCause() == DamageCause.ENTITY_EXPLOSION){
						a.playerdata.get(p).addDamage((int) (e.getDamage() * Tools.randInt(8, 17)));
					}
					a.playerdata.get(p).addDamage((int) (e.getDamage()));
					e.setDamage(0);
				}
			}
		}
	}
	
	@EventHandler
	public void onDamage(EntityDamageByEntityEvent e){
		if (e.getEntity() instanceof Player){
			Player p = (Player) e.getEntity();
			Arena a = plugin.am.searchPlayer(p);
			if (a != null) {
				if (!a.damage || a.specs.contains(p)){
					e.setCancelled(true);
				}
				else if (e.getCause() == DamageCause.FALL) {
					e.setCancelled(true);
				}
				else if (a.state == GameState.INGAME){
					if (e.getCause() == DamageCause.PROJECTILE && e.getDamager() instanceof Fireball){
						a.playerdata.get(p).addDamage((int) (e.getDamage() * Tools.randInt(8, 17)));
					}
				}
			}
		}
	}
	
	@EventHandler
	public void onRegen(EntityRegainHealthEvent e){
		if (e.getEntity() instanceof Player){
			Player p = (Player) e.getEntity();
			Arena a = plugin.am.searchPlayer(p);
			if (a != null) {
				e.setCancelled(true);
			}
		}
	}
	
	@EventHandler
	public void onInvClick(InventoryClickEvent e){
		Player p = (Player) e.getWhoClicked();
		Arena a = plugin.am.searchPlayer(p);
		if (a != null) {
			if (!a.inv_click){
				e.setCancelled(true);
			}
		}
	}
	
	@EventHandler
	public void onInvClick(PlayerDropItemEvent e){
		Player p = e.getPlayer();
		Arena a = plugin.am.searchPlayer(p);
		if (a != null) {
			if (a.im.playerDrop(p)){
				e.getItemDrop().remove();
			}
			else{
				e.setCancelled(true);
			}
		}
	}
	
	@EventHandler
	public void onInvClick(PlayerPickupItemEvent e){
		Player p = e.getPlayer();
		Arena a = plugin.am.searchPlayer(p);
		if (a != null) {
			e.setCancelled(true);
			a.im.pickUp(p, e.getItem());
		}
	}
	
	@EventHandler
	public void onPlace(BlockPlaceEvent e){
		Player p = e.getPlayer();
		Arena a = plugin.am.searchPlayer(p);
		if (a != null) {
			if (!a.build){
				e.setCancelled(true);
			}
		}
	}
	
	@EventHandler
	public void onBreak(BlockBreakEvent e){
		Player p = e.getPlayer();
		Arena a = plugin.am.searchPlayer(p);
		if (a != null) {
			if (!a.blockbreak){
				e.setCancelled(true);
			}
		}
	}
	
	@EventHandler
	public void onQuit(PlayerQuitEvent e){
		Player p = e.getPlayer();
		Arena a = plugin.am.searchPlayer(p);
		if (a != null) {
			a.leave(p);
		}
	}
	
	@EventHandler
	public void onJoin(PlayerJoinEvent e){
		new BukkitRunnable() {
			@Override
			public void run() {
				if (plugin.getConfig().isSet("autojoin")){
					String autojoin  = plugin.getConfig().getString("autojoin");
					if (!autojoin.equals("none") && !autojoin.equals("random"))
						plugin.am.getArena(autojoin).join(e.getPlayer());
					if (autojoin.equals("random")){
						if (plugin.autojoin == null)
							plugin.autojoin = (new ArrayList<Arena> (plugin.am.arenas.values())).get(Tools.randInt(0, plugin.am.arenas.values().size()-1)).name;
						for (Player p : Bukkit.getOnlinePlayers()){
							plugin.am.getArena(plugin.autojoin).join(p);
						}
					}
				}
			}
		}.runTaskLater(plugin, 5);
	}
	
	@EventHandler
	public void onFood(FoodLevelChangeEvent e){
		Player p = (Player) e.getEntity();
		Arena a = plugin.am.searchPlayer(p);
		if (a != null) {
			e.setFoodLevel(20);
		}
	}
	
	@EventHandler
	public void onChat(AsyncPlayerChatEvent e){
		Player p = e.getPlayer();
		Arena a = plugin.am.searchPlayer(p);
		if (a != null) {
			if (a.specs.contains(p)) {
				e.setCancelled(true);
				a.sendMessage(plugin.mf.getMessage("chat_spec", true)
						.replaceAll("%prefix%", plugin.ch.getPrefix(p))
						.replaceAll("%player%", p.getName())
						.replaceAll("%msg%", e.getMessage())
						.replaceAll("%suffix%", plugin.ch.getSuffix(p)));
			}
			else {
				e.setCancelled(true);
				a.sendMessage(plugin.mf.getMessage("chat", true)
						.replaceAll("%prefix%", plugin.ch.getPrefix(p))
						.replaceAll("%player%", p.getName())
						.replaceAll("%msg%", e.getMessage())
						.replaceAll("%suffix%", plugin.ch.getSuffix(p)));
			}
		}
	}
	
	@EventHandler
	public void onPhysic(BlockPhysicsEvent e){
		for (Arena a : plugin.am.arenas.values()) {
			if (a.inMap(e.getBlock().getLocation())) {
				e.setCancelled(true);
				return;
			}
		}
	}
	
	@EventHandler
	public void onInteract(PlayerInteractEvent e){
		Player p = e.getPlayer();
		Arena a = plugin.am.searchPlayer(p);
		if (a != null) {
			if (a.state != GameState.INGAME && a.players.contains(p)){
				return;
			}
			if (e.getAction() == Action.LEFT_CLICK_AIR || e.getAction() == Action.LEFT_CLICK_BLOCK)
			a.im.callLeftclick(p);
			else if (e.getAction() == Action.RIGHT_CLICK_AIR || e.getAction() == Action.RIGHT_CLICK_BLOCK){
				a.im.callRightclick(p);
			}
			if (e.getAction() == Action.RIGHT_CLICK_BLOCK){
				if (e.getClickedBlock().getType().isSolid() && e.getClickedBlock().getRelative(BlockFace.UP).getType() == Material.AIR)
				a.im.callBlockPlace(p , e.getClickedBlock().getLocation().add(0, 1, 0));
			}
			e.setCancelled(true);
		}
	}
	
	
	@SuppressWarnings("deprecation")
	@EventHandler
	public void onMove(PlayerMoveEvent e){
		Player p = e.getPlayer();
		Arena a = plugin.am.searchPlayer(p);
		if (a != null) {
			if (a.specs.contains(p)){
				if (!a.inMap(p.getLocation()))
					p.teleport(a.spectator);
				return;
			}
			if (!a.move){
				if (!(e.getFrom().getX() == e.getTo().getX() && e.getFrom().getZ() == e.getTo().getZ()))
				p.teleport(e.getFrom());
			}
			if (a.state != GameState.INGAME && a.players.contains(p)){
				return;
			}
			else if (a.state == GameState.INGAME && e.getTo().getY() < a.smaller.getY()){
				a.death(e.getPlayer());
			}
			double smash_value = Math.abs(p.getVelocity().getX()) + Math.abs(p.getVelocity().getY());
			try {
				if (a.playerdata.get(p).smashing && a.inMap(p.getLocation())){
					if (smash_value < 0.005){
						a.playerdata.get(p).smashing = false;
						SoundDebugger.playSound(p.getWorld(), p.getLocation(), "ZOMBIE_WOODBREAK", "ENTITY_ZOMBIE_BREAK_DOOR_WOOD");
						a.playerdata.get(p).addDamage(Tools.randInt(1, 30));
						for (Block b : Tools.getBlocks(p.getLocation().getBlock(), 2)){
							p.getWorld().playEffect(b.getLocation(), Effect.STEP_SOUND, b.getTypeId());
							if (Tools.randInt(1, 2) == 2){
								b.breakNaturally(new ItemStack(Material.AIR));
							}
						}
					}
					
				}
			} catch (Exception e1) {

			}
			
			a.playerdata.get(p).velocity_last = smash_value;
			
		}
	}
	
	@EventHandler
    public void onFight(EntityDamageByEntityEvent e) {
		if (e.getEntity() instanceof Player && e.getDamager() instanceof Player) {
			Player p = (Player) e.getEntity();
			Arena a = plugin.am.searchPlayer(p);
			if (a == null){
				return;
			}
			if (a.state != GameState.INGAME){
				return;
			}
			if (a.players.contains(p) && a.players.contains((Player) e.getDamager())){
				Entity en = e.getEntity();
				en.setVelocity(new Vector());
				e.setDamage(0);
				a.playerdata.get(p).addDamage(a.im.getPlayerHit((Player) e.getDamager()));
				a.playerdata.get(p).updateDamage();
				Vector v = e.getDamager().getLocation().getDirection().multiply(a.playerdata.get(p).getMultiplikator());
				v.setY(2);
				double smash_value = Math.abs(v.getX()) + Math.abs(v.getY());
				if (smash_value > 6.5){
					SoundDebugger.playSound(p.getWorld(), p.getLocation(), "SLIME_ATTACK", "BLOCK_SLIME_HIT");
					a.playerdata.get(p).smashing = true;
					a.playerdata.get(p).velocity_last = smash_value;
				}
				en.setVelocity(v);
			}
		}
	}
	
	@EventHandler
    public void onDmg(EntityDamageByEntityEvent e) {
		if (e.getEntity() instanceof Player && !(e.getDamager() instanceof Player)) {
			Player p = (Player) e.getEntity();
			Arena a = plugin.am.searchPlayer(p);
			if (a == null){
				return;
			}
			if (a.state != GameState.INGAME){
				return;
			}
			if (a.players.contains(p)){
				Entity en = e.getEntity();
				en.setVelocity(new Vector());
				a.playerdata.get(p).updateDamage();
				Vector v = e.getDamager().getLocation().getDirection().multiply(a.playerdata.get(p).getMultiplikator()/3);
				v.setY(2);
				double smash_value = Math.abs(v.getX()) + Math.abs(v.getY());
				if (smash_value > 6.5){
					SoundDebugger.playSound(p.getWorld(), p.getLocation(), "SLIME_ATTACK", "BLOCK_SLIME_HIT");
					a.playerdata.get(p).smashing = true;
					a.playerdata.get(p).velocity_last = smash_value;
				}
				en.setVelocity(v);
			}
		}
	}
	
	@EventHandler
    public void onDmg(EntityDamageEvent e) {
		if (e.getEntity() instanceof Item) {
			e.setCancelled(true);
		}
		if (e.getEntity() instanceof Player) {
			Player p = (Player) e.getEntity();
			Arena a = plugin.am.searchPlayer(p);
			if (a == null){
				return;
			}
			if (a.state != GameState.INGAME){
				return;
			}
			if (a.players.contains(p) && e.getCause() == DamageCause.BLOCK_EXPLOSION || e.getCause() == DamageCause.ENTITY_EXPLOSION){
				Entity en = e.getEntity();
				en.setVelocity(new Vector());
				a.playerdata.get(p).updateDamage();
	  		    float x = -3.0F + (float)(Math.random() * 7.0D);
	  		    float y = -4.0F + (float)(Math.random() * 9.0D);
	  		    float z = -3.0F + (float)(Math.random() * 7.0D);
	  		    Vector v = new Vector (x,y,z).multiply(a.playerdata.get(p).getMultiplikator()/3);
				double smash_value = Math.abs(v.getX()) + Math.abs(v.getY());
				if (smash_value > 6.5){
					SoundDebugger.playSound(p.getWorld(), p.getLocation(), "SLIME_ATTACK", "BLOCK_SLIME_HIT");
					a.playerdata.get(p).smashing = true;
					a.playerdata.get(p).velocity_last = smash_value;
				}
				en.setVelocity(v);
			}
		}
	}
}
