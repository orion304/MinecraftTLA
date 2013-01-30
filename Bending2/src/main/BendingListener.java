package main;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UnknownFormatConversionException;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Effect;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockFadeEvent;
import org.bukkit.event.block.BlockFormEvent;
import org.bukkit.event.block.BlockFromToEvent;
import org.bukkit.event.block.BlockPhysicsEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityCombustEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType.SlotType;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerAnimationEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerToggleFlightEvent;
import org.bukkit.event.player.PlayerToggleSneakEvent;
import org.bukkit.event.player.PlayerToggleSprintEvent;
import org.bukkit.event.player.PlayerVelocityEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;

import tools.Abilities;
import tools.AvatarState;
import tools.BendingPlayer;
import tools.BendingType;
import tools.ConfigManager;
import tools.Cooldowns;
import tools.TempBlock;
import tools.Tools;
import waterbending.Bloodbending;
import waterbending.FreezeMelt;
import waterbending.IceSpike2;
import waterbending.Melt;
import waterbending.OctopusForm;
import waterbending.Torrent;
import waterbending.WaterManipulation;
import waterbending.WaterPassive;
import waterbending.WaterSpout;
import waterbending.WaterWall;
import waterbending.Wave;
import airbending.AirBlast;
import airbending.AirBubble;
import airbending.AirBurst;
import airbending.AirScooter;
import airbending.AirShield;
import airbending.AirSpout;
import airbending.AirSuction;
import airbending.AirSwipe;
import airbending.Speed;
import airbending.Tornado;
import chiblocking.HighJump;
import chiblocking.Paralyze;
import chiblocking.RapidPunch;
import earthbending.Catapult;
import earthbending.Collapse;
import earthbending.CompactColumn;
import earthbending.EarthArmor;
import earthbending.EarthBlast;
import earthbending.EarthColumn;
import earthbending.EarthGrab;
import earthbending.EarthPassive;
import earthbending.EarthTunnel;
import earthbending.EarthWall;
import earthbending.Shockwave;
import earthbending.Tremorsense;
import firebending.ArcOfFire;
import firebending.Cook;
import firebending.Enflamed;
import firebending.Extinguish;
import firebending.FireBlast;
import firebending.FireBurst;
import firebending.FireJet;
import firebending.FireShield;
import firebending.FireStream;
import firebending.Fireball;
import firebending.Illumination;
import firebending.Lightning;
import firebending.RingOfFire;
import firebending.WallOfFire;

public class BendingListener implements Listener {

	public Bending plugin;

	public BendingListener(Bending bending) {
		this.plugin = bending;
	}

