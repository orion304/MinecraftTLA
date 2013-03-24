package firebending;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.util.Vector;

import tools.Abilities;
import tools.AvatarState;
import tools.ConfigManager;
import tools.Tools;

public class WallOfFire {

	private static int ID = Integer.MIN_VALUE;
	private int id;
	private Player player;

	private static int range = ConfigManager.wallOfFireRange;
	private int height = ConfigManager.wallOfFireHeight;
	private int width = ConfigManager.wallOfFireWidth;
	private long duration = ConfigManager.wallOfFireDuration;
	private int damage = ConfigManager.wallOfFireDamage;
	private static long interval = ConfigManager.wallOfFireInterval;
	private static long cooldown = ConfigManager.wallOfFireCooldown;
	public static ConcurrentHashMap<Integer, WallOfFire> instances = new ConcurrentHashMap<Integer, WallOfFire>();
	private static Map<Player, Long> durations = new HashMap<Player, Long>();
	private static Map<Player, Long> intervals = new HashMap<Player, Long>();
	private static Map<Player, Location> locations = new HashMap<Player, Location>();
	private static Map<Player, Location> playerlocations = new HashMap<Player, Location>();
	private static Map<Player, Long> cooldowns = new HashMap<Player, Long>();
	private static Map<Player, List<Location>> blockslocation = new HashMap<Player, List<Location>>();
	private static Map<Entity, Long> damaged = new HashMap<Entity, Long>();
	private static long damageinterval = 2000;

	public WallOfFire(Player player) {
		if (ID >= Integer.MAX_VALUE) {
			ID = Integer.MIN_VALUE;
		}
		id = ID++;
		this.player = player;
		instances.put(id, this);
		World world = player.getWorld();
		if (cooldowns.containsKey(player)) {
			if (cooldowns.get(player) + cooldown <= System.currentTimeMillis()) {
				if (Tools.isDay(player.getWorld())) {
					width = (int) Tools.firebendingDayAugment((double) width,
							world);
					height = (int) Tools.firebendingDayAugment((double) height,
							world);
					duration = (long) Tools.firebendingDayAugment(
							(double) duration, world);
					damage = (int) Tools.firebendingDayAugment((double) damage,
							world);
				}
				WallOfFireStart(player);
			}
		} else {

			if (Tools.isDay(player.getWorld())) {
				width = (int) Tools
						.firebendingDayAugment((double) width, world);
				height = (int) Tools.firebendingDayAugment((double) height,
						world);
				duration = (long) Tools.firebendingDayAugment(
						(double) duration, world);
				damage = (int) Tools.firebendingDayAugment((double) damage,
						world);
			}
			WallOfFireStart(player);
		}
	}

	public void WallOfFireStart(Player p) {
		durations.put(p, System.currentTimeMillis());
		intervals.put(p, System.currentTimeMillis());
		Block tblock = p.getTargetBlock(null, range).getRelative(BlockFace.UP);
		Location loc = tblock.getLocation();
		locations.put(p, loc);
		playerlocations.put(p, p.getLocation());
		cooldowns.put(p, System.currentTimeMillis());
		if (tblock.getType() != Material.AIR
				&& !FireStream.isIgnitable(player, tblock)) {
			instances.remove(id);
			durations.remove(p);
			cooldowns.remove(p);
		}
		if (cooldowns.containsKey(p) && AvatarState.isAvatarState(p))
			cooldowns.remove(p);
	}

	public static void manageWallOfFire(int ID) {
		if (instances.containsKey(ID)) {
			WallOfFire wof = instances.get(ID);
			Player p = instances.get(ID).player;

			int damage = wof.damage;
			int width = wof.width;
			int height = wof.height;
			long duration = wof.duration;

			if (durations.containsKey(p)) {
				if (durations.get(p) + duration >= System.currentTimeMillis()) {

					if (intervals.containsKey(p)) {
						if (intervals.get(p) + interval <= System
								.currentTimeMillis()) {

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
								Block block = loc.getWorld().getBlockAt(
										loc.clone().add(
												orth.clone().multiply(
														(double) i)));
								if (FireStream.isIgnitable(p, block))
									block.setType(Material.AIR);
								for (int y = block.getY(); y <= block.getY()
										+ height; y++) {
									Location loca = new Location(
											block.getWorld(), block.getX(),
											(int) y, block.getZ());
									if (Tools.isRegionProtectedFromBuild(p,
											Abilities.WallOfFire, loca))
										continue;
									blocks.add(loca);
									block.getWorld().playEffect(loca,
											Effect.MOBSPAWNER_FLAMES, 1, 20);
									blockslocation.put(p, blocks);
								}
							}
						}
					}
					if (blockslocation.containsKey(p)) {
						for (Location loca : blockslocation.get(p)) {
							FireBlast.removeFireBlastsAroundPoint(loca, 2);
							for (Entity en : Tools.getEntitiesAroundPoint(
									locations.get(p), width + 2)) {
								if (en instanceof Projectile) {
									if (loca.distance(en.getLocation()) <= 3) {
										// Tools.damageEntity(p, en, damage);
										en.setVelocity(en.getVelocity()
												.normalize().setX(0).setZ(0)
												.multiply(0.1));
										en.setFireTicks(40);
									}
								}
							}
							for (Entity en : Tools.getEntitiesAroundPoint(loca,
									2)) {
								if (!damaged.containsKey(en))
									damaged.put(en, System.currentTimeMillis()
											+ damageinterval);
								if (damaged.get(en) + damageinterval <= System
										.currentTimeMillis()) {
									Tools.damageEntity(p, en, damage);
									en.setVelocity(new Vector((en.getLocation()
											.getX() - loca.getBlock()
											.getLocation().getX()) * 0.2, 0.1,
											(en.getLocation().getZ() - loca
													.getBlock().getLocation()
													.getZ()) * 0.2));
									en.setFireTicks(81);
									new Enflamed(en, p);
									damaged.put(en, System.currentTimeMillis());
								}
							}
						}
					}
				}
			}
		}
	}

	public static String getDescription() {
		return "To use this ability, click at a location. A wall of fire "
				+ "will appear at this location, igniting enemies caught in it "
				+ "and blocking projectiles.";
	}
}