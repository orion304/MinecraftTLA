package firebending;

import java.util.ArrayList;
import java.util.concurrent.ConcurrentHashMap;

import org.bukkit.Effect;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

import tools.AvatarState;
import tools.ConfigManager;
import tools.Tools;

public class FireJet {

	public static ConcurrentHashMap<Player, FireJet> instances = new ConcurrentHashMap<Player, FireJet>();
	private static final double defaultfactor = ConfigManager.fireJetSpeed;
	private static final long defaultduration = ConfigManager.fireJetDuration;
	private static final long cooldown = ConfigManager.fireJetCooldown;

	private static ConcurrentHashMap<Player, Long> timers = new ConcurrentHashMap<Player, Long>();

	private Player player;
	// private boolean canfly;
	private long time;
	private long duration = defaultduration;
	private double factor = defaultfactor;

	public FireJet(Player player) {
		if (instances.containsKey(player)) {
			// player.setAllowFlight(canfly);
			instances.remove(player);
			return;
		}
		if (timers.containsKey(player)) {
			if (System.currentTimeMillis() < timers.get(player)
					+ (long) ((double) cooldown / Tools
							.getFirebendingDayAugment(player.getWorld()))) {
				return;
			}
		}
		factor = Tools.firebendingDayAugment(defaultfactor, player.getWorld());
		Block block = player.getLocation().getBlock();
		if (FireStream.isIgnitable(block) || block.getType() == Material.AIR
				|| AvatarState.isAvatarState(player)) {
			player.setVelocity(player.getEyeLocation().getDirection().clone()
					.normalize().multiply(factor));
			block.setType(Material.FIRE);
			this.player = player;
			// canfly = player.getAllowFlight();
			player.setAllowFlight(true);
			time = System.currentTimeMillis();
			timers.put(player, time);
			instances.put(player, this);
		}

	}

	public static boolean checkTemporaryImmunity(Player player) {
		if (instances.containsKey(player)) {
			return true;
		}
		return false;
	}

	public void progress() {
		if (player.isDead() || !player.isOnline()) {
			// player.setAllowFlight(canfly);
			instances.remove(player);
			return;
		}
		if ((Tools.isWater(player.getLocation().getBlock()) || System
				.currentTimeMillis() > time + duration)
				&& !AvatarState.isAvatarState(player)) {
			// player.setAllowFlight(canfly);
			instances.remove(player);
		} else {
			player.getWorld().playEffect(player.getLocation(),
					Effect.MOBSPAWNER_FLAMES, 1);
			player.setVelocity(player.getEyeLocation().getDirection().clone()
					.normalize().multiply(factor));
			player.setFallDistance(0);
		}
	}

	public static void progressAll() {
		for (Player player : instances.keySet()) {
			instances.get(player).progress();
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
