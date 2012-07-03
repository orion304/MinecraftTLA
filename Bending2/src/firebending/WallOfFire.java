package firebending;

import java.util.HashMap;
import java.util.Map;



import org.bukkit.Bukkit;
import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.util.Vector;

import tools.Tools;

public class WallOfFire {
	
	private static int range = 20;
	private static int height = 3;
	private static int width = 6;
	private static long duration = 5000;
	private static int damage = 5;
	private static int interval = 400;
	public static Map<Player, Integer> instances = new HashMap<Player, Integer>();
	private static Map<Player, Long> durations = new HashMap<Player, Long>();
	private static Map<Player, Long> intervals = new HashMap<Player, Long>();
	private static Map<Player, Location> locations = new HashMap<Player, Location>();
	private static Map<Player, Location> playerlocations = new HashMap<Player, Location>();
	
	public WallOfFire(Player player) {
		int i = Integer.MIN_VALUE;
		if (i < Integer.MAX_VALUE){
		instances.put(player, i);
		i++;
		}
		WallOfFireStart(player);
	}

	public void WallOfFireStart(Player p){
		durations.put(p, System.currentTimeMillis());
		intervals.put(p, System.currentTimeMillis());
		Block tblock = p.getTargetBlock(null, range).getRelative(BlockFace.UP);
		Location loc = tblock.getLocation();
		locations.put(p, loc);
		playerlocations.put(p, p.getLocation());
		
	}
	public static void manageWallOfFire(Player p){
		if (intervals.containsKey(p)){
			if (intervals.get(p) + interval <= System.currentTimeMillis()){
		
		if (durations.containsKey(p)){
			if (durations.get(p) + duration >= System.currentTimeMillis()){
				Location loc = locations.get(p);
				Location yaw = playerlocations.get(p);
				intervals.put(p, System.currentTimeMillis());
				Vector direction = yaw.getDirection().normalize();
				double ox, oy, oz;
				ox = -direction.getZ();
				oy = 0;
				oz = direction.getX();
				Vector orth = new Vector(ox, oy, oz);
				orth = orth.normalize();
				for (int i = -width; i <= width; i++) {
					Block block = loc.getWorld().getBlockAt(loc.clone().add(
							orth.clone().multiply((double) i)));
					for (int y = block.getY(); y <= block.getY() + height; y++) {
						Location loca = new Location(block.getWorld(), block.getX(), (int)y , block.getZ());
					    block.getWorld().playEffect(loca, Effect.MOBSPAWNER_FLAMES, 1);
					    for (Entity en: Tools.getEntitiesAroundPoint(loca, 1)){
					    		Tools.damageEntity(p, en, damage);
					    		en.setVelocity(new Vector(en.getLocation().getX() - block.getLocation().getX(), 0 , en.getLocation().getZ() - block.getLocation().getZ()));
					    		en.setFireTicks(25);
					    	}
					    }
					} 
					}
				}
			}
		}
	}
		}
				
				
						
						

				
				
			
		
			
		
	

