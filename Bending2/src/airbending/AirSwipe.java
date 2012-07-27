package airbending;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.concurrent.ConcurrentHashMap;

import main.Bending;

import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import firebending.Illumination;

import tools.AvatarState;
import tools.ConfigManager;
import tools.Tools;

public class AirSwipe {

	public static ConcurrentHashMap<Integer, AirSwipe> instances = new ConcurrentHashMap<Integer, AirSwipe>();
	private static ConcurrentHashMap<Player, Long> timers = new ConcurrentHashMap<Player, Long>();
	static final long soonesttime = ConfigManager.airSwipeCooldown;

	private static int ID = Integer.MIN_VALUE;

	private static int damage = ConfigManager.airdmg;
	private static double affectingradius = ConfigManager.airSwipeRadius;
	private static double pushfactor = ConfigManager.airSwipePush;
	private static double range = ConfigManager.airSwipeRange;
	private static int arc = ConfigManager.airSwipeArc;
	private static int stepsize = 4;
	private static double speed = ConfigManager.airSwipeSpeed;
	private static byte full = AirBlast.full;

	private double speedfactor;

	private static Integer[] breakables = { 6, 31, 32, 37, 38, 39, 40, 50, 59,
			81, 83, 106 };

	private Location origin;
	private Player player;
	private int id;
	private ConcurrentHashMap<Vector, Location> elements = new ConcurrentHashMap<Vector, Location>();
	private ArrayList<Entity> affectedentities = new ArrayList<Entity>();

	public AirSwipe(Player player) {
		if (timers.containsKey(player)) {
			if (System.currentTimeMillis() < timers.get(player) + soonesttime) {
				return;
			}
		}
		if (player.getEyeLocation().getBlock().isLiquid()) {
			return;
		}
		this.player = player;
		origin = player.getEyeLocation();
		instances.put(id, this);

		for (int i = -arc; i <= arc; i += stepsize) {
			double angle = Math.toRadians((double) i);
			Vector direction = player.getEyeLocation().getDirection().clone();

			double x, z, vx, vz;
			x = direction.getX();
			z = direction.getZ();

			vx = x * Math.cos(angle) - z * Math.sin(angle);
			vz = x * Math.sin(angle) + z * Math.cos(angle);

			direction.setX(vx);
			direction.setZ(vz);

			elements.put(direction, origin);
		}

		if (ID == Integer.MAX_VALUE) {
			ID = Integer.MIN_VALUE;
		}
		ID++;
		timers.put(player, System.currentTimeMillis());
	}

	public boolean progress() {
		if (player.isDead() || !player.isOnline()) {
			instances.remove(id);
			return false;
		}
		speedfactor = speed * (Bending.time_step / 1000.);
		if (elements.isEmpty()) {
			instances.remove(id);
			return false;
		}

		advanceSwipe();
		return true;
	}

	private void advanceSwipe() {
		affectedentities.clear();
		for (Vector direction : elements.keySet()) {
			Location location = elements.get(direction);
			if (direction != null && location != null) {
				location = location.clone().add(
						direction.clone().multiply(speedfactor));
				elements.replace(direction, location);

				if (location.distance(origin) > range) {
					elements.remove(direction);
				} else {
					Block block = location.getBlock();
					for (Block testblock : Tools.getBlocksAroundPoint(location,
							affectingradius)) {
						if (testblock.getType() == Material.FIRE) {
							testblock.setType(Material.AIR);
						}
						if (isBlockBreakable(testblock) && !Illumination.blocks.containsKey(testblock)) {
							testblock.breakNaturally();
						}
					}

					if (block.getType() != Material.AIR) {
						if (block.getType() == Material.LAVA
								|| block.getType() == Material.STATIONARY_LAVA) {
							if (block.getData() == full) {
								block.setType(Material.OBSIDIAN);
							} else {
								block.setType(Material.COBBLESTONE);
							}
							elements.remove(direction);
						} else {
							elements.remove(direction);
						}
					} else {
						location.getWorld().playEffect(location, Effect.SMOKE,
								4, (int) AirBlast.range);
						affectPeople(location, direction);
					}
				}
			} else {
				elements.remove(direction);
			}

		}

		if (elements.isEmpty()) {
			instances.remove(id);
		}
	}

	private void affectPeople(Location location, Vector direction) {
		for (Entity entity : Tools.getEntitiesAroundPoint(location,
				affectingradius)) {
			if (entity.getEntityId() != player.getEntityId()) {
				if (AvatarState.isAvatarState(player)) {
					entity.setVelocity(direction.multiply(AvatarState
							.getValue(pushfactor)));
				} else {
					entity.setVelocity(direction.multiply(pushfactor));
				}

				if (entity instanceof LivingEntity
						&& !affectedentities.contains(entity)) {
					Tools.damageEntity(player, entity, damage);
					affectedentities.add(entity);
				}

				if (elements.containsKey(direction)) {
					elements.remove(direction);
				}
			}
		}
	}

	private boolean isBlockBreakable(Block block) {
		Integer id = block.getTypeId();
		if (Arrays.asList(breakables).contains(id)) {
			return true;
		}
		return false;
	}

	public static boolean progress(int ID) {
		return instances.get(ID).progress();
	}

	public static String getDescription() {
		return "To use, simply left-click in a direction. "
				+ "An arc of air will flow from you towards that direction, "
				+ "cutting and pushing back anything in its path. "
				+ "Its damage is minimal, but it still sends the message. "
				+ "This ability will extinguish fires, cool lava, and cut things like grass, "
				+ "mushrooms and flowers.";
	}

}
