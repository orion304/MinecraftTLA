package waterbending;

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
	public ConcurrentHashMap<Player, Long> removeTimers = new ConcurrentHashMap<Player, Long>();
	public static ConcurrentHashMap<Player, Long> cooldowns = new ConcurrentHashMap<Player, Long>();
	public static final int standardheight = ConfigManager.earthColumnHeight;
	public static long removeTimer = 500;

	private static ConcurrentHashMap<Block, Block> alreadydoneblocks = new ConcurrentHashMap<Block, Block>();
	private static ConcurrentHashMap<Block, Integer> baseblocks = new ConcurrentHashMap<Block, Integer>();

	private static int ID = Integer.MIN_VALUE;

	private static double range = 20;
	private long cooldown = 6000;
	private static double speed = 25;
	private static final Vector direction = new Vector(0, 1, 0);

	private static long interval = (long) (1000. / speed);

	private Location origin;
	private Location location;
	private Block block;
	private Player player;
	private int progress = 0;
	private int damage = 8;
	private int id;
	private long time;
	private int height = 2;
	private Vector thrown = new Vector(0, 0.7, 0);
	private ConcurrentHashMap<Block, Block> affectedblocks = new ConcurrentHashMap<Block, Block>();

	public IceSpike(Player player) {
		if (cooldowns.contains(player) && cooldowns.get(player) + cooldown >= System.currentTimeMillis())
			return;
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

	public IceSpike(Player player, Location origin, int damage, Vector throwing, long aoecooldown) {
		this.cooldown = aoecooldown;
		if (cooldowns.contains(player))
				if (cooldowns.get(player) + cooldown >= System.currentTimeMillis())
					return;
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
		for (int i = 1; i <= height; i++) {
			thisblock = block.getWorld().getBlockAt(
					location.clone().add(direction.clone().multiply(i)));
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
		if (block.getType() != Material.ICE)
			return false;
		for (Block block : affectedblocks.keySet()) {
			if (blockInAllAffectedBlocks(block)
					|| alreadydoneblocks.containsKey(block)
					|| block.getType() != Material.AIR
					|| (block.getX() == player.getEyeLocation().getBlock().getX() &&
					block.getZ() == player.getEyeLocation().getBlock().getZ())){
				return false;
			}
		}
		return true;
	}

	public boolean progress() {
		if (System.currentTimeMillis() - time >= interval) {
			time = System.currentTimeMillis();
			if (progress < height){
				moveEarth();
				removeTimers.put(player, System.currentTimeMillis());
			} else {
				if (removeTimers.get(player) + removeTimer <= System.currentTimeMillis()){
					baseblocks.put(
							location.clone()
								.add(direction.clone().multiply(
										-1 * (height))).getBlock(),
										(height - 1));
					cooldowns.put(player, System.currentTimeMillis());
					if (!revertblocks()){
						instances.remove(id);
					}
				}

				return false;
			}
		}
		return true;
	}

	private boolean moveEarth() {
		progress++;
		Block affectedblock = location.clone().add(direction).getBlock();
		location = location.add(direction);
		for (Entity en : Tools.getEntitiesAroundPoint(location, 0.9)){
			if (en instanceof LivingEntity 
					&& en != player){
				LivingEntity le = (LivingEntity)en;
				le.setVelocity(thrown);
				le.damage(damage);
				Tools.verbose(damage + " Hp:" + le.getHealth());
			}
		}
		affectedblock.setType(Material.ICE);
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
	
	public boolean revertblocks(){
			Vector direction = new Vector(0, -1, 0);
			location.getBlock().setType(Material.AIR);//.clone().add(direction).getBlock().setType(Material.AIR);
			location.add(direction);
			if (blockIsBase(location.getBlock()))
				return false;
			return true;
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
