package earthbending;

import java.util.concurrent.ConcurrentHashMap;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;

import tools.Tools;

public class EarthPassive {

	public static ConcurrentHashMap<Block, Long> sandblocks = new ConcurrentHashMap<Block, Long>();
	public static ConcurrentHashMap<Block, Material> sandidentities = new ConcurrentHashMap<Block, Material>();
	private static final long duration = 3000;

	public static boolean softenLanding(Player player) {
		Block block = player.getLocation().getBlock()
				.getRelative(BlockFace.DOWN);
		if (Tools.isEarthbendable(block)
				|| Tools.isTransparentToEarthbending(block)) {

			if (!Tools.isTransparentToEarthbending(block)) {
				Material type = block.getType();
				block.setType(Material.SAND);
				if (!sandblocks.containsKey(block)) {
					sandidentities.put(block, type);
					sandblocks.put(block, System.currentTimeMillis());
				}

			}

			for (Block affectedblock : Tools.getBlocksAroundPoint(
					block.getLocation(), 2)) {
				if (Tools.isEarthbendable(affectedblock)) {
					Material type = affectedblock.getType();
					affectedblock.setType(Material.SAND);
					if (!sandblocks.containsKey(affectedblock)) {
						sandidentities.put(affectedblock, type);
						sandblocks.put(affectedblock,
								System.currentTimeMillis());
					}

				}
			}
			return true;
		}
		return false;
	}

	public static void revertSands() {
		for (Block block : sandblocks.keySet()) {
			if (System.currentTimeMillis() >= sandblocks.get(block) + duration) {
				Material type = sandidentities.get(block);
				sandidentities.remove(block);
				sandblocks.remove(block);
				if (block.getType() == Material.SAND) {
					block.setType(type);
				}
			}
		}

	}

	public static void revertAllSand() {
		for (Block block : sandblocks.keySet()) {
			Material type = sandidentities.get(block);
			sandidentities.remove(block);
			sandblocks.remove(block);
			block.setType(type);

		}
	}

	public static void removeAll() {
		revertAllSand();
	}

}
