package tools;

import java.util.ArrayList;
import java.util.concurrent.ConcurrentHashMap;

import main.ConfigValues;

import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.kitteh.tag.TagAPI;

public class AvatarState {

	public static ConcurrentHashMap<Player, AvatarState> instances = new ConcurrentHashMap<Player, AvatarState>();

	private static final double factor = ConfigValues.AvatarStatePowerFactor;
	private static final boolean regeneration = ConfigValues.AvatarStateRegenerationEnabled;
	private static final boolean speed = ConfigValues.AvatarStateSpeedEnabled;
	private static final boolean damageresistance = ConfigValues.AvatarStateResistanceEnabled;
	private static final boolean fireresistance = ConfigValues.AvatarStateFireResistanceEnabled;

	private static final int regenpower = ConfigValues.AvatarStateRegenerationPower - 1;
	private static final int speedpower = ConfigValues.AvatarStateSpeedPower - 1;
	private static final int resistancepower = ConfigValues.AvatarStateResistancePower - 1;
	private static final int fireresistancepower = ConfigValues.AvatarStateFireResistancePower - 1;

	Player player;

	private long time;
	private static final long interval = 480000;

	// boolean canfly = false;

	public AvatarState(Player player) {
		this.player = player;
		time = System.currentTimeMillis();
		if (instances.containsKey(player)) {
			instances.remove(player);
			player.addPotionEffect(new PotionEffect(PotionEffectType.WEAKNESS, 300, 0));
			TagAPI.refreshPlayer(player);
			return;
		}
		if (BendingPlayer.getBendingPlayer(player).isOnCooldown(Abilities.AvatarState)) {
			return;
		}
		new Flight(player);
		instances.put(player, this);
		TagAPI.refreshPlayer(player);
		BendingPlayer.getBendingPlayer(player).cooldown(Abilities.AvatarState);

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
		if (System.currentTimeMillis() - time >= interval) {
			new AvatarState(player);
		}
		addPotionEffects();
		return true;
	}

	private void addPotionEffects() {
		int duration = 70;
		if (regeneration) {
			player.addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION,
					duration, regenpower));
		}
		if (speed) {
			player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED,
					duration, speedpower));
		}
		if (damageresistance) {
			player.addPotionEffect(new PotionEffect(
					PotionEffectType.DAMAGE_RESISTANCE, duration, resistancepower));
		}
		if (fireresistance) {
			player.addPotionEffect(new PotionEffect(
					PotionEffectType.FIRE_RESISTANCE, duration, fireresistancepower));
		}
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
