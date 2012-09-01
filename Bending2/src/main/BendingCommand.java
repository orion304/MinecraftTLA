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
import chiblocking.Paralyze;
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

	private final String[] bindAliases = { "bind", "b" };
	private final String[] clearAliases = { "clear", "cl" };
	private final String[] chooseAliases = { "choose", "ch" };
	private final String[] addAliases = { "add", "a" };
	private final String[] removeAliases = { "remove", "r" };
	private final String[] permaremoveAliases = { "permaremove", "premove",
			"pr", "p" };
	private final String[] toggleAliases = { "toggle", "t" };
	private final String[] displayAliases = { "display", "disp", "dis", "d" };
	private final String[] reloadAliases = { "reload" };
	private final String[] helpAliases = { "help", "h" };
	private final String[] importAliases = { "import" };
	private final String[] whoAliases = { "who", "wh", "w" };

	private final String[] airbendingAliases = { "air", "a", "airbender",
			"airbending", "airbend" };
	private final String[] earthbendingAliases = { "earth", "e", "earthbender",
			"earthbending", "earthbend" };
	private final String[] firebendingAliases = { "fire", "f", "firebender",
			"firebending", "firebend" };
	private final String[] waterbendingAliases = { "water", "w", "waterbender",
			"waterbending", "waterbend" };
	private final String[] chiblockingAliases = { "chi", "c", "chiblock",
			"chiblocker", "chiblocking" };

	private String[] waterbendingabilities = Abilities
			.getWaterbendingAbilities();
	private String[] airbendingabilities = Abilities.getAirbendingAbilities();
	private String[] earthbendingabilities = Abilities
			.getEarthbendingAbilities();
	private String[] firebendingabilities = Abilities.getFirebendingAbilities();
	private String[] chiblockingabilities = Abilities.getChiBlockingAbilities();

	private File dataFolder;
	private StorageManager config;
	private Server server;
	private boolean verbose = true;

	public BendingCommand(Player player, String[] args, File dataFolder,
			StorageManager config, Server server) {

		this.dataFolder = dataFolder;
		this.config = config;
		this.server = server;

		for (int i = 0; i < args.length; i++) {
			args[i] = args[i].toLowerCase();
		}

		if (args.length >= 1) {
			if (args[args.length - 1].equalsIgnoreCase("&")) {
				verbose = false;

				String[] temp = new String[args.length - 1];
				for (int i = 0; i < args.length - 1; i++) {
					temp[i] = args[i];
				}
				args = temp;
				// String arglist = "";
				// for (String arg : args)
				// arglist = arglist + arg + " ";
				// Tools.verbose(arglist);
			}

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
			} else if (Arrays.asList(whoAliases).contains(arg)) {
				who(player, args);
			} else {
				printHelpDialogue(player);
			}
		} else {

			printHelpDialogue(player);

		}

	}

	private void printWhoUsage(Player player) {
		if (!hasHelpPermission(player, "bending.command.who")) {
			sendNoCommandPermissionMessage(player, "who");
			return;
		}
		printUsageMessage(player, "/bending who",
				"Displays a list of users online, along with their bending types.");
		printUsageMessage(player, "/bending who <player>",
				"Displays which bending types that player has.");
		sendMessage(player, "**This command does not yet work!**");
	}

	private void who(Player player, String[] args) {
		if (!hasPermission(player, "bending.command.who"))
			return;

		if (args.length > 2) {
			printWhoUsage(player);
			return;
		}

		if (args.length == 1) {
			for (Player p : server.getOnlinePlayers()) {
				ChatColor color = ChatColor.WHITE;
				if (Tools.isBender(p.getName(), BendingType.Air))
					color = Tools.getColor(ConfigManager.getColor("Air"));
				if (Tools.isBender(p.getName(), BendingType.Water))
					color = Tools.getColor(ConfigManager.getColor("Water"));
				if (Tools.isBender(p.getName(), BendingType.Fire))
					color = Tools.getColor(ConfigManager.getColor("Fire"));
				if (Tools.isBender(p.getName(), BendingType.Earth))
					color = Tools.getColor(ConfigManager.getColor("Earth"));
				if (Tools.isBender(p.getName(), BendingType.ChiBlocker))
					color = Tools
							.getColor(ConfigManager.getColor("ChiBlocker"));
				sendMessage(player, color + p.getName());
			}
		} else if (args.length == 2) {
			Player p = server.getPlayer(args[1]);
			if (p == null) {
				sendMessage(player, args[1] + " is not on the server.");
			} else {
				sendMessage(player, p.getDisplayName());
				if (!Tools.isBender(p.getName())) {
					sendMessage(player, "-No bending");
				} else {
					if (Tools.isBender(p.getName(), BendingType.Air))
						sendMessage(player,
								Tools.getColor(ConfigManager.getColor("Air"))
										+ "-Airbending");
					if (Tools.isBender(p.getName(), BendingType.Water))
						sendMessage(player,
								Tools.getColor(ConfigManager.getColor("Water"))
										+ "-Waterbending");
					if (Tools.isBender(p.getName(), BendingType.Fire))
						sendMessage(player,
								Tools.getColor(ConfigManager.getColor("Fire"))
										+ "-Firebending");
					if (Tools.isBender(p.getName(), BendingType.Earth))
						sendMessage(player,
								Tools.getColor(ConfigManager.getColor("Earth"))
										+ "-Earthbending");
					if (Tools.isBender(p.getName(), BendingType.ChiBlocker))
						sendMessage(
								player,
								Tools.getColor(ConfigManager
										.getColor("ChiBlocker"))
										+ "-Chiblocking");
				}
			}
		} else {
			printWhoUsage(player);
		}
	}

	private void printUsageMessage(Player player, String command,
			String description) {
		ChatColor color = ChatColor.AQUA;
		if (player == null) {
			Bending.log.info(color + "Usage: " + command);
			Bending.log.info(color + "- " + description);
		} else {
			player.sendMessage(color + "Usage: " + command);
			player.sendMessage(color + "- " + description);
		}
	}

	private void printChooseUsage(Player player) {
		if (!hasHelpPermission(player, "bending.admin.choose")
				&& !hasHelpPermission(player, "bending.admin.rechoose")
				&& !hasHelpPermission(player, "bending.command.choose")) {
			sendNoCommandPermissionMessage(player, "choose");
			return;
		}
		if (hasHelpPermission(player, "bending.command.choose")
				|| hasHelpPermission(player, "bending.admin.rechoose")) {
			printUsageMessage(player, "/bending choose <element>",
					"Choose your <element> (Options: air, water, earth, fire, chiblocker).");
		}
		if (hasHelpPermission(player, "bending.admin.choose")) {
			printUsageMessage(player, "/bending choose <player> <element>",
					"Choose <element> for <player>.");
		}
	}

	private void choose(Player player, String[] args) {
		if (args.length != 2 && args.length != 3) {
			printChooseUsage(player);

			if (!player.hasPermission("bending.command.choose")
					&& !player.hasPermission("bending.admin.rechoose")
					&& !player.hasPermission("bending.admin.choose")) {
				printNoPermissions(player);
				return;
			}
			return;
		}
		if (args.length == 2) {
			if (player == null) {
				printChooseUsage(player);
				return;
			}
			if (!player.hasPermission("bending.command.choose")
					&& !player.hasPermission("bending.admin.rechoose")
					&& !player.hasPermission("bending.admin.choose")) {
				printNoPermissions(player);
				return;
			}
			if (Tools.isBender(player.getName())
					&& !player.hasPermission("bending.admin.rechoose")) {
				printNoPermissions(player);
				return;
			}
			String choice = args[1].toLowerCase();
			if (Arrays.asList(airbendingAliases).contains(choice)) {
				if (!hasHelpPermission(player, "bending.air")) {
					sendMessage(player,
							"You do not have permission to be an airbender.");
					return;
				}
				sendMessage(player, "You are now an airbender!");
				config.removeBending(player);
				config.setBending(player, "air");
				return;
			}
			if (Arrays.asList(firebendingAliases).contains(choice)) {
				if (!hasHelpPermission(player, "bending.fire")) {
					sendMessage(player,
							"You do not have permission to be a firebender.");
					return;
				}
				sendMessage(player, "You are now a firebender!");
				config.removeBending(player);
				config.setBending(player, "fire");
				return;
			}
			if (Arrays.asList(earthbendingAliases).contains(choice)) {
				if (!hasHelpPermission(player, "bending.earth")) {
					sendMessage(player,
							"You do not have permission to be an earthbender.");
					return;
				}
				sendMessage(player, "You are now an earthbender!");
				config.removeBending(player);
				config.setBending(player, "earth");
				return;
			}
			if (Arrays.asList(waterbendingAliases).contains(choice)) {
				if (!hasHelpPermission(player, "bending.water")) {
					sendMessage(player,
							"You do not have permission to be a waterbender.");
					return;
				}
				sendMessage(player, "You are now a waterbender!");
				config.removeBending(player);
				config.setBending(player, "water");
				return;
			}
			if (Arrays.asList(chiblockingAliases).contains(choice)) {
				if (!hasHelpPermission(player, "bending.chiblocking")) {
					sendMessage(player,
							"You do not have permission to be a chiblocker.");
					return;
				}
				sendMessage(player, "You are now a chiblocker!");
				config.removeBending(player);
				config.setBending(player, "chiblocker");
				return;
			}
			printChooseUsage(player);
		} else if (args.length == 3) {
			if (!hasPermission(player, "bending.admin.choose"))
				return;
			String playername = args[1];
			Player targetplayer = server.getPlayer(playername);
			if (targetplayer == null) {
				printChooseUsage(player);
				return;
			}

			String senderName = "The server";
			if (player != null)
				senderName = player.getName();

			String choice = args[2].toLowerCase();
			if (Arrays.asList(airbendingAliases).contains(choice)) {
				if (!hasHelpPermission(targetplayer, "bending.air")) {
					sendMessage(player,
							"They do not have permission to be an airbender.");
					return;
				}
				sendMessage(player,
						"You have changed " + targetplayer.getName()
								+ "'s bending.");
				sendMessage(targetplayer, senderName
						+ " has changed your bending.");
				sendMessage(targetplayer, "You are now an airbender!");
				config.removeBending(targetplayer);
				config.setBending(targetplayer, "air");
				return;
			}
			if (Arrays.asList(firebendingAliases).contains(choice)) {
				if (!hasHelpPermission(targetplayer, "bending.fire")) {
					sendMessage(player,
							"They do not have permission to be a firebender.");
					return;
				}
				sendMessage(player,
						"You have changed " + targetplayer.getName()
								+ "'s bending.");
				sendMessage(targetplayer, senderName
						+ " has changed your bending.");
				sendMessage(targetplayer, "You are now a firebender!");
				config.removeBending(targetplayer);
				config.setBending(targetplayer, "fire");
				return;
			}
			if (Arrays.asList(earthbendingAliases).contains(choice)) {
				if (!hasHelpPermission(targetplayer, "bending.earth")) {
					sendMessage(player,
							"They do not have permission to be an earthbender.");
					return;
				}
				sendMessage(player,
						"You have changed " + targetplayer.getName()
								+ "'s bending.");
				sendMessage(targetplayer, senderName
						+ " has changed your bending.");
				sendMessage(targetplayer, "You are now an earthbender!");
				config.removeBending(targetplayer);
				config.setBending(targetplayer, "earth");
				return;
			}
			if (Arrays.asList(waterbendingAliases).contains(choice)) {
				if (!hasHelpPermission(targetplayer, "bending.water")) {
					sendMessage(player,
							"They do not have permission to be a waterbender.");
					return;
				}
				sendMessage(player,
						"You have changed " + targetplayer.getName()
								+ "'s bending.");
				sendMessage(targetplayer, senderName
						+ " has changed your bending.");
				sendMessage(targetplayer, "You are now a waterbender!");
				config.removeBending(targetplayer);
				config.setBending(targetplayer, "water");
				return;
			}
			if (Arrays.asList(chiblockingAliases).contains(choice)) {
				if (!hasHelpPermission(targetplayer, "bending.chiblocking")) {
					sendMessage(player,
							"They do not have permission to be a chiblocker.");
					return;
				}
				sendMessage(player,
						"You have changed " + targetplayer.getName()
								+ "'s bending.");
				sendMessage(targetplayer, senderName
						+ " has changed your bending.");
				sendMessage(targetplayer, "You are now a chiblocker!");
				config.removeBending(targetplayer);
				config.setBending(targetplayer, "chiblocker");
				return;
			}
			printChooseUsage(player);
		}
	}

	private void sendMessage(Player player, String message) {
		if (!verbose)
			return;
		if (player == null) {
			Bending.log.info(message);
		} else {
			player.sendMessage(message);
		}
	}

	private void printImportUsage(Player player) {
		if (!hasHelpPermission(player, "bending.admin.import")) {
			sendNoCommandPermissionMessage(player, "import");
			return;
		}
		printUsageMessage(player, "/bending import",
				"Imports data from your bendingPlayer.yml file to your MySQL database.");

	}

	private void importBending(Player player, String[] args) {
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

	private void printNoPermissions(Player player) {
		sendMessage(player, ChatColor.RED
				+ "You do not have permission to execute that command.");

	}

	private void help(Player player, String[] args) {
		// int pages = 0;
		// int page = 1;
		List<String> command = new ArrayList<String>();
		for (String s : Bending.commands.keySet()) {
			if (hasHelpPermission(player, "bending." + s)) {
				command.add(Bending.commands.get(s));
			}
		}
		if (args.length > 1) {
			helpCommand(player, args);
			Abilities ability = Abilities.getAbility(args[1]);
			if (Abilities.getAbility(args[1]) != null) {
				ChatColor cc = ChatColor.GOLD;
				if (Abilities.isAirbending(Abilities.getAbility(args[1])))
					cc = Tools.getColor(ConfigManager.color.get("Air"));
				if (Abilities.isFirebending(Abilities.getAbility(args[1])))
					cc = Tools.getColor(ConfigManager.color.get("Fire"));
				if (Abilities.isEarthbending(Abilities.getAbility(args[1])))
					cc = Tools.getColor(ConfigManager.color.get("Earth"));
				if (Abilities.isWaterbending(Abilities.getAbility(args[1])))
					cc = Tools.getColor(ConfigManager.color.get("Water"));
				if (Tools.hasPermission(player, Abilities.getAbility(args[1]))) {

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
					case EarthBlast:
						sendMessage(player, cc + EarthBlast.getDescription());
						break;
					case Collapse:
						sendMessage(player, cc + Collapse.getDescription());
						break;
					case Tremorsense:
						sendMessage(player, cc + Tremorsense.getDescription());
						break;
					case Blaze:
						sendMessage(player, cc + ArcOfFire.getDescription());
						break;
					case HeatControl:
						sendMessage(player, cc + Extinguish.getDescription());
						break;
					case Fireball:
						sendMessage(player, cc + Fireball.getDescription());
						break;
					case FireBlast:
						sendMessage(player, cc + FireBlast.getDescription());
						break;
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
					case Paralyze:
						sendMessage(player, cc + Paralyze.getDescription());
						break;
					}
					return;
				} else {
					sendMessage(player, "You do not have permissions to bind "
							+ cc + ability + ChatColor.WHITE + ".");
				}
			}
			// for (String s : command) {
			//
			// if (args[1].equalsIgnoreCase(s.split(" ")[0])) {
			// sendMessage(
			// player,
			// ChatColor.AQUA
			// + "                                        /bending "
			// + (s.split(" ")[0]));
			// String msg = "";
			// if ((s.split(" ")[0]).equalsIgnoreCase("choose")
			// && hasHelpPermission(player,
			// "bending.command.choose")) {
			// msg =
			// "The command /bending choose <element> let you choose one of the four elements (air, fire, water, air) each having different abilities. ";
			// if (hasHelpPermission(player, "bending.admin.choose"))
			// msg = msg
			// +
			// "The command can also be used to set other people bending element like so /bending choose <player> <element>";
			// sendMessage(player, msg);
			// } else if ((s.split(" ")[0]).equalsIgnoreCase("bind")
			// && hasHelpPermission(player, "bending.command.bind")) {
			// String append = "a slot";
			// if (ConfigManager.bendToItem) {
			// append = "an item";
			// }
			// msg =
			// "The command /bending <bind> <ability> is used to bind an ability to "
			// + append + ".";
			// sendMessage(player, msg);
			// } else if ((s.split(" ")[0].toLowerCase())
			// .equalsIgnoreCase("help")) {
			//
			// } else if ((s.split(" ")[0].toLowerCase())
			// .equalsIgnoreCase("remove")
			// && hasHelpPermission(player, "bending.admin.remove")) {
			// sendMessage(
			// player,
			// "The command /bending remove <player> will remove the player bending. It can also be used to lift the permaremove of a player.");
			// } else if ((s.split(" ")[0]).equalsIgnoreCase("reload")
			// && hasHelpPermission(player, "bending.admin.reload")) {
			// player.sendMessage("The command /bending reload will allow you to reload the configuration, so you can make change while the server is running and don't have to restart/reload it.");
			// } else if ((s.split(" ")[0])
			// .equalsIgnoreCase("permaremove")
			// && hasHelpPermission(player,
			// "bending.admin.permaremove")) {
			// sendMessage(
			// player,
			// "The command /bending permaremove <player> will permanantly remove someone's bending and won't allow him to choose once again until you do /bending remove <player> or manually set his bending");
			// } else if ((s.split(" ")[0]).equalsIgnoreCase("add")
			// && hasHelpPermission(player, "bending.admin.add")) {
			// sendMessage(
			// player,
			// "The command /bending add <element> allow you to add elements to the one you aleredy have. It can be used to stack all for eleents at once.");
			// } else if ((s.split(" ")[0]).equalsIgnoreCase("display")
			// && hasHelpPermission(player,
			// "bending.command.display")) {
			// msg =
			// ("The command /bending display allows you to see a list of your binding so you can remember where you binded what.");
			// if (hasHelpPermission(player,
			// "bending.command.displayelement")) {
			// msg = msg
			// +
			// " The command /bending display <element> can allow you to see a list of abilities an bending element has.";
			// }
			// sendMessage(player, msg);
			// }
			//
			// return;
			// }
			// }

			// try {
			// page = Integer.parseInt(args[1]);
			// } catch (NumberFormatException e) {
			// page = -1;
			// }
		} else {
			printCommands(player);
		}
		// if (page != -1) {
		// pages = command.size() / 8;
		// if (command.size() % 8 != 0)
		// pages++;
		//
		// if (page > pages) {
		// if (pages > 1) {
		// sendMessage(player, ChatColor.RED + "There's only " + pages
		// + " pages of help");
		// } else {
		// sendMessage(player, ChatColor.RED + "There's only " + pages
		// + " page of help");
		// }
		// return;
		// }
		// sendMessage(player, ChatColor.AQUA
		// + "=======================Help===========================");
		// for (int i = 1; i <= 8; i++) {
		// if (command.size() > (i + (8 * page) - 9)) {
		// String comname = "/bending "
		// + command.get((i + (8 * page) - 9));
		// sendMessage(player, comname);
		// }
		// }
		// sendMessage(player, ChatColor.AQUA + "=====================Page "
		// + page + "/" + pages + "========================");
		// return;
		// }

	}

	private void helpCommand(Player player, String[] args) {
		ChatColor color = ChatColor.AQUA;

		String command = args[1];
		if (Arrays.asList(bindAliases).contains(command)) {
			if (!hasHelpPermission(player, "bending.command.bind")) {
				sendMessage(player, color
						+ "You don't have permission to use bind.");
				return;
			}
			sendMessage(player, color + "Command: /bending bind");
			String aliases = "";
			for (String alias : bindAliases)
				aliases = aliases + alias + " ";
			sendMessage(player, color + "Aliases: " + aliases);
			printBindUsage(player);
		} else if (Arrays.asList(clearAliases).contains(command)) {
			if (!hasHelpPermission(player, "bending.command.clear")) {
				sendMessage(player, color
						+ "You don't have permission to use clear.");
				return;
			}
			sendMessage(player, color + "Command: /bending clear");
			String aliases = "";
			for (String alias : clearAliases)
				aliases = aliases + alias + " ";
			sendMessage(player, color + "Aliases: " + aliases);
			printClearUsage(player);
		} else if (Arrays.asList(chooseAliases).contains(command)) {
			if (!hasHelpPermission(player, "bending.command.choose")
					&& !hasHelpPermission(player, "bending.admin.choose")
					&& !hasHelpPermission(player, "bending.admin.rechoose")) {
				sendMessage(player, color
						+ "You don't have permission to use choose.");
				return;
			}
			sendMessage(player, color + "Command: /bending choose");
			String aliases = "";
			for (String alias : chooseAliases)
				aliases = aliases + alias + " ";
			sendMessage(player, color + "Aliases: " + aliases);
			printChooseUsage(player);
		} else if (Arrays.asList(addAliases).contains(command)) {
			if (!hasHelpPermission(player, "bending.command.add")) {
				sendMessage(player, color
						+ "You don't have permission to use add.");
				return;
			}
			sendMessage(player, color + "Command: /bending add");
			String aliases = "";
			for (String alias : addAliases)
				aliases = aliases + alias + " ";
			sendMessage(player, color + "Aliases: " + aliases);
			printAddUsage(player);
		} else if (Arrays.asList(removeAliases).contains(command)) {
			if (!hasHelpPermission(player, "bending.command.remove")) {
				sendMessage(player, color
						+ "You don't have permission to use remove.");
				return;
			}
			sendMessage(player, color + "Command: /bending remove");
			String aliases = "";
			for (String alias : removeAliases)
				aliases = aliases + alias + " ";
			sendMessage(player, color + "Aliases: " + aliases);
			printRemoveUsage(player);
		} else if (Arrays.asList(permaremoveAliases).contains(command)) {
			if (!hasHelpPermission(player, "bending.command.permaremove")) {
				sendMessage(player, color
						+ "You don't have permission to use permaremove.");
				return;
			}
			sendMessage(player, color + "Command: /bending permaremove");
			String aliases = "";
			for (String alias : permaremoveAliases)
				aliases = aliases + alias + " ";
			sendMessage(player, color + "Aliases: " + aliases);
			printPermaremoveUsage(player);
		} else if (Arrays.asList(toggleAliases).contains(command)) {
			if (!hasHelpPermission(player, "bending.command.toggle")) {
				sendMessage(player, color
						+ "You don't have permission to use toggle.");
				return;
			}
			sendMessage(player, color + "Command: /bending toggle");
			String aliases = "";
			for (String alias : toggleAliases)
				aliases = aliases + alias + " ";
			sendMessage(player, color + "Aliases: " + aliases);
			printToggleUsage(player);
		} else if (Arrays.asList(displayAliases).contains(command)) {
			if (!hasHelpPermission(player, "bending.command.display")) {
				sendMessage(player, color
						+ "You don't have permission to use display.");
				return;
			}
			sendMessage(player, color + "Command: /bending display");
			String aliases = "";
			for (String alias : displayAliases)
				aliases = aliases + alias + " ";
			sendMessage(player, color + "Aliases: " + aliases);
			printDisplayUsage(player);
		} else if (Arrays.asList(reloadAliases).contains(command)) {
			if (!hasHelpPermission(player, "bending.command.reload")) {
				sendMessage(player, color
						+ "You don't have permission to use reload.");
				return;
			}
			sendMessage(player, color + "Command: /bending reload");
			String aliases = "";
			for (String alias : reloadAliases)
				aliases = aliases + alias + " ";
			sendMessage(player, color + "Aliases: " + aliases);
			printReloadUsage(player);
		} else if (Arrays.asList(importAliases).contains(command)) {
			if (!hasHelpPermission(player, "bending.command.import")) {
				sendMessage(player, color
						+ "You don't have permission to use import.");
				return;
			}
			sendMessage(player, color + "Command: /bending import");
			String aliases = "";
			for (String alias : importAliases)
				aliases = aliases + alias + " ";
			sendMessage(player, color + "Aliases: " + aliases);
			printImportUsage(player);
		} else if (Arrays.asList(whoAliases).contains(command)) {
			if (!hasHelpPermission(player, "bending.command.who")) {
				sendMessage(player, color
						+ "You don't have permission to use who.");
				return;
			}
			sendMessage(player, color + "Command: /bending who");
			String aliases = "";
			for (String alias : whoAliases)
				aliases = aliases + alias + " ";
			sendMessage(player, color + "Aliases: " + aliases);
			printWhoUsage(player);
		}
	}

	private void printReloadUsage(Player player) {
		if (!hasHelpPermission(player, "bending.admin.reload")) {
			sendNoCommandPermissionMessage(player, "reload");
			return;
		} else {
			printUsageMessage(player, "/bending reload",
					"Reloads the bendingPlayers.yml file.");
		}
	}

	private void reload(Player player, String[] args) {
		if (!hasPermission(player, "bending.admin.reload"))
			return;
		Bending.configManager.load(new File(dataFolder, "config.yml"));
		config.initialize(dataFolder);
		String append = StorageManager.useMySQL ? " Database" : "Players file";
		sendMessage(player, ChatColor.AQUA + "Config and Bending" + append
				+ " was reloaded");

	}

	private void printDisplayUsage(Player player) {
		if (!hasHelpPermission(player, "bending.command.display")) {
			sendNoCommandPermissionMessage(player, "display");
			return;
		}
		if (player != null)
			printUsageMessage(player, "/bending display",
					"Displays all the abilities you have bound.");
		printUsageMessage(player, "/bending display <element>",
				"Displays all available abilites for <element>");
	}

	private void display(Player player, String[] args) {
		if (!hasPermission(player, "bending.command.display"))
			return;

		if (args.length > 2) {
			printDisplayUsage(player);
		}

		if (args.length == 1) {
			if (player == null) {
				printNotFromConsole();
				return;
			}

			boolean none = true;

			if (!ConfigManager.bendToItem) {
				for (int i = 0; i <= 8; i++) {
					Abilities a = config.getAbility(player, i);
					if (a != null) {
						none = false;
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
						String ability = config.getAbility(player, i).name();
						sendMessage(player, "Slot " + (i + 1) + ": " + color
								+ ability);
					}
				}
			} else {

				for (Material mat : Material.values()) {
					Abilities a = config.getAbility(player, mat);
					if (a != null) {
						none = false;
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
			if (none)
				sendMessage(player, "You have no abilities bound.");
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
				printDisplayUsage(player);
			}

		}

	}

	private void printToggleUsage(Player player) {
		if (!hasHelpPermission(player, "bending.command.toggle")) {
			sendNoCommandPermissionMessage(player, "toggle");
			return;
		}
		printUsageMessage(player, "/bending toggle",
				"Toggles your bending on or off. Passives will still work.");
	}

	private void toggle(Player player, String[] args) {
		if (args.length == 1) {
			if (!hasHelpPermission(player, "bending.command.toggle")
					&& !hasHelpPermission(player, "bending.admin.toggle")) {
				printNoPermissions(player);
				return;
			}
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

	private void printNotFromConsole() {
		Bending.log.info("This command cannot be used from the console.");

	}

	private void printPermaremoveUsage(Player player) {
		if (!hasHelpPermission(player, "bending.admin.permaremove")) {
			sendMessage(player,
					"You do not have the permission to use /bending permaremove");
		}
		printUsageMessage(
				player,
				"/bending permaremove <player1> [player2] [player3] ...",
				"Permanently removes the bending of <player1> (optionally accepts a list of players) until someone with permissions chooses their bending.");
	}

	private void permaremove(Player player, String[] args) {
		if (!hasPermission(player, "bending.admin.permaremove"))
			return;
		String playerlist = "";
		for (int i = 1; i < args.length; i++) {
			String playername = args[i];
			String senderName = "The server";
			if (player != null)
				senderName = player.getName();
			Player targetplayer = server.getPlayer(playername);
			if (targetplayer != null) {
				config.permaRemoveBending(targetplayer);
				targetplayer.sendMessage(senderName
						+ " has removed your bending permanently.");
				playerlist = playerlist + targetplayer.getName() + " ";
			}
		}
		sendMessage(player, "You have permanently removed the bending of: "
				+ playerlist);
	}

	private void printRemoveUsage(Player player) {
		if (!hasHelpPermission(player, "bending.admin.remove")) {
			sendMessage(player,
					"You do not have the permission to use /bending remove");
			return;
		}
		if (player != null) {
			printUsageMessage(player, "/bending remove",
					"Removes all your bending.");
		}
		printUsageMessage(player, "/bending remove <player>",
				"Removes all of <player>'s bending.");
	}

	private void remove(Player player, String[] args) {
		if (!hasPermission(player, "bending.admin.remove"))
			return;
		String playerlist = "";
		String senderName = "The server";
		if (player != null)
			senderName = player.getName();
		for (int i = 1; i < args.length; i++) {
			String playername = args[i];
			Player targetplayer = server.getPlayer(playername);
			if (targetplayer != null) {
				config.removeBending(targetplayer);
				targetplayer.sendMessage(senderName
						+ " has removed your bending.");
				playerlist = playerlist + targetplayer.getName() + " ";
			}
		}
		sendMessage(player, "You have removed the bending of: " + playerlist);

	}

	private void printAddUsage(Player player) {
		if (player != null)
			printUsageMessage(player, "/bending add <element>",
					"Add <element>, allowing you to use it along with what you already can do.");
		printUsageMessage(player, "/bending add <player> <element>",
				"Adds <element> to <player>.");
	}

	private void add(Player player, String[] args) {
		if (!hasPermission(player, "bending.admin.add"))
			return;
		if (args.length != 2 && args.length != 3) {
			printAddUsage(player);
			return;
		}
		if (args.length == 2) {
			printAddUsage(player);
			String choice = args[1].toLowerCase();
			if (Arrays.asList(airbendingAliases).contains(choice)) {
				if (Tools.isBender(player.getName(), BendingType.Air)) {
					sendMessage(player, "You are already an airbender.");
					return;
				}
				if (!hasHelpPermission(player, "bending.air")) {
					sendMessage(player,
							"You do not have permission to be an airbender.");
					return;
				}
				sendMessage(player, "You are now also an airbender!");
				config.addBending(player, "air");
				return;
			}
			if (Arrays.asList(firebendingAliases).contains(choice)) {
				if (Tools.isBender(player.getName(), BendingType.Fire)) {
					sendMessage(player, "You are already a firebender.");
					return;
				}
				if (!hasHelpPermission(player, "bending.fire")) {
					sendMessage(player,
							"You do not have permission to be a firebender.");
					return;
				}
				sendMessage(player, "You are now also a firebender!");
				config.addBending(player, "fire");
				return;
			}
			if (Arrays.asList(earthbendingAliases).contains(choice)) {
				if (Tools.isBender(player.getName(), BendingType.Earth)) {
					sendMessage(player, "You are already an earthbender.");
					return;
				}
				if (!hasHelpPermission(player, "bending.earth")) {
					sendMessage(player,
							"You do not have permission to be an earthbender.");
					return;
				}
				sendMessage(player, "You are now also an earthbender!");
				config.addBending(player, "earth");
				return;
			}
			if (Arrays.asList(waterbendingAliases).contains(choice)) {
				if (Tools.isBender(player.getName(), BendingType.Water)) {
					sendMessage(player, "You are already a waterbender.");
					return;
				}
				if (!hasHelpPermission(player, "bending.water")) {
					sendMessage(player,
							"You do not have permission to be a waterbender.");
					return;
				}
				sendMessage(player, "You are now also a waterbender!");
				config.addBending(player, "water");
				return;
			}
			if (Arrays.asList(chiblockingAliases).contains(choice)) {
				if (Tools.isBender(player.getName(), BendingType.ChiBlocker)) {
					sendMessage(player, "You are already a chiblocker.");
					return;
				}
				if (!hasHelpPermission(player, "bending.chiblocking")) {
					sendMessage(player,
							"You do not have permission to be a chiblocker.");
					return;
				}
				sendMessage(player, "You are now also a chiblocker!");
				config.addBending(player, "chiblocker");
				return;
			}
			printAddUsage(player);
		} else if (args.length == 3) {
			String playername = args[1];
			Player targetplayer = server.getPlayer(playername);
			if (targetplayer == null) {
				printAddUsage(player);
				return;
			}

			String senderName = "The server";
			if (player != null)
				senderName = player.getName();

			String choice = args[2].toLowerCase();
			if (Arrays.asList(airbendingAliases).contains(choice)) {
				if (Tools.isBender(targetplayer.getName(), BendingType.Air)) {
					sendMessage(player, targetplayer.getName()
							+ " is already an airbender.");
					return;
				}
				if (!hasHelpPermission(targetplayer, "bending.air")) {
					sendMessage(player,
							"They do not have permission to be an airbender.");
					return;
				}
				sendMessage(player, targetplayer.getName()
						+ " is now also an airbender!");
				sendMessage(targetplayer, senderName
						+ " has now made you also an airbender!");
				config.addBending(targetplayer, "air");
				return;
			}
			if (Arrays.asList(firebendingAliases).contains(choice)) {
				if (Tools.isBender(targetplayer.getName(), BendingType.Fire)) {
					sendMessage(player, targetplayer.getName()
							+ " is already a firebender.");
					return;
				}
				if (!hasHelpPermission(targetplayer, "bending.fire")) {
					sendMessage(player,
							"They do not have permission to be a firebender.");
					return;
				}
				sendMessage(player, targetplayer.getName()
						+ " is now also a firebender!");
				sendMessage(targetplayer, senderName
						+ " has now made you also a firebender!");
				config.addBending(targetplayer, "fire");
				return;
			}
			if (Arrays.asList(earthbendingAliases).contains(choice)) {
				if (Tools.isBender(targetplayer.getName(), BendingType.Earth)) {
					sendMessage(player, targetplayer.getName()
							+ " is already an earthbender.");
					return;
				}
				if (!hasHelpPermission(targetplayer, "bending.earth")) {
					sendMessage(player,
							"They do not have permission to be an earthbender.");
					return;
				}
				sendMessage(player, targetplayer.getName()
						+ " is now also an earthbender!");
				sendMessage(targetplayer, senderName
						+ " has now made you also an earthbender!");
				config.addBending(targetplayer, "earth");
				return;
			}
			if (Arrays.asList(waterbendingAliases).contains(choice)) {
				if (Tools.isBender(targetplayer.getName(), BendingType.Water)) {
					sendMessage(player, targetplayer.getName()
							+ " is already a waterbender.");
					return;
				}
				if (!hasHelpPermission(targetplayer, "bending.water")) {
					sendMessage(player,
							"They do not have permission to be a waterbender.");
					return;
				}
				sendMessage(player, targetplayer.getName()
						+ " is now also a waterbender!");
				sendMessage(targetplayer, senderName
						+ " has now made you also a waterbender!");
				config.addBending(targetplayer, "water");
				return;
			}
			if (Arrays.asList(chiblockingAliases).contains(choice)) {
				if (Tools.isBender(targetplayer.getName(),
						BendingType.ChiBlocker)) {
					sendMessage(player, targetplayer.getName()
							+ " is already a chiblocker.");
					return;
				}
				if (!hasHelpPermission(targetplayer, "bending.chiblocking")) {
					sendMessage(player,
							"They do not have permission to be a chiblocker.");
					return;
				}
				sendMessage(player, targetplayer.getName()
						+ " is now also a chiblocker!");
				sendMessage(targetplayer, senderName
						+ " has now made you also a chiblocker!");
				config.addBending(targetplayer, "chiblocker");
				return;
			}
			printAddUsage(player);
		}

	}

	private void printClearUsage(Player player) {
		printUsageMessage(player, "/bending clear",
				"Clears all abilities you have bound.");
		if (!ConfigManager.bendToItem) {
			printUsageMessage(player, "/bending clear <slot#>",
					"Clears the ability bound to <slot#>");
		} else {
			printUsageMessage(player, "/bending clear <item>",
					"Clears the ability bound to <item>");
		}
	}

	private void clear(Player player, String[] args) {
		if (!hasPermission(player, "bending.command.clear"))
			return;
		if (player == null) {
			printNotFromConsole();
			return;
		}

		if (args.length != 1 && args.length != 2) {
			printClearUsage(player);
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
				printClearUsage(player);
			} else {
				if (Material.matchMaterial(args[1]) != null) {
					config.removeAbility(player,
							Material.matchMaterial(args[1]));
					sendMessage(player, "Item " + args[1] + "has been cleared.");
					return;
				}
				printClearUsage(player);
			}

		}

	}

	private void printBindUsage(Player player) {
		if (!ConfigManager.bendToItem) {
			printUsageMessage(player, "/bending bind <ability>",
					"This binds <ability> to the item you are holding.");
			printUsageMessage(player, "/bending bind <ability> <slot#>",
					"This binds <ability> to <slot#>.");
		} else {
			printUsageMessage(player, "/bending bind <ability>",
					"This binds <ability> to your current slot.");
			printUsageMessage(player, "/bending bind <ability> <item>",
					"This binds <ability> to <item>.");
		}
	}

	private void bind(Player player, String[] args) {
		if (!hasPermission(player, "bending.command.bind"))
			return;
		if (player == null) {
			printNotFromConsole();
			return;
		}

		if (args.length != 2 && args.length != 3) {
			printBindUsage(player);
			return;
		}

		String a = args[1];
		Abilities ability = Abilities.getAbility(a);

		if (ability == null) {
			printBindUsage(player);
			return;
		}

		if (!Tools.hasPermission(player, ability)) {
			printNoPermissions(player);
			return;
		}

		int slot = player.getInventory().getHeldItemSlot();
		Material mat = player.getInventory().getItemInHand().getType();

		if (args.length == 3 && ConfigManager.bendToItem) {
			mat = Material.matchMaterial(args[2]);
			if (mat == null) {
				printNoPermissions(player);
				return;
			}
		} else if (args.length == 3) {
			slot = Integer.parseInt(args[2]);
			if (slot <= 0 || slot >= 10) {
				printNoPermissions(player);
				return;
			}
			slot--;
		}

		ChatColor color = ChatColor.WHITE;
		ChatColor white = ChatColor.WHITE;

		if (Abilities.isWaterbending(ability)) {
			if (!Tools.isBender(player.getName(), BendingType.Water)) {
				ChatColor color2 = ChatColor.WHITE;
				color2 = Tools.getColor(ConfigManager.getColor("Water"));
				sendMessage(player, "You are not a " + color2 + "waterbender.");
				return;
			}
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
		if (Abilities.isAirbending(ability)) {
			if (!Tools.isBender(player.getName(), BendingType.Air)) {
				ChatColor color2 = ChatColor.WHITE;
				color2 = Tools.getColor(ConfigManager.getColor("Air"));
				sendMessage(player, "You are not an " + color2 + "airbender.");
				return;
			}
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
		if (Abilities.isEarthbending(ability)) {
			if (!Tools.isBender(player.getName(), BendingType.Earth)) {
				ChatColor color2 = ChatColor.WHITE;
				color2 = Tools.getColor(ConfigManager.getColor("Earth"));
				sendMessage(player, "You are not an " + color2 + "earthbender.");
				return;
			}
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
		if (Abilities.isChiBlocking(ability)) {
			if (!Tools.isBender(player.getName(), BendingType.ChiBlocker)) {
				ChatColor color2 = ChatColor.WHITE;
				color2 = Tools.getColor(ConfigManager.getColor("ChiBlocker"));
				sendMessage(player, "You are not a " + color2 + "chiblocker.");
				return;
			}
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
		if (Abilities.isFirebending(ability)) {
			if (!Tools.isBender(player.getName(), BendingType.Fire)) {
				ChatColor color2 = ChatColor.WHITE;
				color2 = Tools.getColor(ConfigManager.getColor("Fire"));
				sendMessage(player, "You are not a " + color2 + "firebender.");
				return;
			}
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
		if (ability == Abilities.AvatarState) {
			if (!hasPermission(player, "bending.admin.avatarstate"))
				return;

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

	private boolean hasPermission(Player player, String permission) {
		if (player == null)
			return true;
		if (player.hasPermission(permission)) {
			return true;
		}
		printNoPermissions(player);
		return false;
	}

	private boolean hasHelpPermission(Player player, String permission) {
		if (player == null)
			return true;
		if (player.hasPermission(permission))
			return true;
		return false;
	}

	private void printHelpDialogue(Player player) {
		if (player == null) {
			Bending.log.info("Use /bending help to see a list of commands.");
			Bending.log
					.info("Use /bending help <command> to see how to use that command.");
			Bending.log
					.info("Use /bending help <ability> to get help with that ability.");
		} else {
			player.sendMessage(ChatColor.RED
					+ "Use /bending help to see a list of commands.");
			player.sendMessage(ChatColor.RED
					+ "Use /bending help <command> to see how to use that command.");
			player.sendMessage(ChatColor.RED
					+ "Use /bending help <ability> to get help with that ability.");
		}
	}

	private void sendNoCommandPermissionMessage(Player player, String command) {
		sendMessage(player, "You do not have permission to use /bending "
				+ command + ".");
	}

	private void printCommands(Player player) {
		sendMessage(player, "Bending aliases: bending bend b mtla tla");
		String slot = "slot#";
		if (ConfigManager.bendToItem)
			slot = "item";
		if (hasHelpPermission(player, "bending.command.bind"))
			sendMessage(player, "/bending bind <ability> [" + slot + "]");
		if (hasHelpPermission(player, "bending.command.clear"))
			sendMessage(player, "/bending clear [" + slot + "]");

		if (hasHelpPermission(player, "bending.admin.choose")) {
			sendMessage(player, "/bending choose [player] <element>");
		} else if (hasHelpPermission(player, "bending.command.choose")
				|| hasHelpPermission(player, "bending.admin.rechoose")) {
			sendMessage(player, "/bending choose <element>");
		}

		if (hasHelpPermission(player, "bending.admin.add"))
			sendMessage(player, "/bending add [player] <element>");
		if (hasHelpPermission(player, "bending.admin.remove"))
			sendMessage(player,
					"/bending remove <player1> [player2] [player3] ...");
		if (hasHelpPermission(player, "bending.admin.permaremove"))
			sendMessage(player,
					"/bending permaremove <player1> [player2] [player3] ...");

		if (hasHelpPermission(player, "bending.admin.toggle")) {
			sendMessage(player, "/bending toggle [player]");
		} else if (hasHelpPermission(player, "bending.command.toggle")) {
			sendMessage(player, "/bending toggle");
		}

		if (hasHelpPermission(player, "bending.command.display"))
			sendMessage(player, "/bending display [element]");
		if (hasHelpPermission(player, "bending.admin.reload"))
			sendMessage(player, "/bending reload");
		if (hasHelpPermission(player, "bending.admin.import"))
			sendMessage(player, "/bending import");
		if (hasHelpPermission(player, "bending.command.who"))
			sendMessage(player, "/bending who [player]");

	}

}
