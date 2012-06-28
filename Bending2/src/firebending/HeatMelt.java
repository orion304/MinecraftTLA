package firebending;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

import tools.Tools;
import waterbending.Melt;

public class HeatMelt {

	private static final int range = 20;
	private static final int radius = 3;

	public HeatMelt(Player player) {
		Location location = Tools.getTargetedLocation(player, range);
		for (Block block : Tools.getBlocksAroundPoint(location, radius)) {
			if (Tools.isMeltable(block)) {
				Melt.melt(block);
			} else if (isHeatable(block)) {
				heat(block);
			}
		}
	}

	private static void heat(Block block) {
		if (block.getType() == Material.OBSIDIAN) {
			block.setType(Material.LAVA);
			block.setData((byte) 0x0);
		}
	}

	private static boolean isHeatable(Block block) {
		return false;
	}

}
