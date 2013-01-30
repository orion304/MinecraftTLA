package waterbending;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;

import tools.Abilities;
import tools.ConfigManager;
import tools.TempPotionEffect;
import tools.Tools;

public class IceSpike {

	public static ConcurrentHashMap<Integer, IceSpike> instances = new ConcurrentHashMap<Integer, IceSpike>();
	public ConcurrentHashMap<Player, Long> removeTimers = new ConcurrentHashMap<Player, Long>();
	public static Map<Player, Long> cooldowns = new HashMap<Player, Long>();
	public static final int standardheight = ConfigManager.earthColumnHeight;
	public static long removeTimer = 500;

	private static ConcurrentHashMap<Block, Block> alreadydoneblocks = new ConcurrentHashMap<Block, Block>();
	private static ConcurrentHashMap<Block, Integer> baseblocks = new ConcurrentHashMap<Block, Integer>();

	private static int ID = Integer.MIN_VALUE;

	private static double range = ConfigManager.icespikerange;
	private long cooldown = ConfigManager.icespikecooldown;
	private static double speed = 25;
	private static final Vector direction = new Vector(0, 1, 0);

	private static long interval = (long) (1000. / speed);

	private Location origin;
	private Location location;
	private Block block;
	private Player player;
	private int progress = 0;
	private int damage = ConfigManager.icespikedamage;
	int id;
	private long time;
	int height = 2;
	private Vector thrown = new Vector(0, ConfigManager.icespikethrowingmult, 0);
	private ConcurrentHashMap<Block, Block> affectedblocks = new ConcurrentHashMap<Block, Block>();
	private List<LivingEntity> damaged = new ArrayList<LivingEntity>();

	public IceSpike(Player player) {
		if (cooldowns.containsKey(player))
			if (cooldowns.get(player) + cooldown >= System.currentTimeMillis())
				return;
		try {
			this.player = player;

			double lowestdistance = range + 1;
			Entity closestentity = null;
			for (Entity entity : Tools.getEntitiesAroundPoint(
					player.getLocation(), range)) {
				if (Tools.getDistanceFromLine(player.getLocation()
						.getDirection(), player.getLocation(), entity
						.getLocation()) <= 2
						&& (entity instanceof LivingEntity)
						&& (entity.getEntityId() != player.getEntityId())) {
					double distance = player.getLocation().distance(
							entity.getLocation());
					if (distance < lowestdistance) {
						closestentity = entity;
						lowestdistance = distance;
					}
				}
			}
			if (closestentity != null) {
				Block temptestingblock = closestentity.getLocation().getBlock()
						.getRelative(BlockFace.DOWN, 1);
				// if (temptestingblock.getType() == Material.ICE){
				this.block = temptestingblock;
				// }
			} else {
				this.block = player.getTargetBlock(null, (int) range);
			}
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
				cooldowns.put(player, System.currentTimeMillis());
			}
		}
	}

	public IceSpike(Player player, Location origin, int damage,
			Vector throwing, long aoecooldown) {
		this.cooldown = aoecooldown;
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
					|| (block.getX() == player.getEyeLocation().getBlock()
							.getX() && block.getZ() == player.getEyeLocation()
							.getBlock().getZ())) {
				return false;
			}
		}
		return true;
	}

	public boolean progress() {
		if (System.currentTimeMillis() - time >= interval) {
			time = System.currentTimeMillis();
			if (progress < height) {
				moveEarth();
				removeTimers.put(player, System.currentTimeMillis());
			} else {
				if (removeTimers.get(player) + removeTimer <= System
						.currentTimeMillis()) {
					baseblocks.put(
							location.clone()
									.add(direction.clone().multiply(
											-1 * (height))).getBlock(),
							(height - 1));
					if (!revertblocks()) {
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
		if (Tools.isRegionProtectedFromBuild(player, Abilities.IceSpike,
				location))
			return false;
		for (Entity en : Tools.getEntitiesAroundPoint(location, 1.4)) {
			if (en instanceof LivingEntity && en != player
					&& !damaged.contains(((LivingEntity) en))) {
				LivingEntity le = (LivingEntity) en;
				affect(le);
				// le.setVelocity(thrown);
				// le.damage(damage);
				// damaged.add(le);
				// Tools.verbose(damage + " Hp:" + le.getHealth());
			}
		}
		affectedblock.setType(Material.ICE);
		loadAffectedBlocks();

		if (location.distance(origin) >= height) {
			return false;
		}

		return true;
	}

	private void affect(LivingEntity entity) {
		entity.setVelocity(thrown);
		entity.damage(damage);
		damaged.add(entity);
		PotionEffect slow = new PotionEffect(PotionEffectType.SLOW, 70, 2);
		new TempPotionEffect(entity, slow);
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

	public boolean revertblocks() {
		Vector direction = new Vector(0, -1, 0);
		location.getBlock().setType(Material.AIR);// .clone().add(direction).getBlock().setType(Material.AIR);
		location.add(direction);
		if (blockIsBase(location.getBlock()))
			return false;
		return true;
	}

	public static String getDescription() {
		return "This ability has many functions. Clicking while targetting ice, or an entity over some ice, "
				+ "will raise a spike of ice up, damaging and slowing the target. Tapping sneak (shift) while"
				+ " selecting a water source will select that source that can then be fired with a click. Firing"
				+ " this will launch a spike of ice at your target, dealing a bit of damage and slowing the target. "
				+ "If you sneak (shift) while not selecting a source, many ice spikes will erupt from around you, "
				+ "damaging and slowing those targets.";
	}

}
