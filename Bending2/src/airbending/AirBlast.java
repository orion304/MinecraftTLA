package airbending;

import java.util.ArrayList;
import java.util.concurrent.ConcurrentHashMap;

import main.Bending;
import net.minecraft.server.EntityHuman;

import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.craftbukkit.CraftWorld;
import org.bukkit.craftbukkit.entity.CraftPlayer;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import tools.AvatarState;
import tools.ConfigManager;
import tools.Tools;

public class AirBlast {

	public static ConcurrentHashMap<Integer, AirBlast> instances = new ConcurrentHashMap<Integer, AirBlast>();
	private static ConcurrentHashMap<Player, Long> timers = new ConcurrentHashMap<Player, Long>();
	static final long soonesttime = Tools.timeinterval;

	private static int ID = Integer.MIN_VALUE;
	static final int maxticks = 10000;

	public static double speed = ConfigManager.airBlastSpeed;
	public static double range = ConfigManager.airBlastRange;
	public static double affectingradius = ConfigManager.airBlastRadius;
	public static double pushfactor = ConfigManager.airBlastPush;
	// public static long interval = 2000;
	public static byte full = 0x0;

	private Location location;
	private Location origin;
	private Vector direction;
	private Player player;
	private int id;
	private double speedfactor;
	private int ticks = 0;

	private ArrayList<Block> affectedlevers = new ArrayList<Block>();

	// private long time;

	public AirBlast(Player player) {
		if (timers.containsKey(player)) {
			if (System.currentTimeMillis() < timers.get(player) + soonesttime) {
				return;
			}
		}
		if (player.getEyeLocation().getBlock().isLiquid()) {
			return;
		}
		timers.put(player, System.currentTimeMillis());
		this.player = player;
		location = player.getEyeLocation();
		origin = player.getEyeLocation();
		direction = player.getEyeLocation().getDirection().normalize();
		location = location.add(direction.clone());
		id = ID;
		instances.put(id, this);
		if (ID == Integer.MAX_VALUE)
			ID = Integer.MIN_VALUE;
		ID++;
		// time = System.currentTimeMillis();
		timers.put(player, System.currentTimeMillis());
	}

	public boolean progress() {
		if (player.isDead() || !player.isOnline()) {
			instances.remove(id);
			return false;
		}

		speedfactor = speed * (Bending.time_step / 1000.);

		ticks++;

		if (ticks > maxticks) {
			instances.remove(id);
			return false;
		}

		// if (player.isSneaking()
		// && Tools.getBendingAbility(player) == Abilities.AirBlast) {
		// new AirBlast(player);
		// }

		Block block = location.getBlock();
		for (Block testblock : Tools.getBlocksAroundPoint(location,
				affectingradius)) {
			if (testblock.getType() == Material.FIRE) {
				testblock.setType(Material.AIR);
				testblock.getWorld().playEffect(testblock.getLocation(),
						Effect.EXTINGUISH, 0);
			}
			if (((block.getType() == Material.LEVER) || (block.getType() == Material.STONE_BUTTON))
					&& !affectedlevers.contains(block)) {
				EntityHuman eH = ((CraftPlayer) player).getHandle();

				net.minecraft.server.Block.byId[block.getTypeId()].interact(
						((CraftWorld) block.getWorld()).getHandle(),
						block.getX(), block.getY(), block.getZ(), eH, 0, 0, 0,
						0);

				affectedlevers.add(block);
			}
		}
		if (block.getType() != Material.AIR && !affectedlevers.contains(block)) {
			if (block.getType() == Material.LAVA
					|| block.getType() == Material.STATIONARY_LAVA) {
				if (block.getData() == full) {
					block.setType(Material.OBSIDIAN);
				} else {
					block.setType(Material.COBBLESTONE);
				}
				instances.remove(id);
			}
			return false;
		}

		if (location.distance(origin) > range) {
			instances.remove(id);
			return false;
		}

		for (Entity entity : Tools.getEntitiesAroundPoint(location,
				affectingradius)) {
			affect(entity);
		}

		advanceLocation();

		return true;
	}

	private void advanceLocation() {
		location.getWorld().playEffect(location, Effect.SMOKE, 4, (int) range);
		location = location.add(direction.clone().multiply(speedfactor));
	}

	public static boolean progress(int ID) {
		return instances.get(ID).progress();
	}

	private void affect(Entity entity) {
		if (entity.getEntityId() != player.getEntityId()) {
			Vector velocity = entity.getVelocity();
			if (AvatarState.isAvatarState(player)) {
				entity.setVelocity(velocity.clone().add(
						direction.clone().multiply(
								AvatarState.getValue(pushfactor))));
			} else {
				entity.setVelocity(velocity.add(direction.clone().multiply(
						pushfactor)));
			}
			entity.setFallDistance(0);
			if (entity.getFireTicks() > 0)
				entity.getWorld().playEffect(entity.getLocation(),
						Effect.EXTINGUISH, 0);
			entity.setFireTicks(0);
		}
	}

	public static void removeAll() {
		for (int id : instances.keySet()) {
			instances.remove(id);
		}
	}

	public static String getDescription() {
		return "AirBlast is the most fundamental bending technique of an airbender."
				+ " To use, simply left-click in a direction. A gust of wind will be"
				+ " created at your fingertips, launching anything in its path harmlessly back."
				+ " A gust of air can extinguish fires on the ground or on a player, and can cool lava. "
				+ "Additionally, this ability can flip levers and activate buttons.";
	}
}
