package firebending;

import java.util.concurrent.ConcurrentHashMap;

import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.TNTPrimed;
import org.bukkit.util.Vector;

import tools.Abilities;
import tools.AvatarState;
import tools.ConfigManager;
import tools.Tools;

public class Fireball {

	public static ConcurrentHashMap<Integer, Fireball> instances = new ConcurrentHashMap<Integer, Fireball>();

	private static long defaultchargetime = 2000;
	private static long interval = 25;
	private static double radius = 1.5;

	private static int ID = Integer.MIN_VALUE;

	private int id;
	private double range = 20;
	private Player player;
	private Location origin;
	private Location location;
	private Vector direction;
	private long starttime;
	private long time;
	private long chargetime = defaultchargetime;
	private boolean charged = false;
	private boolean launched = false;

	public Fireball(Player player) {
		this.player = player;
		time = System.currentTimeMillis();
		starttime = time;
		if (Tools.isDay(player.getWorld())) {
			chargetime = (long) (chargetime / ConfigManager.dayFactor);
		}
		if (AvatarState.isAvatarState(player))
			chargetime = 0;
		range = Tools.firebendingDayAugment(range, player.getWorld());
		if (!player.getEyeLocation().getBlock().isLiquid()) {
			id = ID;
			instances.put(id, this);
			if (ID == Integer.MAX_VALUE)
				ID = Integer.MIN_VALUE;
			ID++;
		}

	}

	private void progress() {
		if ((!Tools.canBend(player, Abilities.FireBlast) || Tools
				.getBendingAbility(player) != Abilities.FireBlast) && !launched) {
			remove();
			return;
		}

		if (System.currentTimeMillis() > starttime + chargetime) {
			charged = true;
		}

		if (!player.isSneaking() && !charged) {
			new FireBlast(player);
			remove();
			return;
		}

		if (!player.isSneaking() && !launched) {
			launched = true;
			location = player.getEyeLocation();
			origin = location.clone();
			direction = location.getDirection().normalize().multiply(radius);
		}

		if (System.currentTimeMillis() > time + interval) {
			if (launched)
				if (Tools.isRegionProtectedFromBuild(player, Abilities.Blaze,
						location)) {
					remove();
					return;
				}

			time = System.currentTimeMillis();

			if (!launched && !charged)
				return;
			if (!launched) {
				player.getWorld().playEffect(player.getEyeLocation(),
						Effect.MOBSPAWNER_FLAMES, 0, 3);
				return;
			}

			location = location.clone().add(direction);
			if (location.distance(origin) > range) {
				remove();
				return;
			}

			if (Tools.isSolid(location.getBlock())) {
				explode();
				return;
			} else if (location.getBlock().isLiquid()) {
				remove();
				return;
			}

			fireball();

		}

	}

	private void fireball() {
		for (Block block : Tools.getBlocksAroundPoint(location, radius)) {
			block.getWorld().playEffect(block.getLocation(),
					Effect.MOBSPAWNER_FLAMES, 0, 20);
		}

		for (Entity entity : Tools.getEntitiesAroundPoint(location, 2 * radius)) {
			if (entity.getEntityId() == player.getEntityId())
				continue;
			entity.setFireTicks(120);
			if (entity instanceof LivingEntity) {
				explode();
				return;
			}
		}
	}

	public static boolean isCharging(Player player) {
		for (int id : instances.keySet()) {
			Fireball ball = instances.get(id);
			if (ball.player == player && !ball.launched)
				return true;
		}
		return false;
	}

	private void explode() {
		// List<Block> blocks = Tools.getBlocksAroundPoint(location, 3);
		// List<Block> blocks2 = new ArrayList<Block>();

		// Tools.verbose("Fireball Explode!");
		boolean explode = true;
		for (Block block : Tools.getBlocksAroundPoint(location, 3)) {
			if (Tools.isRegionProtectedFromBuild(player, Abilities.FireBlast,
					block.getLocation())) {
				explode = false;
				break;
			}
		}
		if (explode) {
			Entity tnt = player.getWorld().spawn(location, TNTPrimed.class);
			((TNTPrimed) tnt).setFuseTicks(0);
			((TNTPrimed) tnt).setYield(2);
		}
		// location.getWorld().createExplosion(location, 1);

		ignite(location);
		remove();
	}

	private void ignite(Location location) {
		for (Block block : Tools.getBlocksAroundPoint(location,
				FireBlast.affectingradius)) {
			if (FireStream.isIgnitable(player, block)) {
				block.setType(Material.FIRE);
				if (FireBlast.dissipate) {
					FireStream.ignitedblocks.put(block, player);
					FireStream.ignitedtimes.put(block,
							System.currentTimeMillis());
				}
			}
		}
	}

	public static void progressAll() {
		for (int id : instances.keySet())
			instances.get(id).progress();
	}

	private void remove() {
		instances.remove(id);
	}

	public static void removeAll() {
		for (int id : instances.keySet())
			instances.get(id).remove();
	}

	public static void removeFireballsAroundPoint(Location location,
			double radius) {
		for (int id : instances.keySet()) {
			Fireball fireball = instances.get(id);
			if (!fireball.launched)
				continue;
			Location fireblastlocation = fireball.location;
			if (location.getWorld() == fireblastlocation.getWorld()) {
				if (location.distance(fireblastlocation) <= radius)
					instances.remove(id);
			}
		}

	}
}