	@EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
	public void onPlayerLogin(PlayerLoginEvent event) {
		Player player = event.getPlayer();
		BendingPlayer.getBendingPlayer(player);
		String append = "";
		if ((player.hasPermission("bending.avatar")) && ConfigManager.enabled) {
			append = ConfigManager.getPrefix("Avatar");
		} else if ((Tools.isBender(player.getName(), BendingType.Air))
				&& (ConfigManager.enabled)) {
			append = ConfigManager.getPrefix("Air");
		} else if ((Tools.isBender(player.getName(), BendingType.Earth))
				&& (ConfigManager.enabled)) {
			append = ConfigManager.getPrefix("Earth");
		} else if ((Tools.isBender(player.getName(), BendingType.Fire))
				&& (ConfigManager.enabled)) {
			append = ConfigManager.getPrefix("Fire");
		} else if ((Tools.isBender(player.getName(), BendingType.Water))
				&& (ConfigManager.enabled)) {
			append = ConfigManager.getPrefix("Water");
		} else if ((Tools.isBender(player.getName(), BendingType.ChiBlocker))
				&& (ConfigManager.enabled)) {
			append = ConfigManager.getPrefix("ChiBlocker");
		}

		if (!(ConfigManager.compatibility) && (ConfigManager.enabled))
			player.setDisplayName(append + player.getName());

		if ((ConfigManager.compatibility) && (ConfigManager.enabled)) {
			ChatColor color = ChatColor.WHITE;
			if (ConfigManager.colors) {
				if (player.hasPermission("bending.avatar")) {
					color = Tools.getColor(ConfigManager.getColor("Avatar"));
				} else if (Tools.isBender(player.getName(), BendingType.Air)) {
					color = Tools.getColor(ConfigManager.getColor("Air"));
				} else if (Tools.isBender(player.getName(), BendingType.Earth)) {
					color = Tools.getColor(ConfigManager.getColor("Earth"));
				} else if (Tools.isBender(player.getName(), BendingType.Fire)) {
					color = Tools.getColor(ConfigManager.getColor("Fire"));
				} else if (Tools.isBender(player.getName(), BendingType.Water)) {
					color = Tools.getColor(ConfigManager.getColor("Water"));
				} else if (Tools.isBender(player.getName(),
						BendingType.ChiBlocker)) {
					color = Tools
							.getColor(ConfigManager.getColor("ChiBlocker"));
				}
			}
			player.setDisplayName(color + append + player.getName());
//			player.setDisplayName("<" + color + append + player.getName()
//					+ ChatColor.WHITE + ">");
		}

		YamlConfiguration dc = new YamlConfiguration();
		File sv = new File(Bukkit.getPluginManager().getPlugin("Bending")
				.getDataFolder(), "Armour.sav");
		if (sv.exists()
				&& (dc.contains("Armors." + player.getName() + ".Boots")
						&& dc.contains("Armors." + player.getName()
								+ ".Leggings")
						&& dc.contains("Armors." + player.getName() + ".Chest") && dc
							.contains("Armors." + player.getName() + ".Helm"))) {
			ItemStack boots = new ItemStack(Material.matchMaterial(dc
					.getString("Armors." + player.getName() + ".Boots").split(
							":")[0]));
			ItemStack leggings = new ItemStack(Material.matchMaterial(dc
					.getString("Armors." + player.getName() + ".Leggings")
					.split(":")[0]));
			ItemStack chest = new ItemStack(Material.matchMaterial(dc
					.getString("Armors." + player.getName() + ".Chest").split(
							":")[0]));
			ItemStack helm = new ItemStack(Material.matchMaterial(dc.getString(
					"Armors." + player.getName() + ".Helm").split(":")[0]));
			boots.setDurability(Short.parseShort(dc.getString(
					"Armors." + player.getName() + ".Boots").split(":")[1]));
			leggings.setDurability(Short.parseShort(dc.getString(
					"Armors." + player.getName() + ".Leggings").split(":")[1]));
			chest.setDurability(Short.parseShort(dc.getString(
					"Armors." + player.getName() + ".Chest").split(":")[1]));
			helm.setDurability(Short.parseShort(dc.getString(
					"Armors." + player.getName() + ".Helm").split(":")[1]));
			ItemStack[] armors = { boots, leggings, chest, helm };
			player.getInventory().setArmorContents(armors);
		}
		try {
			dc.save(sv);
		} catch (IOException e) {
		}
	}

	@EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
	public void onPlayerInteract(PlayerInteractEvent event) {
		Player player = event.getPlayer();
		if (event.getAction() == Action.RIGHT_CLICK_BLOCK)
			Cooldowns.forceCooldown(player);
		if (Paralyze.isParalyzed(player) || Bloodbending.isBloodbended(player)) {
			event.setCancelled(true);
		}
	}

	@EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
	public void onBlockPlace(BlockPlaceEvent event) {
		Player player = event.getPlayer();
		Cooldowns.forceCooldown(player);
	}

	@EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
	public void onProjectileLaunch(EntityShootBowEvent event) {
		Entity entity = event.getEntity();
		if (Paralyze.isParalyzed(entity) || Bloodbending.isBloodbended(entity)) {
			event.setCancelled(true);
		}
	}

	@EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
	public void onPlayerChangeVelocity(PlayerVelocityEvent event) {
		Player player = event.getPlayer();
		if (Tools.isBender(player.getName(), BendingType.Water)
				&& Tools.canBendPassive(player, BendingType.Water)) {

			event.setVelocity(WaterPassive.handle(player, event.getVelocity()));
		}
	}

