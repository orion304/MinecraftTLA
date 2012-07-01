package firebending;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

import tools.Abilities;
import tools.BendingType;
import tools.ConfigManager;
import tools.Tools;
import airbending.AirBlast;

public class Extinguish {

	private static double range = ConfigManager.extinguishRange;
	private static double radius = ConfigManager.extinguishRadius;
	private static byte full = AirBlast.full;

	public Extinguish(Player player) {
		for (Block block : Tools.getBlocksAroundPoint(
				player.getTargetBlock(null, (int) range).getLocation(), radius)) {
			if (block.getType() == Material.FIRE) {
				block.setType(Material.AIR);
			} else if (block.getType() == Material.STATIONARY_LAVA) {
				block.setType(Material.OBSIDIAN);
			} else if (block.getType() == Material.LAVA) {
				if (block.getData() == full) {
					block.setType(Material.OBSIDIAN);
				} else {
					block.setType(Material.COBBLESTONE);
				}
			}
		}
	}

	public static boolean canBurn(Player player) {
		if (Tools.getBendingAbility(player) == Abilities.Extinguish
				|| FireJet.checkTemporaryImmunity(player)) {
			player.setFireTicks(0);
			return false;
		}

		if (player.getFireTicks() > 80
				&& Tools.canBendPassive(player, BendingType.Fire)) {
			player.setFireTicks(80);
		}

		// Tools.verbose(player.getFireTicks());

		return true;
	}
}
