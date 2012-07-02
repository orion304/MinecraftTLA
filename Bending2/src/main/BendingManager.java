package main;

import net.minecraft.server.EntityFireball;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

import tools.AvatarState;
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
import airbending.AirPassive;
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
import firebending.FireJet;
import firebending.FireStream;
import firebending.Fireball;
import firebending.Illumination;

public class BendingManager implements Runnable {

	public Bending plugin;

	long time;
	long interval;

	public BendingManager(Bending instance) {
		plugin = instance;
		time = System.currentTimeMillis();
	}

	public void run() {

		interval = System.currentTimeMillis() - time;
		time = System.currentTimeMillis();
		Bending.time_step = interval;

		manageAirbending();
		manageEarthbending();
		manageFirebending();
		manageWaterbending();
		AvatarState.manageAvatarStates();

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

		FireJet.progressAll();

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

		HealingWaters.heal(plugin.getServer());

		WaterPassive.handlePassive(plugin.getServer());
		FastSwimming.HandleSwim(plugin.getServer());

	}

}