	@EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
	public void onPlayerChat(AsyncPlayerChatEvent event) {
		if (!(ConfigManager.enabled))
			return;
		if (!(ConfigManager.compatibility)) {

			Player player = event.getPlayer();
			ChatColor color = ChatColor.WHITE;

			if (ConfigManager.colors) {
				if (player.hasPermission("bending.avatar")) {
					color = Tools.getColor(ConfigManager.getColor("Avatar"));
				} else if (Tools.isBender(player.getName(), BendingType.Air)) {
					color = Tools.getColor(ConfigManager.getColor("Air"));
				} else if (Tools.isBender(player.getName(), BendingType.Earth)) {
					color = Tools.getColor(ConfigManager.getColor("Earth"));
				} else if (Tools.isBender(player.getName(), BendingType.Fire)) {
					color = Tools.getColor(ConfigManager.getColor("Fire"));
				} else if (Tools.isBender(player.getName(), BendingType.Water)) {
					color = Tools.getColor(ConfigManager.getColor("Water"));
				} else if (Tools.isBender(player.getName(),
						BendingType.ChiBlocker)) {
					color = Tools
							.getColor(ConfigManager.getColor("ChiBlocker"));
				}
			}

			try {
				String message = event.getMessage();
				String format = ConfigManager.message_format;
				format = format.replace("<message>", "%2$s");
				format = format.replace("<name>", color + player.getDisplayName());
				format = Tools.colorize(format);
				
				event.setFormat(format);
				event.setMessage(message);
//				format = format.replace("<message>", event.getMessage());
//				format = format.replace("<name>", player.getDisplayName());
//				event.setMessage(format);
				
				
//				event.setFormat("<" + color + player.getDisplayName()
//						+ ChatColor.WHITE + "> " + event.getMessage());
				// event.setFormat(format);
				// // event.setMessage(message + "Test");
				// Tools.verbose(event.getFormat());
			} catch (UnknownFormatConversionException e) {
				e.printStackTrace();
			}
		}
	}

	// event.setMessage(append + event.getMessage());
	// }

	@EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
	public void onPlayerSwing(PlayerAnimationEvent event) {

		Player player = event.getPlayer();

		if (Bloodbending.isBloodbended(player) || Paralyze.isParalyzed(player)) {
			event.setCancelled(true);
		}

		// Tools.verbose(Tools.getBendingAbility(player));

		Abilities[] sourceabilities = { Abilities.OctopusForm, Abilities.Surge };

		AirScooter.check(player);

		Abilities ability = Tools.getBendingAbility(player);
		if (ability == null)
			return;

		if (Tools.canBend(player, ability)) {

			if (!Arrays.asList(sourceabilities).contains(ability)) {
				if (!Cooldowns.canUseAbility(player, ability))
					return;
			}

			if (!Tools.isWeapon(player.getItemInHand().getType())
					|| ConfigManager.useWeapon.get("Air")) {

				if (ability == Abilities.AirBlast) {
					new AirBlast(player);
				}

				if (ability == Abilities.AirSuction) {
					new AirSuction(player);
				}

				if (ability == Abilities.AirSwipe) {
					new AirSwipe(player);
				}

				if (ability == Abilities.AirScooter) {
					new AirScooter(player);
				}

				if (ability == Abilities.AirSpout) {
					new AirSpout(player);
				}

				if (ability == Abilities.AirBurst) {
					AirBurst.coneBurst(player);
				}

			}

			if (!Tools.isWeapon(player.getItemInHand().getType())
					|| ConfigManager.useWeapon.get("Earth")) {

				if (ability == Abilities.Catapult) {
					new Catapult(player);
				}

				if (ability == Abilities.RaiseEarth) {
					new EarthColumn(player);
				}

				if (ability == Abilities.Collapse) {
					new CompactColumn(player);
				}

				if (ability == Abilities.EarthGrab) {
					new EarthGrab(player);
				}

				if (ability == Abilities.EarthBlast) {
					EarthBlast.throwEarth(player);
				}

				if (ability == Abilities.Tremorsense) {
					new Tremorsense(player);
				}

				if (ability == Abilities.EarthArmor) {
					new EarthArmor(player);
				}

				if (ability == Abilities.EarthArmor) {
					new EarthArmor(player);
				}

				if (ability == Abilities.Shockwave) {
					Shockwave.coneShockwave(player);
				}

			}

			if (!Tools.isWeapon(player.getItemInHand().getType())
					|| ConfigManager.useWeapon.get("Fire")) {

				if (ability == Abilities.FireBlast) {
					new FireBlast(player);
				}

				if (ability == Abilities.HeatControl) {
					new Extinguish(player);
				}

				if (ability == Abilities.Blaze) {
					new ArcOfFire(player);
				}

				if (ability == Abilities.FireJet) {
					new FireJet(player);
				}

				if (ability == Abilities.Illumination) {
					new Illumination(player);
				}

				if (ability == Abilities.WallOfFire) {
					new WallOfFire(player);
				}

				if (ability == Abilities.FireBurst) {
					FireBurst.coneBurst(player);
				}

				if (ability == Abilities.FireShield) {
					new FireShield(player);
				}

			}

			if (!Tools.isWeapon(player.getItemInHand().getType())
					|| ConfigManager.useWeapon.get("Water")) {

				if (ability == Abilities.WaterManipulation) {
					WaterManipulation.moveWater(player);
				}

				if (ability == Abilities.IceSpike) {
					IceSpike2.activate(player);
				}

				if (ability == Abilities.PhaseChange) {
					new FreezeMelt(player);
				}

				if (ability == Abilities.Surge) {
					new WaterWall(player);
				}

				if (ability == Abilities.OctopusForm) {
					new OctopusForm(player);
				}

				if (ability == Abilities.Torrent) {
					new Torrent(player);
				}

				// if (ability == Abilities.Wave) {
				// Wave.launch(player);
				// }

				// if (ability ==
				// Abilities.Plantbending) {
				// new Plantbending(player);
				// }

				if (ability == Abilities.WaterSpout) {
					new WaterSpout(player);
				}

				if (ability == Abilities.Bloodbending) {
					Bloodbending.launch(player);
				}

			}

			if (ability == Abilities.AvatarState) {
				new AvatarState(player);
			}

			if (ability == Abilities.HighJump) {
				new HighJump(player);
			}

			if (ability == Abilities.RapidPunch) {
				new RapidPunch(player);
			}

			if (ability == Abilities.Paralyze) {
				// new Paralyze(player);
			}

		}

	}

