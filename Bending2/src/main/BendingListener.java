package main;

import org.bukkit.ChatColor;
import org.bukkit.Effect;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockFadeEvent;
import org.bukkit.event.block.BlockFromToEvent;
import org.bukkit.event.block.BlockPhysicsEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.player.PlayerAnimationEvent;
import org.bukkit.event.player.PlayerChatEvent;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerToggleSneakEvent;
import org.bukkit.event.player.PlayerToggleSprintEvent;
import org.bukkit.event.player.PlayerVelocityEvent;

import tools.Abilities;
import tools.AvatarState;
import tools.BendingType;
import tools.ConfigManager;
import tools.Tools;
import waterbending.FreezeMelt;
import waterbending.Melt;
import waterbending.Plantbending;
import waterbending.WalkOnWater;
import waterbending.WaterManipulation;
import waterbending.WaterPassive;
import waterbending.WaterSpout;
import waterbending.WaterWall;
import waterbending.Wave;
import airbending.AirBlast;
import airbending.AirBubble;
import airbending.AirScooter;
import airbending.AirShield;
import airbending.AirSuction;
import airbending.AirSwipe;
import airbending.Speed;
import airbending.Tornado;
import earthbending.Catapult;
import earthbending.Collapse;
import earthbending.CompactColumn;
import earthbending.EarthBlast;
import earthbending.EarthColumn;
import earthbending.EarthGrab;
import earthbending.EarthPassive;
import earthbending.EarthTunnel;
import earthbending.EarthWall;
import earthbending.Tremorsense;
import firebending.ArcOfFire;
import firebending.Extinguish;
import firebending.FireJet;
import firebending.FireStream;
import firebending.Fireball;
import firebending.HeatMelt;
import firebending.Illumination;
import firebending.RingOfFire;
import firebending.WallOfFire;

public class BendingListener implements Listener {

	public Bending plugin;

	public BendingListener(Bending instance) {
		this.plugin = instance;
	}

