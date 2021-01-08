package de.melays.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;

import de.melays.smash.Arena;
import de.melays.smash.LocationMarker;
import de.melays.smash.main;
import de.melays.smash.errors.MError;

public class Commands {

	public static boolean runCommand (CommandSender sender , Command cmd , String commandName , String[] args , main plugin){
		//Player-only commands
        if (sender instanceof Player) {
            Player p = (Player) sender;
            if (cmd.getName().equalsIgnoreCase("smash")) {
            	if (args.length == 0){
            		sendHelpMessage(sender , plugin.mf.getMessage("prefix", true));
            		return true;
            	}
            	//User Commands
            	else if (args[0].equalsIgnoreCase("join")){
                	if (args.length != 2){
            			p.sendMessage(plugin.mf.getMessage("usage", true).replace("%command%", "/smash join <arena>"));
            			return true;
                	}
                	Arena a = plugin.am.searchPlayer(p);
                	if (a != null){
            			p.sendMessage(plugin.mf.getMessage("alreadyingame", true));
            			return true;
                	}
                	a = plugin.am.getArena(args[1]);
                	if (a == null){
            			p.sendMessage(plugin.mf.getMessage("unknownarena", true));
            			return true;
                	}
                	MError error = a.join(p);
                	if (error != MError.OKAY && error != MError.OKAY_AS_SPEC){
                		if (error == MError.ARENA_FULL){
                			p.sendMessage(plugin.mf.getMessage("arenafull", true));
                			return true;
                		}
                		if (error == MError.ALREADY_INGAME || error == MError.ARENA_LOADING){
                			p.sendMessage(plugin.mf.getMessage("alreadyingame", true));
                			return true;
                		}
                	}
                	return true;
            	}
            	else if (args[0].equalsIgnoreCase("leave")){
            		if (!plugin.getConfig().getString("bungeeserver").equals("none")) {
            			String bungeeserver = plugin.getConfig().getString("bungeeserver");
            			ByteArrayDataOutput out = ByteStreams.newDataOutput();
            			out.writeUTF("Connect");
            			out.writeUTF(bungeeserver);
            			p.sendPluginMessage(plugin, "BungeeCord", out.toByteArray());
            		}
            		else {
                    	Arena a = plugin.am.searchPlayer(p);
                    	if (a == null){
                			p.sendMessage(plugin.mf.getMessage("notingame", true));
                			return true;
                    	}
                    	a.leave(p);
                    	return true;	
            		}
            	}
            	
              	else if (args[0].equalsIgnoreCase("skip")){
                	Arena a = plugin.am.searchPlayer(p);
                	if (a == null){
            			p.sendMessage(plugin.mf.getMessage("notingame", true));
            			return true;
                	}
                	a.voteSkip(p);
                	return true;	
            	}
            	
            	//Admin Commands
            	else if (args[0].equalsIgnoreCase("admin")){
            		if (!p.hasPermission("smash.admin")){
            			p.sendMessage(plugin.mf.getMessage("nopermission", true));
            			return true;
            		}
                	sendAdminMessage(sender , plugin.mf.getMessage("prefix", true));
                	return true;
            	}
            	else if (args[0].equalsIgnoreCase("create")){
            		if (!p.hasPermission("smash.admin")){
            			p.sendMessage(plugin.mf.getMessage("nopermission", true));
            			return true;
            		}
                	if (args.length != 4){
            			p.sendMessage(plugin.mf.getMessage("usage", true).replace("%command%", "/smash create <arena> <min> <max>"));
            			return true;
                	}
                	try{
                		plugin.am.createArena(args[1], Integer.parseInt(args[2]), Integer.parseInt(args[3]));
                		p.sendMessage(plugin.mf.getMessage("prefix", true) + " Created arena "+args[1]+" successfully!");
                		return true;
                	}catch (Exception ex){
                		p.sendMessage(plugin.mf.getMessage("prefix", true) + " Min and/or max players could not be read!");
                		p.sendMessage(plugin.mf.getMessage("usage", true).replace("%command%", "/smash create <arena> <min> <max>"));
                		return true;
                	}
            	}
            	else if (args[0].equalsIgnoreCase("autojoin")){
            		if (!p.hasPermission("smash.admin")){
            			p.sendMessage(plugin.mf.getMessage("nopermission", true));
            			return true;
            		}
                	if (args.length != 2){
            			p.sendMessage(plugin.mf.getMessage("usage", true).replace("%command%", "/smash autojoin <arena/none>"));
            			return true;
                	}
                	plugin.getConfig().set("autojoin", args[1]);
                	plugin.saveConfig();
                	p.sendMessage(plugin.mf.getMessage("prefix", true) + " Autojoin set to "+args[1]);
                	
            	}
            	else if (args[0].equalsIgnoreCase("setspectatorspawn")){
            		if (!p.hasPermission("smash.admin")){
            			p.sendMessage(plugin.mf.getMessage("nopermission", true));
            			return true;
            		}
                	if (args.length != 2){
            			p.sendMessage(plugin.mf.getMessage("usage", true).replace("%command%", "/smash setspectatorspawn <arena>"));
            			return true;
                	}
                	if (!plugin.am.isExisting(args[1])){
            			p.sendMessage(plugin.mf.getMessage("unknownarena", true));
            			return true;
                	}
                	plugin.am.addArenaLocation(args[1], "spectator", p.getLocation());
                	p.sendMessage(plugin.mf.getMessage("prefix", true) + " Spectatorspawn set!");
                	return true;
            	}
            	else if (args[0].equalsIgnoreCase("addgamespawn")){
            		if (!p.hasPermission("smash.admin")){
            			p.sendMessage(plugin.mf.getMessage("nopermission", true));
            			return true;
            		}
                	if (args.length != 2){
            			p.sendMessage(plugin.mf.getMessage("usage", true).replace("%command%", "/smash addgamespawn <arena>"));
            			return true;
                	}
                	if (!plugin.am.isExisting(args[1])){
            			p.sendMessage(plugin.mf.getMessage("unknownarena", true));
            			return true;
                	}
                	int i = plugin.am.addLocationCounting(args[1], "gamelocations", p.getLocation());
                	p.sendMessage(plugin.mf.getMessage("prefix", true) + " Added gamespawn with id " + i);
                	return true;
            	}
            	else if (args[0].equalsIgnoreCase("removegamespawn")){
            		if (!p.hasPermission("smash.admin")){
            			p.sendMessage(plugin.mf.getMessage("nopermission", true));
            			return true;
            		}
                	if (args.length != 3){
            			p.sendMessage(plugin.mf.getMessage("usage", true).replace("%command%", "/smash removegamespawn <arena> <id>"));
            			return true;
                	}
                	if (!plugin.am.isExisting(args[1])){
            			p.sendMessage(plugin.mf.getMessage("unknownarena", true));
            			return true;
                	}
                	try {
                		int i = Integer.parseInt(args[2]);
                		plugin.am.removeLocationCounting(args[1], "gamelocations", i);
                		p.sendMessage(plugin.mf.getMessage("prefix", true) + " Removed gamespawn with id " + i);
                	}catch (Exception ex) {
                		p.sendMessage(plugin.mf.getMessage("prefix", true) + " Error remove gamespawn " + args[2]);
                	}
                	return true;
            	}
            	else if (args[0].equalsIgnoreCase("additemspawn")){
            		if (!p.hasPermission("smash.admin")){
            			p.sendMessage(plugin.mf.getMessage("nopermission", true));
            			return true;
            		}
                	if (args.length != 2){
            			p.sendMessage(plugin.mf.getMessage("usage", true).replace("%command%", "/smash additemspawn <arena>"));
            			return true;
                	}
                	if (!plugin.am.isExisting(args[1])){
            			p.sendMessage(plugin.mf.getMessage("unknownarena", true));
            			return true;
                	}
                	int i = plugin.am.addLocationCounting(args[1], "itemlocations", p.getLocation());
                	p.sendMessage(plugin.mf.getMessage("prefix", true) + " Added itemspawn with id " + i);
                	return true;
            	}
            	else if (args[0].equalsIgnoreCase("removeitemspawn")){
            		if (!p.hasPermission("smash.admin")){
            			p.sendMessage(plugin.mf.getMessage("nopermission", true));
            			return true;
            		}
                	if (args.length != 3){
            			p.sendMessage(plugin.mf.getMessage("usage", true).replace("%command%", "/smash removeitemspawn <arena> <id>"));
            			return true;
                	}
                	if (!plugin.am.isExisting(args[1])){
            			p.sendMessage(plugin.mf.getMessage("unknownarena", true));
            			return true;
                	}
                	try {
                		int i = Integer.parseInt(args[2]);
                		plugin.am.removeLocationCounting(args[1], "itemlocations", i);
                		p.sendMessage(plugin.mf.getMessage("prefix", true) + " Removed itemspawn with id " + i);
                	}catch (Exception ex) {
                		p.sendMessage(plugin.mf.getMessage("prefix", true) + " Error remove itemspawn " + args[2]);
                	}
                	return true;
            	}
            	else if (args[0].equalsIgnoreCase("setbackspawn")){
            		if (!p.hasPermission("smash.admin")){
            			p.sendMessage(plugin.mf.getMessage("nopermission", true));
            			return true;
            		}
                	if (args.length != 2){
            			p.sendMessage(plugin.mf.getMessage("usage", true).replace("%command%", "/smash setbackspawn <arena>"));
            			return true;
                	}
                	if (!plugin.am.isExisting(args[1])){
            			p.sendMessage(plugin.mf.getMessage("unknownarena", true));
            			return true;
                	}
                	plugin.am.addArenaLocation(args[1], "back", p.getLocation());
                	p.sendMessage(plugin.mf.getMessage("prefix", true) + " Backspawn set!");
                	return true;
            	}
            	else if (args[0].equalsIgnoreCase("setlobbyspawn")){
            		if (!p.hasPermission("smash.admin")){
            			p.sendMessage(plugin.mf.getMessage("nopermission", true));
            			return true;
            		}
                	if (args.length != 2){
            			p.sendMessage(plugin.mf.getMessage("usage", true).replace("%command%", "/smash setlobbyspawn <arena>"));
            			return true;
                	}
                	if (!plugin.am.isExisting(args[1])){
            			p.sendMessage(plugin.mf.getMessage("unknownarena", true));
            			return true;
                	}
                	plugin.am.addArenaLocation(args[1], "lobby", p.getLocation());
                	p.sendMessage(plugin.mf.getMessage("prefix", true) + " Lobbyspawn set!");
                	return true;
            	}
            	else if (args[0].equalsIgnoreCase("getmarker")){
            		if (!p.hasPermission("smash.admin")){
            			p.sendMessage(plugin.mf.getMessage("nopermission", true));
            			return true;
            		}
                	if (args.length != 1){
            			p.sendMessage(plugin.mf.getMessage("usage", true).replace("%command%", "/smash getmarker"));
            			return true;
                	}
                	if (!plugin.markers.containsKey(p)){
                		plugin.markers.put(p, new LocationMarker(p , plugin));
                	}
                	plugin.markers.get(p).giveSetupItem();
                	p.sendMessage(plugin.mf.getMessage("prefix", true) + " The markertool has been added to your inventory!");
                	return true;
            	}
            	else if (args[0].equalsIgnoreCase("savearenaarea")){
            		if (!p.hasPermission("smash.admin")){
            			p.sendMessage(plugin.mf.getMessage("nopermission", true));
            			return true;
            		}
                	if (args.length != 2){
            			p.sendMessage(plugin.mf.getMessage("usage", true).replace("%command%", "/smash savearenaarea <arena>"));
            			return true;
                	}
                	if (!plugin.am.isExisting(args[1])){
            			p.sendMessage(plugin.mf.getMessage("unknownarena", true));
            			return true;
                	}
                	if (!plugin.markers.containsKey(p)){
                    	p.sendMessage(plugin.mf.getMessage("prefix", true) + " No locations set! Use /smash getmarker!");
                    	return true;
                	}
                	if (!plugin.markers.get(p).complete()){
                    	p.sendMessage(plugin.mf.getMessage("prefix", true) + " No locations set! Use /smash getmarker!");
                    	return true;
                	}
                	plugin.markers.get(p).generatePositions(args[1]);
                	return true;
            	}
            	else if (args[0].equalsIgnoreCase("load")){
            		if (!p.hasPermission("smash.admin")){
            			p.sendMessage(plugin.mf.getMessage("nopermission", true));
            			return true;
            		}
                	if (args.length != 2){
            			p.sendMessage(plugin.mf.getMessage("usage", true).replace("%command%", "/smash load <arena>"));
            			return true;
                	}
                	Arena a = plugin.am.getArena(args[1]);
                	if (a == null){
            			MError error = plugin.am.loadArena(args[1]);
            			if (error == MError.OKAY){
            				p.sendMessage(plugin.mf.getMessage("prefix", true) + " Arena loaded successfully!");
            			}
            			else{
            				p.sendMessage(plugin.mf.getMessage("prefix", true) + " An error occured: "+error.toString());
            			}
            			return true;
                	}
                	
                	p.sendMessage(plugin.mf.getMessage("prefix", true) + " Arena already loaded!");
                	return true;
            	}
            	else{
            		sendHelpMessage(sender , plugin.mf.getMessage("prefix", true));
            		return true;
            	}
            }
        }
        else{
        	sender.sendMessage(plugin.logger_prefix + "You can only run this command as a player!");
        }
		return true;
	}
	
	public static void sendHelpMessage(CommandSender s , String prefix){
		s.sendMessage(prefix + " SMASH Plugin by MeLays @ melays.de");
		s.sendMessage(prefix + " /smash - Arguments");
		s.sendMessage(prefix + "   join <arena>");
		s.sendMessage(prefix + "   leave");
	}
	
	public static void sendAdminMessage(CommandSender s , String prefix){
		s.sendMessage(prefix + " SMASH Adminhelp");
		s.sendMessage(prefix + " /smash - Arguments");
		s.sendMessage(prefix + "   create <arena> <min> <max>");
		s.sendMessage(prefix + "   setlobbyspawn <arena>");
		s.sendMessage(prefix + "   setspectatorspawn <arena>");
		s.sendMessage(prefix + "   setbackspawn <arena>");
		s.sendMessage(prefix + "   addgamespawn <arena>");
		s.sendMessage(prefix + "   additempawn <arena>");
		s.sendMessage(prefix + "   removegamespawn <arena>");
		s.sendMessage(prefix + "   removeitemspawn <arena>");
		s.sendMessage(prefix + "   getmarker");
		s.sendMessage(prefix + "   savearenarea <arena>");
		s.sendMessage(prefix + "   load <arena>");
	}
}
