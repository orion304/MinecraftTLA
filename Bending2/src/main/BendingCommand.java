package main;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Server;
import org.bukkit.entity.Player;

import tools.Abilities;
import tools.AvatarState;
import tools.BendingType;
import tools.ConfigManager;
import tools.Tools;
import waterbending.Bloodbending;
import waterbending.FreezeMelt;
import waterbending.HealingWaters;
import waterbending.IceSpike;
import waterbending.WaterManipulation;
import waterbending.WaterSpout;
import waterbending.WaterWall;
import airbending.AirBlast;
import airbending.AirBubble;
import airbending.AirScooter;
import airbending.AirShield;
import airbending.AirSpout;
import airbending.AirSuction;
import airbending.AirSwipe;
import airbending.Tornado;
import chiblocking.HighJump;
import chiblocking.RapidPunch;
import earthbending.Catapult;
import earthbending.Collapse;
import earthbending.EarthArmor;
import earthbending.EarthBlast;
import earthbending.EarthColumn;
import earthbending.EarthGrab;
import earthbending.EarthTunnel;
import earthbending.Tremorsense;
import firebending.ArcOfFire;
import firebending.Extinguish;
import firebending.FireBlast;
import firebending.FireJet;
import firebending.Fireball;
import firebending.Illumination;
import firebending.Lightning;
import firebending.WallOfFire;

public class BendingCommand {

	private static final String[] bindAliases = { "bind", "b" };
	private static final String[] clearAliases = { "clear", "cl" };
	private static final String[] chooseAliases = { "choose", "ch" };
	private static final String[] addAliases = { "add", "a" };
	private static final String[] removeAliases = { "remove", "r" };
	private static final String[] permaremoveAliases = { "permaremove",
			"premove", "pr", "p" };
	private static final String[] toggleAliases = { "toggle", "t" };
	private static final String[] displayAliases = { "display", "disp", "dis",
			"d" };
	private static final String[] reloadAliases = { "reload" };
	private static final String[] helpAliases = { "help", "h" };
	private static final String[] importAliases = { "import" };

	private static final String[] airbendingAliases = { "air", "a",
			"airbender", "airbending", "airbend" };
	private static final String[] earthbendingAliases = { "earth", "e",
			"earthbender", "earthbending", "earthbend" };
	private static final String[] firebendingAliases = { "fire", "f",
			"firebender", "firebending", "firebend" };
	private static final String[] waterbendingAliases = { "water", "w",
			"waterbender", "waterbending", "waterbend" };
	private static final String[] chiblockingAliases = { "chi", "c",
			"chiblock", "chiblocker", "chiblocking" };

	private static String[] waterbendingabilities = Abilities
			.getWaterbendingAbilities();
	private static String[] airbendingabilities = Abilities
			.getAirbendingAbilities();
	private static String[] earthbendingabilities = Abilities
			.getEarthbendingAbilities();
	private static String[] firebendingabilities = Abilities
			.getFirebendingAbilities();
	private static String[] chiblockingabilities = Abilities
			.getChiBlockingAbilities();

	private static File dataFolder;
	private static StorageManager config;
	private static Server server;

	public static void handleCommand(Player player, String[] args,
			File dataFolder, StorageManager config, Server server) {

		BendingCommand.dataFolder = dataFolder;
		BendingCommand.config = config;
		BendingCommand.server = server;

		for (int i = 0; i < args.length; i++) {
			args[i] = args[i].toLowerCase();
		}

		if (args.length >= 1) {
			String arg = args[0];
			if (Arrays.asList(bindAliases).contains(arg)) {
				bind(player, args);
			} else if (Arrays.asList(clearAliases).contains(arg)) {
				clear(player, args);
			} else if (Arrays.asList(chooseAliases).contains(arg)) {
				choose(player, args);
			} else if (Arrays.asList(addAliases).contains(arg)) {
				add(player, args);
			} else if (Arrays.asList(removeAliases).contains(arg)) {
				remove(player, args);
			} else if (Arrays.asList(permaremoveAliases).contains(arg)) {
				permaremove(player, args);
			} else if (Arrays.asList(toggleAliases).contains(arg)) {
				toggle(player, args);
			} else if (Arrays.asList(displayAliases).contains(arg)) {
				display(player, args);
			} else if (Arrays.asList(reloadAliases).contains(arg)) {
				reload(player, args);
			} else if (Arrays.asList(helpAliases).contains(arg)) {
				help(player, args);
			} else if (Arrays.asList(importAliases).contains(arg)) {
				importBending(player, args);
			}
		} else {

			printHelpDialogue(player);

		}

	}

	private static void printUsageMessage(Player player, String message) {
		if (player == null) {
			Bending.log.info(message);
		} else {
			player.sendMessage(message);
		}
	}

