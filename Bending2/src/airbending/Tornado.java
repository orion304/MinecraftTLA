package airbending;

import java.util.ArrayList;
import java.util.concurrent.ConcurrentHashMap;

import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import tools.Abilities;
import tools.ConfigManager;
import tools.Tools;

public class Tornado {

	public static ConcurrentHashMap<Integer, Tornado> instances = new ConcurrentHashMap<Integer, Tornado>();

	private static double maxradius = ConfigManager.tornadoRadius;
	private static double maxheight = ConfigManager.tornadoHeight;
	private static double range = ConfigManager.tornadoRange;
	private static int numberOfStreams = (int) (.3 * (double) maxheight);
	private static double NPCpushfactor = ConfigManager.tornadoMobPush;
	private static double PCpushfactor = ConfigManager.tornadoPlayerPush;
	// private static double speed = .75;

	private double height = 2;
	private double radius = height / maxheight * maxradius;

	// private static double speedfactor = 1000 * speed
	// * (Bending.time_step / 1000.);
	private static double speedfactor = 1;

	private ConcurrentHashMap<Integer, Integer> angles = new ConcurrentHashMap<Integer, Integer>();
	private Location origin;
	private Player player;

	// private boolean canfly;

	public Tornado(Player player) {
		this.player = player;
		// canfly = player.getAllowFlight();
		// player.setAllowFlight(true);
		origin = player.getTargetBlock(null, (int) range).getLocation();
		origin.setY(origin.getY() - 1. / 10. * height);

		int angle = 0;
		for (int i = 0; i <= maxheight; i += (int) maxheight / numberOfStreams) {
			angles.put(i, angle);
			angle += 90;
			if (angle == 360)
				angle = 0;
		}

		player.setAllowFlight(true);
		instances.put(player.getEntityId(), this);

	}

	public boolean progress() {
		if (player.isDead() || !player.isOnline()) {
			// player.setAllowFlight(canfly);
			instances.remove(player.getEntityId());
			return false;
		}
		if (!Tools.canBend(player, Abilities.Tornado)
				|| player.getEyeLocation().getBlock().isLiquid()) {
			// player.setAllowFlight(canfly);
			instances.remove(player.getEntityId());
			return false;
		}
		if ((Tools.getBendingAbility(player) != Abilities.Tornado)
				|| (!player.isSneaking())) {
			// player.setAllowFlight(canfly);
			instances.remove(player.getEntityId());
			return false;
		}
		if (Tools
				.isRegionProtectedFromBuild(player, Abilities.AirBlast, origin)) {
			instances.remove(player.getEntityId());
			return false;
		}
		rotateTornado();
		return true;
	}

	private void rotateTornado() {
		origin = player.getTargetBlock(null, (int) range).getLocation();

		double timefactor = height / maxheight;
		radius = timefactor * maxradius;

		if (origin.getBlock().getType() != Material.AIR) {
			origin.setY(origin.getY() - 1. / 10. * height);

			for (Entity entity : Tools.getEntitiesAroundPoint(origin, height)) {
				if (Tools.isRegionProtectedFromBuild(player,
						Abilities.AirBlast, entity.getLocation()))
					continue;
				double y = entity.getLocation().getY();
				double factor;
				if (y > origin.getY() && y < origin.getY() + height) {
					factor = (y - origin.getY()) / height;
					Location testloc = new Location(origin.getWorld(),
							origin.getX(), y, origin.getZ());
					if (testloc.distance(entity.getLocation()) < radius
							* factor) {
						double x, z, vx, vz, mag;
						double angle = 100;
						double vy = 0.7 * NPCpushfactor;
						angle = Math.toRadians(angle);

						x = entity.getLocation().getX() - origin.getX();
						z = entity.getLocation().getZ() - origin.getZ();

						mag = Math.sqrt(x * x + z * z);

						vx = (x * Math.cos(angle) - z * Math.sin(angle)) / mag;
						vz = (x * Math.sin(angle) + z * Math.cos(angle)) / mag;

						if (entity instanceof Player) {
							vy = 0.05 * PCpushfactor;
						}

						if (entity.getEntityId() == player.getEntityId()) {
							Vector direction = player.getEyeLocation()
									.getDirection().clone().normalize();
							vx = direction.getX();
							vz = direction.getZ();
							vy = .6;
						}

						Vector velocity = entity.getVelocity();
						velocity.setX(vx);
						velocity.setZ(vz);
						velocity.setY(vy);
						velocity.multiply(timefactor);
						entity.setVelocity(velocity);
						entity.setFallDistance(0);
					}
				}
			}

			for (int i : angles.keySet()) {
				double x, y, z;
				double angle = (double) angles.get(i);
				angle = Math.toRadians(angle);
				double factor;

				y = origin.getY() + timefactor * (double) i;
				factor = (double) i / height;

				x = origin.getX() + timefactor * factor * radius
						* Math.cos(angle);
				z = origin.getZ() + timefactor * factor * radius
						* Math.sin(angle);

				Location effect = new Location(origin.getWorld(), x, y, z);
				if (!Tools.isRegionProtectedFromBuild(player,
						Abilities.AirBlast, effect))
					origin.getWorld().playEffect(effect, Effect.SMOKE, 4,
							(int) AirBlast.defaultrange);

				angles.put(i, angles.get(i) + 25 * (int) speedfactor);
			}
		}

		if (height < maxheight) {
			height += 1;
		}

		if (height > maxheight) {
			height = maxheight;
		}

	}

	public static boolean progress(int ID) {
		return instances.get(ID).progress();
	}

	public static String getDescription() {
		return "To use, simply sneak (default: shift). "
				+ "This will create a swirling vortex at the targeted location. "
				+ "Any creature or object caught in the vortex will be launched up "
				+ "and out in some random direction. If another player gets caught "
				+ "in the vortex, the launching effect is minimal. Tornado can "
				+ "also be used to transport the user. If the user gets caught in his/her "
				+ "own tornado, his movements are much more manageable. Provided the user doesn't "
				+ "fall out of the vortex, it will take him to a maximum height and move him in "
				+ "the general direction he's looking. Skilled airbenders can scale anything "
				+ "with this ability.";

	}

	public static ArrayList<Player> getPlayers() {
		ArrayList<Player> players = new ArrayList<Player>();
		for (int id : instances.keySet()) {
			players.add(instances.get(id).player);
		}
		return players;
	}

}
