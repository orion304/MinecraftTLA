package firebending;

import java.util.concurrent.ConcurrentHashMap;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import tools.ConfigManager;
import tools.Tools;

public class ArcOfFire {

	private static ConcurrentHashMap<Player, Long> timers = new ConcurrentHashMap<Player, Long>();
	static final long soonesttime = Tools.timeinterval;

	private static int defaultarc = ConfigManager.arcOfFireArc;
	private static int defaultrange = ConfigManager.arcOfFireRange;
	private static int stepsize = 2;

	public ArcOfFire(Player player) {
		if (timers.containsKey(player)) {
			if (System.currentTimeMillis() < timers.get(player) + soonesttime) {
				return;
			}
		}
		timers.put(player, System.currentTimeMillis());
		Location location = player.getLocation();

		int arc = (int) Tools.firebendingDayAugment(defaultarc,
				player.getWorld());

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

			new FireStream(location, direction, player, defaultrange);
		}
	}

	public static String getDescription() {
		return "To use, simply left-click in any direction. "
				+ "An entire arc of fire will flow from your location, "
				+ "igniting anything in its path.";
	}

}
