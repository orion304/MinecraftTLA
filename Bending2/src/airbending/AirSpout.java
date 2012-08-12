package airbending;

import java.util.ArrayList;
import java.util.concurrent.ConcurrentHashMap;

import org.bukkit.Effect;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;

import tools.Abilities;
import tools.Tools;

public class AirSpout {

	private static ConcurrentHashMap<Player, AirSpout> instances = new ConcurrentHashMap<Player, AirSpout>();

	private static final double height = 13;
	private static final long interval = 100;

	private Player player;
	private long time;

	public AirSpout(Player player) {
		if (instances.containsKey(player)) {
			instances.get(player).remove();
			return;
		}
		this.player = player;
		time = System.currentTimeMillis();
		instances.put(player, this);
		spout();
	}

	public static void spoutAll() {
		for (Player player : instances.keySet()) {
			instances.get(player).spout();
		}
	}

	public static ArrayList<Player> getPlayers() {
		ArrayList<Player> players = new ArrayList<Player>();
		players.addAll(instances.keySet());
		return players;
	}

	private void spout() {
		if (!Tools.hasPermission(player, Abilities.AirSpout)
				|| !Tools.hasAbility(player, Abilities.AirSpout)
				|| player.getEyeLocation().getBlock().isLiquid()
				|| Tools.isSolid(player.getEyeLocation().getBlock())) {
			remove();
			return;
		}
		player.setSprinting(false);
		Block block = getGround();
		if (block != null) {
			double dy = player.getLocation().getY() - block.getY();
			if (dy > height) {
				removeFlight();
			} else {
				allowFlight();
			}
			rotateAirColumn(block);
		} else {
			remove();
		}
	}

	private void allowFlight() {
		player.setAllowFlight(true);
		player.setFlying(true);
		// flight speed too
	}

	private void removeFlight() {
		player.setAllowFlight(player.getGameMode() == GameMode.CREATIVE);
		// flight speed too
	}

	private Block getGround() {
		Block standingblock = player.getLocation().getBlock();
		for (int i = 0; i < height + 5; i++) {
			Block block = standingblock.getRelative(BlockFace.DOWN, i);
			if (Tools.isSolid(block) || block.isLiquid()) {
				return block;
			}
		}
		return null;
	}

	private void rotateAirColumn(Block block) {

		if (System.currentTimeMillis() >= time + interval) {
			time = System.currentTimeMillis();

			Location location = block.getLocation();
			Location playerloc = player.getLocation();
			location = new Location(location.getWorld(), playerloc.getX(),
					location.getY(), playerloc.getZ());

			double dy = playerloc.getY() - block.getY();
			if (dy > height)
				dy = height;
			for (int i = 1; i < dy; i++) {
				Location effectloc2 = new Location(location.getWorld(),
						location.getX(), block.getY() + i, location.getZ());
				location.getWorld().playEffect(effectloc2, Effect.SMOKE, 0,
						(int) height + 5);
				location.getWorld().playEffect(effectloc2, Effect.SMOKE, 1,
						(int) height + 5);
				location.getWorld().playEffect(effectloc2, Effect.SMOKE, 2,
						(int) height + 5);
				location.getWorld().playEffect(effectloc2, Effect.SMOKE, 3,
						(int) height + 5);
				location.getWorld().playEffect(effectloc2, Effect.SMOKE, 5,
						(int) height + 5);
				location.getWorld().playEffect(effectloc2, Effect.SMOKE, 6,
						(int) height + 5);
				location.getWorld().playEffect(effectloc2, Effect.SMOKE, 7,
						(int) height + 5);
				location.getWorld().playEffect(effectloc2, Effect.SMOKE, 8,
						(int) height + 5);
			}
		}
	}

	private void remove() {
		removeFlight();
		instances.remove(player);
	}

	public static void removeAll() {
		for (Player player : instances.keySet()) {
			instances.get(player).remove();
		}
	}

	public static String getDescription() {
		return "This ability gives the airbender limited sustained levitation. It is a "
				+ "toggle - click to activate and form a whirling spout of air "
				+ "beneath you, lifting you up. You can bend other abilities while using AirSpout. "
				+ "Click again to deactivate this ability.";
	}

}
