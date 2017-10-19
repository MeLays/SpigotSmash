package de.melays.smash;

import java.util.ArrayList;
import java.util.Random;

import org.bukkit.Location;
import org.bukkit.block.Block;

public class Tools {

	public static ArrayList<Block> getBlocks(Block start, int radius){
		ArrayList<Block> blocks = new ArrayList<Block>();
		for(double x = start.getLocation().getX() - radius; x <= start.getLocation().getX() + radius; x++){
			for(double y = start.getLocation().getY() - radius; y <= start.getLocation().getY() + radius; y++){
				for(double z = start.getLocation().getZ() - radius; z <= start.getLocation().getZ() + radius; z++){
					Location loc = new Location(start.getWorld(), x, y, z);
		            blocks.add(loc.getBlock());
				}
		    }
		}
		return blocks;
	}
	
	public static int randInt(int min, int max) {
	    Random rand = new Random();
	    int randomNum = rand.nextInt((max - min) + 1) + min;
	    return randomNum;
	}
	
	
}
