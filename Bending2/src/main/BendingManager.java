package main;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.concurrent.ConcurrentHashMap;

import net.minecraft.server.EntityFireball;

import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.WorldType;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

import tools.Abilities;
import tools.AvatarState;
import tools.BendingType;
import tools.ConfigManager;
import tools.Information;
import tools.Tools;
import waterbending.Bloodbending;
import waterbending.FastSwimming;
import waterbending.FreezeMelt;
import waterbending.HealingWaters;
import waterbending.IceSpike;
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
import chiblocking.RapidPunch;
import earthbending.Catapult;
import earthbending.CompactColumn;
import earthbending.EarthArmor;
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
import firebending.Lightning;
import firebending.WallOfFire;

public class BendingManager implements Runnable {

	public Bending plugin;

	public static ArrayList<Player> flyingplayers = new ArrayList<Player>();

	private boolean verbose = true;
	private long verbosetime;
	private long verboseinterval = 3 * 60 * 1000;

	long time;
	long interval;
	long reverttime;
	ArrayList<World> worlds = new ArrayList<World>();
	ConcurrentHashMap<World, Boolean> nights = new ConcurrentHashMap<World, Boolean>();
	ConcurrentHashMap<World, Boolean> days = new ConcurrentHashMap<World, Boolean>();

