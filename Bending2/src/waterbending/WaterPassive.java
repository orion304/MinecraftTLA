package waterbending;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Server;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import tools.BendingType;
import tools.Tools;

public class WaterPassive {

	 private static final int factor = 1;

	public static void handlePassive(Server server) {
		 for (Player player : server.getOnlinePlayers()) {
			 if (Tools.isBender(player, BendingType.Water) && Tools.canBendPassive(player, BendingType.Water)) {
				 Material type = player.getLocation().getBlock().getType();
				 if (type == Material.WATER || type == Material.STATIONARY_WATER) {
				      Location l = Tools.getTargetedLocation(player, factor, Material.WATER.getId(), Material.STATIONARY_WATER.getId());
				      Location pL = player.getLocation().getBlock().getRelative(BlockFace.DOWN).getLocation();
				      Vector vc = new Vector(l.getX() - pL.getX(), pL.getBlockY() - l.getY(), l.getZ() - pL.getZ());
				      vc.multiply(0.2D);
				      player.setVelocity(vc);
		 }
		 }
		 }
	}

	public static Vector handle(Player player, Vector velocity) {
		 Vector vec = velocity.clone();
		 //if (player.getLocation().getBlock().getType() == Material.WATER
		 //|| player.getLocation().getBlock().getType() ==
		 //Material.STATIONARY_WATER)
		 //vec = velocity.clone().normalize().multiply(factor);
		 //Tools.verbose("Is this never called or something?");
		 return vec;
	}

}
