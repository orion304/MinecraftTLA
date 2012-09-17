package earthbending;

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

public class EarthBlast {

	public static ConcurrentHashMap<Integer, EarthBlast> instances = new ConcurrentHashMap<Integer, EarthBlast>();
	private static ConcurrentHashMap<Player, EarthBlast> prepared = new ConcurrentHashMap<Player, EarthBlast>();

	private static boolean hitself = ConfigManager.earthBlastHitSelf;
	private static double preparerange = ConfigManager.earthBlastPrepareRange;
	private static double range = ConfigManager.earthBlastRange;
	private static int damage = ConfigManager.earthdmg;
	private static double speed = ConfigManager.earthBlastSpeed;
	private static final double deflectrange = 3;

	private static boolean revert = ConfigManager.earthBlastRevert;
	// private static double speed = 1.5;

	private static long interval = (long) (1000. / speed);

	private static int ID = Integer.MIN_VALUE;

	private Player player;
	private int id;
	private Location location = null;
	private Block sourceblock = null;
	private Material sourcetype = null;
	private boolean progressing = false;
	private Location destination = null;
	private Vector direction = null;
	private boolean falling = false;
	private long time;

	public EarthBlast(Player player) {
		this.player = player;
		if (prepare()) {
			// if (instances.containsKey(player.getEntityId())) {
			// instances.get(player.getEntityId()).cancel();
			// }
			id = ID++;
			if (ID >= Integer.MAX_VALUE)
				ID = Integer.MIN_VALUE;
			instances.put(id, this);
			prepared.put(player, this);
			time = System.currentTimeMillis();
		}

	}

	public boolean prepare() {
		cancelPrevious();
		Block block = player.getTargetBlock(Tools.getTransparentEarthbending(),
				(int) preparerange);
		// if (Tools.isEarthbendable(player, block)) {
		// sourceblock = block;
		// focusBlock();
		// return true;
		// }
		// return false;
		block(player);
		if (Tools.isEarthbendable(player, block)) {
			sourceblock = block;
			focusBlock();
			return true;
		}
		return false;
	}

	private static Location getTargetLocation(Player player) {
		Entity target = Tools.getTargettedEntity(player, range);
		Location location;
		if (target == null) {
			location = Tools.getTargetedLocation(player, range);
		} else {
			// targetting = true;
			location = ((LivingEntity) target).getEyeLocation();
			location.setY(location.getY() - 1);
		}
		return location;
	}

	private void cancelPrevious() {
		if (prepared.containsKey(player)) {
			EarthBlast old = prepared.get(player);
			if (!old.progressing) {
				old.cancel();
			}
		}
		// if (instances.containsKey(player.getEntityId())) {
		// EarthBlast old = instances.get(player.getEntityId());
		// if (old.progressing) {
		// old.breakBlock();
		// } else {
		// old.cancel();
		// }
		// }
	}

	public void cancel() {
		unfocusBlock();
	}

	private void focusBlock() {
		if (EarthPassive.isPassiveSand(sourceblock))
			EarthPassive.revertSand(sourceblock);
		if (sourceblock.getType() == Material.SAND) {
			sourceblock.setType(Material.SANDSTONE);
			sourcetype = Material.SAND;
		} else if (sourceblock.getType() == Material.STONE) {
			sourceblock.setType(Material.COBBLESTONE);
			sourcetype = Material.STONE;
		} else {
			sourcetype = sourceblock.getType();
			sourceblock.setType(Material.STONE);
		}

		location = sourceblock.getLocation();
	}

	private void unfocusBlock() {
		sourceblock.setType(sourcetype);
		instances.remove(id);
	}