	private static void choose(Player player, String[] args) {
		if (args.length != 2 && args.length != 3) {
			if (player == null) {
				printUsageMessage(player,
						"Usage: /bending choose <player> <element>");
			} else {
				if (player.hasPermission("bending.command.choose")
						|| player.hasPermission("bending.admin.rechoose")) {
					printUsageMessage(player,
							"Usage: /bending choose <element>");
				}
				if (player.hasPermission("bending.admin.choose")) {
					printUsageMessage(player,
							"Usage: /bending choose <player> <element>");
				}

				if (!player.hasPermission("bending.command.choose")
						&& !player.hasPermission("bending.admin.rechoose")
						&& !player.hasPermission("bending.admin.choose")) {
					printNoPermissions(player);
					return;
				}
			}
			return;
		}
		if (args.length == 2) {
			if (player == null) {
				printUsageMessage(player,
						"Usage: /bending choose <player> <element>");
				return;
			}
			if (Tools.isBender(player.getName())
					&& !player.hasPermission("bending.admin.rechoose")) {
				printNoPermissions(player);
				return;
			}
			String choice = args[1].toLowerCase();
			if (Arrays.asList(airbendingAliases).contains(choice)) {
				sendMessage(player, "You are now an airbender!");
				config.removeBending(player);
				config.setBending(player, "air");
				return;
			}
			if (Arrays.asList(firebendingAliases).contains(choice)) {
				sendMessage(player, "You are now a firebender!");
				config.removeBending(player);
				config.setBending(player, "fire");
				return;
			}
			if (Arrays.asList(earthbendingAliases).contains(choice)) {
				sendMessage(player, "You are now an earthbender!");
				config.removeBending(player);
				config.setBending(player, "earth");
				return;
			}
			if (Arrays.asList(waterbendingAliases).contains(choice)) {
				sendMessage(player, "You are now a waterbender!");
				config.removeBending(player);
				config.setBending(player, "water");
				return;
			}
			if (Arrays.asList(chiblockingAliases).contains(choice)) {
				sendMessage(player, "You are now a chiblocker!");
				config.removeBending(player);
				config.setBending(player, "chiblocker");
				return;
			}
			printUsageMessage(player, "Usage: /bending choose <element>");
		} else if (args.length == 3) {
			if (!hasPermission(player, "bending.admin.choose"))
				return;
			String playername = args[1];
			Player targetplayer = server.getPlayer(playername);
			if (targetplayer == null) {
				printUsageMessage(player,
						"Usage: /bending choose <player> <element>");
				return;
			}

			String senderName = "The server";
			if (player != null)
				senderName = player.getName();

			String choice = args[1].toLowerCase();
			if (Arrays.asList(airbendingAliases).contains(choice)) {
				sendMessage(player,
						"You have changed " + targetplayer.getName()
								+ "'s bending.");
				sendMessage(targetplayer, senderName
						+ " has changed your bending.");
				sendMessage(targetplayer, "You are now an airbender!");
				config.removeBending(player);
				config.setBending(player, "air");
				return;
			}
			if (Arrays.asList(firebendingAliases).contains(choice)) {
				sendMessage(player,
						"You have changed " + targetplayer.getName()
								+ "'s bending.");
				sendMessage(targetplayer, senderName
						+ " has changed your bending.");
				sendMessage(targetplayer, "You are now a firebender!");
				config.removeBending(player);
				config.setBending(player, "fire");
				return;
			}
			if (Arrays.asList(earthbendingAliases).contains(choice)) {
				sendMessage(player,
						"You have changed " + targetplayer.getName()
								+ "'s bending.");
				sendMessage(targetplayer, senderName
						+ " has changed your bending.");
				sendMessage(targetplayer, "You are now an earthbender!");
				config.removeBending(player);
				config.setBending(player, "earth");
				return;
			}
			if (Arrays.asList(waterbendingAliases).contains(choice)) {
				sendMessage(player,
						"You have changed " + targetplayer.getName()
								+ "'s bending.");
				sendMessage(targetplayer, senderName
						+ " has changed your bending.");
				sendMessage(targetplayer, "You are now a waterbender!");
				config.removeBending(player);
				config.setBending(player, "water");
				return;
			}
			if (Arrays.asList(chiblockingAliases).contains(choice)) {
				sendMessage(player,
						"You have changed " + targetplayer.getName()
								+ "'s bending.");
				sendMessage(targetplayer, senderName
						+ " has changed your bending.");
				sendMessage(targetplayer, "You are now a chiblocker!");
				config.removeBending(player);
				config.setBending(player, "chiblocker");
				return;
			}
			printUsageMessage(player,
					"Usage: /bending choose <player> <element>");
		}
	}

	private static void sendMessage(Player player, String message) {
		if (player == null) {
			Bending.log.info(message);
		} else {
			player.sendMessage(message);
		}
	}

