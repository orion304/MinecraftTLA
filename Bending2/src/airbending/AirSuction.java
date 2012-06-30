package airbending;

import java.util.concurrent.ConcurrentHashMap;

import main.Bending;

import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import tools.AvatarState;
import tools.Tools;

public class AirSuction {

	public static ConcurrentHashMap<Integer, AirSuction> instances = new ConcurrentHashMap<Integer, AirSuction>();
	private static ConcurrentHashMap<Player, Long> timers = new ConcurrentHashMap<Player, Long>();
	static final long soonesttime = Tools.timeinterval;

	private static int ID = Integer.MIN_VALUE;
	private static final int maxticks = AirBlast.maxticks;

	private static double speed = AirBlast.speed;
	private static double range = AirBlast.range;
	private static double affectingradius = AirBlast.affectingradius;
	private static double pushfactor = AirBlast.pushfactor;
	// private static long interval = AirBlast.interval;

	private Location location;
	private Location origin;
	private Vector direction;
	private Player player;
	private int id;
	private int ticks = 0;
	// private long time;

	private double speedfactor;

	public AirSuction(Player player) {
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
		origin = player.getEyeLocation().clone();
		direction = origin.getDirection().clone().normalize().multiply(-1);
		location = Tools.getTargetedLocation(player, (int) range);

		id = ID;
		instances.put(id, this);
		if (ID == Integer.MAX_VALUE)
			ID = Integer.MIN_VALUE;
		ID++;
		// time = System.currentTimeMillis();
		timers.put(player, System.currentTimeMillis());
	}

	public boolean progress() {
		speedfactor = speed * (Bending.time_step / 1000.);

		ticks++;

		if (ticks > maxticks) {
			instances.remove(id);
			return false;
		}
		// if (player.isSneaking()
		// && Tools.getBendingAbility(player) == Abilities.AirSuction) {
		// new AirSuction(player);
		// }

		if ((location.distance(origin) > range)
				|| (location.distance(origin) <= 1)) {
			instances.remove(id);
			return false;
		}

		for (Entity entity : Tools.getEntitiesAroundPoint(location,
				affectingradius)) {
			if (entity.getEntityId() != player.getEntityId()) {
				if (AvatarState.isAvatarState(player)) {
					entity.setVelocity(direction.clone().multiply(
							AvatarState.getValue(pushfactor)));
				} else {
					entity.setVelocity(direction.clone().multiply(pushfactor));
				}
				entity.setFallDistance(0);
			}
		}

		advanceLocation();

		return true;
	}

	private void advanceLocation() {
		location.getWorld().playEffect(location, Effect.SMOKE, 1);
		location = location.add(direction.clone().multiply(speedfactor));
	}

	public static boolean progress(int ID) {
		return instances.get(ID).progress();
	}

}
