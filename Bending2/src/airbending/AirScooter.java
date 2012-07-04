package airbending;

import java.util.ArrayList;
import java.util.concurrent.ConcurrentHashMap;

import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import tools.Tools;

public class AirScooter {

	public static ConcurrentHashMap<Player, AirScooter> instances = new ConcurrentHashMap<Player, AirScooter>();

	// private static final double speed = ConfigManager.airScooterSpeed;
	private static final double speed = 1;
	private static final long interval = 100;
	private static final double scooterradius = 1.2;

	private Player player;
	private Block floorblock;
	private long time;
	private boolean canfly, wasflying;
	private ArrayList<Double> angles = new ArrayList<Double>();

	public AirScooter(Player player) {
		if (instances.containsKey(player)) {
			player.setAllowFlight(canfly);
			player.setFlying(wasflying);
			instances.remove(player);
			return;
		}
		if (!player.isSprinting())
			return;
		this.player = player;
		wasflying = player.isFlying();
		canfly = player.getAllowFlight();
		player.setAllowFlight(true);
		player.setFlying(true);
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
			player.setAllowFlight(canfly);
			player.setFlying(wasflying);
			instances.remove(player);
			return;
		}
		if (Tools
				.isSolid(player
						.getEyeLocation()
						.clone()
						.add(player.getEyeLocation().getDirection().clone()
								.normalize()).getBlock())) {
			player.setAllowFlight(canfly);
			player.setFlying(wasflying);
			instances.remove(player);
			return;
		}
		// player.sendBlockChange(floorblock.getLocation(), 89, (byte) 1);
		// player.getLocation().setY((double) floorblock.getY() + 2.5);

		Vector velocity = player.getEyeLocation().getDirection().clone();
		velocity.setY(0);
		velocity = velocity.clone().normalize().multiply(speed);
		if (player.getLocation().distance(floorblock.getLocation()) == 0) {
			player.setAllowFlight(canfly);
			player.setFlying(wasflying);
			instances.remove(player);
			return;
		}
		if (player.getLocation().getY() < (double) floorblock.getY() + 2.5) {
			velocity.setY(.5 / player.getLocation().distance(
					floorblock.getLocation()));
		} else if (player.getLocation().getY() > (double) floorblock.getY() + 2) {
			velocity.setY(-.5
					/ player.getLocation().distance(floorblock.getLocation()));
		} else {
			velocity.setY(0);
		}
		Location loc = player.getLocation();
		loc.setY((double) floorblock.getY() + 1.5);
		// player.setFlying(true);
		// player.teleport(loc.add(velocity));
		player.setVelocity(velocity);

		if (System.currentTimeMillis() > time + interval) {
			time = System.currentTimeMillis();
			spinScooter();
		}
	}

	private void spinScooter() {
		Location origin = player.getLocation().clone();
		origin.add(0, -scooterradius, 0);
		for (int i = 0; i < 5; i++) {
			double x = Math.cos(Math.toRadians(angles.get(i))) * scooterradius;
			double y = ((double) i) / 2 * scooterradius - scooterradius;
			double z = Math.sin(Math.toRadians(angles.get(i))) * scooterradius;
			player.getWorld().playEffect(origin.clone().add(x, y, z),
					Effect.SMOKE, 1);
		}
		for (int i = 0; i < 5; i++) {
			angles.set(i, angles.get(i) + 10);
		}
	}

	private void getFloor() {
		floorblock = null;
		for (int i = 0; i <= 5; i++) {
			Block block = player.getEyeLocation().getBlock()
					.getRelative(BlockFace.DOWN, i);
			if (Tools.isSolid(block) || block.isLiquid()) {
				floorblock = block;
				return;
			}
		}
	}

	public static void progressAll() {
		for (Player player : instances.keySet()) {
			instances.get(player).progress();
		}
	}

	
	public static String getDescription(){
		return "Bind this ability to a slot and on left click.... to be continued";
	}
}
