package firebending;

import java.util.concurrent.ConcurrentHashMap;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

public class FireJet {

	public static ConcurrentHashMap<Player, Block> instances = new ConcurrentHashMap<Player, Block>();
	private static final double factor = 1.6;

	public FireJet(Player player) {
		Block block = player.getLocation().getBlock();
		if (FireStream.isIgnitable(block)) {
			player.setVelocity(player.getEyeLocation().getDirection().clone()
					.normalize().multiply(factor));
			block.setType(Material.FIRE);
			instances.put(player, block);
		}

	}

	public static boolean checkTemporaryImmunity(Player player) {
		if (instances.contains(player)) {
			return true;
		}
		return false;
	}

	public static void checkBlocks() {
		for (Player player : instances.keySet()) {
			if (instances.get(player).getType() != Material.FIRE)
				instances.remove(player);
		}

	}

}
