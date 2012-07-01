package main;

import org.bukkit.ChatColor;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockFadeEvent;
import org.bukkit.event.block.BlockFromToEvent;
import org.bukkit.event.block.BlockPhysicsEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.player.PlayerAnimationEvent;
import org.bukkit.event.player.PlayerChatEvent;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerToggleSneakEvent;
import org.bukkit.event.player.PlayerToggleSprintEvent;
import org.bukkit.event.player.PlayerVelocityEvent;

import tools.Abilities;
import tools.AvatarState;
import tools.BendingType;
import tools.ConfigManager;
import tools.Tools;
import waterbending.Freeze;
import waterbending.Melt;
import waterbending.Plantbending;
import waterbending.WalkOnWater;
import waterbending.WaterManipulation;
import waterbending.WaterPassive;
import waterbending.WaterWall;
import waterbending.Wave;
import airbending.AirBlast;
import airbending.AirBubble;
import airbending.AirShield;
import airbending.AirSuction;
import airbending.AirSwipe;
import airbending.Speed;
import airbending.Tornado;
import earthbending.Catapult;
import earthbending.CompactColumn;
import earthbending.EarthBlast;
import earthbending.EarthColumn;
import earthbending.EarthGrab;
import earthbending.EarthPassive;
import earthbending.EarthTunnel;
import earthbending.EarthWall;
import earthbending.PatchTheEarth;
import firebending.ArcOfFire;
import firebending.Extinguish;
import firebending.FireJet;
import firebending.FireStream;
import firebending.Fireball;
import firebending.HeatMelt;
import firebending.RingOfFire;

public class BendingListener implements Listener {

	public Bending plugin;

	public BendingListener(Bending instance) {
		this.plugin = instance;
	}

	@EventHandler
	public void onPlayerLogin(PlayerLoginEvent event) {
		Player player = event.getPlayer();
		String append = "";
		if ((player.isOp()) && ConfigManager.enabled) {
			append = ConfigManager.getPrefix("Avatar");
		} else if ((Tools.isBender(player, BendingType.Air))
				&& (ConfigManager.enabled)) {
			append = ConfigManager.getPrefix("Air");
		} else if ((Tools.isBender(player, BendingType.Earth))
				&& (ConfigManager.enabled)) {
			append = ConfigManager.getPrefix("Earth");
		} else if ((Tools.isBender(player, BendingType.Fire))
				&& (ConfigManager.enabled)) {
			append = ConfigManager.getPrefix("Fire");
		} else if ((Tools.isBender(player, BendingType.Water))
				&& (ConfigManager.enabled)) {
			append = ConfigManager.getPrefix("Water");
		} else {
			player.sendMessage("Using '/bending choose <element>' to get started!");
		}
		if (!(ConfigManager.compatibility) && (ConfigManager.enabled))
			player.setDisplayName(append + player.getName());

		if ((ConfigManager.compatibility) && (ConfigManager.enabled)) {
			ChatColor color = ChatColor.WHITE;
			if (ConfigManager.colors) {
				if (player.isOp()) {
					color = Tools.getColor(ConfigManager.getColor("Avatar"));
				} else if (Tools.isBender(player, BendingType.Air)) {
					color = Tools.getColor(ConfigManager.getColor("Air"));
				} else if (Tools.isBender(player, BendingType.Earth)) {
					color = Tools.getColor(ConfigManager.getColor("Earth"));
				} else if (Tools.isBender(player, BendingType.Fire)) {
					color = Tools.getColor(ConfigManager.getColor("Fire"));
				} else if (Tools.isBender(player, BendingType.Water)) {
					color = Tools.getColor(ConfigManager.getColor("Water"));
				}
			}
			player.setDisplayName("<" + color + append + player.getName()
					+ ChatColor.WHITE + ">");
		}
	}

	@EventHandler
	public void onPlayerChangeVelocity(PlayerVelocityEvent event) {
		Player player = event.getPlayer();
		if (Tools.isBender(player, BendingType.Water)
				&& Tools.canBendPassive(player, BendingType.Water)) {

			event.setVelocity(WaterPassive.handle(player, event.getVelocity()));
		}
	}

	@EventHandler
	public void onPlayerChat(PlayerChatEvent event) {
		if (!(ConfigManager.enabled))
			return;
		if (!(ConfigManager.compatibility)) {

			Player player = event.getPlayer();
			ChatColor color = ChatColor.WHITE;

			if (ConfigManager.colors) {
				if (player.isOp()) {
					color = Tools.getColor(ConfigManager.getColor("Avatar"));
				} else if (Tools.isBender(player, BendingType.Air)) {
					color = Tools.getColor(ConfigManager.getColor("Air"));
				} else if (Tools.isBender(player, BendingType.Earth)) {
					color = Tools.getColor(ConfigManager.getColor("Earth"));
				} else if (Tools.isBender(player, BendingType.Fire)) {
					color = Tools.getColor(ConfigManager.getColor("Fire"));
				} else if (Tools.isBender(player, BendingType.Water)) {
					color = Tools.getColor(ConfigManager.getColor("Water"));
				}
			}
			event.setFormat("<" + color + player.getDisplayName()
					+ ChatColor.WHITE + "> " + event.getMessage());
		}
	}

