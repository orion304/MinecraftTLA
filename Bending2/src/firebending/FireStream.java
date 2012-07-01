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
	public static ConcurrentHashMap<LivingEntity, Player> ignitedentities = new ConcurrentHashMap<LivingEntity, Player>();
	private static ConcurrentHashMap<Player, Long> timers = new ConcurrentHashMap<Player, Long>();
	static final long soonesttime = Tools.timeinterval;

	public static int firedamage = 3;
	public static int tickdamage = 2;

	private static int ID = Integer.MIN_VALUE;
	private static double speed = ConfigManager.fireStreamSpeed;
	private static double range = ConfigManager.fireStreamRange;
	private static long interval = (long) (1000. / speed);

	private Player player;
	private Location origin;
	private Location location;
	private Vector direction;
	private int id;
	private long time;

	public FireStream(Player player) {
		if (timers.containsKey(player)) {
			if (System.currentTimeMillis() < timers.get(player) + soonesttime) {
				return;
			}
		}
		this.player = player;
		location = player.getLocation();
		direction = player.getEyeLocation().getDirection();
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
		timers.put(player, System.currentTimeMillis());

	}

	public FireStream(Location location, Vector direction, Player player) {
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
	}

	public static boolean isIgnitable(Block block) {
		Material[] overwriteable = { Material.SAPLING, Material.LONG_GRASS,
				Material.DEAD_BUSH, Material.YELLOW_FLOWER, Material.RED_ROSE,
				Material.BROWN_MUSHROOM, Material.RED_MUSHROOM, Material.FIRE };

		if (Arrays.asList(overwriteable).contains(block.getType())) {
			return true;
		} else if (block.getType() != Material.AIR) {
			return false;
		}

		Block belowblock = block.getRelative(BlockFace.DOWN);
		if (belowblock.getType() == Material.AIR || belowblock.isLiquid()) {
			return false;
		}

		return true;
	}

	private void remove() {
		instances.remove(id);
	}

	public static boolean progress(int ID) {
		return instances.get(ID).progress();
	}

}
