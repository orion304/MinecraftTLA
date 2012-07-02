package earthbending;

import java.util.concurrent.ConcurrentHashMap;

import org.bukkit.Effect;
import org.bukkit.Material;
import org.bukkit.Server;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;

import tools.Abilities;
import tools.ConfigManager;
import tools.Tools;

public class Tremorsense {

	public static ConcurrentHashMap<Player, Tremorsense> instances = new ConcurrentHashMap<Player, Tremorsense>();
	public static ConcurrentHashMap<Block, Player> blocks = new ConcurrentHashMap<Block, Player>();

	private static final long cooldown = ConfigManager.tremorsenseCooldown;
	private static final int maxdepth = ConfigManager.tremorsenseMaxDepth;
	private static final int radius = ConfigManager.tremorsenseRadius;
	private static final byte lightthreshold = ConfigManager.tremorsenseLightThreshold;

	private Player player;
	private Block block;

	private static ConcurrentHashMap<Player, Long> timers = new ConcurrentHashMap<Player, Long>();

	public Tremorsense(Player player) {
		if (timers.containsKey(player)) {
			if (System.currentTimeMillis() < timers.get(player) + cooldown)
				return;
		}
		if (Tools.isEarthbendable(player.getLocation().getBlock()
				.getRelative(BlockFace.DOWN))) {
			this.player = player;
			timers.put(player, System.currentTimeMillis());
			activate();
		}
	}

	public Tremorsense(Player player, boolean value) {
		this.player = player;
		set();
	}

	private void activate() {
		Block block = player.getLocation().getBlock()
				.getRelative(BlockFace.DOWN);
		for (int i = -radius; i <= radius; i++) {
			for (int j = -radius; j <= radius; j++) {
				boolean earth = false;
				boolean foundair = false;
				Block smokeblock = null;
				for (int k = 0; k <= maxdepth; k++) {
					Block blocki = block.getRelative(BlockFace.EAST, i)
							.getRelative(BlockFace.NORTH, j)
							.getRelative(BlockFace.DOWN, k);
					if (Tools.isEarthbendable(blocki) && !earth) {
						earth = true;
						smokeblock = blocki;
					} else if (!Tools.isEarthbendable(blocki) && earth) {
						foundair = true;
						break;
					} else if (!Tools.isEarthbendable(blocki) && !earth
							&& blocki.getType() != Material.AIR) {
						break;
					}
				}
				if (foundair) {
					smokeblock.getWorld().playEffect(
							smokeblock.getRelative(BlockFace.UP).getLocation(),
							Effect.SMOKE, 1);
				}
			}
		}

	}

	private void set() {
		Block standblock = player.getLocation().getBlock()
				.getRelative(BlockFace.DOWN);

		if (Tools.isEarthbendable(standblock) && block == null) {
			block = standblock;
			player.sendBlockChange(block.getLocation(), 89, (byte) 1);
			instances.put(player, this);
		} else if (Tools.isEarthbendable(standblock)
				&& !block.equals(standblock)) {
			revert();
			block = standblock;
			player.sendBlockChange(block.getLocation(), 89, (byte) 1);
			instances.put(player, this);
		} else if (block == null) {
			return;
		} else if (player.getWorld() != block.getWorld()) {
			revert();
		} else if (!Tools.isEarthbendable(standblock)) {
			revert();
		}
	}

	private void revert() {
		if (block != null) {
			player.sendBlockChange(block.getLocation(), block.getTypeId(),
					block.getData());
			instances.remove(player);
		}
	}

	public static void manage(Server server) {
		for (Player player : server.getOnlinePlayers()) {
			if (instances.containsKey(player)
					&& (!Tools.hasAbility(player, Abilities.Tremorsense)
							|| !Tools.canBend(player, Abilities.Tremorsense) || player
							.getLocation().getBlock().getLightLevel() > lightthreshold)) {
				instances.get(player).revert();
			} else if (instances.containsKey(player)) {
				instances.get(player).set();
			} else if (Tools.hasAbility(player, Abilities.Tremorsense)
					&& Tools.canBend(player, Abilities.Tremorsense)
					&& player.getLocation().getBlock().getLightLevel() < lightthreshold) {
				new Tremorsense(player, false);
			}
		}
	}

	public static void removeAll() {
		for (Player player : instances.keySet()) {
			instances.get(player).revert();
		}

	}

}