	// event.setMessage(append + event.getMessage());
	// }

	@EventHandler
	public void onPlayerSwing(PlayerAnimationEvent event) {

		Player player = event.getPlayer();

		if (Tools.canBend(player, Tools.getBendingAbility(player))) {

			if ((!(ConfigManager.useWeapon.get("Air")) && (Tools
					.isWeapon(player.getItemInHand().getType())))
					|| ConfigManager.useWeapon.get("Air")) {

				if (Tools.getBendingAbility(player) == Abilities.AirBlast) {
					new AirBlast(player);
				}

				if (Tools.getBendingAbility(player) == Abilities.AirSuction) {
					new AirSuction(player);
				}

				if (Tools.getBendingAbility(player) == Abilities.AirSwipe) {
					new AirSwipe(player);
				}

			}

			if ((!(ConfigManager.useWeapon.get("Earth")) && (Tools
					.isWeapon(player.getItemInHand().getType())))
					|| ConfigManager.useWeapon.get("Earth")) {

				if (Tools.getBendingAbility(player) == Abilities.Catapult) {
					new Catapult(player);
				}

				if (Tools.getBendingAbility(player) == Abilities.PatchTheEarth) {
					new PatchTheEarth(player);
				}

				if (Tools.getBendingAbility(player) == Abilities.EarthColumn) {
					new EarthColumn(player);
				}

				if (Tools.getBendingAbility(player) == Abilities.CompactColumn) {
					new CompactColumn(player);
				}

				if (Tools.getBendingAbility(player) == Abilities.EarthGrab) {
					new EarthGrab(player);
				}

				if (Tools.getBendingAbility(player) == Abilities.EarthWall) {
					new EarthWall(player);
				}

				if (Tools.getBendingAbility(player) == Abilities.EarthBlast) {
					EarthBlast.throwEarth(player);
				}

			}

			if ((!(ConfigManager.useWeapon.get("Fire")) && (Tools
					.isWeapon(player.getItemInHand().getType())))
					|| ConfigManager.useWeapon.get("Fire")) {

				if (Tools.getBendingAbility(player) == Abilities.Fireball) {
					new Fireball(player);
				}

				if (Tools.getBendingAbility(player) == Abilities.Extinguish) {
					new Extinguish(player);
				}

				if (Tools.getBendingAbility(player) == Abilities.FireStream) {
					new FireStream(player);
				}

				if (Tools.getBendingAbility(player) == Abilities.ArcOfFire) {
					new ArcOfFire(player);
				}

				if (Tools.getBendingAbility(player) == Abilities.RingOfFire) {
					new RingOfFire(player);
				}

				if (Tools.getBendingAbility(player) == Abilities.HeatMelt) {
					new HeatMelt(player);
				}

				if (Tools.getBendingAbility(player) == Abilities.FireJet) {
					new FireJet(player);
				}

			}

			if ((!(ConfigManager.useWeapon.get("Water")) && (Tools
					.isWeapon(player.getItemInHand().getType())))
					|| ConfigManager.useWeapon.get("Water")) {

				if (Tools.getBendingAbility(player) == Abilities.WaterManipulation) {
					WaterManipulation.moveWater(player);
				}

				if (Tools.getBendingAbility(player) == Abilities.Freeze) {
					new Freeze(player);
				}

				if (Tools.getBendingAbility(player) == Abilities.Melt) {
					new Melt(player);
				}

				if (Tools.getBendingAbility(player) == Abilities.WaterWall) {
					new WaterWall(player);
				}

				if (Tools.getBendingAbility(player) == Abilities.Wave) {
					Wave.launch(player);
				}

				if (Tools.getBendingAbility(player) == Abilities.Plantbending) {
					new Plantbending(player);
				}

			}

			if (Tools.getBendingAbility(player) == Abilities.AvatarState) {
				new AvatarState(player);
			}

		}
	}

