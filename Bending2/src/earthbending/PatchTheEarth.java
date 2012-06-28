package earthbending;

import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

import tools.Tools;

public class PatchTheEarth {

	private static final int range = 20;
	private static final double radius = 7;

	public PatchTheEarth(Player player) {
		Location location = player.getTargetBlock(
				Tools.getTransparentEarthbending(), range).getLocation();
		for (Block block : Tools.getBlocksAroundPoint(location, radius)) {
			if (EarthColumn.blockIsBase(block)) {
				// Tools.verbose("lowering thingy");
				new CompactColumn(block.getLocation());
				EarthColumn.removeBlockBase(block);
			}

		}
	}

}
