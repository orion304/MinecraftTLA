package airbending;

import java.util.concurrent.ConcurrentHashMap;

import main.Bending;

import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import tools.Abilities;
import tools.AvatarState;
import tools.ConfigManager;
import tools.Tools;
import waterbending.WaterSpout;

public class AirSuction {

	public static ConcurrentHashMap<Integer, AirSuction> instances = new ConcurrentHashMap<Integer, AirSuction>();
	private static ConcurrentHashMap<Player, Location> origins = new ConcurrentHashMap<Player, Location>();
	private static ConcurrentHashMap<Player, Long> timers = new ConcurrentHashMap<Player, Long>();
	static final long soonesttime = Tools.timeinterval;

	private static int ID = Integer.MIN_VALUE;
	private static final int maxticks = AirBlast.maxticks;

	private static double speed = ConfigManager.airSuctionSpeed;
	private static double range = ConfigManager.airSuctionRange;
	private static double affectingradius = ConfigManager.airSuctionRadius;
	private static double pushfactor = ConfigManager.airSuctionPush;
	private static double originselectrange = 10;
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
		if (AirSpout.getPlayers().contains(player)
				|| WaterSpout.getPlayers().contains(player))
			return;
		timers.put(player, System.currentTimeMillis());
		this.player = player;
		if (origins.containsKey(player)) {
			origin = origins.get(player);
			location = Tools.getTargetedLocation(player, range);
			origins.remove(player);
			Entity entity = Tools.getTargettedEntity(player, range);
			if (entity != null) {
				direction = Tools.getDirection(entity.getLocation(), origin)
						.normalize();
			} else {
				direction = Tools.getDirection(location, origin).normalize();
			}

		} else {
			location = Tools.getTargetedLocation(player, originselectrange,
					Tools.nonOpaque);
			origin = player.getEyeLocation();
			direction = player.getEyeLocation().getDirection().normalize()
					.multiply(-1);
		}

		id = ID;
		instances.put(id, this);
		if (ID == Integer.MAX_VALUE)
			ID = Integer.MIN_VALUE;
		ID++;
		// time = System.currentTimeMillis();
		timers.put(player, System.currentTimeMillis());
	}

	public static void setOrigin(Player player) {
		Location location = Tools.getTargetedLocation(player,
				originselectrange, Tools.nonOpaque);
		if (location.getBlock().isLiquid()
				|| Tools.isSolid(location.getBlock()))
			return;
		if (origins.containsKey(player)) {
			origins.replace(player, location);
		} else {
			origins.put(player, location);
		}
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
		location.getWorld().playEffect(location, Effect.SMOKE, 4,
				(int) AirBlast.defaultrange);
		location = location.add(direction.clone().multiply(speedfactor));
	}

	public static void progressAll() {
		for (int id : instances.keySet())
			instances.get(id).progress();
		for (Player player : origins.keySet()) {
			playOriginEffect(player);
		}
	}

	private static void playOriginEffect(Player player) {
		if (!origins.containsKey(player))
			return;
		Location origin = origins.get(player);
		if (!origin.getWorld().equals(player.getWorld())) {
			origins.remove(player);
			return;
		}

		if (Tools.getBendingAbility(player) != Abilities.AirSuction
				|| !Tools.canBend(player, Abilities.AirSuction)) {
			origins.remove(player);
			return;
		}

		if (origin.distance(player.getEyeLocation()) > originselectrange) {
			origins.remove(player);
			return;
		}

		origin.getWorld().playEffect(origin, Effect.SMOKE, 4,
				(int) originselectrange);
	}

	public static String getDescription() {
		return "To use, simply left-click in a direction. "
				+ "A gust of wind will originate as far as it can in that direction"
				+ " and flow towards you, sucking anything in its path harmlessly with it."
				+ " Skilled benders can use this technique to pull items from precarious locations. "
				+ "Additionally, tapping sneak will change the origin of your next "
				+ "AirSuction to your targeted location.";
	}

}
