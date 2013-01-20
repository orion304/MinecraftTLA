package tools;

import java.util.ArrayList;
import java.util.concurrent.ConcurrentHashMap;

import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class AvatarState {

	public static ConcurrentHashMap<Player, AvatarState> instances = new ConcurrentHashMap<Player, AvatarState>();

	private static final double factor = 5;

	Player player;

	// boolean canfly = false;

	public AvatarState(Player player) {
		this.player = player;
		if (instances.containsKey(player)) {
			instances.remove(player);
		} else {
			instances.put(player, this);
		}
	}

	public static void manageAvatarStates() {
		for (Player player : instances.keySet()) {
			progress(player);
		}
	}

	public static boolean progress(Player player) {
		return instances.get(player).progress();
	}

	private boolean progress() {
		if (!Tools.canBend(player, Abilities.AvatarState)) {
			instances.remove(player);
			return false;
		}
		addPotionEffects();
		return true;
	}

	private void addPotionEffects() {
		int duration = 70;
		player.addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION,
				duration, ConfigManager.Regeneration - 1));
		player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED,
				duration, ConfigManager.Speed - 1));
		player.addPotionEffect(new PotionEffect(
				PotionEffectType.DAMAGE_RESISTANCE, duration, ConfigManager.Resistance - 1));
		player.addPotionEffect(new PotionEffect(
				PotionEffectType.FIRE_RESISTANCE, duration, ConfigManager.FireResistance - 1));
	}

	public static boolean isAvatarState(Player player) {
		if (instances.containsKey(player))
			return true;
		return false;
	}

	public static double getValue(double value) {
		return factor * value;
	}

	public static int getValue(int value) {
		return (int) factor * value;
	}

	public static ArrayList<Player> getPlayers() {
		ArrayList<Player> players = new ArrayList<Player>();
		for (Player player : instances.keySet()) {
			players.add(player);
		}
		return players;
	}

	public static String getDescription() {
		return "The signature ability of the Avatar, this is a toggle. Click to activate to become "
				+ "nearly unstoppable. While in the Avatar State, the user takes severely reduced damage from "
				+ "all sources, regenerates health rapidly, and is granted extreme speed. Nearly all abilities "
				+ "are incredibly amplified in this state. Additionally, AirShield and FireJet become toggle-able "
				+ "abilities and last until you deactivate them or the Avatar State. Click again with the Avatar "
				+ "State selected to deactivate it.";
	}

}
