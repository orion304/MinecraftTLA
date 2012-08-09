package airbending;

import java.util.ArrayList;
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

	// private boolean canfly = false;

	public Speed(Player player) {
		this.player = player;
		id = player.getEntityId();
		// canfly = player.getAllowFlight();
		player.setAllowFlight(true);
		instances.put(id, this);
	}

	public boolean progress() {
		// if (player.isFlying() && player.getGameMode() != GameMode.CREATIVE
		// && !AirScooter.getPlayers().contains(player)
		// && !AvatarState.isAvatarState(player))
		// player.setFlying(false);
		if (player.isSprinting() && Tools.isBender(player.getName(), BendingType.Air)
				&& Tools.canBendPassive(player, BendingType.Air)) {
			applySpeed();
			return true;
		}
		if (player.isSprinting()
				&& Tools.isBender(player.getName(), BendingType.ChiBlocker)) {
			applySpeed();
			return true;
		}
		// player.setAllowFlight(canfly);
		instances.remove(id);
		return false;
	}

	private void applySpeed() {
		int factor = 0;
		if (Tools.isBender(player.getName(), BendingType.Air)
				&& Tools.canBendPassive(player, BendingType.Air)) {
			factor = 1;
		}
		int jumpfactor = factor + 1;
		player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 70,
				factor));
		player.addPotionEffect(new PotionEffect(PotionEffectType.JUMP, 70,
				jumpfactor));
	}

	public static boolean progress(int ID) {
		return instances.get(ID).progress();
	}

	public static ArrayList<Player> getPlayers() {
		ArrayList<Player> players = new ArrayList<Player>();
		for (int id : instances.keySet()) {
			Player player = instances.get(id).player;
			if (player.isSprinting()) {
				players.add(instances.get(id).player);
			} else {
				instances.remove(id);
			}
		}
		return players;
	}

}
