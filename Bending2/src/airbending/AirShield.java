package airbending;

import java.util.HashMap;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import tools.Abilities;
import tools.AvatarState;
import tools.ConfigManager;
import tools.Tools;

public class AirShield {

	public static ConcurrentHashMap<Integer, AirShield> instances = new ConcurrentHashMap<Integer, AirShield>();

	private static double radius = ConfigManager.airShieldRadius;
	private static int numberOfStreams = (int) (.75*(double)radius);
	// private static double speed = 500;

	private double speedfactor;

	private Player player;
	private HashMap<Integer, Integer> angles = new HashMap<Integer, Integer>();

	public AirShield(Player player) {
		this.player = player;
		int angle = 0;
		for (int i = -(int) radius; i <= (int) radius; i += (int) radius * 2
				/ numberOfStreams) {
			angles.put(i, angle);
			angle += 90;
			if (angle == 360)
				angle = 0;
		}

		instances.put(player.getEntityId(), this);
	}

	private void rotateShield() {
		Location origin = player.getLocation();

		for (Entity entity : Tools.getEntitiesAroundPoint(origin, radius)) {
			if (origin.distance(entity.getLocation()) > 2) {
				double x, z, vx, vz, mag;
				double angle = 50;
				angle = Math.toRadians(angle);

				x = entity.getLocation().getX() - origin.getX();
				z = entity.getLocation().getZ() - origin.getZ();

				mag = Math.sqrt(x * x + z * z);

				vx = (x * Math.cos(angle) - z * Math.sin(angle)) / mag;
				vz = (x * Math.sin(angle) + z * Math.cos(angle)) / mag;

				Vector velocity = entity.getVelocity();
				if (AvatarState.isAvatarState(player)) {
					velocity.setX(AvatarState.getValue(vx));
					velocity.setZ(AvatarState.getValue(vz));
				} else {
					velocity.setX(vx);
					velocity.setZ(vz);
				}

				entity.setVelocity(velocity);
				entity.setFallDistance(0);
			}
		}

		Set<Integer> keys = angles.keySet();
		for (int i : keys) {
			double x, y, z;
			double angle = (double) angles.get(i);
			angle = Math.toRadians(angle);

			y = origin.getY() + (double) i;

			x = origin.getX() + radius * Math.cos(angle);
			z = origin.getZ() + radius * Math.sin(angle);

			Location effect = new Location(origin.getWorld(), x, y, z);
			origin.getWorld().playEffect(effect, Effect.SMOKE, 1);

			angles.put(i, angles.get(i) + (int) (10 * speedfactor));
		}

	}

	public boolean progress() {
		speedfactor = 1;
		if (!Tools.canBend(player, Abilities.AirShield)
				|| player.getEyeLocation().getBlock().isLiquid()) {
			instances.remove(player.getEntityId());
			return false;
		}
		if (((Tools.getBendingAbility(player) != Abilities.AirShield) || (!player
				.isSneaking())) && !AvatarState.isAvatarState(player)) {
			instances.remove(player.getEntityId());
			return false;
		}
		rotateShield();
		return true;
	}

	public static boolean progress(int ID) {
		return instances.get(ID).progress();
	}
}
