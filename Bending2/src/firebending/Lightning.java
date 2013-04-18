package firebending;

import java.util.concurrent.ConcurrentHashMap;

import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

import tools.Abilities;
import tools.AvatarState;
import tools.ConfigManager;
import tools.Tools;

public class Lightning {

	public static int defaultdistance = ConfigManager.lightningrange;
	private static long defaultwarmup = ConfigManager.lightningwarmup;
	private static double misschance = ConfigManager.lightningmisschance;

	private Player player;
	private long starttime;
	private boolean charged = false;
	public static ConcurrentHashMap<Player, Lightning> instances = new ConcurrentHashMap<Player, Lightning>();

	public Lightning(Player player) {
		if (instances.containsKey(player)) {
			return;
		}
		this.player = player;
		starttime = System.currentTimeMillis();
		instances.put(player, this);

	}

	private void strike() {
		Location targetlocation = getTargetLocation();
		if (!Tools.isRegionProtectedFromBuild(player, Abilities.Lightning,
				targetlocation))
			player.getWorld().strikeLightning(targetlocation);
		instances.remove(player);
	}

	private Location getTargetLocation() {
		int distance = (int) Tools.firebendingDayAugment(defaultdistance,
				player.getWorld());

		Location targetlocation;
		targetlocation = Tools.getTargetedLocation(player, distance);
		Entity target = Tools.getTargettedEntity(player, distance);
		if (target != null) {
			if (target instanceof LivingEntity) {
				targetlocation = target.getLocation();
			}
		}

		if (targetlocation.getBlock().getType() == Material.AIR)
			targetlocation.add(0, -1, 0);
		if (targetlocation.getBlock().getType() == Material.AIR)
			targetlocation.add(0, -1, 0);

		if (misschance != 0 && !AvatarState.isAvatarState(player)) {
			double A = Math.random() * Math.PI * misschance * misschance;
			double theta = Math.random() * Math.PI * 2;
			double r = Math.sqrt(A) / Math.PI;
			double x = r * Math.cos(theta);
			double z = r * Math.sin(theta);

			targetlocation = targetlocation.add(x, 0, z);
		}

		return targetlocation;
	}

	private void progress() {
		int distance = (int) Tools.firebendingDayAugment(defaultdistance,
				player.getWorld());
		long warmup = (int) ((double) defaultwarmup / ConfigManager.dayFactor);
		if (AvatarState.isAvatarState(player))
			warmup = 0;
		if (System.currentTimeMillis() > starttime + warmup)
			charged = true;

		if (charged) {
			if (player.isSneaking()) {
				player.getWorld().playEffect(
						player.getEyeLocation(),
						Effect.SMOKE,
						Tools.getIntCardinalDirection(player.getEyeLocation()
								.getDirection()), distance);
			} else {
				strike();
			}
		} else {
			if (!player.isSneaking()) {
				instances.remove(player);
			}
		}
	}

	public static void progressAll() {
		for (Player player : instances.keySet()) {
			instances.get(player).progress();
		}
	}

	public static String getDescription() {
		return "Hold sneak while selecting this ability to charge up a lightning strike. Once "
				+ "charged, release sneak to discharge the lightning to the targetted location.";
	}

}
