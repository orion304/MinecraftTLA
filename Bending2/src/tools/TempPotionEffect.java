package tools;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;

public class TempPotionEffect {

	private static ConcurrentHashMap<Player, TempPotionEffect> instances = new ConcurrentHashMap<Player, TempPotionEffect>();

	private Map<PotionEffect, Long> effects = new HashMap<PotionEffect, Long>();

	public TempPotionEffect(Player player, PotionEffect effect, long duration) {
		if (instances.containsKey(player)) {
			TempPotionEffect instance = instances.get(player);
			instance.addEffect(effect);
		} else {
			addEffect(effect);
			instances.put(player, this);
		}
	}

	private void addEffect(PotionEffect effect) {
	}

}