	public void throwEarth() {
		if (sourceblock != null) {
			if (sourceblock.getWorld() == player.getWorld()) {
				if (Tools.tempearthblocks.contains(sourceblock)) {
					if (!revert)
						Tools.removeEarthbendedBlockIndex(sourceblock);
				}
				Entity target = Tools.getTargettedEntity(player, range);
				if (target == null) {
					destination = player.getTargetBlock(
							Tools.getTransparentEarthbending(), (int) range)
							.getLocation();
				} else {
					destination = ((LivingEntity) target).getEyeLocation();
					destination = Tools.getPointOnLine(
							sourceblock.getLocation(), destination, range);
				}
				if (destination.distance(location) <= 1) {
					progressing = false;
					destination = null;
				} else {
					progressing = true;
					sourceblock.getWorld().playEffect(
							sourceblock.getLocation(), Effect.GHAST_SHOOT, 0,
							10);
					direction = getDirection().normalize();
					if (sourcetype != Material.SAND
							&& sourcetype != Material.GRAVEL) {
						sourceblock.setType(sourcetype);
					}
				}
			}

		}
	}

	private Vector getDirection() {
		double x1, y1, z1;
		double x0, y0, z0;

		x1 = destination.getX();
		y1 = destination.getY();
		z1 = destination.getZ();

		x0 = (double) sourceblock.getX();
		y0 = (double) sourceblock.getY();
		z0 = (double) sourceblock.getZ();

		return new Vector(x1 - x0, y1 - y0, z1 - z0);

	}