	private static void importBending(Player player, String[] args) {
		if (!hasPermission(player, "bending.admin.import"))
			return;

		if (StorageManager.useFlatFile) {
			sendMessage(player, ChatColor.AQUA
					+ "MySQL needs to be enabled to import bendingPlayers");
			return;
		}
		BendingPlayers temp = new BendingPlayers(dataFolder);
		Set<String> keys = temp.getKeys();

		for (String s : keys) {
			if (s.contains("<")) {
				String[] getplayername = s.split("<");
				String playername = getplayername[0];
				String[] getSetter = s.split("<");
				String Setter = getSetter[1];
				String binded = Setter.replace("Bind", "").replace(">", "");
				String ability = temp.getKey(s);
				if ((binded.equalsIgnoreCase("0")
						|| binded.equalsIgnoreCase("1")
						|| binded.equalsIgnoreCase("2")
						|| binded.equalsIgnoreCase("3")
						|| binded.equalsIgnoreCase("4")
						|| binded.equalsIgnoreCase("5")
						|| binded.equalsIgnoreCase("6")
						|| binded.equalsIgnoreCase("7") || binded
							.equalsIgnoreCase("8"))) {
					int slot = Integer.parseInt(binded);
					config.setAbility(playername, ability, slot);
				} else {
					config.setAbility(playername, ability,
							Material.matchMaterial(binded));
				}
			} else {
				String playerName = s;
				String bending = temp.getKey(s);
				if (bending.contains("a"))
					config.addBending(playerName, BendingType.Air);
				if (bending.contains("w"))
					config.addBending(playerName, BendingType.Water);
				if (bending.contains("f"))
					config.addBending(playerName, BendingType.Fire);
				if (bending.contains("e"))
					config.addBending(playerName, BendingType.Earth);
				if (bending.contains("c"))
					config.addBending(playerName, BendingType.ChiBlocker);

			}

		}
		temp = null;
		sendMessage(player, ChatColor.AQUA
				+ "Imported BendingPlayers to MySQL.");

	}

	private static void printNoPermissions(Player player) {
		sendMessage(player, ChatColor.RED
				+ "You do not have permission to execute that command.");

	}

