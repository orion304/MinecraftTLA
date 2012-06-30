package earthbending;

import java.util.concurrent.ConcurrentHashMap;

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

	private static double range = 20;
	private static int damage = ConfigManager.earthdmg;
	private static double speed = 35;
	// private static double speed = 1.5;

	private static long interval = (long) (1000. / speed);

	private Player player;
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
			if (instances.containsKey(player.getEntityId())) {
				instances.get(player.getEntityId()).cancel();
			}
			instances.put(player.getEntityId(), this);
			time = System.currentTimeMillis();
		}

	}

	public boolean prepare() {
		cancelPrevious();
		Block block = player.getTargetBlock(Tools.getTransparentEarthbending(),
				(int) range);
		if (Tools.isEarthbendable(block)) {
			sourceblock = block;
			focusBlock();
			return true;
		}
		return false;
	}

	private void cancelPrevious() {
		if (instances.containsKey(player.getEntityId())) {
			EarthBlast old = instances.get(player.getEntityId());
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
		instances.remove(player.getEntityId());
	}

	public void throwEarth() {
		if (sourceblock != null) {
			if (sourceblock.getWorld() == player.getWorld()) {
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
		if (System.currentTimeMillis() - time >= interval) {
			time = System.currentTimeMillis();

			if (!progressing && !falling
					&& Tools.getBendingAbility(player) != Abilities.EarthBlast) {
				unfocusBlock();
				return false;
			}

			if (falling) {
				location = location.clone().add(0, -1, 0);

				if (location.getBlock().getType() != Material.AIR) {
					falling = false;
					unfocusBlock();
					return false;
				}

				for (Entity entity : Tools.getEntitiesAroundPoint(location, 1)) {
					if (entity instanceof LivingEntity) {
						Tools.damageEntity(player, entity, damage);
						falling = false;
					}
				}

				if (!falling) {
					breakBlock();
					return false;
				}

				location.getBlock().setType(sourceblock.getType());
				sourceblock.setType(Material.AIR);

				sourceblock = location.getBlock();

				return true;

			} else {
				if (!progressing) {
					return false;
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

				for (Entity entity : Tools.getEntitiesAroundPoint(location, 3)) {
					if (entity instanceof LivingEntity) {
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
		sourceblock.breakNaturally();

		instances.remove(player.getEntityId());
	}

	public static void throwEarth(Player player) {
		if (instances.containsKey(player.getEntityId())) {
			instances.get(player.getEntityId()).throwEarth();
		}
	}

	public static boolean progress(int ID) {
		return instances.get(ID).progress();
	}

	public static void removeAll() {
		for (int id : instances.keySet()) {
			instances.remove(id);
		}
	}

}
