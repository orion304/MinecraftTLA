package waterbending;

import java.util.concurrent.ConcurrentHashMap;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Server;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

import tools.Abilities;
import tools.Tools;

public class WaterSpout {

	public static ConcurrentHashMap<Block, Block> affectedblocks = new ConcurrentHashMap<Block, Block>();

	private static final int height = 8;
	// private static final byte half = 0x4;
	private static final byte full = 0x0;

	public static void handleSpouts(Server server) {
		// affectedblocks.clear();
		for (Block block : affectedblocks.keySet()) {
			boolean remove = true;
			for (Player player : server.getOnlinePlayers()) {
				if (Tools.getBendingAbility(player) == Abilities.WaterSpout
						&& Tools.canBend(player, Abilities.WaterSpout)) {
					Location loc1 = player.getLocation().clone();
					loc1.setY(0);
					Location loc2 = block.getLocation().clone();
					loc2.setY(0);
					if (loc1.distance(loc2) < 1.5)
						remove = false;
				}
			}
			if (remove)
				remove(block);
		}

		for (Player player : server.getOnlinePlayers()) {
			if (Tools.getBendingAbility(player) == Abilities.WaterSpout
					&& Tools.canBend(player, Abilities.WaterSpout)) {
				spout(player);
			}
		}

	}

	private static void remove(Block block) {
		affectedblocks.remove(block);
		block.setType(Material.AIR);
		// block.setData(half);
	}

	private static void spout(Player player) {
		Location location = player.getLocation().clone().add(0, .7, 0);
		Block block;
		int height = spoutableWaterHeight(location);
		if (height != 0) {
			for (int i = 0; i <= height - 1; i++) {
				block = location.clone().add(0, -i, 0).getBlock();
				block.setType(Material.WATER);
				block.setData(full);
				affectedblocks.put(block, block);
			}
		}
	}

	private static int spoutableWaterHeight(Location location) {
		Block blocki;
		for (int i = 0; i <= height; i++) {
			blocki = location.clone().add(0, -i, 0).getBlock();
			if (!affectedblocks.contains(blocki)) {
				if (blocki.getType() == Material.WATER
						|| blocki.getType() == Material.STATIONARY_WATER)
					return i;
				if (blocki.getType() == Material.ICE
						|| blocki.getType() == Material.SNOW
						|| blocki.getType() == Material.SNOW_BLOCK) {
					blocki.setType(Material.WATER);
					blocki.setData(full);
					return i;
				}
				if (blocki.getType() != Material.AIR)
					return 0;
			}
		}
		return 0;
	}

	public static void removeAll() {
		for (Block block : affectedblocks.keySet()) {
			block.setType(Material.AIR);
			affectedblocks.remove(block);
		}
	}
}