	@EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
	public void onPlayerSneak(PlayerToggleSneakEvent event) {

		Player player = event.getPlayer();
		// Tools.verbose(Tools.getBendingAbility(player));

		Abilities[] sourceabilities = { Abilities.AirBlast,
				Abilities.AirSuction, Abilities.EarthBlast,
				Abilities.WaterManipulation, Abilities.Surge,
				Abilities.IceSpike };

		AirScooter.check(player);

		Abilities ability = Tools.getBendingAbility(player);
		if (ability == null)
			return;

		if (!player.isSneaking() && Tools.canBend(player, ability)) {

			if (!Arrays.asList(sourceabilities).contains(ability)) {
				if (!Cooldowns.canUseAbility(player, ability))
					return;
			}

			if (ability == Abilities.AirShield) {
				new AirShield(player);
			}

			if (!(Tools.isWeapon(player.getItemInHand().getType()))
					|| ConfigManager.useWeapon.get("Air")) {

				if (ability == Abilities.AirBlast) {
					AirBlast.setOrigin(player);
				}

				if (ability == Abilities.AirSuction) {
					AirSuction.setOrigin(player);
				}

				if (ability == Abilities.AirBurst) {
					new AirBurst(player);
				}

				if (ability == Abilities.AirSwipe) {
					AirSwipe.charge(player);
				}
			}

			if (ability == Abilities.Tornado) {
				new Tornado(player);
			}

			if (ability == Abilities.EarthBlast) {
				new EarthBlast(player);
			}

			if (ability == Abilities.Shockwave) {
				new Shockwave(player);
			}

			if (ability == Abilities.Collapse) {
				new Collapse(player);
			}

			if (ability == Abilities.WaterManipulation) {
				new WaterManipulation(player);
			}

			if (ability == Abilities.IceSpike) {
				new IceSpike2(player);
			}

			if (ability == Abilities.EarthTunnel) {
				new EarthTunnel(player);
			}

			if (ability == Abilities.RaiseEarth) {
				new EarthWall(player);
			}

			if (ability == Abilities.Surge) {
				WaterWall.form(player);
			}

			if (ability == Abilities.OctopusForm) {
				OctopusForm.form(player);
			}

			if (ability == Abilities.Torrent) {
				Torrent.create(player);
			}

			// if (ability == Abilities.Wave) {
			// new Wave(player);
			// }

			if (ability == Abilities.Bloodbending) {
				new Bloodbending(player);
			}

			if (ability == Abilities.PhaseChange) {
				new Melt(player);
			}

			if (ability == Abilities.Lightning) {
				new Lightning(player);
			}

			if (ability == Abilities.Blaze) {
				new RingOfFire(player);
			}

			if (ability == Abilities.FireBurst) {
				new FireBurst(player);
			}

			if (ability == Abilities.FireBlast) {
				new Fireball(player);
			}

			if (ability == Abilities.FireShield) {
				FireShield.shield(player);
			}

			if (ability == Abilities.HeatControl) {
				new Cook(player);
			}

		}

	}

	@EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
	public void onPlayerSprint(PlayerToggleSprintEvent event) {
		Player player = event.getPlayer();

		if (!player.isSprinting()
				&& Tools.isBender(player.getName(), BendingType.Air)
				&& Tools.canBendPassive(player, BendingType.Air)) {
			new Speed(player);
		}

		if (!player.isSprinting()
				&& Tools.isBender(player.getName(), BendingType.ChiBlocker)) {
			new Speed(player);
		}
	}

