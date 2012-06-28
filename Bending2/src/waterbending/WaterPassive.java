package waterbending;

import org.bukkit.Server;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

public class WaterPassive {

	// private static final double factor = 5;

	public static void handlePassive(Server server) {
		// for (Player player : server.getOnlinePlayers()) {
		// if (Tools.isBender(player, BendingType.Water)
		// && Tools.canBendPassive(player, BendingType.Water)) {
		// Material type = player.getLocation().getBlock().getType();
		// if (type == Material.WATER || type == Material.STATIONARY_WATER) {
		// player.setVelocity(player.getVelocity().normalize()
		// .multiply(factor));
		// }
		// }
		// }
	}

	public static Vector handle(Player player, Vector velocity) {
		Vector vec = velocity.clone();
		// if (player.getLocation().getBlock().getType() == Material.WATER
		// || player.getLocation().getBlock().getType() ==
		// Material.STATIONARY_WATER)
		// vec = velocity.clone().normalize().multiply(factor);
		// Tools.verbose("Is this never called or something?");
		return vec;
	}

}
