package waterbending;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

import tools.AvatarState;
import tools.Tools;

public class Melt {

	private static final int defaultrange = Freeze.defaultrange;
	private static final int defaultradius = Freeze.defaultradius;

	public Melt(Player player) {
		int range = defaultrange;
		int radius = defaultradius;
		if (AvatarState.isAvatarState(player)) {
			range = AvatarState.getValue(range);
			radius = AvatarState.getValue(radius);
		}
		Location location = Tools.getTargetedLocation(player, range);
		for (Block block : Tools.getBlocksAroundPoint(location, radius)) {
			melt(block);
		}
	}

	public static void melt(Block block) {
		if (Tools.isMeltable(block)) {
			if (Freeze.frozenblocks.containsKey(block)) {
				Freeze.thaw(block);
			} else {
				block.setType(Material.WATER);
				block.setData((byte) 0x7);
			}
		}
		if (block.getType() == Material.SNOW) {
			block.setType(Material.AIR);
		}
	}

}