	@EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
	public void onPlayerDamage(EntityDamageEvent event) {
		// Entity entity = event.getEntity();
		// if (Paralyze.isParalyzed(entity)) {
		// event.setCancelled(true);
		// return;
		// }
		if (event.getEntity() instanceof Player) {
			// Tools.verbose(event.getCause());
			Player player = (Player) event.getEntity();

			if (Tools.isBender(player.getName(), BendingType.Earth)
					&& event.getCause() == DamageCause.FALL)
				Shockwave.fallShockwave(player);

			if (Tools.isBender(player.getName(), BendingType.Air)
					&& event.getCause() == DamageCause.FALL
					&& Tools.canBendPassive(player, BendingType.Air)) {
				if (!BendingManager.flyingplayers.contains(player)) {
					player.setAllowFlight(true);
					BendingManager.flyingplayers.add(player);
				}
				AirBurst.fallBurst(player);
				player.setFallDistance(0);
				event.setDamage(0);
				event.setCancelled(true);
			} else if (Tools.isBender(player.getName(), BendingType.Water)
					&& event.getCause() == DamageCause.FALL
					&& Tools.canBendPassive(player, BendingType.Water)) {
				if (WaterPassive.softenLanding(player)) {
					if (!BendingManager.flyingplayers.contains(player)) {
						player.setAllowFlight(true);
						BendingManager.flyingplayers.add(player);
					}
					player.setFallDistance(0);
					event.setDamage(0);
					event.setCancelled(true);
				}
			} else if (Tools.isBender(player.getName(), BendingType.Earth)
					&& event.getCause() == DamageCause.FALL
					&& Tools.canBendPassive(player, BendingType.Earth)) {
				if (EarthPassive.softenLanding(player)) {
					if (!BendingManager.flyingplayers.contains(player)) {
						player.setAllowFlight(true);
						BendingManager.flyingplayers.add(player);
					}
					player.setFallDistance(0);
					event.setDamage(0);
					event.setCancelled(true);

				}
			} else if (Tools.isBender(player.getName(), BendingType.ChiBlocker)
					&& event.getCause() == DamageCause.FALL) {
				event.setDamage((int) ((double) event.getDamage() * (ConfigManager.falldamagereduction / 100.)));
			}

			if (Tools.isBender(player.getName(), BendingType.Fire)
					&& (event.getCause() == DamageCause.FIRE || event
							.getCause() == DamageCause.FIRE_TICK)) {
				event.setCancelled(!Extinguish.canBurn(player));
			}

			if (Tools.isBender(player.getName(), BendingType.Earth)
					&& (event.getCause() == DamageCause.SUFFOCATION && TempBlock
							.isTempBlock(player.getEyeLocation().getBlock()))) {
				event.setDamage(0);
				event.setCancelled(true);
			}
		}
	}

	@EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
	public void onEntityCombust(EntityCombustEvent event) {
		// Tools.verbose("Caught an ignition");
		Entity entity = event.getEntity();
		Block block = entity.getLocation().getBlock();
		if (FireStream.ignitedblocks.containsKey(block)
				&& entity instanceof LivingEntity) {
			// Tools.verbose("Passed it to Enflamed.");
			new Enflamed(entity, FireStream.ignitedblocks.get(block));
		}
	}

	@EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
	public void onEntityDamageEvent(EntityDamageEvent event) {
		Entity entity = event.getEntity();
		if (event.getCause() == DamageCause.FIRE
				&& FireStream.ignitedblocks.containsKey(entity.getLocation()
						.getBlock())) {
			new Enflamed(entity, FireStream.ignitedblocks.get(entity
					.getLocation().getBlock()));
		}
		if (Enflamed.isEnflamed(entity)
				&& event.getCause() == DamageCause.FIRE_TICK) {
			// Tools.verbose("Deal Enflamed damage.");
			event.setCancelled(true);
			Enflamed.dealFlameDamage(entity);
		}
	}

