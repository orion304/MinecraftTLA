package earthbending;

import java.util.ArrayList;
import java.util.concurrent.ConcurrentHashMap;

import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;

import tools.ConfigManager;
import tools.Tools;

public class Collapse {

	private static final int range = ConfigManager.collapseRange;
	private static final double defaultradius = ConfigManager.collapseRadius;
	private static final int height = EarthColumn.standardheight;

	private ConcurrentHashMap<Block, Block> blocks = new ConcurrentHashMap<Block, Block>();
	private ConcurrentHashMap<Block, Integer> baseblocks = new ConcurrentHashMap<Block, Integer>();
	private double radius = defaultradius;

	public Collapse(Player player) {
		// if (AvatarState.isAvatarState(player))
		// radius = AvatarState.getValue(defaultradius);
		Location location = player.getTargetBlock(
				Tools.getTransparentEarthbending(), range).getLocation();
		for (Block block : Tools.getBlocksAroundPoint(location, radius)) {
			if (Tools.isEarthbendable(block) && !blocks.containsKey(block)
					&& block.getY() >= location.getBlockY()) {
				getAffectedBlocks(block);
			}
		}

		for (Block block : baseblocks.keySet()) {
			new CompactColumn(block.getLocation());
		}
	}

	private void getAffectedBlocks(Block block) {
		Block baseblock = block;
		int tall = 0;
		ArrayList<Block> bendableblocks = new ArrayList<Block>();
		bendableblocks.add(block);
		for (int i = 1; i <= height; i++) {
			Block blocki = block.getRelative(BlockFace.DOWN, i);
			if (Tools.isEarthbendable(blocki)) {
				baseblock = blocki;
				bendableblocks.add(blocki);
				tall++;
			} else {
				break;
			}
		}
		baseblocks.put(baseblock, tall);
		for (Block blocki : bendableblocks) {
			blocks.put(blocki, baseblock);
		}

	}

	public static String getDescription() {
		return "This ability acts like CompactColumn in an area around your targetted location - "
				+ "all earth that can be moved downwards will be moved downwards. "
				+ "This ability is especially risky or deadly in caves, depending on the "
				+ "earthbender's goal and technique.";
	}
}