	private static void help(Player player, String[] args) {
		int pages = 0;
		int page = 1;
		List<String> command = new ArrayList<String>();
		for (String s : Bending.commands.keySet()) {
			if (hasHelpPermission(player, "bending." + s)) {
				command.add(Bending.commands.get(s));
			}
		}
		if (args.length > 1) {
			if (Abilities.getAbility(args[1]) != null) {
				if (Tools.hasPermission(player, Abilities.getAbility(args[1]))) {

					ChatColor cc = ChatColor.GOLD;
					if (Abilities.isAirbending(Abilities.getAbility(args[1])))
						cc = Tools.getColor(ConfigManager.color.get("Air"));
					if (Abilities.isFirebending(Abilities.getAbility(args[1])))
						cc = Tools.getColor(ConfigManager.color.get("Fire"));
					if (Abilities.isEarthbending(Abilities.getAbility(args[1])))
						cc = Tools.getColor(ConfigManager.color.get("Earth"));
					if (Abilities.isWaterbending(Abilities.getAbility(args[1])))
						cc = Tools.getColor(ConfigManager.color.get("Water"));
					sendMessage(
							player,
							("                                                "
									+ cc + Abilities.getAbility(args[1]).name()));
					switch (Abilities.getAbility(args[1])) {
					case AirBlast:
						sendMessage(player, cc + AirBlast.getDescription());
						break;
					case AirBubble:
						sendMessage(player, cc + AirBubble.getDescription());
						break;
					case AirShield:
						sendMessage(player, cc + AirShield.getDescription());
						break;
					case AirSuction:
						sendMessage(player, cc + AirSuction.getDescription());
						break;
					case AirSwipe:
						sendMessage(player, cc + AirSwipe.getDescription());
						break;
					case Tornado:
						sendMessage(player, cc + Tornado.getDescription());
						break;
					case AirScooter:
						sendMessage(player, cc + AirScooter.getDescription());
						break;
					// case AirBurst:
					// sendMessage(player, cc
					// + AirBurst.getDescription());
					// break;
					// case AirSpout:
					// sendMessage(player, cc
					// + AirSpout.getDescription());
					// break;
					case Catapult:
						sendMessage(player, cc + Catapult.getDescription());
						break;
					case RaiseEarth:
						sendMessage(player, cc + EarthColumn.getDescription());
						break;
					case EarthGrab:
						sendMessage(player, cc + EarthGrab.getDescription());
						break;
					case EarthTunnel:
						sendMessage(player, cc + EarthTunnel.getDescription());
						break;
					// case CompactColumn:
					// sendMessage(player, cc
					// + CompactColumn.getDescription());
					// break;
					case EarthBlast:
						sendMessage(player, cc + EarthBlast.getDescription());
						break;
					case Collapse:
						sendMessage(player, cc + Collapse.getDescription());
						break;
					case Tremorsense:
						sendMessage(player, cc + Tremorsense.getDescription());
						break;
					// case Shockwave:
					// sendMessage(player, cc
					// + Shockwave.getDescription());
					// break;
					case Blaze:
						sendMessage(player, cc + ArcOfFire.getDescription());
						break;
					case ControlHeat:
						sendMessage(player, cc + Extinguish.getDescription());
						break;
					case Fireball:
						sendMessage(player, cc + Fireball.getDescription());
						break;
					case FireBlast:
						sendMessage(player, cc + FireBlast.getDescription());
						break;
					// case HeatMelt:
					// sendMessage(player, cc
					// + HeatMelt.getDescription());
					// break;
					// case RingOfFire:
					// sendMessage(player, cc
					// + RingOfFire.getDescription());
					// break;
					case FireJet:
						sendMessage(player, cc + FireJet.getDescription());
						break;
					case Illumination:
						sendMessage(player, cc + Illumination.getDescription());
						break;
					case Lightning:
						sendMessage(player, cc + Lightning.getDescription());
						break;
					case WallOfFire:
						sendMessage(player, cc + WallOfFire.getDescription());
						break;
					case Bloodbending:
						sendMessage(player, cc + Bloodbending.getDescription());
						break;
					case WaterBubble:
						sendMessage(player, cc + AirBubble.getDescription());
						break;
					case PhaseChange:
						sendMessage(player, cc + FreezeMelt.getDescription());
						break;
					case HealingWaters:
						sendMessage(player, cc + HealingWaters.getDescription());
						break;
					// case Plantbending:
					// sendMessage(player, cc
					// + Plantbending.getDescription());
					// break;
					// case WalkOnWater:
					// sendMessage(player, cc
					// + WalkOnWater.getDescription());
					// break;
					case WaterManipulation:
						sendMessage(player,
								cc + WaterManipulation.getDescription());
						break;
					case WaterSpout:
						sendMessage(player, cc + WaterSpout.getDescription());
						break;
					case Surge:
						sendMessage(player, cc + WaterWall.getDescription());
						break;
					// case Wave:
					// sendMessage(player, cc + Wave.getDescription());
					// break;
					case AvatarState:
						sendMessage(player, cc + AvatarState.getDescription());
						break;
					case EarthArmor:
						sendMessage(player, cc + EarthArmor.getDescription());
						break;
					case RapidPunch:
						sendMessage(player, cc + RapidPunch.getDescription());
						break;
					case HighJump:
						sendMessage(player, cc + HighJump.getDescription());
						break;
					case IceSpike:
						sendMessage(player, cc + IceSpike.getDescription());
						break;
					case AirSpout:
						sendMessage(player, cc + AirSpout.getDescription());
						break;
					}
					return;
				}
			}
			for (String s : command) {
				if (args[1].equalsIgnoreCase(s.split(" ")[0])) {
					sendMessage(
							player,
							ChatColor.AQUA
									+ "                                        /bending "
									+ (s.split(" ")[0]));
					String msg = "";
					if ((s.split(" ")[0]).equalsIgnoreCase("choose")
							&& hasHelpPermission(player,
									"bending.command.choose")) {
						msg = "The command /bending choose <element> let you choose one of the four elements (air, fire, water, air) each having different abilities. ";
						if (hasHelpPermission(player, "bending.admin.choose"))
							msg = msg
									+ "The command can also be used to set other people bending element like so /bending choose <player> <element>";
						sendMessage(player, msg);
					} else if ((s.split(" ")[0]).equalsIgnoreCase("bind")
							&& hasHelpPermission(player, "bending.command.bind")) {
						String append = "a slot";
						if (ConfigManager.bendToItem) {
							append = "an item";
						}
						msg = "The command /bending <bind> <ability> is used to bind an ability to "
								+ append + ".";
						sendMessage(player, msg);
					} else if ((s.split(" ")[0].toLowerCase())
							.equalsIgnoreCase("help")) {

					} else if ((s.split(" ")[0].toLowerCase())
							.equalsIgnoreCase("remove")
							&& hasHelpPermission(player, "bending.admin.remove")) {
						sendMessage(
								player,
								"The command /bending remove <player> will remove the player bending. It can also be used to lift the permaremove of a player.");
					} else if ((s.split(" ")[0]).equalsIgnoreCase("reload")
							&& hasHelpPermission(player, "bending.admin.reload")) {
						player.sendMessage("The command /bending reload will allow you to reload the configuration, so you can make change while the server is running and don't have to restart/reload it.");
					} else if ((s.split(" ")[0])
							.equalsIgnoreCase("permaremove")
							&& hasHelpPermission(player,
									"bending.admin.permaremove")) {
						sendMessage(
								player,
								"The command /bending permaremove <player> will permanantly remove someone's bending and won't allow him to choose once again until you do /bending remove <player> or manually set his bending");
					} else if ((s.split(" ")[0]).equalsIgnoreCase("add")
							&& hasHelpPermission(player, "bending.admin.add")) {
						sendMessage(
								player,
								"The command /bending add <element> allow you to add elements to the one you aleredy have. It can be used to stack all for eleents at once.");
					} else if ((s.split(" ")[0]).equalsIgnoreCase("display")
							&& hasHelpPermission(player,
									"bending.command.display")) {
						msg = ("The command /bending display allows you to see a list of your binding so you can remember where you binded what.");
						if (hasHelpPermission(player,
								"bending.command.displayelement")) {
							msg = msg
									+ " The command /bending display <element> can allow you to see a list of abilities an bending element has.";
						}
						sendMessage(player, msg);
					}

					return;
				}
			}

			try {
				page = Integer.parseInt(args[1]);
			} catch (NumberFormatException e) {
				page = -1;
			}
		}
		if (page != -1) {
			pages = command.size() / 8;
			if (command.size() % 8 != 0)
				pages++;

			if (page > pages) {
				if (pages > 1) {
					sendMessage(player, ChatColor.RED + "There's only " + pages
							+ " pages of help");
				} else {
					sendMessage(player, ChatColor.RED + "There's only " + pages
							+ " page of help");
				}
				return;
			}
			sendMessage(player, ChatColor.AQUA
					+ "=======================Help===========================");
			for (int i = 1; i <= 8; i++) {
				if (command.size() > (i + (8 * page) - 9)) {
					String comname = "/bending "
							+ command.get((i + (8 * page) - 9));
					sendMessage(player, comname);
				}
			}
			sendMessage(player, ChatColor.AQUA + "=====================Page "
					+ page + "/" + pages + "========================");
			return;
		}

	}

