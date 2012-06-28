package airbending;

import java.util.concurrent.ConcurrentHashMap;

import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import tools.BendingType;
import tools.Tools;

public class Speed {

	public static ConcurrentHashMap<Integer, Speed> instances = new ConcurrentHashMap<Integer, Speed>();

	private Player player;
	private int id;

	public Speed(Player player) {
		this.player = player;
		id = player.getEntityId();
		instances.put(id, this);
	}

	public boolean progress() {
		if (player.isSprinting() && Tools.isBender(player, BendingType.Air)
				&& Tools.canBendPassive(player, BendingType.Air)) {
			applySpeed();
			return true;
		}
		instances.remove(id);
		return false;
	}

	private void applySpeed() {
		player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 70, 1));
	}

	public static boolean progress(int ID) {
		return instances.get(ID).progress();
	}

}
