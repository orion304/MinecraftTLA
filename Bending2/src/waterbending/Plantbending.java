package waterbending;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

import tools.ConfigManager;
import tools.Tools;

public class Plantbending {

	private static final int range = ConfigManager.plantbendingRange;

	public Plantbending(Player player) {
		Block block = player.getTargetBlock(null, range);
		if (Tools.isPlant(block)
				&& !(block.getType() == Material.VINE
						|| block.getType() == Material.LEAVES
						|| block.getType() == Material.SUGAR_CANE || block
						.getType() == Material.CACTUS)) {
			block.setType(Material.WATER);
		}
	}

}
