package waterbending;

import java.util.concurrent.ConcurrentHashMap;

import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import tools.Abilities;
import tools.AvatarState;
import tools.ConfigManager;
import tools.TempBlock;
import tools.Tools;

public class WaterManipulation {

	public static ConcurrentHashMap<Integer, WaterManipulation> instances = new ConcurrentHashMap<Integer, WaterManipulation>();
	public static ConcurrentHashMap<Block, Block> affectedblocks = new ConcurrentHashMap<Block, Block>();
	public static ConcurrentHashMap<Player, Integer> prepared = new ConcurrentHashMap<Player, Integer>();

	private static int ID = Integer.MIN_VALUE;

	private static final byte full = 0x0;
	private static final byte half = 0x4;

	private static double range = ConfigManager.waterManipulationRange;
	private static int defaultdamage = ConfigManager.waterdmg;
	private static double speed = ConfigManager.waterManipulationSpeed;
	// private static double speed = 1.5;

	private static long interval = (long) (1000. / speed);

	private Player player;
	private int id;
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
	// private boolean targetting = false;
	private boolean displacing = false;
	private long time;
	private int damage = defaultdamage;

	public WaterManipulation(Player player) {
		this.player = player;
		if (prepare()) {
			id = ID;
			instances.put(id, this);
			prepared.put(player, id);
			if (ID == Integer.MAX_VALUE)
				ID = Integer.MIN_VALUE;
			ID++;
			time = System.currentTimeMillis();
		}
	}

	public boolean prepare() {
		Block block = player.getTargetBlock(null, (int) range);
		if (prepared.containsKey(player)
				&& !Tools.isWaterbendable(block, player)) {
			instances.get(prepared.get(player)).displacing = true;
			instances.get(prepared.get(player)).moveWater();
		}
		cancelPrevious();
		if (Tools.isWaterbendable(block, player)) {
			sourceblock = block;
			focusBlock();
			return true;
		}
		return false;
	}

