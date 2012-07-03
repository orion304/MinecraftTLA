package waterbending;

import java.util.concurrent.ConcurrentHashMap;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

import tools.Abilities;
import tools.AvatarState;
import tools.ConfigManager;
import tools.Tools;

public class FreezeMelt {

	public static ConcurrentHashMap<Block, Byte> frozenblocks = new ConcurrentHashMap<Block, Byte>();

	public static final int defaultrange = ConfigManager.freezeMeltRange;
	public static final int defaultradius = ConfigManager.freezeMeltRadius;

	public FreezeMelt(Player player) {
		int range = (int) Tools.waterbendingNightAugment(defaultrange,
				player.getWorld());
		int radius = (int) Tools.waterbendingNightAugment(defaultradius,
				player.getWorld());
		if (AvatarState.isAvatarState(player)) {
			range = AvatarState.getValue(range);
			// radius = AvatarState.getValue(radius);
		}
		Location location = Tools.getTargetedLocation(player, range);
		for (Block block : Tools.getBlocksAroundPoint(location, radius)) {
			if (isFreezable(block))
				freeze(block);
		}
	}

	private static boolean isFreezable(Block block) {
		if (block.getType() == Material.WATER
				|| block.getType() == Material.STATIONARY_WATER)
			if (WaterManipulation.canPhysicsChange(block))
				return true;
		return false;
	}

	static void freeze(Block block) {
		byte data = block.getData();
		block.setType(Material.ICE);
		frozenblocks.put(block, data);
	}

	public static void thaw(Block block) {
		if (frozenblocks.containsKey(block)) {
			byte data = frozenblocks.get(block);
			frozenblocks.remove(block);
			block.setType(Material.WATER);
			block.setData(data);
		}
	}

	public static void handleFrozenBlocks() {
		for (Block block : frozenblocks.keySet()) {
			if (canThaw(block))
				thaw(block);
		}
	}

	public static boolean canThaw(Block block) {
		if (frozenblocks.containsKey(block)) {
			for (Player player : block.getWorld().getPlayers()) {
				if (Tools.hasAbility(player, Abilities.FreezeMelt)
						&& Tools.canBend(player, Abilities.FreezeMelt)) {
					double range = Tools.waterbendingNightAugment(defaultrange,
							player.getWorld());
					if (AvatarState.isAvatarState(player)) {
						range = AvatarState.getValue(range);
					}
					if (block.getLocation().distance(player.getLocation()) <= range)
						return false;
				}
			}
		}
		if (!WaterManipulation.canPhysicsChange(block))
			return false;
		return true;
	}

	private static void thawAll() {
		for (Block block : frozenblocks.keySet()) {
			if (block.getType() == Material.ICE) {
				byte data = frozenblocks.get(block);
				block.setType(Material.WATER);
				block.setData(data);
				frozenblocks.remove(block);
			}
		}
	}

	public static void removeAll() {
		thawAll();
	}

}