	@EventHandler
	public void onPlayerSneak(PlayerToggleSneakEvent event) {

		Player player = event.getPlayer();

		if (!player.isSneaking()
				&& Tools.canBend(player, Tools.getBendingAbility(player))) {

			if (Tools.getBendingAbility(player) == Abilities.AirShield) {
				new AirShield(player);
			}

			if ((!(ConfigManager.useWeapon.get("Air")) && (Tools
					.isWeapon(player.getItemInHand().getType())))
					|| ConfigManager.useWeapon.get("Air")) {

				if (Tools.getBendingAbility(player) == Abilities.AirBlast) {
					new AirBlast(player);
				}

				if (Tools.getBendingAbility(player) == Abilities.AirSuction) {
					new AirSuction(player);
				}
			}

			if (Tools.getBendingAbility(player) == Abilities.Tornado) {
				new Tornado(player);
			}

			if (Tools.getBendingAbility(player) == Abilities.EarthBlast) {
				new EarthBlast(player);
			}

			if (Tools.getBendingAbility(player) == Abilities.WaterManipulation) {
				new WaterManipulation(player);
			}

			if (Tools.getBendingAbility(player) == Abilities.EarthTunnel) {
				new EarthTunnel(player);
			}

			if (Tools.getBendingAbility(player) == Abilities.WaterWall) {
				WaterWall.form(player);
			}

			if (Tools.getBendingAbility(player) == Abilities.Wave) {
				new Wave(player);
			}

		}

	}

	@EventHandler
	public void onPlayerSprint(PlayerToggleSprintEvent event) {
		Player player = event.getPlayer();

		if (!player.isSprinting() && Tools.isBender(player, BendingType.Air)
				&& Tools.canBendPassive(player, BendingType.Air)) {
			new Speed(player);
		}
	}

	@EventHandler
	public void onPlayerDamage(EntityDamageEvent event) {
		if (event.getEntity() instanceof Player) {
			// Tools.verbose(event.getCause());
			Player player = (Player) event.getEntity();

			if (Tools.isBender(player, BendingType.Air)
					&& event.getCause() == DamageCause.FALL
					&& Tools.canBendPassive(player, BendingType.Air)) {
				event.setCancelled(true);
			} else if (Tools.isBender(player, BendingType.Earth)
					&& event.getCause() == DamageCause.FALL
					&& Tools.canBendPassive(player, BendingType.Earth)) {
				event.setCancelled(EarthPassive.softenLanding(player));
			}

			if (Tools.isBender(player, BendingType.Fire)
					&& (event.getCause() == DamageCause.FIRE || event
							.getCause() == DamageCause.FIRE_TICK)) {
				event.setCancelled(!Extinguish.canBurn(player));
			}
		}
	}

	// @EventHandler
	// public void onEntityDamage(EntityDamageByBlockEvent event) {
	// Tools.verbose(event.getCause());
	// if (event.getEntity() instanceof LivingEntity) {
	// if (event.getCause() == DamageCause.FIRE
	// && FireStream.ignitedblocks.contains(event.getDamager())) {
	// event.setDamage(0);
	// Tools.damageEntity(
	// FireStream.ignitedblocks.get(event.getDamager()),
	// event.getEntity(), FireStream.firedamage);
	// }
	//
	// if (event.getCause() == DamageCause.FIRE_TICK
	// && FireStream.ignitedblocks.contains(event.getEntity())) {
	// event.setDamage(0);
	// Tools.damageEntity(
	// FireStream.ignitedblocks.get(event.getDamager()),
	// event.getEntity(), FireStream.tickdamage);
	// }
	// }
	//
	// }
	// @EventHandler
	// public void onEntityCombust(EntityCombustByBlockEvent event) {
	// if (FireStream.ignitedblocks.contains(event.getCombuster())) {
	// FireStream.ignitedentities.put((LivingEntity) event.getEntity(),
	// FireStream.ignitedblocks.get(event.getCombuster()));
	// }
	// }

	@EventHandler
	public void onBlockFlowTo(BlockFromToEvent event) {
		Block toblock = event.getToBlock();
		Block fromblock = event.getBlock();
		event.setCancelled(!AirBubble.canFlowTo(toblock));
		if (!event.isCancelled()) {
			event.setCancelled(!WaterManipulation.canFlowFromTo(fromblock,
					toblock));
		}
	}

	@EventHandler
	public void onBlockMeltEvent(BlockFadeEvent event) {
		Block block = event.getBlock();
		event.setCancelled(!WalkOnWater.canThaw(block));
		if (!event.isCancelled()) {
			event.setCancelled(!WaterManipulation.canPhysicsChange(block));
		}
	}

	@EventHandler
	public void onBlockPhysics(BlockPhysicsEvent event) {
		Block block = event.getBlock();
		event.setCancelled(!WaterManipulation.canPhysicsChange(block));

	}

	@EventHandler
	public void onBlockBreak(BlockBreakEvent event) {
		Block block = event.getBlock();
		if (!Freeze.canThaw(block)) {
			Freeze.thaw(block);
		} else if (!WalkOnWater.canThaw(block)) {
			WalkOnWater.thaw(block);
		} else if (!WaterWall.canThaw(block)) {
			WaterWall.thaw(block);
		}
	}

}