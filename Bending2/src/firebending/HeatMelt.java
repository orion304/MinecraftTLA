package firebending;

import main.ConfigValues;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

import tools.Tools;
import waterbending.Melt;

public class HeatMelt {

	private static final int range = ConfigValues.HeatControlRange;
	private static final int radius = ConfigValues.HeatControlRadius;

	public HeatMelt(Player player) {
		Location location = Tools.getTargetedLocation(player,
				(int) Tools.firebendingDayAugment(range, player.getWorld()));
		for (Block block : Tools.getBlocksAroundPoint(location,
				(int) Tools.firebendingDayAugment(radius, player.getWorld()))) {
			if (Tools.isMeltable(block)) {
				Melt.melt(player, block);
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

	public static String getDescription() {
		return "To use, simply left-click. "
				+ "Any meltable blocks around that target location will immediately melt.";
	}

}