	private void cancelPrevious() {
		if (prepared.containsKey(player)) {
			WaterManipulation old = instances.get(prepared.get(player));
			if (!old.progressing) {
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
		remove(id);
	}

	public void moveWater() {
		if (sourceblock != null) {
			if (sourceblock.getWorld() == player.getWorld()) {
				Entity target = Tools.getTargettedEntity(player, range);
				if (target == null || displacing) {
					targetdestination = player.getTargetBlock(
							Tools.getTransparentEarthbending(), (int) range)
							.getLocation();
				} else {
					// targetting = true;
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
					if (Tools.isPlant(sourceblock))
						new Plantbending(sourceblock);
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

	private static void remove(int id) {
		Player player = instances.get(id).player;
		if (prepared.containsKey(player)) {
			if (prepared.get(player) == id)
				prepared.remove(player);
		}
		instances.remove(id);
	}

	public boolean progress() {
		if (player.isDead() || !player.isOnline()) {
			remove(id);
			return false;
		}
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
				// if (!Tools.isSolid(sourceblock.getRelative(BlockFace.DOWN))
				// || targetting) {
				// finalRemoveWater(sourceblock);
				// } else {
				// sourceblock.setData(full);
				// affectedblocks.remove(sourceblock);
				// }
				//
				// instances.remove(player.getEntityId());
				breakBlock();
				return false;

			} else {
				if (!progressing) {
					sourceblock.getWorld().playEffect(location, Effect.SMOKE,
							4, (int) range);
					return false;
				}

				// Tools.verbose(firstdestination);

				if (sourceblock.getLocation().distance(firstdestination) < .5) {
					settingup = false;
				}

				if (!player.isSneaking() && displacing) {
					displacing = false;
					breakBlock();
					return false;
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
				if (Tools.isTransparentToEarthbending(block)
						&& !block.isLiquid()) {
					Tools.breakBlock(block);
				} else if (block.getType() != Material.AIR) {
					breakBlock();
					return false;
				}

				if (!displacing) {
					for (Entity entity : Tools.getEntitiesAroundPoint(location,
							2)) {
						if (entity instanceof LivingEntity
								&& entity.getEntityId() != player.getEntityId()) {
							entity.setVelocity(entity.getVelocity().clone()
									.add(direction));
							if (AvatarState.isAvatarState(player))
								damage = AvatarState.getValue(damage);
							Tools.damageEntity(player, entity, (int) Tools
									.waterbendingNightAugment(damage,
											player.getWorld()));
							progressing = false;
						}
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

				if (location.distance(targetdestination) <= 1) {

					falling = true;
					progressing = false;
				}

				return true;
			}
		}

		return false;

	}

	private void breakBlock() {

		if (!Tools.isSolid(sourceblock.getRelative(BlockFace.DOWN))
				|| !displacing) {
			finalRemoveWater(sourceblock);
		} else {
			sourceblock.setData(full);
			affectedblocks.remove(sourceblock);
		}

		finalRemoveWater(sourceblock);
		remove(id);
	}

	private void reduceWater(Block block) {
		if (affectedblocks.containsKey(block)) {
			if (!Tools.adjacentToThreeOrMoreSources(block)
					&& !Tools.adjacentToAnyWater(block)) {
				block.setType(Material.WATER);
				block.setData(half);
			}
			oldwater = block;
		}
	}

	private void removeWater(Block block) {
		if (block != null) {
			if (affectedblocks.containsKey(block)) {
				if (!Tools.adjacentToThreeOrMoreSources(block)) {
					block.setType(Material.AIR);
				}
				affectedblocks.remove(block);
			}
		}
	}

	private void finalRemoveWater(Block block) {
		if (affectedblocks.containsKey(block)) {
			if (!Tools.adjacentToThreeOrMoreSources(block)
					&& !Tools.adjacentToAnyWater(block)) {
				block.setType(Material.WATER);
				block.setData(half);
				// block.setType(Material.AIR);
			}
			affectedblocks.remove(block);
		}
	}

	private static void addWater(Block block) {
		if (!affectedblocks.containsKey(block)) {
			affectedblocks.put(block, block);
		}
		if (FreezeMelt.frozenblocks.containsKey(block))
			FreezeMelt.frozenblocks.remove(block);
		block.setType(Material.WATER);
		block.setData(full);
	}

	public static void moveWater(Player player) {
		if (prepared.containsKey(player)) {
			instances.get(prepared.get(player)).moveWater();
			prepared.remove(player);
		}
	}

	public static boolean progress(int ID) {
		return instances.get(ID).progress();
	}

	public static boolean canFlowFromTo(Block from, Block to) {
		// if (to.getType() == Material.TORCH)
		// return true;
		if (affectedblocks.containsKey(to) || affectedblocks.containsKey(from)) {
			// Tools.verbose("affectedblocks");
			return false;
		}
		if (WaterSpout.affectedblocks.containsKey(to)
				|| WaterSpout.affectedblocks.containsKey(from)) {
			// Tools.verbose("waterspout");
			return false;
		}
		if (WaterWall.affectedblocks.containsKey(to)
				|| WaterWall.affectedblocks.containsKey(from)) {
			// Tools.verbose("waterwallaffectedblocks");
			return false;
		}
		if (WaterWall.wallblocks.containsKey(to)
				|| WaterWall.wallblocks.containsKey(from)) {
			// Tools.verbose("waterwallwall");
			return false;
		}
		if (Wave.isBlockWave(to) || Wave.isBlockWave(from)) {
			// Tools.verbose("wave");
			return false;
		}
		if (TempBlock.isTempBlock(to) || TempBlock.isTempBlock(from)) {
			// Tools.verbose("tempblock");
			return false;
		}
		if (Tools.adjacentToFrozenBlock(to)
				|| Tools.adjacentToFrozenBlock(from)) {
			// Tools.verbose("frozen");
			return false;
		}

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
		if (TempBlock.isTempBlock(block))
			return false;
		return true;
	}

	public static boolean canBubbleWater(Block block) {
		return canPhysicsChange(block);
	}

	public static String getDescription() {
		// TODO Auto-generated method stub
		return "To use, place your cursor over a waterbendable object and tap sneak (default: shift). "
				+ "Smoke will appear where you've selected, indicating the origin of your ability. "
				+ "After you have selected an origin, simply left-click in any direction and you will "
				+ "see your water spout off in that direction, slicing any creature in its path. "
				+ "If you look towards a creature when you use this ability, it will target that creature. "
				+ "A collision from Water Manipulation both knocks the target back and deals some damage.";
	}

}
