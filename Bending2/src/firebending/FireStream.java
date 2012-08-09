package firebending;

import java.util.Arrays;
import java.util.concurrent.ConcurrentHashMap;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import tools.ConfigManager;
import tools.Tools;

public class FireStream {

	public static ConcurrentHashMap<Integer, FireStream> instances = new ConcurrentHashMap<Integer, FireStream>();
	public static ConcurrentHashMap<Block, Player> ignitedblocks = new ConcurrentHashMap<Block, Player>();
	public static ConcurrentHashMap<Block, Long> ignitedtimes = new ConcurrentHashMap<Block, Long>();
	public static ConcurrentHashMap<LivingEntity, Player> ignitedentities = new ConcurrentHashMap<LivingEntity, Player>();
	// private static ConcurrentHashMap<Player, Long> timers = new
	// ConcurrentHashMap<Player, Long>();
	static final long soonesttime = Tools.timeinterval;

	public static int firedamage = 3;
	public static int tickdamage = 2;

	private static int ID = Integer.MIN_VALUE;
	private static double speed = ConfigManager.fireStreamSpeed;
	// private static double defaultrange = ConfigManager.fireStreamRange;
	private static long interval = (long) (1000. / speed);

	private static long dissipateAfter = ConfigManager.dissipateAfter;

	private Player player;
	private Location origin;
	private Location location;
	private Vector direction;
	private int id;
	private long time;
	private double range;

	//
	// public FireStream(Player player) {
	// if (timers.containsKey(player)) {
	// if (System.currentTimeMillis() < timers.get(player) + soonesttime) {
	// return;
	// }
	// }
	// range = Tools.firebendingDayAugment(defaultrange, player.getWorld());
	// this.player = player;
	// location = player.getLocation();
	// direction = player.getEyeLocation().getDirection();
	// origin = location.clone();
	// this.location = origin.clone();
	// this.direction = direction.clone();
	// this.direction.setY(0);
	// this.direction = this.direction.clone().normalize();
	// this.location = this.location.clone().add(this.direction);
	// id = ID;
	// if (ID >= Integer.MAX_VALUE) {
	// ID = Integer.MIN_VALUE;
	// }
	// ID++;
	// time = System.currentTimeMillis();
	// instances.put(id, this);
	// timers.put(player, System.currentTimeMillis());
	//
	// }

	public FireStream(Location location, Vector direction, Player player,
			int range) {
		this.range = Tools.firebendingDayAugment(range, player.getWorld());
		this.player = player;
		origin = location.clone();
		this.location = origin.clone();
		this.direction = direction.clone();
		this.direction.setY(0);
		this.direction = this.direction.clone().normalize();
		this.location = this.location.clone().add(this.direction);
		id = ID;
		if (ID >= Integer.MAX_VALUE) {
			ID = Integer.MIN_VALUE;
		}
		ID++;
		time = System.currentTimeMillis();
		instances.put(id, this);
	}

	public boolean progress() {
		if (System.currentTimeMillis() - time >= interval) {
			location = location.clone().add(direction);
			time = System.currentTimeMillis();
			if (location.distance(origin) > range) {
				remove();
				return false;
			}
			Block block = location.getBlock();
			if (isIgnitable(block)) {
				ignite(block);
				return true;
			} else if (isIgnitable(block.getRelative(BlockFace.DOWN))) {
				ignite(block.getRelative(BlockFace.DOWN));
				location = block.getRelative(BlockFace.DOWN).getLocation();
				return true;
			} else if (isIgnitable(block.getRelative(BlockFace.UP))) {
				ignite(block.getRelative(BlockFace.UP));
				location = block.getRelative(BlockFace.UP).getLocation();
				return true;
			} else {
				remove();
				return false;
			}

		}
		return false;
	}

	private void ignite(Block block) {
		block.setType(Material.FIRE);
		ignitedblocks.put(block, this.player);
		ignitedtimes.put(block, System.currentTimeMillis());
	}

	public static boolean isIgnitable(Block block) {
		Material[] overwriteable = { Material.SAPLING, Material.LONG_GRASS,
				Material.DEAD_BUSH, Material.YELLOW_FLOWER, Material.RED_ROSE,
				Material.BROWN_MUSHROOM, Material.RED_MUSHROOM, Material.FIRE,
				Material.SNOW };

		if (Arrays.asList(overwriteable).contains(block.getType())) {
			return true;
		} else if (block.getType() != Material.AIR) {
			return false;
		}

		Material[] ignitable = { Material.BEDROCK, Material.BOOKSHELF,
				Material.BRICK, Material.CLAY, Material.CLAY_BRICK,
				Material.COAL_ORE, Material.COBBLESTONE, Material.DIAMOND_ORE,
				Material.DIAMOND_BLOCK, Material.DIRT, Material.ENDER_STONE,
				Material.GLOWING_REDSTONE_ORE, Material.GOLD_BLOCK,
				Material.GRAVEL, Material.GRASS, Material.HUGE_MUSHROOM_1,
				Material.HUGE_MUSHROOM_2, Material.LAPIS_BLOCK,
				Material.LAPIS_ORE, Material.LOG, Material.MOSSY_COBBLESTONE,
				Material.MYCEL, Material.NETHER_BRICK, Material.NETHERRACK,
				Material.OBSIDIAN, Material.REDSTONE_ORE, Material.SAND,
				Material.SANDSTONE, Material.SMOOTH_BRICK, Material.STONE,
				Material.SOUL_SAND, Material.SNOW_BLOCK, Material.WOOD,
				Material.WOOL, Material.LEAVES };

		Block belowblock = block.getRelative(BlockFace.DOWN);
		if (Arrays.asList(ignitable).contains(belowblock.getType())) {
			return true;
		}

		return false;
	}

	private void remove() {
		instances.remove(id);
	}

	public static void removeAll() {
		for (Block block : ignitedblocks.keySet())
			remove(block);
	}

	public static void dissipateAll() {
		if (dissipateAfter != 0)
			for (Block block : ignitedtimes.keySet()) {
				if (block.getType() != Material.FIRE) {
					remove(block);
				} else {
					long time = ignitedtimes.get(block);
					if (System.currentTimeMillis() > time + dissipateAfter) {
						block.setType(Material.AIR);
						remove(block);
					}
				}
			}
	}

	public static boolean progress(int ID) {
		return instances.get(ID).progress();
	}

	public static String getDescription() {
		return "This ability no longer exists.";
	}

	public static void remove(Block block) {
		if (ignitedblocks.containsKey(block)) {
			ignitedblocks.remove(block);
		}
		if (ignitedtimes.containsKey(block)) {
			ignitedtimes.remove(block);
		}

	}

}
