package airbending;

import java.util.ArrayList;
import java.util.concurrent.ConcurrentHashMap;

import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;

import tools.Abilities;
import tools.ConfigManager;
import tools.Tools;

public class AirScooter {

	public static ConcurrentHashMap<Player, AirScooter> instances = new ConcurrentHashMap<Player, AirScooter>();

	private static final double speed = ConfigManager.airScooterSpeed;
	private static final long interval = 100;
	private static final double scooterradius = 1;

	private Player player;
	private Block floorblock;
	private long time;
	// private boolean canfly, wasflying;
	private ArrayList<Double> angles = new ArrayList<Double>();

	public AirScooter(Player player) {
		if (instances.containsKey(player)) {
			instances.get(player).remove();
			return;
		}
		if (!player.isSprinting()
				|| Tools.isSolid(player.getEyeLocation().getBlock())
				|| player.getEyeLocation().getBlock().isLiquid())
			return;
		if (Tools.isSolid(player.getLocation().add(0, -.5, 0).getBlock()))
			return;
		this.player = player;
		// wasflying = player.isFlying();
		// canfly = player.getAllowFlight();
		player.setAllowFlight(true);
		player.setFlying(true);
		player.setSprinting(false);
		time = System.currentTimeMillis();
		for (int i = 0; i < 5; i++) {
			angles.add((double) (60 * i));
		}
		instances.put(player, this);
		progress();
	}

	private void progress() {
		getFloor();
		// Tools.verbose(player);
		if (floorblock == null) {
			remove();
			return;
		}
		if (!Tools.canBend(player, Abilities.AirScooter)
				|| !Tools.hasAbility(player, Abilities.AirScooter)) {
			remove();
			return;
		}
		if (!player.isOnline() || player.isDead() || !player.isFlying()) {
			remove();
			return;
		}
		// if (Tools
		// .isSolid(player
		// .getEyeLocation()
		// .clone()
		// .add(player.getEyeLocation().getDirection().clone()
		// .normalize()).getBlock())) {
		// remove();
		// return;
		// }
		// player.sendBlockChange(floorblock.getLocation(), 89, (byte) 1);
		// player.getLocation().setY((double) floorblock.getY() + 2.5);

		Vector velocity = player.getEyeLocation().getDirection().clone();
		velocity.setY(0);
		velocity = velocity.clone().normalize().multiply(speed);
		if (System.currentTimeMillis() > time + interval) {
			time = System.currentTimeMillis();
			if (player.getVelocity().length() < speed * .5) {
				remove();
				return;
			}
			spinScooter();
		}
		double distance = player.getLocation().getY()
				- (double) floorblock.getY();
		double dx = Math.abs(distance - 2.4);
		if (distance > 2.75) {
			velocity.setY(-.25 * dx * dx);
		} else if (distance < 2) {
			velocity.setY(.25 * dx * dx);
		} else {
			velocity.setY(0);
		}
		Location loc = player.getLocation();
		loc.setY((double) floorblock.getY() + 1.5);
		// player.setFlying(true);
		// player.teleport(loc.add(velocity));
		player.setSprinting(false);
		player.removePotionEffect(PotionEffectType.SPEED);
		player.setVelocity(velocity);
	}

	private void spinScooter() {
		Location origin = player.getLocation().clone();
		origin.add(0, -scooterradius, 0);
		for (int i = 0; i < 5; i++) {
			double x = Math.cos(Math.toRadians(angles.get(i))) * scooterradius;
			double y = ((double) i) / 2 * scooterradius - scooterradius;
			double z = Math.sin(Math.toRadians(angles.get(i))) * scooterradius;
			player.getWorld().playEffect(origin.clone().add(x, y, z),
					Effect.SMOKE, 4, (int) AirBlast.range);
		}
		for (int i = 0; i < 5; i++) {
			angles.set(i, angles.get(i) + 10);
		}
	}

	private void getFloor() {
		floorblock = null;
		for (int i = 0; i <= 7; i++) {
			Block block = player.getEyeLocation().getBlock()
					.getRelative(BlockFace.DOWN, i);
			if (Tools.isSolid(block) || block.isLiquid()) {
				floorblock = block;
				return;
			}
		}
	}

	private void remove() {
		player.setAllowFlight(false);
		player.setFlying(false);
		instances.remove(player);
	}

	public static void check(Player player) {
		if (instances.containsKey(player)) {
			instances.get(player).remove();
		}
	}

	public static void progressAll() {
		for (Player player : instances.keySet()) {
			instances.get(player).progress();
		}
	}

	public static String getDescription() {
		return "AirScooter is a fast means of transportation. To use, sprint, jump then click with "
				+ "this ability selected. You will hop on a scooter of air and be propelled forward "
				+ "in the direction you're looking (you don't need to press anything). "
				+ "This ability can be used to levitate above liquids, but it cannot go up steep slopes. "
				+ "Any other actions will deactivate this ability.";
	}

	public static void removeAll() {
		for (Player player : instances.keySet()) {
			instances.get(player).remove();
		}
	}

	public static ArrayList<Player> getPlayers() {
		ArrayList<Player> players = new ArrayList<Player>();
		for (Player player : instances.keySet()) {
			players.add(player);
		}
		return players;
	}
}
