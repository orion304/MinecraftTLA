package firebending;

import java.util.concurrent.ConcurrentHashMap;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

import tools.Tools;

public class FireJet {

	public static ConcurrentHashMap<Player, FireJet> instances = new ConcurrentHashMap<Player, FireJet>();
	private static final double factor = 1.6;
	private static final long duration = 2000;
	private static final long cooldown = 6000;

	private static ConcurrentHashMap<Player, Long> timers = new ConcurrentHashMap<Player, Long>();

	private Player player;
	private long time;

	public FireJet(Player player) {
		if (instances.containsKey(player)) {
			return;
		}
		if (timers.containsKey(player)) {
			if (System.currentTimeMillis() < timers.get(player) + cooldown) {
				return;
			}
		}
		Block block = player.getLocation().getBlock();
		if (FireStream.isIgnitable(block)) {
			player.setVelocity(player.getEyeLocation().getDirection().clone()
					.normalize().multiply(factor));
			block.setType(Material.FIRE);
			this.player = player;
			time = System.currentTimeMillis();
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
		if (Tools.isWater(player.getLocation().getBlock())
				|| System.currentTimeMillis() > time + duration) {
			instances.remove(player);
		} else {
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

}