	private static void reload(Player player, String[] args) {
		if (!hasPermission(player, "bending.admin.reload"))
			return;
		Bending.configManager.load(new File(dataFolder, "config.yml"));
		config.initialize(dataFolder);
		String append = StorageManager.useMySQL ? " Database" : "Players file";
		sendMessage(player, ChatColor.AQUA + "Config and Bending" + append
				+ " was reloaded");

	}

	private static void display(Player player, String[] args) {
		if (!hasPermission(player, "bending.command.display"))
			return;

		if (args.length > 2) {
			if (player != null)
				printUsageMessage(player, "Usage: /bending display");
			printUsageMessage(player, "Usage: /bending display <element>");
		}

		if (args.length == 1) {
			if (player == null) {
				printNotFromConsole();
				return;
			}

			if (!ConfigManager.bendToItem) {
				for (int i = 0; i <= 8; i++) {
					Abilities a = config.getAbility(player, i);
					ChatColor color = ChatColor.WHITE;
					if (Abilities.isAirbending(a)) {
						color = Tools.getColor(ConfigManager.getColor("Air"));
					} else if (Abilities.isChiBlocking(a)) {
						color = Tools.getColor(ConfigManager
								.getColor("ChiBlocker"));
					} else if (Abilities.isEarthbending(a)) {
						color = Tools.getColor(ConfigManager.getColor("Earth"));
					} else if (Abilities.isFirebending(a)) {
						color = Tools.getColor(ConfigManager.getColor("Fire"));
					} else if (Abilities.isWaterbending(a)) {
						color = Tools.getColor(ConfigManager.getColor("Water"));
					}
					if (a != null) {
						String ability = config.getAbility(player, i).name();
						sendMessage(player, "Slot " + (i + 1) + ": " + color
								+ ability);
					}
				}
			} else {

				for (Material mat : Material.values()) {
					Abilities a = config.getAbility(player, mat);
					if (a != null) {
						ChatColor color = ChatColor.WHITE;
						if (Abilities.isAirbending(a)) {
							color = Tools.getColor(ConfigManager
									.getColor("Air"));
						} else if (Abilities.isChiBlocking(a)) {
							color = Tools.getColor(ConfigManager
									.getColor("ChiBlocker"));
						} else if (Abilities.isEarthbending(a)) {
							color = Tools.getColor(ConfigManager
									.getColor("Earth"));
						} else if (Abilities.isFirebending(a)) {
							color = Tools.getColor(ConfigManager
									.getColor("Fire"));
						} else if (Abilities.isWaterbending(a)) {
							color = Tools.getColor(ConfigManager
									.getColor("Water"));
						}
						String ability = config.getAbility(player, mat).name();
						sendMessage(player, mat.name().replaceAll("_", " ")
								+ ": " + color + ability);
					}
				}
			}
		}

		if (args.length == 2) {
			String[] abilitylist = null;
			String choice = args[1].toLowerCase();
			ChatColor color = ChatColor.WHITE;
			if (Arrays.asList(airbendingAliases).contains(choice)) {
				abilitylist = airbendingabilities;
				color = Tools.getColor(ConfigManager.getColor("Air"));
			} else if (Arrays.asList(waterbendingAliases).contains(choice)) {
				abilitylist = waterbendingabilities;
				color = Tools.getColor(ConfigManager.getColor("Water"));
			} else if (Arrays.asList(earthbendingAliases).contains(choice)) {
				abilitylist = earthbendingabilities;
				color = Tools.getColor(ConfigManager.getColor("Earth"));
			} else if (Arrays.asList(firebendingAliases).contains(choice)) {
				abilitylist = firebendingabilities;
				color = Tools.getColor(ConfigManager.getColor("Fire"));
			} else if (Arrays.asList(chiblockingAliases).contains(choice)) {
				abilitylist = chiblockingabilities;
				color = Tools.getColor(ConfigManager.getColor("ChiBlocker"));
			}

			if (abilitylist != null) {
				for (String ability : abilitylist) {
					if (Tools.hasPermission(player,
							Abilities.getAbility(ability))) {
						sendMessage(player, color + ability);
					}
				}
				return;
			} else {
				if (player != null)
					printUsageMessage(player, "Usage: /bending display");
				printUsageMessage(player, "Usage: /bending display <element>");
			}

		}

	}

