package de.melays.smash.logger;

import de.melays.smash.main;

public class Logger {
	
	main plugin;
	
	public Logger (main m){
		plugin = m;
	}
	
	LoggerLevel logger;
	
	public void log (String s, LoggerLevel l){
		System.out.println(plugin.logger_prefix + l.toString() + " " + s);
	}
	
}
