package earthbending;

import java.util.concurrent.ConcurrentHashMap;

import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import tools.ConfigManager;
import tools.Tools;

public class Catapult {

	public static ConcurrentHashMap<Integer, Catapult> instances = new ConcurrentHashMap<Integer, Catapult>();
	private static ConcurrentHashMap<Player, Long> timers = new ConcurrentHashMap<Player, Long>();
	static final long soonesttime = Tools.timeinterval;

	private static int length = ConfigManager.catapultLength;
	private static double speed = ConfigManager.catapultSpeed;
	private static double push = ConfigManager.catapultPush;

	private static long interval = (long) (1000. / speed);
	// private static long interval = 1500;

	private Player player;
	private Location origin;
	private Location location;
	private Vector direction;
	private int distance;
	private boolean catapult = false;
	private long time;
	private int ticks = 0;

	public Catapult(Player player) {
		if (timers.containsKey(player)) {
			if (System.currentTimeMillis() < timers.get(player) + soonesttime) {
				return;
			}
		}
		this.player = player;
		origin = player.getEyeLocation().clone();
		direction = origin.getDirection().clone().normalize();
		Vector neg = direction.clone().multiply(-1);

		Block block;
		distance = 0;
		for (int i = 0; i <= length; i++) {
			location = origin.clone().add(neg.clone().multiply((double) i));
			block = location.getBlock();
			if (Tools.isEarthbendable(block)) {
				// block.setType(Material.SANDSTONE);
				distance = Tools.getEarthbendableBlocksLength(block, neg,
						length - i);
				break;
			} else if (!Tools.isTransparentToEarthbending(block)) {
				break;
			}
		}

		// Tools.verbose(distance);

		if (distance != 0) {
			if ((double) distance >= location.distance(origin)) {
				catapult = true;
			}
			time = System.currentTimeMillis() - interval;
			// time = System.currentTimeMillis();
			instances.put(player.getEntityId(), this);
			timers.put(player, System.currentTimeMillis());
		}

	}

	public boolean progress() {
		if (player.isDead() || !player.isOnline()) {
			instances.remove(player.getEntityId());
			return false;
		}
		if (System.currentTimeMillis() - time >= interval) {
			// Tools.verbose("Catapult progressing");
			time = System.currentTimeMillis();
			if (!moveEarth()) {
				instances.remove(player.getEntityId());
				return false;
			}
		}
		return true;
	}

	private boolean moveEarth() {
		// Tools.verbose(distance);
		// Tools.verbose(direction);
		// Location loc = location.clone().add(direction);
		if (ticks > distance) {
			return false;
		} else {
			ticks++;
		}

		Tools.moveEarth(location, direction, distance);
		location = location.clone().add(direction);

		if (catapult) {
			if (location.distance(origin) < .5) {
				// if (loc.distance(origin) < .5) {
				for (Entity entity : Tools.getEntitiesAroundPoint(origin, 2)) {
					entity.setVelocity(direction.clone().multiply(
							push * distance / length));
				}
				// Tools.moveEarth(location, direction, distance);
				// location = location.clone().add(direction);
				return false;
			}
		} else {
			if (location.distance(origin) <= length - distance) {
				// if (loc.distance(origin) <= length - distance) {
				for (Entity entity : Tools.getEntitiesAroundPoint(location, 2)) {
					entity.setVelocity(direction.clone().multiply(
							push * distance / length));
				}
				// Tools.moveEarth(location, direction, distance);
				// location = location.clone().add(direction);
				return false;
			}
		}
		// Tools.moveEarth(location, direction, distance);
		// location = location.clone().add(direction);
		return true;
	}

	public static boolean progress(int ID) {
		return instances.get(ID).progress();
	}

	public static void removeAll() {
		for (int id : instances.keySet()) {
			instances.remove(id);
		}
	}
	public static String getDescription(){
		return "To use, left-click while looking in the direction you want to be launched. A pillar of earth will jut up from under you and launch you in that direction - if and only if there is enough earth behind where you're looking to launch you. Skillful use of this ability takes much time and work, and it does result in the death of certain gung-ho earthbenders. If you plan to use this ability, be sure you've read about your passive ability you innately have as an earthbender.";
	}
}
