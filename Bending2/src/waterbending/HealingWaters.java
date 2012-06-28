package waterbending;

import org.bukkit.Material;
import org.bukkit.Server;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import tools.Abilities;
import tools.Tools;

public class HealingWaters {

	private static final double range = 5;
	private static final long interval = 750;

	private static long time = 0;

	public static void heal(Server server) {
		if (System.currentTimeMillis() - time >= interval) {
			time = System.currentTimeMillis();
			for (Player player : server.getOnlinePlayers()) {
				if (Tools.getBendingAbility(player) == Abilities.HealingWaters
						&& Tools.canBend(player, Abilities.HealingWaters)) {
					heal(player);
				}
			}
		}
	}

	private static void heal(Player player) {
		if (inWater(player)) {
			if (player.isSneaking()) {
				Entity entity = Tools.getTargettedEntity(player, range);
				if (entity instanceof Player && inWater(entity)) {
					giveHP((Player) entity);
				}
			} else {
				giveHP(player);
			}
		}
	}

	private static void giveHP(Player player) {
		if (!player.isDead() && player.getHealth() < 20) {
			// int hp = player.getHealth();
			// if (hp < 20) {
			// hp++;
			// }
			// player.setHealth(hp);
			applyHealing(player);
		}
	}

	private static boolean inWater(Entity entity) {
		if (entity.getLocation().getBlock().getType() == Material.WATER
				|| entity.getLocation().getBlock().getType() == Material.STATIONARY_WATER)
			return true;
		return false;
	}

	private static void applyHealing(Player player) {
		player.addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION,
				70, 1));
	}
}