	@EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
	public void onEntityDamage(EntityDamageByEntityEvent event) {

		if (Paralyze.isParalyzed(event.getDamager())) {
			event.setCancelled(true);
			return;
		}

		// Tools.verbose(event.getCause());

		boolean dodged = false;

		if (event.getDamager() instanceof Player
				&& event.getEntity() instanceof Player) {
			Player sourceplayer = (Player) event.getDamager();
			Player targetplayer = (Player) event.getEntity();
			if (Tools.isBender(sourceplayer.getName(), BendingType.ChiBlocker)
					&& event.getCause() == DamageCause.ENTITY_ATTACK
					&& event.getDamage() == 1
					&& (!Tools.isWeapon(sourceplayer.getItemInHand().getType()) || ConfigManager.useWeapon
							.get("ChiBlocker"))) {
				Tools.blockChi(targetplayer, System.currentTimeMillis());
			}
		}
		if (event.getEntity() instanceof Player) {
			if ((event.getCause() == DamageCause.ENTITY_ATTACK
					|| event.getCause() == DamageCause.ENTITY_EXPLOSION || event
					.getCause() == DamageCause.PROJECTILE)
					&& Tools.isBender(((Player) event.getEntity()).getName(),
							BendingType.ChiBlocker)) {
				double rand = Math.random();
				// Tools.verbose(rand + " " + (ConfigManager.dodgechance) /
				// 100.);
				if (rand <= ConfigManager.dodgechance / 100.
						&& !Paralyze.isParalyzed(event.getEntity())) {
					event.getEntity()
							.getWorld()
							.playEffect(event.getEntity().getLocation(),
									Effect.SMOKE, 1);
					dodged = true;
					event.setCancelled(true);
				}
			}
		}
		if (event.getDamager() instanceof Player) {
			if (!dodged)
				new Paralyze((Player) event.getDamager(), event.getEntity());
			if (Tools.isBender(((Player) event.getDamager()).getName(),
					BendingType.ChiBlocker)
					&& event.getCause() == DamageCause.ENTITY_ATTACK
					&& !Tools.isWeapon(((Player) event.getDamager())
							.getItemInHand().getType())) {
				// event.setDamage((int) (ConfigManager.punchdamage));
			}
		}

	}

	// @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
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
	// @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
	// public void onEntityCombust(EntityCombustByBlockEvent event) {
	// if (FireStream.ignitedblocks.contains(event.getCombuster())) {
	// FireStream.ignitedentities.put((LivingEntity) event.getEntity(),
	// FireStream.ignitedblocks.get(event.getCombuster()));
	// }
	// }

	@EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
	public void onBlockFlowTo(BlockFromToEvent event) {
		Block toblock = event.getToBlock();
		Block fromblock = event.getBlock();
		event.setCancelled(!AirBubble.canFlowTo(toblock));
		if (!event.isCancelled()) {
			event.setCancelled(!WaterManipulation.canFlowFromTo(fromblock,
					toblock));
		}
		if (!event.isCancelled()) {
			if (Illumination.blocks.containsKey(toblock))
				toblock.setType(Material.AIR);
		}
	}

	@EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
	public void onBlockMeltEvent(BlockFadeEvent event) {
		Block block = event.getBlock();
		if (block.getType() == Material.FIRE) {
			return;
		}
		event.setCancelled(Illumination.blocks.containsKey(block));
		if (!event.isCancelled()) {
			event.setCancelled(!WaterManipulation.canPhysicsChange(block));
		}
		if (!event.isCancelled()) {
			event.setCancelled(FreezeMelt.frozenblocks.containsKey(block));
		}
		if (!event.isCancelled()) {
			event.setCancelled(!Wave.canThaw(block));
		}
		if (!event.isCancelled()) {
			event.setCancelled(!Torrent.canThaw(block));
		}
		if (FireStream.ignitedblocks.containsKey(block)) {
			FireStream.remove(block);
		}
	}

	@EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
	public void onBlockPhysics(BlockPhysicsEvent event) {
		Block block = event.getBlock();
		event.setCancelled(!WaterManipulation.canPhysicsChange(block));
		if (!event.isCancelled())
			event.setCancelled(Illumination.blocks.containsKey(block));
		if (!event.isCancelled())
			event.setCancelled(Tools.tempnophysics.contains(block));
	}

	@EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
	public void onBlockBreak(BlockBreakEvent event) {
		Block block = event.getBlock();
		Player player = event.getPlayer();
		if (WaterWall.wasBrokenFor(player, block)
				|| OctopusForm.wasBrokenFor(player, block)
				|| Torrent.wasBrokenFor(player, block)) {
			event.setCancelled(true);
			return;
		}
		if (FreezeMelt.frozenblocks.containsKey(block)) {
			FreezeMelt.thaw(block);
			event.setCancelled(true);
			// } else if (!WalkOnWater.canThaw(block)) {
			// WalkOnWater.thaw(block);
		} else if (WaterWall.wallblocks.containsKey(block)) {
			WaterWall.thaw(block);
			event.setCancelled(true);
		} else if (Illumination.blocks.containsKey(block)) {
			event.setCancelled(true);
			// } else if (Illumination.blocks.containsKey(block
			// .getRelative(BlockFace.UP))) {
			// event.setCancelled(true);
		} else if (!Wave.canThaw(block)) {
			Wave.thaw(block);
			event.setCancelled(true);
			// event.setCancelled(true);
		} else if (Tools.movedearth.containsKey(block)) {
			// Tools.removeEarthbendedBlockIndex(block);
			Tools.removeRevertIndex(block);
		}
	}

