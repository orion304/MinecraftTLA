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

	public static String getDescription() {
		return "Plantbending gives great utility to waterbenders. Provided you have Plantbending bound to any of your slots, "
				+ "it augments the rest of your abilities. Instead of being limited to water, "
				+ "snow and ice for sources of water, you can instead use any plant as a water source. "
				+ "So instead of focusing your ability on water, you could, for example, focus it on a "
				+ "block of leaves and it would suck the water out of the leaves for your other technique. "
				+ "Additionally, if you are close to a plant, you can click this ability to turn the plant into a "
				+ "source block of water.";
	}
}
