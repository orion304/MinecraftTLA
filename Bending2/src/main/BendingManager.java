package main;

import java.util.ArrayList;
import java.util.concurrent.ConcurrentHashMap;

import net.minecraft.server.EntityFireball;

import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.WorldType;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

import tools.AvatarState;
import tools.BendingType;
import tools.ConfigManager;
import tools.Information;
import tools.Tools;
import waterbending.Bloodbending;
import waterbending.FastSwimming;
import waterbending.FreezeMelt;
import waterbending.HealingWaters;
import waterbending.WalkOnWater;
import waterbending.WaterManipulation;
import waterbending.WaterPassive;
import waterbending.WaterSpout;
import waterbending.WaterWall;
import waterbending.Wave;
import airbending.AirBlast;
import airbending.AirBubble;
import airbending.AirBurst;
import airbending.AirPassive;
import airbending.AirScooter;
import airbending.AirShield;
import airbending.AirSuction;
import airbending.AirSwipe;
import airbending.Speed;
import airbending.Tornado;
import earthbending.Catapult;
import earthbending.CompactColumn;
import earthbending.EarthBlast;
import earthbending.EarthColumn;
import earthbending.EarthPassive;
import earthbending.EarthTunnel;
import earthbending.Tremorsense;
import firebending.FireBlast;
import firebending.FireJet;
import firebending.FireStream;
import firebending.Fireball;
import firebending.Illumination;
import firebending.WallOfFire;

public class BendingManager implements Runnable {

	public Bending plugin;

	public static ArrayList<Player> flyingplayers = new ArrayList<Player>();

	long time;
	long interval;
	long reverttime;
	ArrayList<World> worlds = new ArrayList<World>();
	ConcurrentHashMap<World, Boolean> nights = new ConcurrentHashMap<World, Boolean>();
	ConcurrentHashMap<World, Boolean> days = new ConcurrentHashMap<World, Boolean>();

	public BendingManager(Bending instance) {
		plugin = instance;
		time = System.currentTimeMillis();
		reverttime = time;
	}

	public void run() {

		interval = System.currentTimeMillis() - time;
		time = System.currentTimeMillis();
		Bending.time_step = interval;

		manageAirbending();
		manageEarthbending();
		manageFirebending();
		manageWaterbending();
		// manageMessages();
		AvatarState.manageAvatarStates();
		handleFlying();
		handleDayNight();

	}

	private void manageAirbending() {
		AirPassive.handlePassive(plugin.getServer());
		AirBubble.handleBubbles(plugin.getServer());

		for (int ID : AirBlast.instances.keySet()) {
			AirBlast.progress(ID);
		}

		for (int ID : AirShield.instances.keySet()) {
			AirShield.progress(ID);
		}

		for (int ID : AirSuction.instances.keySet()) {
			AirSuction.progress(ID);
		}

		for (int ID : AirSwipe.instances.keySet()) {
			AirSwipe.progress(ID);
		}

		for (int ID : Speed.instances.keySet()) {
			Speed.progress(ID);
		}

		for (int ID : Tornado.instances.keySet()) {
			Tornado.progress(ID);
		}

		AirBurst.progressAll();

		AirScooter.progressAll();
	}

	private void manageEarthbending() {

		for (int ID : Catapult.instances.keySet()) {
			Catapult.progress(ID);
		}

		for (int ID : EarthColumn.instances.keySet()) {
			EarthColumn.progress(ID);
		}

		for (int ID : CompactColumn.instances.keySet()) {
			CompactColumn.progress(ID);
		}

		for (int ID : EarthBlast.instances.keySet()) {
			EarthBlast.progress(ID);
		}

		for (Player player : EarthTunnel.instances.keySet()) {
			EarthTunnel.progress(player);
		}

		EarthPassive.revertSands();

		Tremorsense.manage(plugin.getServer());

		if (ConfigManager.reverseearthbending
				&& time > reverttime + ConfigManager.revertchecktime) {
			Tools.verbose("Removing up to " + Tools.tempearthblocks.size()
					+ " blocks...");
			reverttime = time;
			for (Block block : Tools.tempearthblocks.keySet()) {
				boolean remove = true;

				Block index = Tools.tempearthblocks.get(block);
				if (Tools.movedearth.containsKey(index)) {
					Information info = Tools.movedearth.get(index);
					if (time < info.getTime() + ConfigManager.revertchecktime) {
						remove = false;
					}
				}

				// for (Player player : block.getWorld().getPlayers()) {
				//
				// if ((Tools.isBender(player, BendingType.Earth) && player
				// .getLocation().distance(block.getLocation()) < 25)
				// || player.getLocation().distance(
				// block.getLocation()) < 3) {
				// remove = false;
				// break;
				// }
				// }

				if (remove)
					Tools.removeEarthbendedBlock(block);

			}

			Tools.verbose("Still " + Tools.tempearthblocks.size()
					+ " remaining.");
		}

	}

