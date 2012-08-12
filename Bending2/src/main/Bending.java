package main;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Logger;

import main.BendingListener;
import main.BendingManager;
import main.BendingPlayers;
import main.Metrics;
import main.Metrics.Graph;
import main.StorageManager;
import net.minecraft.server.EntityFireball;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import tools.Abilities;
import tools.AvatarState;
import tools.BendingType;
import tools.ConfigManager;
import tools.Tools;
import waterbending.Bloodbending;
import waterbending.FreezeMelt;
import waterbending.HealingWaters;
import waterbending.Plantbending;
import waterbending.WaterManipulation;
import waterbending.WaterSpout;
import waterbending.WaterWall;
import waterbending.Wave;
import airbending.AirBlast;
import airbending.AirBubble;
import airbending.AirScooter;
import airbending.AirShield;
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
import firebending.HeatMelt;
import firebending.Illumination;
import firebending.Lightning;
import firebending.WallOfFire;

public class Bending extends JavaPlugin {

	public static long time_step = 1; // in ms
	public static Logger log = Logger.getLogger("Minecraft");

	public final BendingManager manager = new BendingManager(this);
	public final BendingListener listener = new BendingListener(this);
	public final TagAPIListener Taglistener = new TagAPIListener();
	
	private static Map<String, String> commands = new HashMap<String, String>();
	public static ConcurrentHashMap<String, List<BendingType>> benders = new ConcurrentHashMap<String, List<BendingType>>();

	// public BendingPlayers config = new BendingPlayers(getDataFolder(),
	// getResource("bendingPlayers.yml"));
	public static ConfigManager configManager = new ConfigManager();
	public StorageManager config;
	public Tools tools;

	public String[] waterbendingabilities;
	public String[] airbendingabilities;
	public String[] earthbendingabilities;
	public String[] firebendingabilities;
	public String[] chiblockingabilities;

	public void onDisable() {

		Fireball.removeAllFireballs();
		Tools.stopAllBending();
	}

	public void onEnable() {

		configManager.load(new File(getDataFolder(), "config.yml"));

		config = new StorageManager(getDataFolder());

		tools = new Tools(config);

		tools = new Tools(config);
		
		for (OfflinePlayer player: Bukkit.getOfflinePlayers()){
			benders.put(player.getName(), config.getBendingTypes(player.getName()));
		}
		
		waterbendingabilities = Abilities.getWaterbendingAbilities();
		airbendingabilities = Abilities.getAirbendingAbilities();
		earthbendingabilities = Abilities.getEarthbendingAbilities();
		firebendingabilities = Abilities.getFirebendingAbilities();
		chiblockingabilities = Abilities.getChiBlockingAbilities();

		getServer().getPluginManager().registerEvents(listener, this);
		
		if (Bukkit.getPluginManager().getPlugin("TagAPI") != null
				&& ConfigManager.useTagAPI){
			getServer().getPluginManager().registerEvents(Taglistener, this);
		}

		getServer().getScheduler().scheduleSyncRepeatingTask(this, manager, 0,
				1);

		removeFireballs();

		log.info("Bending v" + this.getDescription().getVersion()
				+ " has been loaded.");

		try {
			Metrics metrics = new Metrics(this);

			Graph bending = metrics.createGraph("Bending");

			bending.addPlotter(new Metrics.Plotter("Air") {

				@Override
				public int getValue() {
					int i = 0;
					for (OfflinePlayer p : Bukkit.getServer()
							.getOfflinePlayers()) {
						if (Tools.isBender(p.getName(), BendingType.Air))
							i++;
					}
					return i;
				}

			});

			bending.addPlotter(new Metrics.Plotter("Fire") {

				@Override
				public int getValue() {
					int i = 0;
					for (OfflinePlayer p : Bukkit.getServer()
							.getOfflinePlayers()) {
						if (Tools.isBender(p.getName(), BendingType.Fire))
							i++;
					}
					return i;
				}

			});

			bending.addPlotter(new Metrics.Plotter("Water") {

				@Override
				public int getValue() {
					int i = 0;
					for (OfflinePlayer p : Bukkit.getServer()
							.getOfflinePlayers()) {
						if (Tools.isBender(p.getName(), BendingType.Water))
							i++;
					}
					return i;
				}

			});

			bending.addPlotter(new Metrics.Plotter("Earth") {

				@Override
				public int getValue() {
					int i = 0;
					for (OfflinePlayer p : Bukkit.getServer()
							.getOfflinePlayers()) {
						if (Tools.isBender(p.getName(), BendingType.Earth))
							i++;
					}
					return i;
				}

			});

			bending.addPlotter(new Metrics.Plotter("Chi Blocker") {

				@Override
				public int getValue() {
					int i = 0;
					for (OfflinePlayer p : Bukkit.getServer()
							.getOfflinePlayers()) {
						if (Tools.isBender(p.getName(), BendingType.ChiBlocker))
							i++;
					}
					return i;
				}

			});

			bending.addPlotter(new Metrics.Plotter("Non-Bender") {

				@Override
				public int getValue() {
					int i = 0;
					for (OfflinePlayer p : Bukkit.getServer()
							.getOfflinePlayers()) {

						if (!Tools.isBender(p.getName(),
								BendingType.ChiBlocker)
								&& !Tools.isBender(p.getName(),
										BendingType.Air)
								&& !Tools.isBender(p.getName(),
										BendingType.Fire)
								&& !Tools.isBender(p.getName(),
										BendingType.Water)
								&& !Tools.isBender(p.getName(),
										BendingType.Earth))
							i++;
					}
					return i;
				}

			});

			metrics.start();
			log.info("Bending is sending data for Plugin Metrics.");
		} catch (IOException e) {
			// Failed to submit the stats :-(
		}

		registerCommands();

	}

