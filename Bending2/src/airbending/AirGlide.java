package airbending;

import java.util.ArrayList;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;

import main.ConfigValues;

import org.bukkit.Effect;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;

import com.etriacraft.bendingplus.APIs.AirGliderAPI;

import tools.Flight;
import tools.Tools;

public class AirGlide {
	
	private static ConcurrentHashMap<Player, AirGlide> instances = new ConcurrentHashMap<Player, AirGlide>();
	private Player player;
	
	public AirGlide(Player player) {
		this.player = player;
		if (instances.containsKey(player)) {
			instances.remove(player);
			return;
		}
		if (player.getItemInHand() != null) {
			if (AirGliderAPI.isAirGlider(player.getItemInHand())) {
				player.setVelocity(player.getEyeLocation().getDirection().clone().normalize().multiply(0.7));
				new Flight(player);
				instances.put(player, this);
				System.out.println("test");
			}
		}

	}
	
	public void progress() {
		if (player.isDead() || !player.isOnline()) {
			instances.remove(player);
			return;
		}
		if (Tools.isWater(player.getLocation().getBlock()) ||
				player.getItemInHand() == null ||
				!AirGliderAPI.isAirGlider(player.getItemInHand())) {
			instances.remove(player);
		}
		player.setVelocity(player.getEyeLocation().getDirection().clone().normalize().multiply(0.7));
		int maxheight = (player.getFoodLevel() * 3) + ConfigValues.SeaLevel;
		if (player.getLocation().getY() > maxheight) {
			instances.remove(player);
		}
		player.getWorld().playEffect(player.getLocation(), Effect.SMOKE, 1);
		Random rand = new Random();
		if (rand.nextInt(499) == 0) {
			player.setFoodLevel(player.getFoodLevel() - 1);
		}
	}

	public static void progressAll() {
		for (Player player: instances.keySet()) {
			instances.get(player).progress();
		}
	}
	
	public static ArrayList<Player> getPlayers() {
		ArrayList<Player> players = new ArrayList<Player>();
		for (Player player: instances.keySet()) {
			players.add(player);
		}
		return players;
	}
	
	public static String getDescription() {
		return "The Air Glide is an easy form of transporation for Airbenders. To use, bind the ability while holding the Air Glider "
				+ "item and left click. Be careful with using your glider for extended periods of time, as your hunger bar will "
				+ "deplete at a faster than normal rate. The height in which you can glide is also proportional to your hunger bar.";
	}

}
