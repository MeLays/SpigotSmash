package de.melays.smash;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

import de.melays.smash.logger.LoggerLevel;

public class ArenaBackup {
	
	Arena a;
	
	public ArenaBackup(Arena a){
		this.a = a;
	}
	
	ArrayList<AdvancedMaterial> blocks = new ArrayList<AdvancedMaterial>();
	
    public static ArrayList<Block> blocksFromTwoPoints(Location loc1, Location loc2)
    {
    	ArrayList<Block> blocks = new ArrayList<Block>();
 
        int topBlockX = (loc1.getBlockX() < loc2.getBlockX() ? loc2.getBlockX() : loc1.getBlockX());
        int bottomBlockX = (loc1.getBlockX() > loc2.getBlockX() ? loc2.getBlockX() : loc1.getBlockX());
 
        int topBlockY = (loc1.getBlockY() < loc2.getBlockY() ? loc2.getBlockY() : loc1.getBlockY());
        int bottomBlockY = (loc1.getBlockY() > loc2.getBlockY() ? loc2.getBlockY() : loc1.getBlockY());
 
        int topBlockZ = (loc1.getBlockZ() < loc2.getBlockZ() ? loc2.getBlockZ() : loc1.getBlockZ());
        int bottomBlockZ = (loc1.getBlockZ() > loc2.getBlockZ() ? loc2.getBlockZ() : loc1.getBlockZ());
 
        for(int x = bottomBlockX; x <= topBlockX; x++)
        {
            for(int z = bottomBlockZ; z <= topBlockZ; z++)
            {
                for(int y = bottomBlockY; y <= topBlockY; y++)
                {
                    Block block =  new Location(loc1.getWorld() , x, y, z).getBlock();
                   
                    blocks.add(block);
                }
            }
        }
       
        return blocks;
    }
	
	@SuppressWarnings("deprecation")
	public void saveArena(){
		long startTime = System.currentTimeMillis();
		a.plugin.logger.log("Storing the arena "+a.name+" ...", LoggerLevel.INFORMATION);
		blocks = new ArrayList<AdvancedMaterial>();
		ArrayList<Block> itt = blocksFromTwoPoints(a.smaller , a.bigger);
		for (Block b : itt){
			blocks.add(new AdvancedMaterial(b.getType() , b.getData() , b.getLocation()));
		}
		long endTime = System.currentTimeMillis();
		long duration = (endTime - startTime);
		a.plugin.logger.log("Done. This took a total of "+duration+" ms", LoggerLevel.INFORMATION);
	}
	
	public void restoreArena(){
		long startTime = System.currentTimeMillis();
		a.plugin.logger.log("Restoring the arena "+a.name+" ...", LoggerLevel.INFORMATION);
		ArenaBackup.setBlocksFast(this.blocks.get(0).loc.getWorld(), this.blocks);
		long endTime = System.currentTimeMillis();
		long duration = (endTime - startTime);
		a.plugin.logger.log("Done. This took a total of "+duration+" ms", LoggerLevel.INFORMATION);
	}
	
	public static Class<?> getNMSClass(String nmsClassString) throws ClassNotFoundException {
	    String version = Bukkit.getServer().getClass().getPackage().getName().replace(".", ",").split(",")[3] + ".";
	    String name = "net.minecraft.server." + version + nmsClassString;
	    Class<?> nmsClass = Class.forName(name);
	    return nmsClass;
	}
	
	public static Class<?> getBukkit(String nmsClassString) throws ClassNotFoundException {
	    String version = Bukkit.getServer().getClass().getPackage().getName().replace(".", ",").split(",")[3] + ".";
	    String name = "org.bukkit.craftbukkit." + version + nmsClassString;
	    Class<?> nmsClass = Class.forName(name);
	    return nmsClass;
	}
	
	public static Object getConnection(Player player) throws SecurityException, NoSuchMethodException, NoSuchFieldException, IllegalArgumentException, IllegalAccessException, InvocationTargetException {
	    Method getHandle = player.getClass().getMethod("getHandle");
	    Object nmsPlayer = getHandle.invoke(player);
	    Field conField = nmsPlayer.getClass().getField("playerConnection");
	    Object con = conField.get(nmsPlayer);
	    return con;
	}
	
	@SuppressWarnings("deprecation")
	public static void setBlocksFast (World world ,ArrayList<AdvancedMaterial> cluster_list) {
		try {
			//Minecraft Server World ObjectgetBukkit
			Class<?> world_class = getNMSClass("World");
			Class<?> craftworld_class = getBukkit("CraftWorld");
			Object craft_world = craftworld_class.cast(world);
			Object world_server = craft_world.getClass().getMethod("getHandle").invoke(craft_world , new Object[]{});
			
			Method getChunkAt = null;
			
			for (Method n : world_class.cast(world_server).getClass().getMethods()) {
				if (n.getName().equals("getChunkAt")) {
					getChunkAt = n;
				}
			}
			
			//BlockPosition Object
			Class<?> block_position_class = getNMSClass("BlockPosition");
			@SuppressWarnings("rawtypes")
			Constructor bp_constructor = block_position_class.getConstructor(int.class , int.class , int.class);
			
			//Minecraft Server Chunk Object
			Class<?> chunk_class = getNMSClass("Chunk");
			
			//Get IBlockData Object
			Class<?> iblockdata_class = getNMSClass("IBlockData");
			Class<?> block_class = getNMSClass("Block");
			
			for (AdvancedMaterial rl : cluster_list) {	
				
				Location loc = rl.loc;
								
				int x = loc.getBlockX();
				int y = loc.getBlockY();
				int z = loc.getBlockZ();
				
				byte data = rl.b;
				int blockId = rl.m.getId();
				
				if (!world.isChunkLoaded( x >> 4, z >> 4)) {
					world.loadChunk( x >> 4, z >> 4);
				}
				Object chunk = getChunkAt.invoke(world_server , x >> 4, z >> 4);
				Object bp = bp_constructor.newInstance(x , y , z);
				
				//Combined BlockID
				int combined = blockId + (data << 12);
				
				Method getByCombinedId = null;
				
				for (Method n : block_class.getMethods()) {
					if (n.getName().equals("getByCombinedId")) {
						getByCombinedId = n;
					}
				}
				
				Object idb_object = getByCombinedId.invoke(null , combined);
				
				Method a = chunk_class.getMethod("a", block_position_class, iblockdata_class);
				
				a.invoke(chunk, block_position_class.cast(bp) , iblockdata_class.cast(idb_object));
				world.refreshChunk(x >> 4, z >> 4);
			}
		} catch (InvocationTargetException e) {
			e.getCause().printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		} 
	}
	
}