	// @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
	// public void onBlockDamage(BlockDamageEvent event) {
	// Block block = event.getBlock();
	// if (Illumination.blocks.containsKey(block)) {
	// Illumination.revert(block);
	// event.setCancelled(true);
	// }
	// }

	@EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
	public void onPlayerMove(PlayerMoveEvent event) {
		Player player = event.getPlayer();
		if (Paralyze.isParalyzed(player)) {
			event.setCancelled(true);
			return;
		}
		if (WaterSpout.instances.containsKey(event.getPlayer())
				|| AirSpout.getPlayers().contains(event.getPlayer())) {
			Vector vel = new Vector();
			vel.setX(event.getTo().getX() - event.getFrom().getX());
			vel.setY(event.getTo().getY() - event.getFrom().getY());
			vel.setZ(event.getTo().getZ() - event.getFrom().getZ());
			// You now know the old velocity. Set to match recommended velocity
			double currspeed = vel.length();
			double maxspeed = .15;
			if (currspeed > maxspeed) {
				// only if moving set a factor
				// double recspeed = 0.6;
				// vel = vel.ultiply(recspeed * currspeed);
				vel = vel.normalize().multiply(maxspeed);
				// apply the new velocity (MAY REQUIRE A SCHEDULED TASK
				// INSTEAD!)
				event.getPlayer().setVelocity(vel);
			}
		}
		if (Bloodbending.isBloodbended(player)) {
			double distance1, distance2;
			Location loc = Bloodbending.getBloodbendingLocation(player);
			distance1 = event.getFrom().distance(loc);
			distance2 = event.getTo().distance(loc);
			if (distance2 > distance1)
				player.setVelocity(new Vector(0, 0, 0));
			// return;
		}

		// if (Tools.isBender(player, BendingType.Water)
		// && (Tools.getBendingAbility(player) == Abilities.WalkOnWater)) {
		// WalkOnWater.freeze(player);
		// }
	}

	@EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
	public void onEntityExplode(EntityExplodeEvent event) {
		for (Block block : event.blockList()) {
			if (FreezeMelt.frozenblocks.containsKey(block)) {
				FreezeMelt.thaw(block);
			}
			// if (WalkOnWater.affectedblocks.containsKey(block)) {
			// WalkOnWater.thaw(block);
			// }
			if (WaterWall.wallblocks.containsKey(block)) {
				block.setType(Material.AIR);
			}
			if (!Wave.canThaw(block)) {
				Wave.thaw(block);
			}
			if (Tools.movedearth.containsKey(block)) {
				// Tools.removeEarthbendedBlockIndex(block);
				Tools.removeRevertIndex(block);
			}
		}

		// if (event.getEntity() == null) {
		// Plugin ch = Bukkit.getPluginManager().getPlugin("CreeperHeal");
		// if (ch != null) {
		// CreeperHeal creeperheal = (CreeperHeal) Bukkit
		// .getPluginManager().getPlugin("CreeperHeal");
		// creeperheal
		// .recordBlocks(event.blockList(), event.getLocation());
		// }
		// }

	}

	@EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
	public void onPlayerKick(PlayerKickEvent event) {
		Tools.verbose(event.getReason());
		if (BendingManager.flyingplayers.contains(event.getPlayer())
				|| Bloodbending.isBloodbended(event.getPlayer())) {
			event.setCancelled(true);
			event.setReason(null);
		}
	}

	@EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
	public void onBlockForm(BlockFormEvent event) {
		if (TempBlock.isTempBlock(event.getBlock()))
			event.setCancelled(true);
		if (!WaterManipulation.canPhysicsChange(event.getBlock()))
			event.setCancelled(true);
	}

	// @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
	// public void onEntityTarget(EntityTargetEvent event) {
	// Entity entity = event.getEntity();
	// if (Paralyze.isParalyzed(entity) || Bloodbending.isBloodbended(entity))
	// event.setCancelled(true);
	// }
	//
	// @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
	// public void onEntityTargetLiving(EntityTargetLivingEntityEvent event) {
	// Entity entity = event.getEntity();
	// if (Paralyze.isParalyzed(entity) || Bloodbending.isBloodbended(entity))
	// event.setCancelled(true);
	// }

