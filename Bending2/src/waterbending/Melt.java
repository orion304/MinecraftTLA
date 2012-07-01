package waterbending;

import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

import tools.AvatarState;
import tools.Tools;

public class Melt {

	private static final int defaultrange = FreezeMelt.defaultrange;
	private static final int defaultradius = FreezeMelt.defaultradius;
	private static final int defaultevaporateradius = 3;

	public Melt(Player player) {
		int range = defaultrange;
		int radius = defaultradius;

		if (AvatarState.isAvatarState(player)) {
			range = AvatarState.getValue(range);
			radius = AvatarState.getValue(radius);
		}
		boolean evaporate = false;
		Location location = Tools.getTargetedLocation(player, range);
		if (Tools.isWater(player.getTargetBlock(null, range))) {
			evaporate = true;
			radius = defaultevaporateradius;
		}
		for (Block block : Tools.getBlocksAroundPoint(location, radius)) {
			if (evaporate) {
				if (block.getY() > 62)
					evaporate(block);
			} else {
				melt(block);
			}
		}
	}

	public static void melt(Block block) {
		if (Tools.isMeltable(block)) {
			if (FreezeMelt.frozenblocks.containsKey(block)) {
				FreezeMelt.thaw(block);
			} else {
				block.setType(Material.WATER);
				block.setData((byte) 0x7);
			}
		}
		if (block.getType() == Material.SNOW) {
			block.setType(Material.AIR);
		}
	}

	public static void evaporate(Block block) {
		if (Tools.isWater(block)) {
			block.setType(Material.AIR);
			block.getWorld().playEffect(block.getLocation(), Effect.SMOKE, 1);
		}
	}

}
