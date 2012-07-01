package waterbending;

import java.util.ArrayList;
import java.util.concurrent.ConcurrentHashMap;

import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import tools.Abilities;
import tools.AvatarState;
import tools.ConfigManager;
import tools.Tools;

public class WaterWall {

	public static ConcurrentHashMap<Integer, WaterWall> instances = new ConcurrentHashMap<Integer, WaterWall>();

	private static final long interval = 30;

	public static ConcurrentHashMap<Block, Block> affectedblocks = new ConcurrentHashMap<Block, Block>();
	public static ConcurrentHashMap<Block, Player> wallblocks = new ConcurrentHashMap<Block, Player>();

	private static final byte full = 0x0;
	private static final byte half = 0x4;

	private static double range = ConfigManager.waterWallRange;
	private static final double defaultradius = ConfigManager.waterWallRadius;
	// private static double speed = 1.5;

	private Player player;
	private Location location = null;
	private Block sourceblock = null;
	private Block oldwater = null;
	private boolean progressing = false;
	private Location firstdestination = null;
	private Location targetdestination = null;
	private Vector firstdirection = null;
	private Vector targetdirection = null;
	// private boolean falling = false;
	private boolean settingup = false;
	private boolean forming = false;
	private boolean frozen = false;
	private long time;
	private double radius = defaultradius;

	public WaterWall(Player player) {
		this.player = player;
		if (AvatarState.isAvatarState(player)) {
			radius = AvatarState.getValue(radius);
		}
		if (instances.containsKey(player.getEntityId())) {
			if (instances.get(player.getEntityId()).progressing) {
				freezeThaw(player);
			} else if (prepare()) {
				if (instances.containsKey(player.getEntityId())) {
					instances.get(player.getEntityId()).cancel();
				}
				// Tools.verbose("New water wall prepared");
				instances.put(player.getEntityId(), this);
				time = System.currentTimeMillis();

			}
		} else if (prepare()) {
			if (instances.containsKey(player.getEntityId())) {
				instances.get(player.getEntityId()).cancel();
			}
			// Tools.verbose("New water wall prepared");
			instances.put(player.getEntityId(), this);
			time = System.currentTimeMillis();
		}

	}

	private static void freezeThaw(Player player) {
		instances.get(player.getEntityId()).freezeThaw();
	}

	private void freezeThaw() {
		if (frozen) {
			thaw();
		} else {
			freeze();
		}
	}

	private void freeze() {
		frozen = true;
		for (Block block : wallblocks.keySet()) {
			if (wallblocks.get(block) == player) {
				block.setType(Material.ICE);
			}
		}
	}

