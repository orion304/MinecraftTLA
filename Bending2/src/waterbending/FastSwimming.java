package waterbending;

import org.bukkit.Server;
import org.bukkit.entity.Player;

import tools.Abilities;
import tools.BendingType;
import tools.ConfigManager;
import tools.TempBlock;
import tools.Tools;

public class FastSwimming {

	// private static Map<Player, Location> locations = new HashMap<Player,
	// Location>();
	// private static Map<Player, Long> timers = new HashMap<Player, Long>();
	// private static long interval = ConfigManager.fastSwimmingInterval;
	private static double factor = ConfigManager.fastSwimmingFactor;

	public static void HandleSwim(Server server) {
		for (Player player : server.getOnlinePlayers()) {
			Abilities ability = Tools.getBendingAbility(player);
			if (Tools.isBender(player.getName(), BendingType.Water)
					&& Tools.canBendPassive(player, BendingType.Water)
					&& player.isSneaking()
					&& Tools.isWater(player.getLocation().getBlock())
					&& !TempBlock.isTempBlock(player.getLocation().getBlock())
					&& !(ability == Abilities.WaterManipulation
							|| ability == Abilities.Surge
							|| ability == Abilities.HealingWaters
							|| ability == Abilities.PhaseChange
							|| ability == Abilities.Bloodbending
							|| ability == Abilities.IceSpike || Tools
							.getBendingAbility(player) == Abilities.OctopusForm)) {
				player.setVelocity(player.getEyeLocation().getDirection()
						.clone().normalize().multiply(factor));
			}
		}
		// for (Player p : s.getOnlinePlayers()) {
		// if ((!Tools.isBender(p, BendingType.Water)
		// || !Tools.canBendPassive(p, BendingType.Water) || p
		// .isSneaking()) && timers.containsKey(p))
		// timers.remove(p);
		// if (Tools.isBender(p, BendingType.Water)
		// && Tools.canBendPassive(p, BendingType.Water)
		// && p.getLocation().getBlock().isLiquid()
		// && !timers.containsKey(p)) {
		// timers.put(p, System.currentTimeMillis());
		// }
		// if (timers.containsKey(p)) {
		// if (timers.get(p) + (interval - 21) >= System
		// .currentTimeMillis()) {
		// locations.put(p, p.getLocation().getBlock().getLocation());
		// }
		// }
		// if (timers.containsKey(p)) {
		// if (!(timers.get(p) + interval >= System.currentTimeMillis())
		// && locations.containsKey(p)
		// && ((int) locations.get(p).getX() != (int) p
		// .getLocation().getBlock().getLocation().getX() || (int) locations
		// .get(p).getZ() != (int) p.getLocation()
		// .getBlock().getLocation().getZ())
		// && p.getLocation().getBlock().isLiquid()) {
		//
		// if (!p.getEyeLocation().getBlock().isLiquid()) {
		// timers.put(p, System.currentTimeMillis());
		// if ((p.getLocation().getYaw() > -45 && p.getLocation()
		// .getYaw() <= 45)
		// && locations.get(p).getZ() < p.getLocation()
		// .getZ()) {
		// Vector v = p.getLocation().getDirection().setY(0);
		// p.setVelocity(v.normalize().multiply(factor));
		// } else if ((p.getLocation().getYaw() > 45 && p
		// .getLocation().getYaw() <= 135)
		// && locations.get(p).getX() > p.getLocation()
		// .getX()) {
		// Vector v = p.getLocation().getDirection().setY(0);
		// p.setVelocity(v.normalize().multiply(factor));
		// } else if ((p.getLocation().getYaw() > 135 && p
		// .getLocation().getYaw() <= 225)
		// && locations.get(p).getZ() > p.getLocation()
		// .getZ()) {
		// Vector v = p.getLocation().getDirection().setY(0);
		// p.setVelocity(v.normalize().multiply(factor));
		// } else if ((p.getLocation().getYaw() > 225 && p
		// .getLocation().getYaw() <= 315)
		// && locations.get(p).getX() < p.getLocation()
		// .getX()) {
		// Vector v = p.getLocation().getDirection().setY(0);
		// p.setVelocity(v.normalize().multiply(factor));
		// }
		// } else {
		// timers.put(p, System.currentTimeMillis());
		// Vector v = p.getLocation().getDirection().normalize()
		// .multiply(factor);
		// p.setVelocity(v);
		// }
		// }
		// }
		// }
	}
}