	private static void toggle(Player player, String[] args) {
		if (args.length == 1) {
			if (!hasPermission(player, "bending.command.toggle"))
				return;
			if (player == null) {
				printNotFromConsole();
				return;
			}
			if (!Tools.toggledBending.contains(player)) {
				Tools.toggledBending.add(player);
				sendMessage(
						player,
						ChatColor.AQUA
								+ "You toggled your bending. You now can't use bending until you use that command again.");
			} else {
				Tools.toggledBending.remove(player);
				sendMessage(
						player,
						ChatColor.AQUA
								+ "You toggled your bending back. You can now use them freely again!");
			}
		} else {
			if (!hasPermission(player, "bending.admin.toggle"))
				return;
			String senderName = "The server";
			if (player != null)
				senderName = player.getName();
			String playerlist = "";
			for (int i = 1; i < args.length; i++) {
				String name = args[i];
				Player targetplayer = server.getPlayer(name);
				if (targetplayer != null) {
					if (!Tools.toggledBending.contains(targetplayer)) {
						Tools.toggledBending.add(targetplayer);
						sendMessage(
								player,
								ChatColor.AQUA
										+ senderName
										+ " has toggled your bending. You now can't use bending until you use /bending toggle.");
					} else {
						Tools.toggledBending.remove(targetplayer);
						sendMessage(
								player,
								ChatColor.AQUA
										+ senderName
										+ "has toggled your bending back. You can now use them freely again!");
					}

					playerlist = playerlist + " " + targetplayer.getName();
				}
			}
			sendMessage(player, "You have toggled the bending of: "
					+ playerlist);
		}

	}

	private static void printNotFromConsole() {
		Bending.log.info("This command cannot be used from the console.");

	}

	private static void permaremove(Player player, String[] args) {
		if (!hasPermission(player, "bending.admin.permaremove"))
			return;
		String playerlist = "";
		for (int i = 1; i < args.length; i++) {
			String playername = args[i];
			Player targetplayer = server.getPlayer(playername);
			if (targetplayer != null) {
				config.permaRemoveBending(targetplayer);
				targetplayer.sendMessage(player.getName()
						+ " has removed your bending permanently.");
				playerlist = playerlist + targetplayer.getName() + " ";
			}
		}
		sendMessage(player, "You have permanently removed the bending of: "
				+ playerlist);
	}

	private static void remove(Player player, String[] args) {
		if (!hasPermission(player, "bending.admin.remove"))
			return;
		String playerlist = "";
		for (int i = 1; i < args.length; i++) {
			String playername = args[i];
			Player targetplayer = server.getPlayer(playername);
			if (targetplayer != null) {
				config.removeBending(targetplayer);
				targetplayer.sendMessage(player.getName()
						+ " has removed your bending.");
				playerlist = playerlist + targetplayer.getName() + " ";
			}
		}
		sendMessage(player, "You have removed the bending of: " + playerlist);

	}

	private static void add(Player player, String[] args) {
		if (!hasPermission(player, "bending.admin.add"))
			return;
		if (args.length != 2 && args.length != 3) {
			if (player == null) {
				printUsageMessage(player,
						"Usage: /bending add <player> <element>");
			} else {
				printUsageMessage(player, "Usage: /bending add <element>");
				printUsageMessage(player,
						"Usage: /bending add <player> <element>");
			}
			return;
		}
		if (args.length == 2) {
			if (player == null) {
				printUsageMessage(player,
						"Usage: /bending add <player> <element>");
				return;
			}
			String choice = args[1].toLowerCase();
			if (Arrays.asList(airbendingAliases).contains(choice)) {
				if (Tools.isBender(player.getName(), BendingType.Air)) {
					sendMessage(player, "You are already an airbender.");
					return;
				}
				sendMessage(player, "You are now also an airbender!");
				config.addBending(player, "air");
				return;
			}
			if (Arrays.asList(firebendingAliases).contains(choice)) {
				if (Tools.isBender(player.getName(), BendingType.Air)) {
					sendMessage(player, "You are already a firebender.");
					return;
				}
				sendMessage(player, "You are now also a firebender!");
				config.addBending(player, "fire");
				return;
			}
			if (Arrays.asList(earthbendingAliases).contains(choice)) {
				if (Tools.isBender(player.getName(), BendingType.Air)) {
					sendMessage(player, "You are already an earthbender.");
					return;
				}
				sendMessage(player, "You are now also an earthbender!");
				config.addBending(player, "earth");
				return;
			}
			if (Arrays.asList(waterbendingAliases).contains(choice)) {
				if (Tools.isBender(player.getName(), BendingType.Air)) {
					sendMessage(player, "You are already a waterbender.");
					return;
				}
				sendMessage(player, "You are now also a waterbender!");
				config.addBending(player, "water");
				return;
			}
			if (Arrays.asList(chiblockingAliases).contains(choice)) {
				if (Tools.isBender(player.getName(), BendingType.Air)) {
					sendMessage(player, "You are already a chiblocker.");
					return;
				}
				sendMessage(player, "You are now also a chiblocker!");
				config.addBending(player, "chiblocker");
				return;
			}
			printUsageMessage(player, "Usage: /bending choose <element>");
		} else if (args.length == 3) {
			String playername = args[1];
			Player targetplayer = server.getPlayer(playername);
			if (targetplayer == null) {
				printUsageMessage(player,
						"Usage: /bending choose <player> <element>");
				return;
			}

			String senderName = "The server";
			if (player != null)
				senderName = player.getName();

			String choice = args[1].toLowerCase();
			if (Arrays.asList(airbendingAliases).contains(choice)) {
				if (Tools.isBender(targetplayer.getName(), BendingType.Air)) {
					sendMessage(player, targetplayer.getName()
							+ " is already an airbender.");
					return;
				}
				sendMessage(player, targetplayer.getName()
						+ " is now also an airbender!");
				sendMessage(targetplayer, senderName
						+ " has now made you also an airbender!");
				config.addBending(player, "air");
				return;
			}
			if (Arrays.asList(firebendingAliases).contains(choice)) {
				if (Tools.isBender(targetplayer.getName(), BendingType.Air)) {
					sendMessage(player, targetplayer.getName()
							+ " is already a firebender.");
					return;
				}
				sendMessage(player, targetplayer.getName()
						+ " is now also a firebender!");
				sendMessage(targetplayer, senderName
						+ " has now made you also a firebender!");
				config.addBending(player, "fire");
				return;
			}
			if (Arrays.asList(earthbendingAliases).contains(choice)) {
				if (Tools.isBender(targetplayer.getName(), BendingType.Air)) {
					sendMessage(player, targetplayer.getName()
							+ " is already an earthbender.");
					return;
				}
				sendMessage(player, targetplayer.getName()
						+ " is now also an earthbender!");
				sendMessage(targetplayer, senderName
						+ " has now made you also an earthbender!");
				config.addBending(player, "earth");
				return;
			}
			if (Arrays.asList(waterbendingAliases).contains(choice)) {
				if (Tools.isBender(targetplayer.getName(), BendingType.Air)) {
					sendMessage(player, targetplayer.getName()
							+ " is already a waterbender.");
					return;
				}
				sendMessage(player, targetplayer.getName()
						+ " is now also a waterbender!");
				sendMessage(targetplayer, senderName
						+ " has now made you also a waterbender!");
				config.addBending(player, "water");
				return;
			}
			if (Arrays.asList(chiblockingAliases).contains(choice)) {
				if (Tools.isBender(targetplayer.getName(), BendingType.Air)) {
					sendMessage(player, targetplayer.getName()
							+ " is already a chiblocker.");
					return;
				}
				sendMessage(player, targetplayer.getName()
						+ " is now also a chiblocker!");
				sendMessage(targetplayer, senderName
						+ " has now made you also a chiblocker!");
				config.addBending(player, "chiblocker");
				return;
			}
			printUsageMessage(player, "Usage: /bending add <element>");
			printUsageMessage(player, "Usage: /bending add <player> <element>");
		}

	}

