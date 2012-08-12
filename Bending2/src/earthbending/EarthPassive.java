package earthbending;

import java.util.concurrent.ConcurrentHashMap;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;

import tools.ConfigManager;
import tools.Tools;

public class EarthPassive {

	public static ConcurrentHashMap<Block, Long> sandblocks = new ConcurrentHashMap<Block, Long>();
	public static ConcurrentHashMap<Block, Material> sandidentities = new ConcurrentHashMap<Block, Material>();
	private static final long duration = ConfigManager.earthPassive;

	public static boolean softenLanding(Player player) {
		Block block = player.getLocation().getBlock()
				.getRelative(BlockFace.DOWN);
		if (Tools.isEarthbendable(block)
				|| Tools.isTransparentToEarthbending(block)) {

			if (!Tools.isTransparentToEarthbending(block)) {
				Material type = block.getType();
				if (Tools.isSolid(block.getRelative(BlockFace.DOWN))) {
					block.setType(Material.SAND);
					if (!sandblocks.containsKey(block)) {
						sandidentities.put(block, type);
						sandblocks.put(block, System.currentTimeMillis());
					}
				}

			}

			for (Block affectedblock : Tools.getBlocksAroundPoint(
					block.getLocation(), 2)) {
				if (Tools.isEarthbendable(affectedblock)) {
					if (Tools
							.isSolid(affectedblock.getRelative(BlockFace.DOWN))) {
						Material type = affectedblock.getType();
						affectedblock.setType(Material.SAND);
						if (!sandblocks.containsKey(affectedblock)) {
							sandidentities.put(affectedblock, type);
							sandblocks.put(affectedblock,
									System.currentTimeMillis());
						}
					}

				}
			}
			return true;
		}
		return false;
	}

	public static boolean isPassiveSand(Block block) {
		return (sandblocks.containsKey(block));
	}

	public static void revertSand(Block block) {
		Material type = sandidentities.get(block);
		sandidentities.remove(block);
		sandblocks.remove(block);
		if (block.getType() == Material.SAND) {
			block.setType(type);
		}
	}

	public static void revertSands() {
		for (Block block : sandblocks.keySet()) {
			if (System.currentTimeMillis() >= sandblocks.get(block) + duration) {
				revertSand(block);
			}
		}

	}

	public static void revertAllSand() {
		for (Block block : sandblocks.keySet()) {
			revertSand(block);
		}
	}

	public static void removeAll() {
		revertAllSand();
	}

}