	@EventHandler
	public void onPlayerLogin(PlayerLoginEvent event) {
		Player player = event.getPlayer();
		String append = "";
		if ((player.hasPermission("bending.avatar")) && ConfigManager.enabled) {
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
		} else if ((Tools.isBender(player, BendingType.ChiBlocker))
				&& (ConfigManager.enabled)) {
			append = ConfigManager.getPrefix("ChiBlocker");
		} else {
			BendingManager.newplayers.add(player);
		}
		if (!(ConfigManager.compatibility) && (ConfigManager.enabled))
			player.setDisplayName(append + player.getName());

		if ((ConfigManager.compatibility) && (ConfigManager.enabled)) {
			ChatColor color = ChatColor.WHITE;
			if (ConfigManager.colors) {
				if (player.hasPermission("bending.avatar")) {
					color = Tools.getColor(ConfigManager.getColor("Avatar"));
				} else if (Tools.isBender(player, BendingType.Air)) {
					color = Tools.getColor(ConfigManager.getColor("Air"));
				} else if (Tools.isBender(player, BendingType.Earth)) {
					color = Tools.getColor(ConfigManager.getColor("Earth"));
				} else if (Tools.isBender(player, BendingType.Fire)) {
					color = Tools.getColor(ConfigManager.getColor("Fire"));
				} else if (Tools.isBender(player, BendingType.Water)) {
					color = Tools.getColor(ConfigManager.getColor("Water"));
				} else if (Tools.isBender(player, BendingType.ChiBlocker)) {
					color = Tools
							.getColor(ConfigManager.getColor("ChiBlocker"));
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
				if (player.hasPermission("bending.avatar")) {
					color = Tools.getColor(ConfigManager.getColor("Avatar"));
				} else if (Tools.isBender(player, BendingType.Air)) {
					color = Tools.getColor(ConfigManager.getColor("Air"));
				} else if (Tools.isBender(player, BendingType.Earth)) {
					color = Tools.getColor(ConfigManager.getColor("Earth"));
				} else if (Tools.isBender(player, BendingType.Fire)) {
					color = Tools.getColor(ConfigManager.getColor("Fire"));
				} else if (Tools.isBender(player, BendingType.Water)) {
					color = Tools.getColor(ConfigManager.getColor("Water"));
				} else if (Tools.isBender(player, BendingType.ChiBlocker)) {
					color = Tools
							.getColor(ConfigManager.getColor("ChiBlocker"));
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

		// Tools.verbose(Tools.getBendingAbility(player));

		AirScooter.check(player);

		if (Tools.canBend(player, Tools.getBendingAbility(player))) {

			if (!Tools.isWeapon(player.getItemInHand().getType())
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

				if (Tools.getBendingAbility(player) == Abilities.AirScooter) {
					new AirScooter(player);
				}

			}

			if (!Tools.isWeapon(player.getItemInHand().getType())
					|| ConfigManager.useWeapon.get("Earth")) {

				if (Tools.getBendingAbility(player) == Abilities.Catapult) {
					new Catapult(player);
				}

				if (Tools.getBendingAbility(player) == Abilities.Collapse) {
					new Collapse(player);
				}

				if (Tools.getBendingAbility(player) == Abilities.RaiseEarth) {
					new EarthColumn(player);
				}

				if (Tools.getBendingAbility(player) == Abilities.CompactColumn) {
					new CompactColumn(player);
				}

				if (Tools.getBendingAbility(player) == Abilities.EarthGrab) {
					new EarthGrab(player);
				}

				if (Tools.getBendingAbility(player) == Abilities.EarthBlast) {
					EarthBlast.throwEarth(player);
				}

				if (Tools.getBendingAbility(player) == Abilities.Tremorsense) {
					new Tremorsense(player);
				}

			}

			if (!Tools.isWeapon(player.getItemInHand().getType())
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

				if (Tools.getBendingAbility(player) == Abilities.Illumination) {
					new Illumination(player);
				}

				if (Tools.getBendingAbility(player) == Abilities.Walloffire
						&& player.isOp()) {
					new WallOfFire(player);
				}

			}

			if (!Tools.isWeapon(player.getItemInHand().getType())
					|| ConfigManager.useWeapon.get("Water")) {

				if (Tools.getBendingAbility(player) == Abilities.WaterManipulation) {
					WaterManipulation.moveWater(player);
				}

				if (Tools.getBendingAbility(player) == Abilities.FreezeMelt) {
					new FreezeMelt(player);
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

				if (Tools.getBendingAbility(player) == Abilities.WaterSpout
						&& player.isOp()) {
					new WaterSpout(player);
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
		// Tools.verbose(Tools.getBendingAbility(player));

		AirScooter.check(player);

		if (!player.isSneaking()
				&& Tools.canBend(player, Tools.getBendingAbility(player))) {

			if (Tools.getBendingAbility(player) == Abilities.AirShield) {
				new AirShield(player);
			}

			if (!(Tools.isWeapon(player.getItemInHand().getType()))
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

			if (Tools.getBendingAbility(player) == Abilities.RaiseEarth) {
				new EarthWall(player);
			}

			if (Tools.getBendingAbility(player) == Abilities.WaterWall) {
				WaterWall.form(player);
			}

			if (Tools.getBendingAbility(player) == Abilities.Wave) {
				new Wave(player);
			}

			if (Tools.getBendingAbility(player) == Abilities.FreezeMelt) {
				new Melt(player);
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

		if (!player.isSprinting()
				&& Tools.isBender(player, BendingType.ChiBlocker)) {
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
				event.setDamage(0);
				event.setCancelled(true);
			} else if (Tools.isBender(player, BendingType.Earth)
					&& event.getCause() == DamageCause.FALL
					&& Tools.canBendPassive(player, BendingType.Earth)) {
				event.setDamage(0);
				event.setCancelled(EarthPassive.softenLanding(player));
			} else if (Tools.isBender(player, BendingType.ChiBlocker)
					&& event.getCause() == DamageCause.FALL) {
				event.setDamage((int) ((double) event.getDamage() * (ConfigManager.falldamagereduction / 100.)));
			}

			if (Tools.isBender(player, BendingType.Fire)
					&& (event.getCause() == DamageCause.FIRE || event
							.getCause() == DamageCause.FIRE_TICK)) {
				event.setCancelled(!Extinguish.canBurn(player));
			}
		}
	}

	@EventHandler
	public void onEntityDamage(EntityDamageByEntityEvent event) {
		if (event.getDamager() instanceof Player
				&& event.getEntity() instanceof Player) {
			Player sourceplayer = (Player) event.getDamager();
			Player targetplayer = (Player) event.getEntity();
			if (Tools.isBender(sourceplayer, BendingType.ChiBlocker)
					&& (!Tools.isWeapon(sourceplayer.getItemInHand().getType()) || ConfigManager.useWeapon
							.get("ChiBlocker"))) {
				Tools.blockChi(targetplayer, System.currentTimeMillis());
			}
		}
		if (event.getEntity() instanceof Player) {
			if ((event.getCause() == DamageCause.ENTITY_ATTACK
					|| event.getCause() == DamageCause.ENTITY_EXPLOSION || event
					.getCause() == DamageCause.PROJECTILE)
					&& Tools.isBender((Player) event.getEntity(),
							BendingType.ChiBlocker)) {
				double rand = Math.random();
				// Tools.verbose(rand + " " + (ConfigManager.dodgechance) /
				// 100.);
				if (rand <= ConfigManager.dodgechance / 100.) {
					event.getEntity()
							.getWorld()
							.playEffect(event.getEntity().getLocation(),
									Effect.SMOKE, 1);
					event.setCancelled(true);
				}
			}
		}
		if (event.getDamager() instanceof Player) {
			if (Tools.isBender((Player) event.getDamager(),
					BendingType.ChiBlocker)
					&& event.getCause() == DamageCause.ENTITY_ATTACK
					&& !Tools.isWeapon(((Player) event.getDamager())
							.getItemInHand().getType())) {
				event.setDamage((int) ((double) event.getDamage() * ConfigManager.punchmultiplier));
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
		if (block.getType() == Material.FIRE) {
			return;
		}
		event.setCancelled(!WalkOnWater.canThaw(block));
		if (!event.isCancelled()) {
			event.setCancelled(!WaterManipulation.canPhysicsChange(block));
		}
		if (!event.isCancelled()) {
			event.setCancelled(FreezeMelt.frozenblocks.containsKey(block));
		}
		if (!event.isCancelled()) {
			event.setCancelled(!Wave.canThaw(block));
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
		if (!FreezeMelt.canThaw(block)) {
			FreezeMelt.thaw(block);
		} else if (!WalkOnWater.canThaw(block)) {
			WalkOnWater.thaw(block);
		} else if (!WaterWall.canThaw(block)) {
			WaterWall.thaw(block);
		} else if (Illumination.blocks.containsKey(block)) {
			event.setCancelled(true);
		} else if (!Wave.canThaw(block)) {
			block.setType(Material.AIR);
			event.setCancelled(true);
		} else if (Tools.tempearthblocks.containsKey(block)) {
			Tools.removeEarthbendedBlockIndex(block);
		}
	}

	// @EventHandler
	// public void onBlockDamage(BlockDamageEvent event) {
	// Block block = event.getBlock();
	// if (Illumination.blocks.containsKey(block)) {
	// Illumination.revert(block);
	// event.setCancelled(true);
	// }
	// }

	@EventHandler
	public void onPlayerMove(PlayerMoveEvent event) {
		Player player = event.getPlayer();
		if (Tools.isBender(player, BendingType.Water)
				&& (Tools.getBendingAbility(player) == Abilities.WalkOnWater)) {
			WalkOnWater.freeze(player);
		}
	}

	@EventHandler(priority = EventPriority.HIGH)
	public void onEntityExplode(EntityExplodeEvent event) {
		for (Block block : event.blockList()) {
			if (FreezeMelt.frozenblocks.containsKey(block)) {
				FreezeMelt.thaw(block);
			}
			if (WalkOnWater.affectedblocks.containsKey(block)) {
				WalkOnWater.thaw(block);
			}
			if (WaterWall.wallblocks.containsKey(block)) {
				block.setType(Material.AIR);
			}
			if (!Wave.canThaw(block)) {
				Wave.thaw(block);
			}
			if (Tools.tempearthblocks.contains(block)) {
				Tools.removeEarthbendedBlockIndex(block);
			}
		}

	}
}