	public void reloadConfiguration() {
		getConfig().options().copyDefaults(true);
		saveConfig();

	}

	private void registerCommands() {
		commands.put("command.admin", "remove <player>");
		commands.put("admin.reload", "reload");
		commands.put("admin.permaremove", "permaremove <player>");
		commands.put("command.choose", "choose <element>");
		commands.put("admin.choose", "choose <player> <element>");
		commands.put("admin.add", "add <element>");
		commands.put("command.displayelement", "display <element>");
		commands.put("command.clear", "clear");
		commands.put("command.display", "display");
		commands.put("command.bind", "bind <ability>");
	}

	private void removeFireballs() {
		for (World world : getServer().getWorlds()) {
			for (Entity entity : world.getEntities()) {
				if (entity instanceof EntityFireball) {
					entity.remove();
				}
			}
		}

	}

	public boolean onCommand(CommandSender sender, Command cmd, String label,
			String[] args) {
		Player player = null;
		if (sender instanceof Player) {
			player = (Player) sender;
		}
		if (cmd.getName().equalsIgnoreCase("bending")) {
			if (Arrays.asList(args).isEmpty()) {
				sender.sendMessage(ChatColor.RED
						+ "Use /bending help <page> if you want to see a list of commands.");
				sender.sendMessage(ChatColor.RED
						+ "Use /bending help <ability> if you want to see how to use it.");
				sender.sendMessage(ChatColor.RED
						+ "Use /bending help <command> if you need help with a command.");
				return true;
			}

			if (args[0].equalsIgnoreCase("remove")
					&& args.length >= 2
					&& (sender.hasPermission("bending.admin.remove") || player == null)) {
				String playerlist = "";
				for (String playername : Arrays.asList(args)) {
					Player targetplayer = this.getServer()
							.getPlayer(playername);
					if (targetplayer != null) {
						config.removeBending(targetplayer);
						targetplayer.sendMessage(player.getName()
								+ " has removed your bending.");
						playerlist = playerlist + targetplayer.getName() + " ";
					}
				}
				sender.sendMessage("You have removed the bending of: "
						+ playerlist);
				return true;
			}

			if (args[0].equalsIgnoreCase("reload")
					&& args.length == 1
					&& (sender.hasPermission("bending.admin.reload") || (player == null))) {
				configManager
						.load(new File(this.getDataFolder(), "config.yml"));
				config.initialize(getDataFolder());
				String append = StorageManager.useMySQL ? " Database" : "Players file";
				sender.sendMessage(ChatColor.AQUA + "Config and Bending" + append + " was reloaded");
				return true;
			}

			if (args[0].equalsIgnoreCase("toggle")
					&& args.length == 1
					&& (sender.hasPermission("bending.command.toggle"))) {
				if (!Tools.toggledBending.contains(player)){
					Tools.toggledBending.add(player);
					player.sendMessage(ChatColor.AQUA + "You toggled your bending. You now can't use bending until you use that command again.");
					return true;
				} else {
					Tools.toggledBending.remove(player);
					player.sendMessage(ChatColor.AQUA + "You toggled you bending back. You can now use them freely again!");
					return true;
				}
			}

			if (args[0].equalsIgnoreCase("permaremove")
					&& args.length >= 2
					&& (sender.hasPermission("bending.admin.permaremove") || player == null)) {
				String playerlist = "";
				for (String playername : Arrays.asList(args)) {
					Player targetplayer = this.getServer()
							.getPlayer(playername);
					if (targetplayer != null) {
						config.permaRemoveBending(targetplayer);
						targetplayer.sendMessage(player.getName()
								+ " has removed your bending permanently.");
						playerlist = playerlist + targetplayer.getName() + " ";
					}
				}
				sender.sendMessage("You have permanently removed the bending of: "
						+ playerlist);
				return true;
			}

			if (args[0].equalsIgnoreCase("choose")
					&& sender.hasPermission("bending.command.choose")) {
				if (args.length == 1)
					return false;
				if (args.length == 2) {
					if (player == null) {
						sender.sendMessage("That command cannot be used from the console");
						return true;
					}
					if (Tools.isBender(player.getName())
							&& !sender.hasPermission("bending.admin.rechoose")) {
						sender.sendMessage("You've already chosen your bending abilities. Only ops can change this now.");
						return true;
					}
					if (args[1].equalsIgnoreCase("water")
							|| args[1].equalsIgnoreCase("air")
							|| args[1].equalsIgnoreCase("fire")
							|| args[1].equalsIgnoreCase("earth")
							|| args[1].equalsIgnoreCase("chiblocker")) {
						String part = " a ";
						if (args[1].toLowerCase().startsWith("a")
								|| args[1].toLowerCase().startsWith("e")) {
							part = " an ";
						}
						if (args[1].equalsIgnoreCase("chiblocker")) {
							sender.sendMessage("You are now a chiblocker!");
						} else {
							part = part + args[1].toLowerCase();
							sender.sendMessage("You are now" + part + "bender!");
						}
						config.removeBending(player);
						config.setBending(player, args[1]);
						return true;
					}
				} else if (sender.hasPermission("bending.admin.choose")
						|| player == null) {
					String playername = args[1];
					Player targetplayer = getServer().getPlayer(playername);
					if (targetplayer == null) {
						sender.sendMessage("Usage: /bending choose [player] [element]");
						return true;
					} else if (args[2].equalsIgnoreCase("water")
							|| args[2].equalsIgnoreCase("air")
							|| args[2].equalsIgnoreCase("fire")
							|| args[2].equalsIgnoreCase("earth")
							|| args[2].equalsIgnoreCase("chiblocker")) {
						String part = " a ";
						if (args[2].toLowerCase().startsWith("a")
								|| args[2].toLowerCase().startsWith("e")) {
							part = " an ";
						}
						part = part + args[2].toLowerCase();
						if (args[2].equalsIgnoreCase("chiblocker")) {
							targetplayer.sendMessage(sender.getName()
									+ " has made you a chiblocker!");
						} else {
							targetplayer.sendMessage(sender.getName()
									+ " has made you" + part + "bender!");
						}
						sender.sendMessage("You have changed "
								+ targetplayer.getName() + "'s bending.");
						config.removeBending(targetplayer);
						config.setBending(targetplayer, args[2]);
						return true;
					}
					sender.sendMessage("Usage: /bending add [player] [element]");
				} else {
					sender.sendMessage(ChatColor.RED
							+ "Use /bending help <page> if you want to see a list of commands.");
					sender.sendMessage(ChatColor.RED
							+ "Use /bending help <ability> if you want to see how to use it.");
					sender.sendMessage(ChatColor.RED
							+ "Use /bending help <command> if you need help with a command.");
					return true;
				}
			}

			if (args[0].equalsIgnoreCase("add")
					&& (sender.hasPermission("bending.admin.add") || player == null)) {
				if (args.length == 1) {
					sender.sendMessage(ChatColor.RED
							+ "Use /bending help <page> if you want to see a list of commands.");
					sender.sendMessage(ChatColor.RED
							+ "Use /bending help <ability> if you want to see how to use it.");
					sender.sendMessage(ChatColor.RED
							+ "Use /bending help <command> if you need help with a command.");
					return true;
				}
				if (args.length == 2) {
					if (player == null) {
						sender.sendMessage("That command cannot be used from the console");
						return true;
					}
					if (args[1].equalsIgnoreCase("water")
							&& Tools.isBender(player.getName(), BendingType.Water)) {
						sender.sendMessage("You are already a waterbender!");
						return true;
					} else if (args[1].equalsIgnoreCase("air")
							&& Tools.isBender(player.getName(), BendingType.Air)) {
						sender.sendMessage("You are already an airbender!");
						return true;
					} else if (args[1].equalsIgnoreCase("earth")
							&& Tools.isBender(player.getName(), BendingType.Earth)) {
						sender.sendMessage("You are already an earthbender!");
						return true;
					} else if (args[1].equalsIgnoreCase("fire")
							&& Tools.isBender(player.getName(), BendingType.Fire)) {
						sender.sendMessage("You are already a firebender!");
						return true;
					} else if (args[1].equalsIgnoreCase("chiblocker")
							&& Tools.isBender(player.getName(), BendingType.ChiBlocker)) {
						sender.sendMessage("You are already a chiblocker!");
						return true;
					}
					if (args[1].equalsIgnoreCase("water")
							|| args[1].equalsIgnoreCase("air")
							|| args[1].equalsIgnoreCase("fire")
							|| args[1].equalsIgnoreCase("earth")
							|| args[1].equalsIgnoreCase("chiblocker")) {
						String part = " a ";
						if (args[1].toLowerCase().startsWith("a")
								|| args[1].toLowerCase().startsWith("e")) {
							part = " an ";
						}
						part = part + args[1].toLowerCase();
						if (args[1].equalsIgnoreCase("chiblocker")) {
							sender.sendMessage("You are now also a chiblocker!");
						} else {
							sender.sendMessage("You are now also" + part
									+ "bender!");
						}
						config.addBending(player, args[1]);
						return true;
					}
				} else if (args.length == 3) {
					String playername = args[1];
					Player targetplayer = getServer().getPlayer(playername);
					if (targetplayer == null) {
						sender.sendMessage("Usage: /bending choose [player] [element]");
						return true;
					} else if (args[2].equalsIgnoreCase("water")
							&& Tools.isBender(targetplayer.getName(), BendingType.Water)) {
						sender.sendMessage(targetplayer.getName()
								+ " is already a waterbender!");
						return true;
					} else if (args[2].equalsIgnoreCase("air")
							&& Tools.isBender(targetplayer.getName(), BendingType.Air)) {
						sender.sendMessage(targetplayer.getName()
								+ " is already an airbender!");
						return true;
					} else if (args[2].equalsIgnoreCase("earth")
							&& Tools.isBender(targetplayer.getName(), BendingType.Earth)) {
						sender.sendMessage(targetplayer.getName()
								+ " is already an earthbender!");
						return true;
					} else if (args[2].equalsIgnoreCase("fire")
							&& Tools.isBender(targetplayer.getName(), BendingType.Fire)) {
						sender.sendMessage(targetplayer.getName()
								+ " is already a firebender!");
						return true;
					} else if (args[2].equalsIgnoreCase("chiblocker")
							&& Tools.isBender(targetplayer.getName(),
									BendingType.ChiBlocker)) {
						sender.sendMessage(targetplayer.getName()
								+ " is already a chiblocker!");
						return true;
					} else if (args[2].equalsIgnoreCase("water")
							|| args[2].equalsIgnoreCase("air")
							|| args[2].equalsIgnoreCase("fire")
							|| args[2].equalsIgnoreCase("earth")
							|| args[2].equalsIgnoreCase("chiblocker")) {
						String part = " a ";
						if (args[2].toLowerCase().startsWith("a")
								|| args[2].toLowerCase().startsWith("e")) {
							part = " an ";
						}
						part = part + args[2].toLowerCase();
						if (args[2].equalsIgnoreCase("chiblocker")) {
							targetplayer.sendMessage(sender.getName()
									+ " has made you also a chiblocker!");
						} else {
							targetplayer.sendMessage(sender.getName()
									+ " has made you also" + part + "bender!");
						}
						sender.sendMessage("You have added to "
								+ targetplayer.getName() + "'s bending.");
						config.addBending(targetplayer, args[2]);
						return true;
					}
					sender.sendMessage("Usage: /bending add [player] [element]");
				} else {
					sender.sendMessage(ChatColor.RED
							+ "Use /bending help <page> if you want to see a list of commands.");
					sender.sendMessage(ChatColor.RED
							+ "Use /bending help <ability> if you want to see how to use it.");
					sender.sendMessage(ChatColor.RED
							+ "Use /bending help <command> if you need help with a command.");
					return true;
				}
			}

			if (args.length > 1) {
				if (args[0].equalsIgnoreCase("display")
						&& player != null
						&& sender
								.hasPermission("bending.command.displayelement")) {
					String[] abilitylist = null;
					if (args[1].equalsIgnoreCase("air")) {
						abilitylist = airbendingabilities;
					} else if (args[1].equalsIgnoreCase("water")) {
						abilitylist = waterbendingabilities;
					} else if (args[1].equalsIgnoreCase("earth")) {
						abilitylist = earthbendingabilities;
					} else if (args[1].equalsIgnoreCase("fire")) {
						abilitylist = firebendingabilities;
					} else if (args[1].equalsIgnoreCase("chiblocker")) {
						abilitylist = chiblockingabilities;
					}

					if (abilitylist != null) {
						for (String ability : abilitylist) {
							if (Tools.hasPermission(player,
									Abilities.getAbility(ability))) {
								// if (Permissions != null) {
								// if (Permissions.has(player,
								// args[1].toLowerCase() + "bending."
								// + ability)) {
								// sender.sendMessage(ability);
								// }
								// } else {
								// sender.sendMessage(ability);
								// }
								sender.sendMessage(ability);
							}
						}
						return true;
					}
				} else if (args[0].equalsIgnoreCase("clear") && player != null
						&& sender.hasPermission("bending.commnd.clear")) {
					if (!ConfigManager.bendToItem) {
						if (Integer.parseInt(args[1]) > 0
								&& Integer.parseInt(args[1]) < 10) {
							config.removeAbility(player,
									Integer.parseInt(args[1]) - 1);
							return true;
						}
					} else {
						if (Material.matchMaterial(args[1]) != null) {
							config.removeAbility(player,
									Material.matchMaterial(args[1]));
							return true;
						}
					}
				}
			}

			else if (args[0].equalsIgnoreCase("display") && player != null
					&& sender.hasPermission("bending.command.display")) {
				if (!ConfigManager.bendToItem) {
					for (int i = 0; i <= 8; i++) {
						Abilities a = config.getAbility(player, i);
						if (a != null) {
							String ability = config.getAbility(player, i)
									.name();
							sender.sendMessage("Slot " + (i + 1) + ": "
									+ ability);
						}
					}
				} else {

					for (Material mat : Material.values()) {
						Abilities a = config.getAbility(player, mat);
						if (a != null) {
							String ability = config.getAbility(player, mat)
									.name();
							sender.sendMessage(mat.name().replaceAll("_", " ")
									+ ": " + ability);
						}
					}
				}
				return true;
			} else if (args[0].equalsIgnoreCase("clear") && player != null) {
				if (!ConfigManager.bendToItem) {
					for (int i = 0; i <= 8; i++) {
						config.removeAbility(player, i);

					}
				} else {
					for (Material mat : Material.values()) {
						config.removeAbility(player, mat.getId());
					}
				}
				return true;
			}

			if (args[0].equalsIgnoreCase("bind") && args.length == 2
					&& player != null
					&& sender.hasPermission("bending.command.bind")) {

				String a = args[1];
				Abilities ability = Abilities.getAbility(a);

				if (ability != null) {

					int slot = (player).getInventory().getHeldItemSlot();
					Material mat = player.getInventory().getItemInHand()
							.getType();

					if (Tools.isBender(player.getName(), BendingType.Water)
							&& Abilities.isWaterbending(ability)
							&& Tools.hasPermission(player, ability)) {
						if (!ConfigManager.bendToItem) {
							config.setAbility(player, ability, slot);
							sender.sendMessage(ability.name()
									+ " bound to slot " + (slot + 1));
						} else {
							config.setAbility(player, ability, mat);
							char[] tocap = mat.name().replaceAll("_", " ")
									.toCharArray();
							boolean cap = true;
							for (int i = 0; i < tocap.length; i++) {
								if (cap) {
									tocap[i] = Character.toUpperCase(tocap[i]);
									cap = false;
								}
								if (Character.isWhitespace(tocap[i]))
									cap = true;

							}
							sender.sendMessage(ability.name() + " bound to "
									+ tocap.toString());
						}
						return true;
					}
					if (Tools.isBender(player.getName(), BendingType.Air)
							&& Abilities.isAirbending(ability)
							&& Tools.hasPermission(player, ability)) {
						if (!ConfigManager.bendToItem) {
							config.setAbility(player, ability, slot);
							sender.sendMessage(ability.name()
									+ " bound to slot " + (slot + 1));
						} else {
							config.setAbility(player, ability, mat);
							sender.sendMessage(ability.name() + " bound to "
									+ mat.name().replaceAll("_", " "));
						}
						return true;
					}
					if (Tools.isBender(player.getName(), BendingType.Earth)
							&& Abilities.isEarthbending(ability)
							&& Tools.hasPermission(player, ability)) {
						if (!ConfigManager.bendToItem) {
							config.setAbility(player, ability, slot);
							sender.sendMessage(ability.name()
									+ " bound to slot " + (slot + 1));
						} else {
							config.setAbility(player, ability, mat);
							sender.sendMessage(ability.name() + " bound to "
									+ mat.name().replaceAll("_", " "));
						}
						return true;
					}
					if (Tools.isBender(player.getName(), BendingType.ChiBlocker)
							&& Abilities.isChiBlocking(ability)
							&& Tools.hasPermission(player, ability)) {
						if (!ConfigManager.bendToItem) {
							config.setAbility(player, ability, slot);
							sender.sendMessage(ability.name()
									+ " bound to slot " + (slot + 1));
						} else {
							config.setAbility(player, ability, mat);
							sender.sendMessage(ability.name() + " bound to "
									+ mat.name().replaceAll("_", " "));
						}
						return true;
					}
					if (Tools.isBender(player.getName(), BendingType.Fire)
							&& Abilities.isFirebending(ability)
							&& Tools.hasPermission(player, ability)) {
						if (!ConfigManager.bendToItem) {
							config.setAbility(player, ability, slot);
							sender.sendMessage(ability.name()
									+ " bound to slot " + (slot + 1));
						} else {
							config.setAbility(player, ability, mat);
							sender.sendMessage(ability.name() + " bound to "
									+ mat.name().replaceAll("_", " "));
						}
						return true;
					}
					if (sender.hasPermission("bending.admin.avatarstate")
							&& ability == Abilities.AvatarState
							&& Tools.hasPermission(player, ability)) {
						if (!ConfigManager.bendToItem) {
							config.setAbility(player, ability, slot);
							sender.sendMessage(ability.name()
									+ " bound to slot " + (slot + 1));
						} else {
							config.setAbility(player, ability, mat);
							sender.sendMessage(ability.name() + " bound to "
									+ mat.name().replaceAll("_", " "));
						}
						return true;
					}
				}
			}
			if (args[0].equalsIgnoreCase("help")
					&& (sender.hasPermission("bending.command.help") || player == null)) {
				int pages = 0;
				int page = 1;
				List<String> command = new ArrayList<String>();
				for (String s : commands.keySet()) {
					if (sender.hasPermission("bending." + s)) {
						command.add(commands.get(s));
					}
				}
				if (args.length > 1) {
					if (Abilities.getAbility(args[1]) != null) {
						if (Tools.hasPermission(player,
								Abilities.getAbility(args[1]))) {

							ChatColor cc = ChatColor.GOLD;
							if (Abilities.isAirbending(Abilities
									.getAbility(args[1])))
								cc = Tools.getColor(ConfigManager.color
										.get("Air"));
							if (Abilities.isFirebending(Abilities
									.getAbility(args[1])))
								cc = Tools.getColor(ConfigManager.color
										.get("Fire"));
							if (Abilities.isEarthbending(Abilities
									.getAbility(args[1])))
								cc = Tools.getColor(ConfigManager.color
										.get("Earth"));
							if (Abilities.isWaterbending(Abilities
									.getAbility(args[1])))
								cc = Tools.getColor(ConfigManager.color
										.get("Water"));
							sender.sendMessage(("                                                "
									+ cc + Abilities.getAbility(args[1]).name()));
							switch (Abilities.getAbility(args[1])) {
							case AirBlast:
								sender.sendMessage(cc
										+ AirBlast.getDescription());
								break;
							case AirBubble:
								sender.sendMessage(cc
										+ AirBubble.getDescription());
								break;
							case AirShield:
								sender.sendMessage(cc
										+ AirShield.getDescription());
								break;
							case AirSuction:
								sender.sendMessage(cc
										+ AirSuction.getDescription());
								break;
							case AirSwipe:
								sender.sendMessage(cc
										+ AirSwipe.getDescription());
								break;
							case Tornado:
								sender.sendMessage(cc
										+ Tornado.getDescription());
								break;
							case AirScooter:
								sender.sendMessage(cc
										+ AirScooter.getDescription());
								break;
							// case AirBurst:
							// sender.sendMessage(cc
							// + AirBurst.getDescription());
							// break;
							// case AirSpout:
							// sender.sendMessage(cc
							// + AirSpout.getDescription());
							// break;
							case Catapult:
								sender.sendMessage(cc
										+ Catapult.getDescription());
								break;
							case RaiseEarth:
								sender.sendMessage(cc
										+ EarthColumn.getDescription());
								break;
							case EarthGrab:
								sender.sendMessage(cc
										+ EarthGrab.getDescription());
								break;
							case EarthTunnel:
								sender.sendMessage(cc
										+ EarthTunnel.getDescription());
								break;
							// case CompactColumn:
							// sender.sendMessage(cc
							// + CompactColumn.getDescription());
							// break;
							case EarthBlast:
								sender.sendMessage(cc
										+ EarthBlast.getDescription());
								break;
							case Collapse:
								sender.sendMessage(cc
										+ Collapse.getDescription());
								break;
							case Tremorsense:
								sender.sendMessage(cc
										+ Tremorsense.getDescription());
								break;
							// case Shockwave:
							// sender.sendMessage(cc
							// + Shockwave.getDescription());
							// break;
							case Blaze:
								sender.sendMessage(cc
										+ ArcOfFire.getDescription());
								break;
							case Extinguish:
								sender.sendMessage(cc
										+ Extinguish.getDescription());
								break;
							case Fireball:
								sender.sendMessage(cc
										+ Fireball.getDescription());
								break;
							case FireBlast:
								sender.sendMessage(cc
										+ FireBlast.getDescription());
								break;
							case HeatMelt:
								sender.sendMessage(cc
										+ HeatMelt.getDescription());
								break;
							// case RingOfFire:
							// sender.sendMessage(cc
							// + RingOfFire.getDescription());
							// break;
							case FireJet:
								sender.sendMessage(cc
										+ FireJet.getDescription());
								break;
							case Illumination:
								sender.sendMessage(cc
										+ Illumination.getDescription());
								break;
							case Lightning:
								sender.sendMessage(cc
										+ Lightning.getDescription());
								break;
							case WallOfFire:
								sender.sendMessage(cc
										+ WallOfFire.getDescription());
								break;
							case Bloodbending:
								sender.sendMessage(cc
										+ Bloodbending.getDescription());
								break;
							case WaterBubble:
								sender.sendMessage(cc
										+ AirBubble.getDescription());
								break;
							case FreezeMelt:
								sender.sendMessage(cc
										+ FreezeMelt.getDescription());
								break;
							case HealingWaters:
								sender.sendMessage(cc
										+ HealingWaters.getDescription());
								break;
							case Plantbending:
								sender.sendMessage(cc
										+ Plantbending.getDescription());
								break;
							// case WalkOnWater:
							// sender.sendMessage(cc
							// + WalkOnWater.getDescription());
							// break;
							case WaterManipulation:
								sender.sendMessage(cc
										+ WaterManipulation.getDescription());
								break;
							case WaterSpout:
								sender.sendMessage(cc
										+ WaterSpout.getDescription());
								break;
							case WaterWall:
								sender.sendMessage(cc
										+ WaterWall.getDescription());
								break;
							case Wave:
								sender.sendMessage(cc + Wave.getDescription());
								break;
							case AvatarState:
								sender.sendMessage(cc
										+ AvatarState.getDescription());
								break;
							case EarthArmor:
								sender.sendMessage(cc
										+ EarthArmor.getDescription());
								break;
							case RapidPunch:
								sender.sendMessage(cc
										+ RapidPunch.getDescription());
								break;
							case HighJump:
								sender.sendMessage(cc
										+ HighJump.getDescription());
								break;
							}
							return true;
						}
					}
					for (String s : command) {
						if (args[1].equalsIgnoreCase(s.split(" ")[0])) {
							sender.sendMessage(ChatColor.AQUA
									+ "                                        /bending "
									+ (s.split(" ")[0]));
							String msg = "";
							if ((s.split(" ")[0]).equalsIgnoreCase("choose")
									&& sender
											.hasPermission("bending.command.choose")) {
								msg = "The command /bending choose <element> let you choose one of the four elements (air, fire, water, air) each having different abilities. ";
								if (sender
										.hasPermission("bending.admin.choose"))
									msg = msg
											+ "The command can also be used to set other people bending element like so /bending choose <player> <element>";
								sender.sendMessage(msg);
							} else if ((s.split(" ")[0])
									.equalsIgnoreCase("bind")
									&& sender
											.hasPermission("bending.command.bind")) {
								String append = "a slot";
								if (ConfigManager.bendToItem) {
									append = "an item";
								}
								msg = "The command /bending <bind> <ability> is used to bind an ability to "
										+ append + ".";
								sender.sendMessage(msg);
							} else if ((s.split(" ")[0].toLowerCase())
									.equalsIgnoreCase("help")) {

							} else if ((s.split(" ")[0].toLowerCase())
									.equalsIgnoreCase("remove")
									&& sender
											.hasPermission("bending.admin.remove")) {
								sender.sendMessage("The command /bending remove <player> will remove the player bending. It can also be used to lift the permaremove of a player.");
							} else if ((s.split(" ")[0])
									.equalsIgnoreCase("reload")
									&& sender
											.hasPermission("bending.admin.reload")) {
								player.sendMessage("The command /bending reload will allow you to reload the configuration, so you can make change while the server is running and don't have to restart/reload it.");
							} else if ((s.split(" ")[0])
									.equalsIgnoreCase("permaremove")
									&& sender
											.hasPermission("bending.admin.permaremove")) {
								sender.sendMessage("The command /bending permaremove <player> will permanantly remove someone's bending and won't allow him to choose once again until you do /bending remove <player> or manually set his bending");
							} else if ((s.split(" ")[0])
									.equalsIgnoreCase("add")
									&& sender
											.hasPermission("bending.admin.add")) {
								sender.sendMessage("The command /bending add <element> allow you to add elements to the one you aleredy have. It can be used to stack all for eleents at once.");
							} else if ((s.split(" ")[0])
									.equalsIgnoreCase("display")
									&& sender
											.hasPermission("bending.command.display")) {
								msg = ("The command /bending display allows you to see a list of your binding so you can remember where you binded what.");
								if (sender
										.hasPermission("bending.command.displayelement")) {
									msg = msg
											+ " The command /bending display <element> can allow you to see a list of abilities an bending element has.";
								}
								sender.sendMessage(msg);
							}

							return true;
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
							sender.sendMessage(ChatColor.RED + "There's only "
									+ pages + " pages of help");
						} else {
							sender.sendMessage(ChatColor.RED + "There's only "
									+ pages + " page of help");
						}
						return true;
					}
					sender.sendMessage(ChatColor.AQUA
							+ "=======================Help===========================");
					for (int i = 1; i <= 8; i++) {
						if (command.size() > (i + (8 * page) - 9)) {
							String comname = "/bending "
									+ command.get((i + (8 * page) - 9));
							sender.sendMessage(comname);
						}
					}
					sender.sendMessage(ChatColor.AQUA
							+ "=====================Page " + page + "/" + pages
							+ "========================");
					return true;
				}
			}
			if (args[0].equalsIgnoreCase("import")
					&& (sender.hasPermission("bending.command.import") || player == null)) {
				if (StorageManager.useFlatFile) {
					sender.sendMessage(ChatColor.AQUA + "MySQL needs to be enabled to import bendingPlayers");
					return true;
				}
				BendingPlayers temp = new BendingPlayers(getDataFolder());
				Set<String> keys = temp.getKeys();

				for (String s : keys) {
					if (s.contains("<")) {
						String[] getplayername = s.split("<");
						String playername = getplayername[0];
						String[] getSetter = s.split("<");
						String Setter = getSetter[1];
						String binded = Setter.replace("Bind", "").replace(">",
								"");
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
							config.addBending(playerName,
									BendingType.ChiBlocker);

					}

				}
				temp = null;
				sender.sendMessage(ChatColor.AQUA + "Imported BendingPlayers to MySQL.");
				return true;
			}
		if (args[0].equalsIgnoreCase("check"))
				 {
			for (String players: benders.keySet()){
			sender.sendMessage(players + " :" + benders.get(players).size());
			sender.sendMessage(Tools.isBender(players) ? "True" : "False");
			sender.sendMessage(ChatColor.RED + (Tools.isBender(players, BendingType.Fire) ? "True" : "False"));
			sender.sendMessage(ChatColor.AQUA + (Tools.isBender(players, BendingType.Water) ? "True" : "False"));
			sender.sendMessage(ChatColor.GRAY + (Tools.isBender(players, BendingType.Air) ? "True" : "False"));
			sender.sendMessage(ChatColor.GREEN + (Tools.isBender(players, BendingType.Earth) ? "True" : "False"));
			sender.sendMessage(ChatColor.GOLD + (Tools.isBender(players, BendingType.ChiBlocker) ? "True" : "False"));
					for (BendingType type: benders.get(players)){
						sender.sendMessage(ChatColor.AQUA + type.toString());
					}
						
			}
				 }
		}
		sender.sendMessage(ChatColor.RED
				+ "Use /bending help <page> if you want to see a list of commands.");
		sender.sendMessage(ChatColor.RED
				+ "Use /bending help <ability> if you want to see how to use it.");
		sender.sendMessage(ChatColor.RED
				+ "Use /bending help <command> if you need help with a command.");
		return true;

	}
}