	public boolean progress() {
		if (player.isDead() || !player.isOnline()) {
			breakBlock();
			return false;
		}
		if (System.currentTimeMillis() - time >= interval) {
			time = System.currentTimeMillis();

			if (falling) {
				breakBlock();
				return false;
			}

			if (!progressing && !falling) {
				if (Tools.getBendingAbility(player) != Abilities.EarthBlast) {
					unfocusBlock();
					return false;
				}
				if (sourceblock == null) {
					instances.remove(player.getEntityId());
					return false;
				}
				if (player.getWorld() != sourceblock.getWorld()) {
					unfocusBlock();
					return false;
				}
				if (sourceblock.getLocation().distance(player.getLocation()) > preparerange) {
					unfocusBlock();
					return false;
				}
				if (sourceblock.getType() == Material.AIR) {
					instances.remove(player.getEntityId());
					return false;
				}
			}

			if (falling) {
				// location = location.clone().add(0, -1, 0);
				//
				// if (location.getBlock().getType() == Material.SNOW
				// || Tools.isPlant(location.getBlock())) {
				// Tools.breakBlock(location.getBlock());
				// } else if (location.getBlock().getType() != Material.AIR) {
				// falling = false;
				// unfocusBlock();
				// return false;
				// }
				//
				// for (Entity entity : Tools.getEntitiesAroundPoint(location,
				// 1)) {
				// if (entity instanceof LivingEntity
				// && (entity.getEntityId() != player.getEntityId() || hitself))
				// {
				// Tools.damageEntity(player, entity, damage);
				// falling = false;
				//
				// }
				// }
				//
				// if (!falling) {
				// breakBlock();
				// return false;
				// }
				//
				// if (revert) {
				// Tools.addTempEarthBlock(sourceblock, location.getBlock());
				// }
				//
				// location.getBlock().setType(sourceblock.getType());
				// sourceblock.setType(Material.AIR);
				//
				// sourceblock = location.getBlock();
				//
				// return true;
				breakBlock();

			} else {
				if (!progressing) {
					return false;
				}

				location = location.clone().add(direction);

				Tools.removeSpouts(location, player);

				Block block = location.getBlock();
				if (block.getLocation().equals(sourceblock.getLocation())) {
					location = location.clone().add(direction);
					block = location.getBlock();
				}

				if (Tools.isTransparentToEarthbending(player, block)
						&& !block.isLiquid()) {
					Tools.breakBlock(block);
				} else {
					breakBlock();
					return false;
				}

				for (Entity entity : Tools.getEntitiesAroundPoint(location, 3)) {
					if (Tools.isRegionProtectedFromBuild(player,
							Abilities.EarthBlast, entity.getLocation()))
						continue;
					if (entity instanceof LivingEntity
							&& (entity.getEntityId() != player.getEntityId() || hitself)) {
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

				if (revert) {
					Tools.addTempEarthBlock(sourceblock, block);
				}

				block.setType(sourceblock.getType());
				sourceblock.setType(Material.AIR);
				// if (block.getType() != Material.AIR) {
				// block.setType(Material.GLOWSTONE);
				// } else {
				// block.setType(Material.GLASS);
				// }
				sourceblock = block;

				if (location.distance(destination) < 1) {
					if (sourcetype == Material.SAND
							|| sourcetype == Material.GRAVEL) {
						progressing = false;
						sourceblock.setType(sourcetype);
					}

					falling = true;
					progressing = false;
				}

				return true;
			}
		}

		return false;

	}

	private void breakBlock() {
		sourceblock.setType(sourcetype);
		if (revert) {
			Tools.addTempAirBlock(sourceblock);
		} else {
			sourceblock.breakNaturally();
		}

		instances.remove(id);
	}

	public static void throwEarth(Player player) {
		if (prepared.containsKey(player)) {
			prepared.get(player).throwEarth();
			prepared.remove(player);
		}

		redirectTargettedBlasts(player);
	}

	public static boolean progress(int ID) {
		return instances.get(ID).progress();
	}

	public static void removeAll() {
		for (int id : instances.keySet()) {
			instances.get(id).breakBlock();
		}
	}

	private static void redirectTargettedBlasts(Player player) {
		for (int id : instances.keySet()) {
			EarthBlast blast = instances.get(id);

			if (!blast.progressing)
				continue;

			if (blast.player.equals(player))
				blast.redirect(player, getTargetLocation(player));

			Location location = player.getEyeLocation();
			Vector vector = location.getDirection();
			Location mloc = blast.location;
			if (mloc.distance(location) <= range
					&& Tools.getDistanceFromLine(vector, location,
							blast.location) < deflectrange
					&& mloc.distance(location.clone().add(vector)) < mloc
							.distance(location.clone().add(
									vector.clone().multiply(-1)))) {
				blast.redirect(player, getTargetLocation(player));
			}

		}
	}

	private void redirect(Player player, Location targetlocation) {
		if (progressing) {
			if (location.distance(player.getLocation()) <= range)
				direction = Tools.getDirection(location, targetlocation)
						.normalize();
			destination = targetlocation;
		}
	}

	private static void block(Player player) {
		for (int id : instances.keySet()) {
			EarthBlast blast = instances.get(id);

			if (blast.player.equals(player))
				continue;

			if (!blast.location.getWorld().equals(player.getWorld()))
				continue;

			if (!blast.location.getWorld().equals(player.getWorld()))
				continue;

			if (!blast.progressing)
				continue;

			Location location = player.getEyeLocation();
			Vector vector = location.getDirection();
			Location mloc = blast.location;
			if (mloc.distance(location) <= range
					&& Tools.getDistanceFromLine(vector, location,
							blast.location) < deflectrange
					&& mloc.distance(location.clone().add(vector)) < mloc
							.distance(location.clone().add(
									vector.clone().multiply(-1)))) {
				blast.breakBlock();
			}

		}
	}

	public static String getDescription() {
		return "To use, place your cursor over an earthbendable object (dirt, rock, ores, etc) "
				+ "and tap sneak (default: shift). The object will temporarily turn to stone, "
				+ "indicating that you have it focused as the source for your ability. "
				+ "After you have selected an origin (you no longer need to be sneaking), "
				+ "simply left-click in any direction and you will see your object launch "
				+ "off in that direction, smashing into any creature in its path. If you look "
				+ "towards a creature when you use this ability, it will target that creature. "
				+ "A collision from Earth Blast both knocks the target back and deals some damage. "
				+ "You cannot have multiple of these abilities flying at the same time.";
	}

	public static void removeAroundPoint(Location location, double radius) {

		for (int id : instances.keySet()) {
			EarthBlast blast = instances.get(id);
			if (blast.location.getWorld().equals(location.getWorld()))
				if (blast.location.distance(location) <= radius)
					blast.breakBlock();

		}

	}

}