	private static void clear(Player player, String[] args) {
		if (!hasPermission(player, "bending.command.clear"))
			return;
		if (player == null) {
			printNotFromConsole();
			return;
		}

		if (args.length != 1 && args.length != 2) {
			printUsageMessage(player, "Usage: /bending clear");
			if (!ConfigManager.bendToItem) {
				printUsageMessage(player, "Usage: /bending clear <slot#>");
			} else {
				printUsageMessage(player, "Usage: /bending clear <item>");
			}
		}

		if (args.length == 1) {
			if (!ConfigManager.bendToItem) {
				for (int i = 0; i <= 8; i++) {
					config.removeAbility(player, i);
				}
			} else {
				for (Material mat : Material.values()) {
					config.removeAbility(player, mat.getId());
				}
			}
			sendMessage(player, "Your abilities have been cleared.");
		} else if (args.length == 2) {
			if (!ConfigManager.bendToItem) {
				if (Integer.parseInt(args[1]) > 0
						&& Integer.parseInt(args[1]) < 10) {
					config.removeAbility(player, Integer.parseInt(args[1]) - 1);
					sendMessage(player, "Slot " + args[1]
							+ " has been cleared.");
					return;
				}
				printUsageMessage(player, "Usage: /bending clear");
				printUsageMessage(player, "Usage: /bending clear <slot#>");
			} else {
				if (Material.matchMaterial(args[1]) != null) {
					config.removeAbility(player,
							Material.matchMaterial(args[1]));
					printUsageMessage(player, "Item " + args[1]
							+ "has been cleared.");
					return;
				}
				printUsageMessage(player, "Usage: /bending clear");
				printUsageMessage(player, "Usage: /bending clear <item>");
			}

		}

	}

