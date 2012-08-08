package waterbending;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import tools.ConfigManager;
import tools.Tools;

public class IceSpike {
	
	public static ConcurrentHashMap<Integer, IceSpike> instances = new ConcurrentHashMap<Integer, IceSpike>();
	public static ConcurrentHashMap<Player, Long> removeTimers = new ConcurrentHashMap<Player, Long>();
	public static ConcurrentHashMap<String, List<Block>> changedBlocks = new ConcurrentHashMap<String, List<Block>>();
	public static final int standardheight = ConfigManager.earthColumnHeight;
	public static long removeTimer = 2000;

	private static ConcurrentHashMap<Block, Block> alreadydoneblocks = new ConcurrentHashMap<Block, Block>();
	private static ConcurrentHashMap<Block, Integer> baseblocks = new ConcurrentHashMap<Block, Integer>();

	private static int ID = Integer.MIN_VALUE;

	private static double range = 20;
	private static long cooldown = 6000;
	private static double speed = 25;
	private static final Vector direction = new Vector(0, 1, 0);

	private static long interval = (long) (1000. / speed);

	private Location origin;
	private Location location;
	private Block block;
	private Player player;
	private int damage = 8;
	private int id;
	private long time;
	private long coolingTime;
	private int height = 2;
	private Vector thrown = new Vector(0, 0.8, 0);
	private ConcurrentHashMap<Block, Block> affectedblocks = new ConcurrentHashMap<Block, Block>();

	public IceSpike(Player player) {
		try {
			this.player = player;
			block = player.getTargetBlock(null,
					(int) range);
			origin = block.getLocation();
			location = origin.clone();
		} catch (IllegalStateException e) {
			return;
		}

		loadAffectedBlocks();

		if (height != 0) {
			if (canInstantiate()) {
				id = ID;
				instances.put(id, this);
				if (ID >= Integer.MAX_VALUE) {
					ID = Integer.MIN_VALUE;
				}
				ID++;
				time = System.currentTimeMillis() - interval;
			}
		}
	}

	public IceSpike(Player player, Location origin, int damage, Vector throwing) {
		this.player = player;
		this.origin = origin;
		location = origin.clone();
		block = location.getBlock();
		this.damage = damage;
		this.thrown = throwing;

		loadAffectedBlocks();

		if (block.getType() == Material.ICE) {
			if (canInstantiate()) {
				id = ID;
				instances.put(id, this);
				if (ID >= Integer.MAX_VALUE) {
					ID = Integer.MIN_VALUE;
				}
				ID++;
				time = System.currentTimeMillis() - interval;
			}
		}
	}

	private void loadAffectedBlocks() {
		affectedblocks.clear();
		Block thisblock;
		for (int i = 0; i <= height; i++) {
			thisblock = block.getWorld().getBlockAt(
					location.clone().add(direction.clone().multiply(-i)));
			affectedblocks.put(thisblock, thisblock);
		}
	}

	private boolean blockInAffectedBlocks(Block block) {
		if (affectedblocks.containsKey(block)) {
			return true;
		}
		return false;
	}

	public static boolean blockInAllAffectedBlocks(Block block) {
		for (int ID : instances.keySet()) {
			if (instances.get(ID).blockInAffectedBlocks(block))
				return true;
		}
		return false;
	}

	public static void revertBlock(Block block) {
		for (int ID : instances.keySet()) {
			if (instances.get(ID).blockInAffectedBlocks(block)) {
				instances.get(ID).affectedblocks.remove(block);
			}
		}
	}

	private boolean canInstantiate() {
		for (Block block : affectedblocks.keySet()) {
			if (blockInAllAffectedBlocks(block)
					|| alreadydoneblocks.containsKey(block)) {
				return false;
			}
		}
		return true;
	}

	public boolean progress() {
		if (System.currentTimeMillis() - time >= interval) {
			time = System.currentTimeMillis();
			if (!moveEarth()) {
				cooldown = System.currentTimeMillis();
				removeTimers.put(player, System.currentTimeMillis());
				instances.remove(id);
				baseblocks.put(
						location.clone()
								.add(direction.clone().multiply(
										-1 * (height))).getBlock(),
						(height - 1));
				for (Block block : affectedblocks.keySet()) {
					alreadydoneblocks.put(block, block);
					//if (!blockIsBase(block))
					//	block.setType(Material.AIR);
				}

				return false;
			}
		}
		return true;
	}

	private boolean moveEarth() {
		List<Block> tempblocks;
		if (changedBlocks.contains(player.getName())){
			tempblocks = changedBlocks.get(player.getName());
		} else {
			tempblocks = new ArrayList<Block>();
		}
		Block affectedblock = location.clone().add(direction).getBlock();
		location = location.add(direction);
		for (Entity en : Tools.getEntitiesAroundPoint(location, 0.9)){
			if (en instanceof LivingEntity){
				LivingEntity le = (LivingEntity)en;
				le.setVelocity(thrown);//new Vector(0, 0.6, 0));
				le.damage(damage);
			}
		}
		affectedblock.setType(Material.ICE);
		tempblocks.add(affectedblock);
		changedBlocks.put(player.getName(), tempblocks);
		loadAffectedBlocks();

		if (location.distance(origin) >= height) {
			return false;
		}

		return true;
	}

	public static boolean progress(Player player) {
		return instances.get(player).progress();
	}

	public static boolean blockIsBase(Block block) {
		if (baseblocks.containsKey(block)) {
			return true;
		}
		return false;
	}

	public static void removeBlockBase(Block block) {
		if (baseblocks.containsKey(block)) {
			baseblocks.remove(block);
		}

	}

	public static void removeAll() {
		for (int ID : instances.keySet()) {
			instances.remove(ID);
		}
	}

	public static void resetBlock(Block block) {

		if (alreadydoneblocks.containsKey(block)) {
			alreadydoneblocks.remove(block);
		}

	}

	public static String getDescription() {
		return "To use, simply left-click on an earthbendable block. "
				+ "A column of earth will shoot upwards from that location. "
				+ "Anything in the way of the column will be brought up with it, "
				+ "leaving talented benders the ability to trap brainless entities up there. "
				+ "Additionally, simply sneak (default shift) looking at an earthbendable block. "
				+ "A wall of earth will shoot upwards from that location. "
				+ "Anything in the way of the wall will be brought up with it. ";
	}

}