	private void manageFirebending() {

		for (int ID : FireStream.instances.keySet()) {
			FireStream.progress(ID);
		}

		for (Block block : FireStream.ignitedblocks.keySet()) {
			if (block.getType() != Material.FIRE) {
				FireStream.ignitedblocks.remove(block);
			}
		}

		for (EntityFireball entity : Fireball.fireballs.keySet()) {
			if (System.currentTimeMillis() >= Fireball.fireballs.get(entity)
					+ Fireball.duration) {
				entity.die();
			}
		}
		for (Player ID : WallOfFire.instances.keySet()) {
			WallOfFire.manageWallOfFire(ID);
		}

		FireBlast.progressAll();

		FireJet.progressAll();

		FireStream.dissipateAll();

		Illumination.manage(plugin.getServer());

	}

	private void manageWaterbending() {
		WalkOnWater.handleFreezing(plugin.getServer());

		FreezeMelt.handleFrozenBlocks();

		WaterSpout.handleSpouts(plugin.getServer());

		for (int ID : WaterManipulation.instances.keySet()) {
			WaterManipulation.progress(ID);
		}

		for (int ID : WaterWall.instances.keySet()) {
			WaterWall.progress(ID);
		}

		for (int ID : Wave.instances.keySet()) {
			Wave.progress(ID);
		}

		Bloodbending.progressAll();

		HealingWaters.heal(plugin.getServer());

		WaterPassive.handlePassive(plugin.getServer());
		FastSwimming.HandleSwim(plugin.getServer());

	}

	private void handleFlying() {

		ArrayList<Player> players = new ArrayList<Player>();
		ArrayList<Player> newflyingplayers = new ArrayList<Player>();
		ArrayList<Player> avatarstateplayers = new ArrayList<Player>();
		ArrayList<Player> airscooterplayers = new ArrayList<Player>();

		players.addAll(Tornado.getPlayers());
		players.addAll(Speed.getPlayers());
		players.addAll(FireJet.getPlayers());
		avatarstateplayers = AvatarState.getPlayers();
		airscooterplayers = AirScooter.getPlayers();
		players.addAll(avatarstateplayers);

		for (Player player : flyingplayers) {
			if (!avatarstateplayers.contains(player)
					&& !airscooterplayers.contains(player))
				player.setFlying(false);
			if (players.contains(player) && !newflyingplayers.contains(player)) {
				newflyingplayers.add(player);
			} else if (!players.contains(player)
					&& !avatarstateplayers.contains(player)
					&& !airscooterplayers.contains(player)) {
				player.setAllowFlight(player.getGameMode() == GameMode.CREATIVE);
			}
		}

		for (Player player : players) {
			if (!flyingplayers.contains(player)) {
				newflyingplayers.add(player);
				player.setAllowFlight(true);
			}
		}

		flyingplayers.clear();
		flyingplayers.addAll(newflyingplayers);

	}

	public static void removeFlyers() {
		for (Player player : flyingplayers) {
			player.setAllowFlight(player.getGameMode() == GameMode.CREATIVE);
		}
	}

	private void handleDayNight() {

		if (worlds.isEmpty())
			for (World world : plugin.getServer().getWorlds())
				if (world.getWorldType() == WorldType.NORMAL) {
					worlds.add(world);
					nights.put(world, false);
					days.put(world, false);
				}

		for (World world : worlds) {
			boolean night = nights.get(world);
			boolean day = days.get(world);
			if (Tools.isDay(world) && !day) {
				for (Player player : world.getPlayers()) {
					if (Tools.isBender(player, BendingType.Fire))
						player.sendMessage(ChatColor.RED
								+ "You feel the strength of the rising sun empowering your firebending.");
				}
				days.replace(world, true);
			}

			if (!Tools.isDay(world) && day) {
				for (Player player : world.getPlayers()) {
					if (Tools.isBender(player, BendingType.Fire))
						player.sendMessage(ChatColor.RED
								+ "You feel the empowering of your firebending subside as the sun sets.");
				}
				days.replace(world, false);
			}

			if (Tools.isNight(world) && !night) {
				for (Player player : world.getPlayers()) {
					if (Tools.isBender(player, BendingType.Water))
						player.sendMessage(ChatColor.BLUE
								+ "You feel the strength of the rising moon empowering your waterbending.");
				}
				nights.replace(world, true);
			}

			if (!Tools.isNight(world) && night) {
				for (Player player : world.getPlayers()) {
					if (Tools.isBender(player, BendingType.Water))
						player.sendMessage(ChatColor.BLUE
								+ "You feel the empowering of your waterbending subside as the moon sets.");
				}
				nights.replace(world, false);
			}
		}

	}
	// private void manageMessages() {
	// for (Player player : newplayers) {
	// player.sendMessage(ChatColor.GOLD
	// + "Use '/bending choose <element>' to get started!");
	// }
	// newplayers.clear();
	// }

}
