package firebending;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.util.Vector;

import tools.Tools;

public class WallOfFire {
	
	private static int range = 20;
	private static int height = 3;
	private static int width = 6;
	private static long duration = 5000;
	private static int damage = 2;
	private static int interval = 400;
	private static int cooldown = 10000;
	public static Map<Player, Integer> instances = new HashMap<Player, Integer>();
	private static Map<Player, Long> durations = new HashMap<Player, Long>();
	private static Map<Player, Long> intervals = new HashMap<Player, Long>();
	private static Map<Player, Location> locations = new HashMap<Player, Location>();
	private static Map<Player, Location> playerlocations = new HashMap<Player, Location>();
	private static Map<Player, Long> cooldowns = new HashMap<Player, Long>();
	private static Map<Player, List<Location>> blockslocation = new HashMap<Player, List<Location>>();
	private static Map<Entity, Long> damaged = new HashMap<Entity, Long>();
	private static long damageinterval = 1000;
	
	public WallOfFire(Player player) {
		int i = Integer.MIN_VALUE;
		if (i < Integer.MAX_VALUE){
		instances.put(player, i);
		i++;
		}
		if (cooldowns.containsKey(player)){
		if (cooldowns.get(player) + cooldown <= System.currentTimeMillis()){	
			Tools.verbose("Hello " + player.getName() + "  " + System.currentTimeMillis() + "  " + (System.currentTimeMillis() + cooldown));
		WallOfFireStart(player);
		}
		} else {
			WallOfFireStart(player);
		}
	}

	public void WallOfFireStart(Player p){
				durations.put(p, System.currentTimeMillis());
				intervals.put(p, System.currentTimeMillis());
				Block tblock = p.getTargetBlock(null, range).getRelative(BlockFace.UP);
				Location loc = tblock.getLocation();
				locations.put(p, loc);
				playerlocations.put(p, p.getLocation());
				cooldowns.put(p, System.currentTimeMillis());
				if (!tblock.isEmpty() || !FireStream.isIgnitable(tblock)){
					instances.remove(p);
					durations.remove(p);
				}
	}
	public static void manageWallOfFire(Player p){
				
		if (durations.containsKey(p)){
			if (durations.get(p) + duration >= System.currentTimeMillis()){
				
				if (intervals.containsKey(p)){
					if (intervals.get(p) + interval <= System.currentTimeMillis()){
				
								List<Location> blocks = new ArrayList<Location>();
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
								blocks.add(loc);
								for (int i = -width; i <= width; i++) {
									Block block = loc.getWorld().getBlockAt(loc.clone().add(
											orth.clone().multiply((double) i)));
									if (FireStream.isIgnitable(block))
										block.setType(Material.AIR);
									for (int y = block.getY(); y <= block.getY() + height; y++) {
										Location loca = new Location(block.getWorld(), block.getX(), (int)y , block.getZ());
										blocks.add(loca);
										block.getWorld().playEffect(loca, Effect.MOBSPAWNER_FLAMES, 1);
										blockslocation.put(p, blocks);
									} 
								}
					}
				}
				if (blockslocation.containsKey(p)){
					for (Location loca: blockslocation.get(p)){
						for (Entity en: Tools.getEntitiesAroundPoint(locations.get(p), width + 2)){
							if (en instanceof Projectile){
								if (loca.distance(en.getLocation()) <= 3){
									//Tools.damageEntity(p, en, damage);
									en.setVelocity(en.getVelocity().normalize().setX(0).setZ(0).multiply(0.1));
									en.setFireTicks(40);
								}
							}
						}
						for (Entity en: Tools.getEntitiesAroundPoint(loca, 2)){
							if (!damaged.containsKey(en))
								damaged.put(en, System.currentTimeMillis() + damageinterval);
							if (damaged.get(en) + damageinterval <= System.currentTimeMillis()){
								Tools.damageEntity(p, en, damage);
								en.setVelocity(new Vector((en.getLocation().getX() - loca.getBlock().getLocation().getX()) * 0.2, 0.1 , (en.getLocation().getZ() - loca.getBlock().getLocation().getZ()) * 0.2));
								en.setFireTicks(81);
								damaged.put(en, System.currentTimeMillis());
							}
						}
					}
				}
			}
		}
	}
}