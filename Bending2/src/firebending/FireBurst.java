package firebending;

import java.util.concurrent.ConcurrentHashMap;

import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import tools.Abilities;
import tools.Tools;

public class FireBurst {
	private static ConcurrentHashMap<Player, FireBurst> instances = new ConcurrentHashMap<Player, FireBurst>();

	private Player player;
	private long starttime;
	private int damage = 3;
	private long chargetime = 2500;
	private double deltheta = 10;
	private double delphi = 10;
	private boolean charged = false;

	public FireBurst(Player player) {
		if (instances.containsKey(player))
			return;
		starttime = System.currentTimeMillis();
		this.player = player;
		instances.put(player, this);
	}

	public static void coneBurst(Player player) {
		if (instances.containsKey(player))
			instances.get(player).coneBurst();
	}

	private void coneBurst() {
		if (charged) {
			Location location = player.getEyeLocation();
			Vector vector = location.getDirection();
			double angle = Math.toRadians(30);
			double x, y, z;
			double r = 1;
			for (double theta = 0; theta < 180; theta += deltheta) {
				double dphi = delphi / Math.sin(Math.toRadians(theta));
				for (double phi = 0; phi < 360; phi += dphi) {
					double rphi = Math.toRadians(phi);
					double rtheta = Math.toRadians(theta);
					x = r * Math.cos(rphi) * Math.sin(rtheta);
					y = r * Math.sin(rphi) * Math.sin(rtheta);
					z = r * Math.cos(rtheta);
					Vector direction = new Vector(x, y, z);
					if (direction.angle(vector) <= angle) {
						// Tools.verbose(direction.angle(vector));
						// Tools.verbose(direction);
						new FireBlast(location, direction.normalize(), player,
								damage);
					}
				}
			}
		}
		// Tools.verbose("--" + AirBlast.instances.size() + "--");
		instances.remove(player);
	}

	private void sphereBurst() {
		if (charged) {
			Location location = player.getEyeLocation();
			double x, y, z;
			double r = 1;
			for (double theta = 30; theta < 180; theta += deltheta) {
				double dphi = delphi / Math.sin(Math.toRadians(theta));
				for (double phi = 0; phi < 360; phi += dphi) {
					double rphi = Math.toRadians(phi);
					double rtheta = Math.toRadians(theta);
					x = r * Math.cos(rphi) * Math.sin(rtheta);
					y = r * Math.sin(rphi) * Math.sin(rtheta);
					z = r * Math.cos(rtheta);
					Vector direction = new Vector(x, y, z);
					new FireBlast(location, direction.normalize(), player,
							damage);
				}
			}
		}
		// Tools.verbose("--" + AirBlast.instances.size() + "--");
		instances.remove(player);
	}

	private void progress() {
		if (!Tools.canBend(player, Abilities.FireBurst)
				|| Tools.getBendingAbility(player) != Abilities.FireBurst) {
			instances.remove(player);
			return;
		}
		if (System.currentTimeMillis() > starttime + chargetime && !charged) {
			charged = true;
		}

		if (!player.isSneaking()) {
			if (charged) {
				sphereBurst();
			} else {
				instances.remove(player);
			}
		} else if (charged) {
			Location location = player.getEyeLocation();
			// location = location.add(location.getDirection().normalize());
			location.getWorld().playEffect(location, Effect.MOBSPAWNER_FLAMES,
					4, 3);
		}
	}

	public static void progressAll() {
		for (Player player : instances.keySet())
			instances.get(player).progress();
	}

	public static String getDescription() {
		return "AirBurst is one of the most powerful abilities in the airbender's arsenal. "
				+ "To use, press and hold sneak to charge your burst. "
				+ "Once charged, you can either release sneak to launch a cone-shaped burst "
				+ "of air in front of you, or click to release the burst in a sphere around you. "
				+ "Additionally, having this ability selected when you land on the ground from a "
				+ "large enough fall will create a burst of air around you.";
	}
}
