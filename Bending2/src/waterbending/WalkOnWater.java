package waterbending;

import java.util.ArrayList;
import java.util.concurrent.ConcurrentHashMap;

import org.bukkit.Material;
import org.bukkit.Server;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

import tools.Abilities;
import tools.ConfigManager;
import tools.Tools;

public class WalkOnWater {

	private static ConcurrentHashMap<Block, Byte> affectedblocks = new ConcurrentHashMap<Block, Byte>();
	private static ArrayList<Player> players = new ArrayList<Player>();

	private static double radius = ConfigManager.walkOnWaterRadius;

	public static void handleFreezing(Server server) {
		players.clear();
		for (World world : server.getWorlds()) {
			for (Player player : world.getPlayers()) {
				if (Tools.getBendingAbility(player) == Abilities.WalkOnWater
						&& Tools.canBend(player, Abilities.WalkOnWater)) {
					players.add(player);
				}
			}
		}
		freeze();
		thaw();
	}

	private static void freeze() {
		for (Player player : players) {
			for (Block block : Tools.getBlocksAroundPoint(player.getLocation(),
					radius)) {
				double dy = player.getLocation().getY() - block.getY();
				if (dy <= 1 && dy > 0)
					if (block.getType() == Material.WATER
							|| block.getType() == Material.STATIONARY_WATER) {
						byte data = block.getData();
						if (block.getType() == Material.STATIONARY_WATER)
							data = 0x0;
						block.setType(Material.ICE);
						affectedblocks.put(block, data);
					}
			}
		}
	}

	private static void thaw() {
		for (Block block : affectedblocks.keySet()) {
			boolean thaw = true;
			for (Player player : players) {
				if (block.getWorld() != player.getWorld()) {
					thaw = true;
				} else if (player.getLocation().distance(block.getLocation()) <= radius
						&& Tools.canBend(player, Abilities.WalkOnWater)) {
					thaw = false;
				}
			}
			if (thaw) {
				thaw(block);
			}
		}
	}

	public static void thaw(Block block) {
		if (affectedblocks.containsKey(block)) {
			byte data = affectedblocks.get(block);
			affectedblocks.remove(block);
			block.setType(Material.WATER);
			block.setData(data);
		}

	}

	public static boolean canThaw(Block block) {
		if (affectedblocks.containsKey(block)) {
			return false;
		}
		return true;
	}

	public static boolean canPhysicsChange(Block block) {
		for (Block testblock : affectedblocks.keySet()) {
			if (Tools.isBlockTouching(block, testblock)) {
				return false;
			}
		}
		return true;
	}

	private static void thawAll() {
		for (Block block : affectedblocks.keySet()) {
			if (block.getType() == Material.ICE) {
				byte data = affectedblocks.get(block);
				block.setType(Material.WATER);
				block.setData(data);
				affectedblocks.remove(block);
			}
		}
	}

	public static void removeAll() {
		thawAll();
	}

}
