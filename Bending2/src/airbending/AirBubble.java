package airbending;

import java.util.concurrent.ConcurrentHashMap;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Server;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

import tools.Abilities;
import tools.ConfigManager;
import tools.Tools;
import waterbending.WaterManipulation;

public class AirBubble {

	public static ConcurrentHashMap<Integer, AirBubble> instances = new ConcurrentHashMap<Integer, AirBubble>();

	private static double radius = ConfigManager.airBubbleRadius;
	// private static byte full = AirBlast.full;

	private Player player;
	private ConcurrentHashMap<Block, Byte> waterorigins;

	public AirBubble(Player player) {
		this.player = player;
		waterorigins = new ConcurrentHashMap<Block, Byte>();
		instances.put(player.getEntityId(), this);
	}

	private void pushWater() {
		Location location = player.getLocation();

		for (Block block : Tools.getBlocksAroundPoint(location, radius)) {
			if (block.getType() == Material.STATIONARY_WATER
					|| block.getType() == Material.WATER) {
				if (WaterManipulation.canBubbleWater(block)) {

					waterorigins.put(block, block.getData());

					block.setType(Material.AIR);
				}
			}

		}

		for (Block block : waterorigins.keySet()) {
			if (block.getWorld() != location.getWorld()) {
				byte data = waterorigins.get(block);
				block = block.getLocation().getBlock();
				if (block.getType() == Material.AIR) {
					block.setType(Material.WATER);
					block.setData(data);
				}
				waterorigins.remove(block);
			} else if (block.getLocation().distance(location) > radius) {
				byte data = waterorigins.get(block);
				block = block.getLocation().getBlock();
				if (block.getType() == Material.AIR) {
					block.setType(Material.WATER);
					block.setData(data);
				}
				waterorigins.remove(block);
			}
		}
	}

	public boolean progress() {
		if (player.isDead() || !player.isOnline()) {
			instances.remove(player.getEntityId());
			return false;
		}
		if (((Tools.getBendingAbility(player) == Abilities.AirBubble) && Tools
				.canBend(player, Abilities.AirBubble))
				|| ((Tools.getBendingAbility(player) == Abilities.WaterBubble) && Tools
						.canBend(player, Abilities.WaterBubble))) {
			pushWater();
			return true;
		}
		removeBubble();
		return false;
		// if ((Tools.getBendingAbility(player) != Abilities.AirBubble && Tools
		// .getBendingAbility(player) != Abilities.WaterBubble)) {
		// removeBubble();
		// return false;
		// }
		// pushWater();
		// return true;
	}

	public static void handleBubbles(Server server) {
		for (World world : server.getWorlds()) {
			for (Player player : world.getPlayers()) {
				if ((Tools.getBendingAbility(player) == Abilities.AirBubble || Tools
						.getBendingAbility(player) == Abilities.WaterBubble)
						&& !instances.containsKey(player.getEntityId())) {
					new AirBubble(player);
				}
			}
		}

		for (int ID : instances.keySet()) {
			progress(ID);
		}
	}

	private void removeBubble() {
		for (Block block : waterorigins.keySet()) {
			byte data = waterorigins.get(block);
			block = block.getLocation().getBlock();
			if (block.getType() == Material.AIR) {
				block.setType(Material.WATER);
				block.setData(data);
			}
		}
		instances.remove(player.getEntityId());
	}

	public static boolean progress(int ID) {
		return instances.get(ID).progress();
	}

	public boolean blockInBubble(Block block) {
		if (block.getWorld() != player.getWorld()) {
			return false;
		}
		if (block.getLocation().distance(player.getLocation()) <= radius) {
			return true;
		}
		return false;
	}

	public static boolean canFlowTo(Block block) {
		for (int ID : instances.keySet()) {
			if (instances.get(ID).blockInBubble(block)) {
				return false;
			}
		}
		return true;
	}

	public static void removeAll() {
		for (int id : instances.keySet()) {
			instances.get(id).removeBubble();
		}
	}

}
