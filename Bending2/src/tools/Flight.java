package tools;

import java.util.ArrayList;
import java.util.concurrent.ConcurrentHashMap;

import org.bukkit.GameMode;
import org.bukkit.entity.Player;

import waterbending.Bloodbending;
import waterbending.WaterSpout;
import airbending.AirGlide;
import airbending.AirScooter;
import airbending.AirSpout;
import airbending.Speed;
import airbending.Tornado;
import earthbending.Catapult;
import firebending.FireJet;

public class Flight {

	private static ConcurrentHashMap<Player, Flight> instances = new ConcurrentHashMap<Player, Flight>();

	private static long duration = 5000;

	private Player player = null, source = null;
	private boolean couldFly = false, wasFlying = false;
	private long time;

	public Flight(Player player) {
		this(player, null);
	}

	public Flight(Player player, Player source) {

		if (instances.containsKey(player)) {
			Flight flight = instances.get(player);
			flight.refresh(source);
			instances.replace(player, flight);
			return;
		}

		couldFly = player.getAllowFlight();
		wasFlying = player.isFlying();
		this.player = player;
		this.source = source;
		time = System.currentTimeMillis();
		instances.put(player, this);
	}

	public static Player getLaunchedBy(Player player) {
		if (instances.containsKey(player)) {
			return instances.get(player).source;
		}

		return null;
	}

	private void revert() {
		player.setAllowFlight(couldFly);
		player.setFlying(wasFlying);
	}

	private void remove() {
		instances.remove(player);
	}

	private void refresh(Player source) {
		this.source = source;
		time = System.currentTimeMillis();
		instances.replace(player, this);
	}

	public static void handle() {

		ArrayList<Player> players = new ArrayList<Player>();
		ArrayList<Player> newflyingplayers = new ArrayList<Player>();
		ArrayList<Player> avatarstateplayers = new ArrayList<Player>();
		ArrayList<Player> airscooterplayers = new ArrayList<Player>();
		ArrayList<Player> waterspoutplayers = new ArrayList<Player>();
		ArrayList<Player> airspoutplayers = new ArrayList<Player>();
		ArrayList<Player> airglideplayers = new ArrayList<Player>();

		players.addAll(Tornado.getPlayers());
//		players.addAll(AirGlide.getPlayers());
		players.addAll(Speed.getPlayers());
		players.addAll(FireJet.getPlayers());
		players.addAll(Catapult.getPlayers());
		avatarstateplayers = AvatarState.getPlayers();
		airscooterplayers = AirScooter.getPlayers();
		waterspoutplayers = WaterSpout.getPlayers();
		airspoutplayers = AirSpout.getPlayers();
		airglideplayers = AirGlide.getPlayers();

		for (Player player : instances.keySet()) {
			Flight flight = instances.get(player);
			if (avatarstateplayers.contains(player)
					|| airscooterplayers.contains(player)
					|| waterspoutplayers.contains(player)
					|| airspoutplayers.contains(player)) {
				continue;
			}
			if (Bloodbending.isBloodbended(player)) {
				player.setAllowFlight(true);
				player.setFlying(false);
				continue;
			}

			if (players.contains(player)) {
				flight.refresh(null);
				player.setAllowFlight(true);
				if (player.getGameMode() != GameMode.CREATIVE)
					player.setFlying(false);
				newflyingplayers.add(player);
				continue;
			}

			if (flight.source == null) {
				flight.revert();
				flight.remove();
			} else {
				if (System.currentTimeMillis() > flight.time + duration) {
					flight.remove();
				}
			}

		}

	}

	public static void removeAll() {
		for (Player player : instances.keySet()) {
			Flight flight = instances.get(player);
			if (flight.source != null)
				flight.revert();
			flight.remove();
		}
	}

}
