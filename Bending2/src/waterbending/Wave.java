package waterbending;

import java.util.ArrayList;
import java.util.concurrent.ConcurrentHashMap;

import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import tools.Abilities;
import tools.AvatarState;
import tools.Tools;

public class Wave {

	public static ConcurrentHashMap<Integer, Wave> instances = new ConcurrentHashMap<Integer, Wave>();

	private static final long interval = 30;

	// public static ConcurrentHashMap<Block, Block> affectedblocks = new
	// ConcurrentHashMap<Block, Block>();

	private static final byte full = 0x0;
	// private static final byte half = 0x4;
	private static final double defaultmaxradius = 3;
	private static final double factor = 1;
	private static final double upfactor = .2;

	private static double range = 20;
	// private static int damage = 5;
	// private static double speed = 1.5;

	private Player player;
	private Location location = null;
	private Block sourceblock = null;
	private boolean progressing = false;
	private Location targetdestination = null;
	private Vector targetdirection = null;
	private ConcurrentHashMap<Block, Block> wave = new ConcurrentHashMap<Block, Block>();
	private double radius = 1;
	private long time;
	private double maxradius = defaultmaxradius;

	public Wave(Player player) {
		this.player = player;
		if (AvatarState.isAvatarState(player)) {
			maxradius = AvatarState.getValue(radius);
		}
		if (prepare()) {
			if (instances.containsKey(player.getEntityId())) {
				instances.get(player.getEntityId()).cancel();
			}
			instances.put(player.getEntityId(), this);
			time = System.currentTimeMillis();
		}

	}

	public boolean prepare() {
		cancelPrevious();
		Block block = player.getTargetBlock(null, (int) range);
		if (Tools.isWaterbendable(block, player)) {
			sourceblock = block;
			focusBlock();
			return true;
		}
		return false;
	}

	private void cancelPrevious() {
		if (instances.containsKey(player.getEntityId())) {
			Wave old = instances.get(player.getEntityId());
			if (old.progressing) {
				old.breakBlock();
			} else {
				old.cancel();
			}
		}
	}

	public void cancel() {
		unfocusBlock();
	}

	private void focusBlock() {
		location = sourceblock.getLocation();
	}

	private void unfocusBlock() {
		instances.remove(player.getEntityId());
	}

	public void moveWater() {
		if (sourceblock != null) {
			Entity target = Tools.getTargettedEntity(player, range);
			if (target == null) {
				targetdestination = player.getTargetBlock(
						Tools.getTransparentEarthbending(), (int) range)
						.getLocation();
			} else {
				targetdestination = ((LivingEntity) target).getEyeLocation();
			}
			if (targetdestination.distance(location) <= 1) {
				progressing = false;
				targetdestination = null;
			} else {
				progressing = true;
				targetdirection = getDirection(sourceblock.getLocation(),
						targetdestination).normalize();
				targetdestination = location.clone().add(
						targetdirection.clone().multiply(range));
				addWater(sourceblock);
			}

		}
	}

	private Vector getDirection(Location location, Location destination) {
		double x1, y1, z1;
		double x0, y0, z0;

		x1 = destination.getX();
		y1 = destination.getY();
		z1 = destination.getZ();

		x0 = location.getX();
		y0 = location.getY();
		z0 = location.getZ();

		return new Vector(x1 - x0, y1 - y0, z1 - z0);

	}

	public boolean progress() {
		if (System.currentTimeMillis() - time >= interval) {
			time = System.currentTimeMillis();

			if (!progressing
					&& Tools.getBendingAbility(player) != Abilities.Wave) {
				unfocusBlock();
				return false;
			}

			if (!progressing) {
				sourceblock.getWorld().playEffect(location, Effect.SMOKE, 1);
				return false;
			}

			Vector direction = targetdirection;

			location = location.clone().add(direction);
			Block blockl = location.getBlock();

			ArrayList<Block> blocks = new ArrayList<Block>();

			if ((blockl.getType() == Material.AIR
					|| blockl.getType() == Material.FIRE
					|| Tools.isPlant(blockl) || Tools.isWaterbendable(blockl,
					player)) && blockl.getType() != Material.LEAVES) {

				for (double i = 0; i <= radius; i += .5) {
					for (double angle = 0; angle < 360; angle += 10) {
						Vector vec = Tools.getOrthogonalVector(targetdirection,
								angle, i);
						Block block = location.clone().add(vec).getBlock();
						if (!blocks.contains(block)
								&& (block.getType() == Material.AIR || block
										.getType() == Material.FIRE)) {
							blocks.add(block);
						}
						if (!blocks.contains(block)
								&& (Tools.isPlant(block) && block.getType() != Material.LEAVES)) {
							blocks.add(block);
							block.breakNaturally();
						}
					}
				}
			}

			for (Block block : wave.keySet()) {
				if (!blocks.contains(block))
					finalRemoveWater(block);
			}

			for (Block block : blocks) {
				if (!wave.containsKey(block))
					addWater(block);
			}

			if (wave.isEmpty()) {
				breakBlock();
				progressing = false;
				return false;
			}

			for (Entity entity : Tools.getEntitiesAroundPoint(location,
					2 * radius)) {
				boolean knockback = false;
				for (Block block : wave.keySet()) {
					if (entity.getLocation().distance(block.getLocation()) <= 2)
						knockback = true;
				}
				if (knockback) {
					Vector dir = direction.clone();
					dir.setY(dir.getY() * upfactor);
					entity.setVelocity(entity.getVelocity().clone()
							.add(dir.clone().multiply(factor)));
					entity.setFallDistance(0);
				}

			}

			if (!progressing) {
				breakBlock();
				return false;
			}

			if (location.distance(targetdestination) < 1) {
				progressing = false;
				breakBlock();
			}

			if (radius < maxradius)
				radius += .5;

			return true;
		}

		return false;

	}

	private void breakBlock() {
		for (Block block : wave.keySet()) {
			finalRemoveWater(block);
		}
		instances.remove(player.getEntityId());
	}

	private void finalRemoveWater(Block block) {
		if (wave.containsKey(block)) {
			// block.setType(Material.WATER);
			// block.setData(half);
			if (!Tools.adjacentToThreeOrMoreSources(block) || radius > 1) {
				block.setType(Material.AIR);
			}
			wave.remove(block);
		}
	}

	private void addWater(Block block) {
		block.setType(Material.WATER);
		block.setData(full);
		wave.put(block, block);
	}

	public static void moveWater(Player player) {
		if (instances.containsKey(player.getEntityId())) {
			instances.get(player.getEntityId()).moveWater();
		}
	}

	public static boolean progress(int ID) {
		return instances.get(ID).progress();
	}

	public static boolean isBlockWave(Block block) {
		for (int ID : instances.keySet()) {
			if (instances.get(ID).wave.containsKey(block))
				return true;
		}
		return false;
	}

	public static void launch(Player player) {
		moveWater(player);
	}

	public static void removeAll() {
		for (int id : instances.keySet()) {
			for (Block block : instances.get(id).wave.keySet()) {
				block.setType(Material.AIR);
				instances.get(id).wave.remove(block);
			}
		}
	}

}
