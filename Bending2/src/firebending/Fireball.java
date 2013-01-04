package firebending;

import org.bukkit.entity.Player;

import tools.ConfigManager;

public class Fireball {

	// public static ConcurrentHashMap<EntityFireball, Long> fireballs = new
	// ConcurrentHashMap<EntityFireball, Long>();
	public static final long duration = 5000;

	// private static ConcurrentHashMap<Player, Long> timers = new
	// ConcurrentHashMap<Player, Long>();
	public static final long soonesttime = ConfigManager.fireballCooldown;

	// private static final double speedfactor = ConfigManager.fireballSpeed;

	public Fireball(Player player) {
		// if (player.getEyeLocation().getBlock().isLiquid())
		// return;
		// if (timers.containsKey(player)) {
		// if (System.currentTimeMillis() < timers.get(player)
		// + (long) ((double) soonesttime / Tools
		// .getFirebendingDayAugment(player.getWorld()))) {
		// return;
		// }
		// }
		//
		// Location playerLoc = player.getEyeLocation();
		// Vector direction = player
		// .getEyeLocation()
		// .getDirection()
		// .clone()
		// .normalize()
		// .multiply(
		// Tools.firebendingDayAugment(speedfactor,
		// player.getWorld()));
		// double dx = direction.getX();
		// double dy = direction.getY();
		// double dz = direction.getZ();
		//
		// // CraftPlayer craftPlayer = (CraftPlayer) player;
		// // EntityLiving playerEntity = craftPlayer.getHandle();
		// // EntityFireball fireball = new EntityFireball(
		// // ((CraftWorld) player.getWorld()).getHandle(), playerEntity, dx,
		// // dy, dz);
		//
		// double distance = 2;
		// Vector aim = direction.clone();
		// fireball.locX = playerLoc.getX() + aim.getX() * distance;
		// fireball.locY = playerLoc.getY() + aim.getY() * distance;
		// fireball.locZ = playerLoc.getZ() + aim.getZ() * distance;
		//
		// fireball.dirX = dx;
		// fireball.dirY = dy;
		// fireball.dirZ = dz;
		//
		// ((CraftWorld) player.getWorld()).getHandle().addEntity(fireball);
		// fireballs.put(fireball, System.currentTimeMillis());
		// timers.put(player, System.currentTimeMillis());
		// ((Entity) fireball).setVelocity(aim);
		// fireball.setDirection(dx, dy, dz);
		// fireball.
	}

	public static void removeAllFireballs() {
		// for (EntityFireball fireball : fireballs.keySet()) {
		// fireball.die();
		// }
	}

	public static String getDescription() {
		return "This ability has been removed.";
	}
}
