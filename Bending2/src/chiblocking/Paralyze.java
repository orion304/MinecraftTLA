package chiblocking;

import java.util.concurrent.ConcurrentHashMap;

import org.bukkit.entity.Creature;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

import tools.Abilities;
import tools.AvatarState;
import tools.BendingType;
import tools.ConfigManager;
import tools.Tools;

public class Paralyze {

	private static ConcurrentHashMap<Entity, Long> entities = new ConcurrentHashMap<Entity, Long>();
	private static ConcurrentHashMap<Entity, Long> cooldowns = new ConcurrentHashMap<Entity, Long>();

	private static final long cooldown = ConfigManager.paralyzeCooldown;
	private static final long duration = ConfigManager.paralyzeDuration;

	public Paralyze(Player sourceplayer, Entity targetentity) {
		if (Tools.isBender(sourceplayer.getName(), BendingType.ChiBlocker)
				&& Tools.getBendingAbility(sourceplayer) == Abilities.Paralyze) {
			if (cooldowns.containsKey(targetentity)) {
				if (System.currentTimeMillis() < cooldowns.get(targetentity)
						+ cooldown) {
					return;
				} else {
					cooldowns.remove(targetentity);
				}
			}
			paralyze(targetentity);
			cooldowns.put(targetentity, System.currentTimeMillis());
		}
	}

	private static void paralyze(Entity entity) {
		entities.put(entity, System.currentTimeMillis());
		if (entity instanceof Creature) {
			((Creature) entity).setTarget(null);
		}
	}

	public static boolean isParalyzed(Entity entity) {
		if (entity instanceof Player) {
			if (AvatarState.isAvatarState((Player) entity))
				return false;
		}
		if (entities.containsKey(entity)) {
			if (System.currentTimeMillis() < entities.get(entity) + duration) {
				return true;
			}
			entities.remove(entity);
		}
		return false;

	}

	public static String getDescription() {
		return "Paralyzes the target, making them unable to do anything for a short "
				+ "period of time. This ability has a long cooldown.";
	}
}