	private void thaw() {
		frozen = false;
		for (Block block : wallblocks.keySet()) {
			if (wallblocks.get(block) == player) {
				block.setType(Material.WATER);
				block.setData(full);
			}
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
			WaterWall old = instances.get(player.getEntityId());
			if (old.progressing) {
				old.removeWater(old.sourceblock);
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
			targetdestination = player.getTargetBlock(
					Tools.getTransparentEarthbending(), (int) range)
					.getLocation();

			if (targetdestination.distance(location) <= 1) {
				progressing = false;
				targetdestination = null;
			} else {
				progressing = true;
				settingup = true;
				firstdestination = getToEyeLevel();
				firstdirection = getDirection(sourceblock.getLocation(),
						firstdestination).normalize();
				targetdirection = getDirection(firstdestination,
						targetdestination).normalize();
				addWater(sourceblock);
			}

		}
	}

	private Location getToEyeLevel() {
		Location loc = sourceblock.getLocation().clone();
		loc.setY(targetdestination.getY());
		return loc;
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
		if (!Tools.canBend(player, Abilities.WaterWall)) {
			if (!forming)
				removeWater(oldwater);
			breakBlock();
			unfocusBlock();
			return false;
		}
		if (System.currentTimeMillis() - time >= interval) {
			time = System.currentTimeMillis();

			if (!forming) {
				removeWater(oldwater);
			}

			if (!progressing
					&& Tools.getBendingAbility(player) != Abilities.WaterWall) {
				unfocusBlock();
				return false;
			}

			if (progressing
					&& (!player.isSneaking() || Tools.getBendingAbility(player) != Abilities.WaterWall)) {
				breakBlock();
				return false;
			}

			if (!progressing) {
				sourceblock.getWorld().playEffect(location, Effect.SMOKE, 1);
				return false;
			}

			if (forming) {
				ArrayList<Block> blocks = new ArrayList<Block>();
				Location loc = Tools.getTargetedLocation(player, (int) range,
						8, 9, 79);
				Vector dir = player.getEyeLocation().getDirection();
				Vector vec;
				Block block;
				for (double i = 0; i <= radius; i += 0.5) {
					for (double angle = 0; angle < 360; angle += 10) {
						// loc.getBlock().setType(Material.GLOWSTONE);
						vec = Tools.getOrthogonalVector(dir.clone(), angle, i);
						block = loc.clone().add(vec).getBlock();
						if (wallblocks.containsKey(block)) {
							blocks.add(block);
						} else if (!blocks.contains(block)
								&& (block.getType() == Material.AIR || block
										.getType() == Material.FIRE)) {
							if (frozen) {
								block.setType(Material.ICE);
							} else {
								block.setType(Material.WATER);
								block.setData(full);
							}
							// block.setType(Material.GLASS);
							blocks.add(block);
							wallblocks.put(block, player);
							// Tools.verbose(wallblocks.size());
						}
					}
				}

				for (Block blocki : wallblocks.keySet()) {
					if (wallblocks.get(blocki) == player
							&& !blocks.contains(blocki)) {
						finalRemoveWater(blocki);
					}
				}

				return true;
			}

			if (sourceblock.getLocation().distance(firstdestination) < .5) {
				settingup = false;
			}

			Vector direction;
			if (settingup) {
				direction = firstdirection;
			} else {
				direction = targetdirection;
			}

			location = location.clone().add(direction);

			Block block = location.getBlock();
			if (block.getLocation().equals(sourceblock.getLocation())) {
				location = location.clone().add(direction);
				block = location.getBlock();
			}
			if (block.getType() != Material.AIR) {
				breakBlock();
				return false;
			}

			if (!progressing) {
				breakBlock();
				return false;
			}

			addWater(block);
			reduceWater(sourceblock);
			sourceblock = block;

			if (location.distance(targetdestination) < 1) {

				removeWater(sourceblock);
				removeWater(oldwater);
				forming = true;
			}

			return true;
		}

		return false;

	}

	private void breakBlock() {
		finalRemoveWater(sourceblock);
		for (Block block : wallblocks.keySet()) {
			if (wallblocks.get(block) == player) {
				finalRemoveWater(block);
			}
		}
		instances.remove(player.getEntityId());
	}

	private void reduceWater(Block block) {
		if (affectedblocks.contains(block)) {
			if (!Tools.adjacentToThreeOrMoreSources(block)) {
				block.setType(Material.WATER);
				block.setData(half);
			}
			oldwater = block;
		}
	}

	private void removeWater(Block block) {
		if (block != null) {
			if (affectedblocks.contains(block)) {
				if (!Tools.adjacentToThreeOrMoreSources(block)) {
					block.setType(Material.AIR);
				}
				affectedblocks.remove(block);
			}
		}
	}

	private static void finalRemoveWater(Block block) {
		if (affectedblocks.containsKey(block)) {
			// block.setType(Material.WATER);
			// block.setData(half);
			if (!Tools.adjacentToThreeOrMoreSources(block)) {
				block.setType(Material.AIR);
			}
			affectedblocks.remove(block);
		}

		if (wallblocks.containsKey(block)) {
			wallblocks.remove(block);
			if (block.getType() == Material.ICE
					|| block.getType() == Material.WATER
					|| block.getType() == Material.STATIONARY_WATER) {
				block.setType(Material.AIR);
			}
			// block.setType(Material.WATER);
			// block.setData(half);
		}
	}

	private void addWater(Block block) {
		if (!affectedblocks.contains(block)) {
			affectedblocks.put(block, block);
		}
		block.setType(Material.WATER);
		block.setData(full);
	}

	public static void moveWater(Player player) {
		if (instances.containsKey(player.getEntityId())) {
			instances.get(player.getEntityId()).moveWater();
		}
	}

	public static boolean progress(int ID) {
		return instances.get(ID).progress();
	}

	public static void form(Player player) {
		moveWater(player);
	}

	public static void removeAll() {
		for (Block block : affectedblocks.keySet()) {
			if (block.getType() == Material.ICE
					|| block.getType() == Material.WATER
					|| block.getType() == Material.STATIONARY_WATER) {
				block.setType(Material.AIR);
			}
			affectedblocks.remove(block);
			wallblocks.remove(block);
		}
		for (Block block : wallblocks.keySet()) {
			if (block.getType() == Material.ICE
					|| block.getType() == Material.WATER
					|| block.getType() == Material.STATIONARY_WATER) {
				block.setType(Material.AIR);
			}
			affectedblocks.remove(block);
			wallblocks.remove(block);
		}
	}

	public static boolean canThaw(Block block) {
		if (wallblocks.keySet().contains(block))
			return false;
		return true;
	}

	public static void thaw(Block block) {
		finalRemoveWater(block);
	}

}
