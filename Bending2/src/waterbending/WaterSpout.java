package waterbending;

import java.util.concurrent.ConcurrentHashMap;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Server;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffectType;

import tools.Abilities;
import tools.ConfigManager;
import tools.TempBlock;
import tools.Tools;

public class WaterSpout {

	public static ConcurrentHashMap<Player, WaterSpout> instances = new ConcurrentHashMap<Player, WaterSpout>();
	public static ConcurrentHashMap<Block, Block> affectedblocks = new ConcurrentHashMap<Block, Block>();
	public static ConcurrentHashMap<Block, Block> newaffectedblocks = new ConcurrentHashMap<Block, Block>();
	public static ConcurrentHashMap<Block, Block> baseblocks = new ConcurrentHashMap<Block, Block>();

	private static final int height = ConfigManager.waterSpoutHeight;
	// private static final byte half = 0x4;
	private static final byte full = 0x0;
	private Player player;
	private boolean wasflying, canfly;
	private TempBlock baseblock;

	public WaterSpout(Player player) {
		if (instances.containsKey(player)) {
			instances.get(player).remove();
			return;
		}
		this.player = player;
		wasflying = player.isFlying();
		canfly = player.getAllowFlight();
		player.setAllowFlight(true);
		player.setFlying(true);
		instances.put(player, this);
	}

	private void remove() {
		revertBaseBlock(player);
		player.setAllowFlight(canfly);
		player.setFlying(wasflying);
		instances.remove(player);
	}

	public static void handleSpouts(Server server) {
		// affectedblocks.clear();
		newaffectedblocks.clear();

		for (Player player : instances.keySet()) {
			if (Tools.hasAbility(player, Abilities.WaterSpout)
					&& Tools.canBend(player, Abilities.WaterSpout)) {
				spout(player);
			} else {
				instances.get(player).remove();
			}
		}

		for (Block block : affectedblocks.keySet()) {
			if (!newaffectedblocks.containsKey(block)) {
				remove(block);
			}
		}

		// for (Block block : affectedblocks.keySet()) {
		// boolean remove = true;
		// for (Player player : instances.keySet()) {
		// if (Tools.hasAbility(player, Abilities.WaterSpout)
		// && Tools.canBend(player, Abilities.WaterSpout)
		// && player.getWorld() == block.getWorld()) {
		// Location loc1 = player.getLocation().clone();
		// loc1.setY(0);
		// Location loc2 = block.getLocation().clone();
		// loc2.setY(0);
		// if (loc1.distance(loc2) < 1)
		// remove = false;
		// }
		// }
		// if (remove)
		// remove(block);
		// }

	}

	private static void remove(Block block) {
		affectedblocks.remove(block);
		TempBlock.revertBlock(block, Material.AIR);
		// block.setType(Material.AIR);
		// block.setData(half);
	}

	private static void spout(Player player) {
		player.setSprinting(false);
		player.removePotionEffect(PotionEffectType.SPEED);
		Location location = player.getLocation().clone();
		Block block;
		int height = spoutableWaterHeight(location, player);
		// Tools.verbose(height + " " + WaterSpout.height + " "
		// + affectedblocks.size());
		if (height != 0) {
			for (int i = 0; i <= height - 1; i++) {
				block = location.clone().add(0, -i, 0).getBlock();
				new TempBlock(block, Material.WATER, full);
				// block.setType(Material.WATER);
				// block.setData(full);
				if (!affectedblocks.containsKey(block)) {
					affectedblocks.put(block, block);
				}
				newaffectedblocks.put(block, block);
			}
		} else {
			instances.get(player).remove();
		}
	}

	private static int spoutableWaterHeight(Location location, Player player) {
		Block blocki;
		for (int i = 0; i <= height; i++) {
			blocki = location.clone().add(0, -i, 0).getBlock();
			if (!affectedblocks.contains(blocki)) {
				if (blocki.getType() == Material.WATER
						|| blocki.getType() == Material.STATIONARY_WATER) {
					revertBaseBlock(player);
					return i;
				}
				if (blocki.getType() == Material.ICE
						|| blocki.getType() == Material.SNOW
						|| blocki.getType() == Material.SNOW_BLOCK) {
					if (!TempBlock.isTempBlock(blocki)) {
						revertBaseBlock(player);
						instances.get(player).baseblock = new TempBlock(blocki,
								Material.WATER, full);
					}
					// blocki.setType(Material.WATER);
					// blocki.setData(full);
					return i;
				}
				if (blocki.getType() != Material.AIR) {
					revertBaseBlock(player);
					return 0;
				}
			}
		}
		revertBaseBlock(player);
		return 0;
	}

	public static void revertBaseBlock(Player player) {
		if (instances.containsKey(player)) {
			if (instances.get(player).baseblock != null) {
				instances.get(player).baseblock.revertBlock();
				instances.get(player).baseblock = null;
			}
		}
	}

	public static void removeAll() {
		for (Player player : instances.keySet()) {
			instances.get(player).remove();
		}
		for (Block block : affectedblocks.keySet()) {
			// block.setType(Material.AIR);
			TempBlock.revertBlock(block, Material.AIR);
			affectedblocks.remove(block);
		}
	}

	public static String getDescription() {
		return "To use, simply have it selected. If you are in water at the surface and you try to swim up, water will begin funneling from below and lift you up in a column. You can move while in this column and it will stay under you, provided there's water (or ice, snow, etc) an adequate distance below to pool from. Skilled benders can also use this ability to take no falling damage if they're falling onto ice or shallow water, as the spout will pop up and cushion the fall.";
	}
}
