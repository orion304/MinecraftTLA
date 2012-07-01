package waterbending;

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
import tools.ConfigManager;
import tools.Tools;

public class WaterManipulation {

	public static ConcurrentHashMap<Integer, WaterManipulation> instances = new ConcurrentHashMap<Integer, WaterManipulation>();
	public static ConcurrentHashMap<Block, Block> affectedblocks = new ConcurrentHashMap<Block, Block>();

	private static final byte full = 0x0;
	private static final byte half = 0x4;

	private static double range = ConfigManager.waterManipulationRange;
	private static int damage = ConfigManager.waterdmg;
	private static double speed = ConfigManager.waterManipulationSpeed;
	// private static double speed = 1.5;

	private static long interval = (long) (1000. / speed);

	private Player player;
	private Location location = null;
	private Block sourceblock = null;
	private Block oldwater = null;
	private boolean progressing = false;
	private Location firstdestination = null;
	private Location targetdestination = null;
	private Vector firstdirection = null;
	private Vector targetdirection = null;
	private boolean falling = false;
	private boolean settingup = false;
	private long time;

	public WaterManipulation(Player player) {
		this.player = player;
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
			WaterManipulation old = instances.get(player.getEntityId());
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
			if (sourceblock.getWorld() == player.getWorld()) {
				Entity target = Tools.getTargettedEntity(player, range);
				if (target == null) {
					targetdestination = player.getTargetBlock(
							Tools.getTransparentEarthbending(), (int) range)
							.getLocation();
				} else {
					targetdestination = ((LivingEntity) target)
							.getEyeLocation();
					targetdestination.setY(targetdestination.getY() - 1);
				}
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
					targetdestination = Tools.getPointOnLine(firstdestination,
							targetdestination, range);
					addWater(sourceblock);
				}
			}

		}
	}

	private Location getToEyeLevel() {
		Location loc = sourceblock.getLocation().clone();
		double dy = targetdestination.getY() - sourceblock.getY();
		if (dy <= 2) {
			loc.setY(sourceblock.getY() + 2);
		} else {
			loc.setY(targetdestination.getY() - 1);
		}
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
		if (System.currentTimeMillis() - time >= interval) {
			time = System.currentTimeMillis();

			removeWater(oldwater);

			if (!progressing
					&& !falling
					&& Tools.getBendingAbility(player) != Abilities.WaterManipulation) {
				unfocusBlock();
				return false;
			}

			if (falling) {
				// location = location.clone().add(0, -1, 0);
				//
				// if (location.getBlock().getType() != Material.AIR) {
				// falling = false;
				// unfocusBlock();
				// return false;
				// }
				//
				// for (Entity entity : Tools.getEntitiesAroundPoint(location,
				// 1)) {
				// if (entity instanceof LivingEntity) {
				// Tools.damageEntity(player, entity, damage);
				// falling = false;
				// }
				// }
				//
				// if (!falling) {
				// breakBlock();
				// return false;
				// }
				//
				// location.getBlock().setType(sourceblock.getType());
				// sourceblock.setType(Material.AIR);
				//
				// sourceblock = location.getBlock();

				finalRemoveWater(sourceblock);
				instances.remove(player.getEntityId());
				return false;

			} else {
				if (!progressing) {
					sourceblock.getWorld()
							.playEffect(location, Effect.SMOKE, 1);
					return false;
				}

				// Tools.verbose(firstdestination);

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

				for (Entity entity : Tools.getEntitiesAroundPoint(location, 2)) {
					if (entity instanceof LivingEntity
							&& entity.getEntityId() != player.getEntityId()) {
						entity.setVelocity(entity.getVelocity().clone()
								.add(direction));
						Tools.damageEntity(player, entity, damage);
						progressing = false;
					}
				}

				if (!progressing) {
					breakBlock();
					return false;
				}

				addWater(block);
				reduceWater(sourceblock);
				// if (block.getType() != Material.AIR) {
				// block.setType(Material.GLOWSTONE);
				// } else {
				// block.setType(Material.GLASS);
				// }
				sourceblock = block;

				if (location.distance(targetdestination) < 1) {

					falling = true;
					progressing = false;
				}

				return true;
			}
		}

		return false;

	}

	private void breakBlock() {
		finalRemoveWater(sourceblock);
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

	private void finalRemoveWater(Block block) {
		if (affectedblocks.contains(block)) {
			if (!Tools.adjacentToThreeOrMoreSources(block)) {
				block.setType(Material.WATER);
				block.setData(half);
			}
			affectedblocks.remove(block);
		}
	}

	private static void addWater(Block block) {
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

	public static boolean canFlowFromTo(Block from, Block to) {
		if (affectedblocks.containsKey(to) || affectedblocks.containsKey(from))
			return false;
		if (WaterSpout.affectedblocks.containsKey(to)
				|| WaterSpout.affectedblocks.containsKey(from))
			return false;
		if (WaterWall.affectedblocks.containsKey(to)
				|| WaterWall.affectedblocks.containsKey(from))
			return false;
		if (WaterWall.wallblocks.containsKey(to)
				|| WaterWall.wallblocks.containsKey(from))
			return false;
		if (Wave.isBlockWave(to) || Wave.isBlockWave(from))
			return false;
		return true;
	}

	public static boolean canPhysicsChange(Block block) {
		if (affectedblocks.containsKey(block))
			return false;
		if (WaterSpout.affectedblocks.containsKey(block))
			return false;
		if (WaterWall.affectedblocks.containsKey(block))
			return false;
		if (WaterWall.wallblocks.containsKey(block))
			return false;
		if (Wave.isBlockWave(block))
			return false;
		return true;
	}

	public static boolean canBubbleWater(Block block) {
		return canPhysicsChange(block);
	}

}