	private static void bind(Player player, String[] args) {
		if (!hasPermission(player, "bending.command.bind"))
			return;
		if (player == null) {
			printNotFromConsole();
			return;
		}

		if (args.length != 2 && args.length != 3) {
			printUsageMessage(player, "/bending bind <ability>");
			if (!ConfigManager.bendToItem) {
				printUsageMessage(player, "/bending bind <ability> <slot#>");
			} else {
				printUsageMessage(player, "/bending bind <ability> <item>");
			}
			return;
		}

		String a = args[1];
		Abilities ability = Abilities.getAbility(a);

		if (ability == null) {
			printUsageMessage(player, "/bending bind <ability>");
			if (!ConfigManager.bendToItem) {
				printUsageMessage(player, "/bending bind <ability> <slot#>");
			} else {
				printUsageMessage(player, "/bending bind <ability> <item>");
			}
			return;
		}

		if (Tools.hasPermission(player, ability)) {
			printNoPermissions(player);
			return;
		}

		int slot = player.getInventory().getHeldItemSlot();
		Material mat = player.getInventory().getItemInHand().getType();

		if (args.length == 3 && ConfigManager.bendToItem) {
			mat = Material.matchMaterial(args[2]);
			if (mat == null) {
				printUsageMessage(player, "/bending bind <ability>");
				printUsageMessage(player, "/bending bind <ability> <item>");
				return;
			}
		} else if (args.length == 3) {
			slot = Integer.parseInt(args[2]);
			if (slot <= 0 || slot >= 10) {
				printUsageMessage(player, "/bending bind <ability>");
				printUsageMessage(player, "/bending bind <ability> <slot#>");
				return;
			}
			slot--;
		}

		ChatColor color = ChatColor.WHITE;
		ChatColor white = ChatColor.WHITE;

		if (Tools.isBender(player.getName(), BendingType.Water)
				&& Abilities.isWaterbending(ability)) {
			color = Tools.getColor(ConfigManager.getColor("Water"));
			if (!ConfigManager.bendToItem) {
				config.setAbility(player, ability, slot);
				sendMessage(player, color + ability.name() + white
						+ " bound to slot " + (slot + 1));
			} else {
				config.setAbility(player, ability, mat);
				char[] tocap = mat.name().replaceAll("_", " ").toCharArray();
				boolean cap = true;
				for (int i = 0; i < tocap.length; i++) {
					if (cap) {
						tocap[i] = Character.toUpperCase(tocap[i]);
						cap = false;
					}
					if (Character.isWhitespace(tocap[i]))
						cap = true;

				}
				sendMessage(player, color + ability.name() + white
						+ " bound to " + tocap.toString());
			}
			return;
		}
		if (Tools.isBender(player.getName(), BendingType.Air)
				&& Abilities.isAirbending(ability)) {
			color = Tools.getColor(ConfigManager.getColor("Air"));
			if (!ConfigManager.bendToItem) {
				config.setAbility(player, ability, slot);
				sendMessage(player, color + ability.name() + white
						+ " bound to slot " + (slot + 1));
			} else {
				config.setAbility(player, ability, mat);
				sendMessage(player, color + ability.name() + white
						+ " bound to " + mat.name().replaceAll("_", " "));
			}
			return;
		}
		if (Tools.isBender(player.getName(), BendingType.Earth)
				&& Abilities.isEarthbending(ability)) {
			color = Tools.getColor(ConfigManager.getColor("Earth"));
			if (!ConfigManager.bendToItem) {
				config.setAbility(player, ability, slot);
				sendMessage(player, color + ability.name() + white
						+ " bound to slot " + (slot + 1));
			} else {
				config.setAbility(player, ability, mat);
				sendMessage(player, color + ability.name() + white
						+ " bound to " + mat.name().replaceAll("_", " "));
			}
			return;
		}
		if (Tools.isBender(player.getName(), BendingType.ChiBlocker)
				&& Abilities.isChiBlocking(ability)) {
			color = Tools.getColor(ConfigManager.getColor("ChiBlocker"));
			if (!ConfigManager.bendToItem) {
				config.setAbility(player, ability, slot);
				sendMessage(player, color + ability.name() + white
						+ " bound to slot " + (slot + 1));
			} else {
				config.setAbility(player, ability, mat);
				sendMessage(player, color + ability.name() + white
						+ " bound to " + mat.name().replaceAll("_", " "));
			}
			return;
		}
		if (Tools.isBender(player.getName(), BendingType.Fire)
				&& Abilities.isFirebending(ability)) {
			color = Tools.getColor(ConfigManager.getColor("Fire"));
			if (!ConfigManager.bendToItem) {
				config.setAbility(player, ability, slot);
				sendMessage(player, color + ability.name() + white
						+ " bound to slot " + (slot + 1));
			} else {
				config.setAbility(player, ability, mat);
				sendMessage(player, color + ability.name() + white
						+ " bound to " + mat.name().replaceAll("_", " "));
			}
			return;
		}
		if (player.hasPermission("bending.admin.avatarstate")
				&& ability == Abilities.AvatarState) {
			color = ChatColor.DARK_PURPLE;
			if (!ConfigManager.bendToItem) {
				config.setAbility(player, ability, slot);
				sendMessage(player, color + ability.name() + white
						+ " bound to slot " + (slot + 1));
			} else {
				config.setAbility(player, ability, mat);
				sendMessage(player, color + ability.name() + white
						+ " bound to " + mat.name().replaceAll("_", " "));
			}
			return;

		}

	}

	private static boolean hasPermission(Player player, String permission) {
		if (player == null)
			return true;
		if (player.hasPermission(permission)) {
			return true;
		}
		printNoPermissions(player);
		return false;
	}

	private static boolean hasHelpPermission(Player player, String permission) {
		if (player == null)
			return true;
		if (player.hasPermission(permission))
			return true;
		return false;
	}

	private static void printHelpDialogue(Player player) {
		if (player == null) {
			Bending.log
					.info("Use /bending help <page> if you want to see a list of commands.");
			Bending.log
					.info("Use /bending help <command> if you help with a command.");
		} else {
			player.sendMessage(ChatColor.RED
					+ "Use /bending help <page> if you want to see a list of commands.");
			player.sendMessage(ChatColor.RED
					+ "Use /bending help <ability> if you want to see how to use it.");
			player.sendMessage(ChatColor.RED
					+ "Use /bending help <command> if you need help with a command.");
		}
	}

}
