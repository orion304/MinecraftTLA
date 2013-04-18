package tools;

import java.util.concurrent.ConcurrentHashMap;

import org.bukkit.entity.Player;

public class Cooldowns {

	private static ConcurrentHashMap<Player, Cooldowns> cooldowns = new ConcurrentHashMap<Player, Cooldowns>();

	private static final long globalcooldown = 250;
	private static ConcurrentHashMap<Abilities, Long> abilitycooldowns = new ConcurrentHashMap<Abilities, Long>();

	private long mostRecent = 0;
	private ConcurrentHashMap<Abilities, Long> individualcooldowns = new ConcurrentHashMap<Abilities, Long>();

	public Cooldowns(Player player) {
		if (!cooldowns.containsKey(player)) {
			mostRecent = System.currentTimeMillis();
			cooldowns.put(player, this);
		}
	}

	public static void forceCooldown(Player player) {
		if (!cooldowns.containsKey(player)) {
			new Cooldowns(player);
		} else {
			Cooldowns cd = cooldowns.get(player);
			cd.mostRecent = System.currentTimeMillis();
		}
	}

	public static void initialize() {
		for (Abilities ability : Abilities.values()) {
			long cd = 0;
			// switch (ability) {
			// case WaterManipulation:
			// cd = 1000;
			// break;
			// case EarthBlast:
			// cd = 1000;
			// break;
			// }
			abilitycooldowns.put(ability, cd);
		}
	}

	public static boolean canUseAbility(Player player) {
		return canUseAbility(player, null);
	}

	public static boolean canUseAbility(Player player, Abilities ability) {
		if (ability == Abilities.AvatarState)
			return true;
		if (!cooldowns.containsKey(player)) {
			new Cooldowns(player);
			return true;
		}

		Cooldowns cooldown = cooldowns.get(player);
		if (System.currentTimeMillis() < cooldown.mostRecent + globalcooldown)
			return false;

		if (ability != null) {
			if (!cooldown.individualcooldowns.containsKey(ability)) {
				cooldown.mostRecent = System.currentTimeMillis();
				cooldown.individualcooldowns.put(ability,
						System.currentTimeMillis());
				return true;
			}
			long lasttime = cooldown.individualcooldowns.get(ability);
			if (System.currentTimeMillis() < lasttime
					+ abilitycooldowns.get(ability))
				return false;

			cooldown.individualcooldowns.put(ability,
					System.currentTimeMillis());
		}

		cooldown.mostRecent = System.currentTimeMillis();
		return true;
	}

}