	public BendingManager(Bending bending) {
		plugin = bending;
		time = System.currentTimeMillis();
		verbosetime = System.currentTimeMillis();
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
		manageChiBlocking();
		// manageMessages();
		AvatarState.manageAvatarStates();
		handleFlying();
		handleDayNight();

		if (verbose
				&& System.currentTimeMillis() > verbosetime + verboseinterval)
			handleVerbosity();

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

		for (Player player : EarthArmor.instances.keySet()) {
			EarthArmor.moveArmor(player);
		}

		EarthPassive.revertSands();

		Tremorsense.manage(plugin.getServer());

		if (ConfigManager.reverseearthbending
				&& time > reverttime + ConfigManager.revertchecktime) {
			Tools.writeToLog("Removing up to " + Tools.tempearthblocks.size()
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

			for (Block block : Tools.movedearth.keySet()) {
				Information info = Tools.movedearth.get(block);
				if (time >= info.getTime() + ConfigManager.revertchecktime) {
					if (Tools.tempearthblocks.containsKey(info.getBlock()))
						Tools.verbose("PROBLEM!");
					block.setType(info.getType());
					Tools.movedearth.remove(block);
				}
			}

			Tools.writeToLog("Still " + Tools.tempearthblocks.size()
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
			if (!entity.isAlive()) {
				Fireball.fireballs.remove(entity);
			}
		}
		for (int ID : WallOfFire.instances.keySet()) {
			WallOfFire.manageWallOfFire(ID);
		}

		Lightning.progressAll();

		FireBlast.progressAll();

		FireJet.progressAll();

		FireStream.dissipateAll();

		Illumination.manage(plugin.getServer());

	}

	private void manageChiBlocking() {
		for (Player p : RapidPunch.targets.keySet())
			RapidPunch.startPunch(p);
	}

	private void manageWaterbending() {
		// WalkOnWater.handleFreezing(plugin.getServer());

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

		for (int ID : IceSpike.instances.keySet()) {
			IceSpike.instances.get(ID).progress();
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
		ArrayList<Player> waterspoutplayers = new ArrayList<Player>();

		players.addAll(Tornado.getPlayers());
		players.addAll(Speed.getPlayers());
		players.addAll(FireJet.getPlayers());
		avatarstateplayers = AvatarState.getPlayers();
		airscooterplayers = AirScooter.getPlayers();
		waterspoutplayers = WaterSpout.getPlayers();
		// players.addAll(avatarstateplayers);

		for (Player player : plugin.getServer().getOnlinePlayers()) {
			if (avatarstateplayers.contains(player)
					|| airscooterplayers.contains(player)
					|| waterspoutplayers.contains(player)) {
				continue;
			}
			if (Bloodbending.isBloodbended(player)) {
				player.setAllowFlight(true);
				player.setFlying(false);
				continue;
			}
			if (flyingplayers.contains(player) && players.contains(player)) {
				player.setAllowFlight(true);
				if (player.getGameMode() != GameMode.CREATIVE)
					player.setFlying(false);
				newflyingplayers.add(player);
			} else if (players.contains(player)
					&& !flyingplayers.contains(player)) {
				newflyingplayers.add(player);
				if (player.getGameMode() != GameMode.CREATIVE)
					player.setFlying(false);
			} else if (flyingplayers.contains(player)
					&& !players.contains(player)) {
				player.setAllowFlight(player.getGameMode() == GameMode.CREATIVE);
				if (player.getGameMode() != GameMode.CREATIVE)
					player.setFlying(false);
			} else {
				player.setAllowFlight(player.getGameMode() == GameMode.CREATIVE);
				if (player.getGameMode() != GameMode.CREATIVE)
					player.setFlying(false);
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
					if (Tools.isBender(player.getName(), BendingType.Fire))
						player.sendMessage(ChatColor.RED
								+ "You feel the strength of the rising sun empowering your firebending.");
				}
				days.replace(world, true);
			}

			if (!Tools.isDay(world) && day) {
				for (Player player : world.getPlayers()) {
					if (Tools.isBender(player.getName(), BendingType.Fire))
						player.sendMessage(ChatColor.RED
								+ "You feel the empowering of your firebending subside as the sun sets.");
				}
				days.replace(world, false);
			}

			if (Tools.isNight(world) && !night) {
				for (Player player : world.getPlayers()) {
					if (Tools.isBender(player.getName(), BendingType.Water))
						player.sendMessage(ChatColor.BLUE
								+ "You feel the strength of the rising moon empowering your waterbending.");
				}
				nights.replace(world, true);
			}

			if (!Tools.isNight(world) && night) {
				for (Player player : world.getPlayers()) {
					if (Tools.isBender(player.getName(), BendingType.Water))
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

	private void handleVerbosity() {
		verbosetime = System.currentTimeMillis();

		int airblasts, airbubbles, airscooters, airshields, airsuctions, airswipes, tornados; // ,airbursts,
																								// airspouts;

		int airblastplayers = 0;
		airblasts = AirBlast.instances.size();

		int airbubbleplayers = 0;
		airbubbles = AirBubble.instances.size();

		// int airburstplayers = 0;
		// airbursts = AirBurst.instances.size();

		int airscooterplayers = 0;
		airscooters = AirScooter.instances.size();

		int airshieldplayers = 0;
		airshields = AirShield.instances.size();

		// int airspoutplayer = 0;
		// airspouts = AirSpout.instances.size();

		int airsuctionplayers = 0;
		airsuctions = AirSuction.instances.size();

		int airswipeplayers = 0;
		airswipes = AirSwipe.instances.size();

		int tornadoplayers = 0;
		tornados = Tornado.instances.size();

		int catapults, compactcolumns, earthblasts, earthcolumns, earthtunnels, tremorsenses; // ,shockwaves;

		int catapultplayers = 0;
		catapults = Catapult.instances.size();

		int compactcolumnplayers = 0;
		compactcolumns = CompactColumn.instances.size();

		int earthblastplayers = 0;
		earthblasts = EarthBlast.instances.size();

		int earthcolumnplayers = 0;
		earthcolumns = EarthColumn.instances.size();

		int earthtunnelplayers = 0;
		earthtunnels = EarthTunnel.instances.size();

		// int shockwaveplayers = 0;
		// shockwaves = Shockwave.instances.size();

		int tremorsenseplayers = 0;
		tremorsenses = Tremorsense.instances.size();

		int fireballs, fireblasts, firejets, firestreams, illuminations, walloffires; // ,lightings;

		int fireblastplayers = 0;
		fireblasts = FireBlast.instances.size();

		int firestreamplayers = 0;
		firestreams = FireStream.instances.size();

		int fireballplayers = 0;
		fireballs = Fireball.fireballs.size();

		int firejetplayers = 0;
		firejets = FireJet.instances.size();

		int illuminationplayers = 0;
		illuminations = Illumination.instances.size();

		// int lightningplayers = 0;
		// lightnings = Lightning.instances.size();

		int walloffireplayers = 0;
		walloffires = WallOfFire.instances.size();

		int bloodbendings, freezemelts, watermanipulations, waterspouts, waterwalls, waves;

		int bloodbendingplayers = 0;
		bloodbendings = Bloodbending.instances.size();

		int freezemeltplayers = 0;
		freezemelts = FreezeMelt.frozenblocks.size();

		int watermanipulationplayers = 0;
		watermanipulations = WaterManipulation.instances.size();

		int waterspoutplayers = 0;
		waterspouts = WaterSpout.instances.size();

		int waterwallplayers = 0;
		waterwalls = WaterWall.instances.size();

		int waveplayers = 0;
		waves = Wave.instances.size();

		for (Player player : plugin.getServer().getOnlinePlayers()) {
			Abilities ability = Tools.getBendingAbility(player);
			if (ability == Abilities.AirBlast)
				airblastplayers++;
			if (ability == Abilities.AirBubble)
				airbubbleplayers++;
			// if (ability==Abilities.AirBurst)
			// airburstplayers++;
			if (ability == Abilities.AirScooter)
				airscooterplayers++;
			if (ability == Abilities.AirShield)
				airshieldplayers++;
			// if (ability==Abilities.AirSpout)
			// airspoutplayers++;
			if (ability == Abilities.AirSuction)
				airsuctionplayers++;
			if (ability == Abilities.AirSwipe)
				airswipeplayers++;
			if (ability == Abilities.Tornado)
				tornadoplayers++;

			if (ability == Abilities.Catapult)
				catapultplayers++;
			if (ability == Abilities.Collapse)
				compactcolumnplayers++;
			// if (ability == Abilities.CompactColumn)
			// compactcolumnplayers++;
			if (ability == Abilities.EarthBlast)
				earthblastplayers++;
			if (ability == Abilities.RaiseEarth)
				earthcolumnplayers++;
			if (ability == Abilities.EarthGrab)
				earthcolumnplayers++;
			if (ability == Abilities.EarthTunnel)
				earthtunnelplayers++;
			if (Tools.hasAbility(player, Abilities.Tremorsense))
				tremorsenseplayers++;
			// if (ability==Abilities.Shockwave) shockwaveplayers++;

			if (ability == Abilities.Blaze)
				firestreamplayers++;
			if (ability == Abilities.Fireball)
				fireballplayers++;
			if (ability == Abilities.FireBlast)
				fireblastplayers++;
			if (Tools.hasAbility(player, Abilities.FireJet))
				firejetplayers++;
			if (Tools.hasAbility(player, Abilities.Illumination))
				illuminationplayers++;
			// if (ability==Abilities.Lightning) lightningplayers++;
			if (ability == Abilities.WallOfFire)
				walloffireplayers++;

			if (ability == Abilities.Bloodbending)
				bloodbendingplayers++;
			if (Tools.hasAbility(player, Abilities.FreezeMelt))
				freezemeltplayers++;
			// if (ability == Abilities.WalkOnWater)
			// freezemeltplayers++;
			if (ability == Abilities.WaterBubble)
				airbubbleplayers++;
			if (ability == Abilities.WaterManipulation)
				watermanipulationplayers++;
			if (Tools.hasAbility(player, Abilities.WaterSpout))
				waterspoutplayers++;
			if (ability == Abilities.WaterWall)
				waterwallplayers++;
			if (ability == Abilities.Wave)
				waveplayers++;
		}

		Tools.writeToLog("Debug data at "
				+ Calendar.getInstance().get(Calendar.HOUR) + "h "
				+ Calendar.getInstance().get(Calendar.MINUTE) + "m "
				+ Calendar.getInstance().get(Calendar.SECOND) + "s");

		verbose("airblasts", airblasts, airblastplayers, false);
		verbose("airbubbles", airbubbles, airbubbleplayers, true);
		// verbose("airbursts", airbursts, airburstplayers, false);
		verbose("airscooters", airscooters, airscooterplayers, true);
		verbose("airshields", airshields, airshieldplayers, true);
		// verbose("airspouts", airspouts, airspoutplayers, false);
		verbose("airsuctions", airsuctions, airsuctionplayers, false);
		verbose("airswipes", airswipes, airswipeplayers, false);
		verbose("tornados", tornados, tornadoplayers, true);

		verbose("catapults", catapults, catapultplayers, true);
		verbose("compactcolumns", compactcolumns, compactcolumnplayers, false);
		verbose("earthblasts", earthblasts, earthblastplayers, true);
		verbose("earthcolumns", earthcolumns, earthcolumnplayers, false);
		verbose("earthtunnels", earthtunnels, earthtunnelplayers, true);
		// verbose("shockwaves", shockwaves, shockwaveplayers, false);
		verbose("tremorsenses", tremorsenses, tremorsenseplayers, true);

		verbose("fireballs", fireballs, fireballplayers, false);
		verbose("fireblasts", fireblasts, fireblastplayers, false);
		verbose("firejets", firejets, firejetplayers, true);
		verbose("firestreams", firestreams, firestreamplayers, false);
		verbose("illuminations", illuminations, illuminationplayers, true);
		// verbose("lightnings", lightnings, lightningplayers, true);
		verbose("walloffires", walloffires, walloffireplayers, false);

		verbose("bloodbendings", bloodbendings, bloodbendingplayers, true);
		verbose("freezemelts", freezemelts, freezemeltplayers, false);
		verbose("watermanipulations", watermanipulations,
				watermanipulationplayers, false);
		verbose("waterspouts", waterspouts, waterspoutplayers, true);
		verbose("waterwalls", waterwalls, waterwallplayers, true);
		verbose("waves", waves, waveplayers, true);

		Tools.writeToLog(null);
	}

	private void verbose(String name, int instances, int players,
			boolean warning) {
		if (warning && instances > players) {
			name = "==WARNING== " + name;
		}
		Tools.writeToLog(name + ": " + instances + " instances for " + players
				+ " players.");
	}
}