	// @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
	// public void onEntityEvent(EntityEvent event) {
	// if (Paralyze.isParalyzed(event.getEntity())
	// || Bloodbending.isBloodbended(event.getEntity()))
	// if ((event instanceof EntityChangeBlockEvent
	// || event instanceof EntityExplodeEvent
	// || event instanceof EntityInteractEvent
	// || event instanceof EntityShootBowEvent
	// || event instanceof EntityTargetEvent
	// || event instanceof EntityTeleportEvent
	// || event instanceof ProjectileLaunchEvent || event instanceof
	// SlimeSplitEvent)
	// && (event instanceof Cancellable)) {
	// ((Cancellable) event).setCancelled(true);
	// }
	// }

	// @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
	// public void onPlayerInteract(PlayerInteractEntityEvent event){
	// Entity rightclicked = event.getRightClicked();
	// Player player = event.getPlayer();
	// if (!Tools.isBender(player, BendingType.Air))
	// return;
	// if (!(player.getItemInHand().getType() == Material.AIR))
	// return;
	// EntityType type = event.getRightClicked().getType();
	// if (type == EntityType.COW || type == EntityType.CHICKEN || type ==
	// EntityType.SHEEP
	// || type == EntityType.PIG){
	// rightclicked.setPassenger(player);
	// }
	// if (rightclicked.getPassenger() == player){
	// rightclicked.setPassenger(null);
	// }
	//
	// }

	@EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
	public void onInventoryClick(InventoryClickEvent event) {
		if (event.getSlotType() == SlotType.ARMOR
				&& !EarthArmor.canRemoveArmor((Player) event.getWhoClicked()))
			event.setCancelled(true);
	}

	@EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
	public void onPlayerQuit(PlayerQuitEvent event) {
		if (EarthArmor.instances.containsKey(event.getPlayer())) {
			EarthArmor.removeEffect(event.getPlayer());
			event.getPlayer().removePotionEffect(
					PotionEffectType.DAMAGE_RESISTANCE);
		}
	}

	@EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
	public void onPlayerToggleFlight(PlayerToggleFlightEvent event) {
		Player p = event.getPlayer();
		if (Tornado.getPlayers().contains(p) || Bloodbending.isBloodbended(p)
				|| Speed.getPlayers().contains(p)
				|| FireJet.getPlayers().contains(p)
				|| AvatarState.getPlayers().contains(p)) {
			event.setCancelled(p.getGameMode() != GameMode.CREATIVE);
		}
	}

	@EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
	public void onPlayerDeath(PlayerDeathEvent event) {
		if (EarthArmor.instances.containsKey(event.getEntity())) {
			List<ItemStack> drops = event.getDrops();
			List<ItemStack> newdrops = new ArrayList<ItemStack>();
			for (int i = 0; i < drops.size(); i++) {
				if (!(drops.get(i).getType() == Material.LEATHER_BOOTS
						|| drops.get(i).getType() == Material.LEATHER_CHESTPLATE
						|| drops.get(i).getType() == Material.LEATHER_HELMET
						|| drops.get(i).getType() == Material.LEATHER_LEGGINGS || drops
						.get(i).getType() == Material.AIR))
					newdrops.add((drops.get(i)));
			}
			if (EarthArmor.instances.get(event.getEntity()).oldarmor != null) {
				for (ItemStack is : EarthArmor.instances.get(event.getEntity()).oldarmor) {
					if (!(is.getType() == Material.AIR))
						newdrops.add(is);
				}
			}
			event.getDrops().clear();
			event.getDrops().addAll(newdrops);
			EarthArmor.removeEffect(event.getEntity());
		}
	}
}
// @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
// public void onPlayerInteract(PlayerInteractEntityEvent event){
// Entity rightclicked = event.getRightClicked();
// Player player = event.getPlayer();
// if (!Tools.isBender(player, BendingType.Air))
// return;
// if (!(player.getItemInHand().getType() == Material.AIR))
// return;
// EntityType type = event.getRightClicked().getType();
// if (type == EntityType.COW || type == EntityType.CHICKEN || type ==
// EntityType.SHEEP
// || type == EntityType.PIG){
// rightclicked.setPassenger(player);
// }
// if (rightclicked.getPassenger() == player){
// rightclicked.setPassenger(null);
// }
//
// }

