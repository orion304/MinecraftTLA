package waterbending;

import org.bukkit.Material;
import org.bukkit.Server;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import tools.BendingType;
import tools.Tools;

public class WaterPassive {

	// private static final double factor = 5;

	public static void handlePassive(Server server) {
		for (Player player : server.getOnlinePlayers()) {
			if (Tools.isBender(player, BendingType.Water)
					&& Tools.canBendPassive(player, BendingType.Water)) {
				if (player.getLocation().getBlock().isLiquid()) {
					for (Block block : Tools.getBlocksAroundPoint(
							player.getLocation(), 2)) {
						if (Tools.adjacentToThreeOrMoreSources(block)
								&& Tools.isWater(block)) {
							byte full = 0x0;
							block.setType(Material.WATER);
							block.setData(full);
						}
					}
				}
				// Material type = player.getLocation().getBlock().getType();
				// if (type == Material.WATER || type ==
				// Material.STATIONARY_WATER) {
				// player.setVelocity(player.getVelocity().normalize()
				// .multiply(factor));
				// }
			}
		}
	}

	public static Vector handle(Player player, Vector velocity) {
		Vector vec = velocity.clone();
		// if (player.getLocation().getBlock().getType() == Material.WATER
		// || player.getLocation().getBlock().getType() ==
		// Material.STATIONARY_WATER)
		// vec = velocity.clone().normalize().multiply(factor);
		// Tools.verbose("Is this never called or something?");
		return vec;
	}

}
