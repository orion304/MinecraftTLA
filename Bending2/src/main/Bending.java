package main;

import java.io.File;
import java.util.Arrays;
import java.util.logging.Logger;

import net.minecraft.server.EntityFireball;

import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import tools.Abilities;
import tools.BendingType;
import tools.ConfigManager;
import tools.Tools;
import firebending.Fireball;

public class Bending extends JavaPlugin {

	public static long time_step = 1; // in ms
	public static Logger log = Logger.getLogger("Minecraft");

	public final BendingManager manager = new BendingManager(this);
	public final BendingListener listener = new BendingListener(this);

	// public BendingPlayers config = new BendingPlayers(getDataFolder(),
	// getResource("bendingPlayers.yml"));
	public static ConfigManager configManager = new ConfigManager();
	public BendingPlayers config = new BendingPlayers(getDataFolder());
	public Tools tools = new Tools(config);

	public String[] waterbendingabilities;
	public String[] airbendingabilities;
	public String[] earthbendingabilities;
	public String[] firebendingabilities;

	public void onDisable() {

		Fireball.removeAllFireballs();
		Tools.stopAllBending();

	}

	public void onEnable() {

		waterbendingabilities = Abilities.getWaterbendingAbilities();
		airbendingabilities = Abilities.getAirbendingAbilities();
		earthbendingabilities = Abilities.getEarthbendingAbilities();
		firebendingabilities = Abilities.getFirebendingAbilities();

		getServer().getPluginManager().registerEvents(listener, this);

		getServer().getScheduler().scheduleSyncRepeatingTask(this, manager, 0,
				1);

		removeFireballs();

		log.info("Bending v" + this.getDescription().getVersion()
				+ " has been loaded.");

		configManager.load(new File(getDataFolder(), "config.yml"));

	}

	public void reloadConfiguration() {
		getConfig().options().copyDefaults(true);
		saveConfig();

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

		Player player = (Player) sender;

		if (cmd.getName().equalsIgnoreCase("bending")) {
			if (Arrays.asList(args).isEmpty()) {
				return false;
			}

			if (args[0].equalsIgnoreCase("remove") && args.length >= 2
					&& sender.isOp()) {
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

			if (args[0].equalsIgnoreCase("reload") && args.length >= 2
					&& sender.isOp()) {
				configManager
						.load(new File(this.getDataFolder(), "config.yml"));
				sender.sendMessage("Config reloaded");
				return true;
			}

			if (args[0].equalsIgnoreCase("permaremove") && args.length >= 2
					&& sender.isOp()) {
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

			if (args[0].equalsIgnoreCase("choose")) {
				if (args.length == 1)
					return false;
				if (args.length == 2) {
					if (config.isBender(player) && !sender.isOp()) {
						sender.sendMessage("You've already chosen your bending abilities. Only ops can change this now.");
						return true;
					}
					if (args[1].equalsIgnoreCase("water")
							|| args[1].equalsIgnoreCase("air")
							|| args[1].equalsIgnoreCase("fire")
							|| args[1].equalsIgnoreCase("earth")) {
						String part = " a ";
						if (args[1].toLowerCase().startsWith("a")
								|| args[1].toLowerCase().startsWith("e")) {
							part = " an ";
						}
						part = part + args[1].toLowerCase();
						sender.sendMessage("You are now" + part + "bender!");
						config.setBending(player, args[1]);
						return true;
					}
				} else if (sender.isOp()) {
					String playername = args[1];
					Player targetplayer = getServer().getPlayer(playername);
					if (targetplayer == null) {
						sender.sendMessage("Usage: /bending choose [player] [element]");
						return true;
					} else if (args[2].equalsIgnoreCase("water")
							|| args[2].equalsIgnoreCase("air")
							|| args[2].equalsIgnoreCase("fire")
							|| args[2].equalsIgnoreCase("earth")) {
						String part = " a ";
						if (args[2].toLowerCase().startsWith("a")
								|| args[1].toLowerCase().startsWith("e")) {
							part = " an ";
						}
						part = part + args[1].toLowerCase();
						targetplayer.sendMessage(sender.getName()
								+ " has made you" + part + "bender!");
						sender.sendMessage("You have changed "
								+ targetplayer.getName() + "'s bending.");
						config.removeBending(targetplayer);
						config.setBending(targetplayer, args[2]);
						return true;
					}
					sender.sendMessage("Usage: /bending choose [player] [element]");
				} else {
					return false;
				}
			}

			if (args.length > 1) {
				if (args[0].equalsIgnoreCase("display")) {
					String[] abilitylist = null;
					if (args[1].equalsIgnoreCase("air")) {
						abilitylist = airbendingabilities;
					} else if (args[1].equalsIgnoreCase("water")) {
						abilitylist = waterbendingabilities;
					} else if (args[1].equalsIgnoreCase("earth")) {
						abilitylist = earthbendingabilities;
					} else if (args[1].equalsIgnoreCase("fire")) {
						abilitylist = firebendingabilities;
					}

					if (abilitylist != null) {
						for (String ability : abilitylist) {
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
						return true;
					}
				} else if (args[0].equalsIgnoreCase("clear")
						&& Integer.parseInt(args[1]) > 0
						&& Integer.parseInt(args[1]) < 10) {
					config.removeAbility(player, Integer.parseInt(args[1]) - 1);
					return true;
				}
			}

			else if (args[0].equalsIgnoreCase("display")) {
				for (int i = 0; i <= 8; i++) {
					Abilities a = config.getAbility(player, i);
					if (a != null) {
						String ability = config.getAbility(player, i).name();
						sender.sendMessage("Slot " + (i + 1) + ": " + ability);
					}
				}
				return true;
			} else if (args[0].equalsIgnoreCase("clear")) {
				for (int i = 0; i <= 8; i++) {
					config.removeAbility(player, i);

				}
				return true;
			}

			if (args[0].equalsIgnoreCase("bind") && args.length == 2) {

				String a = args[1];
				Abilities ability = Abilities.getAbility(a);

				if (ability != null) {

					int slot = (player).getInventory().getHeldItemSlot();

					if (config.isBender(player, BendingType.Water)
							&& Abilities.isWaterbending(ability)) {
						config.setAbility(player, ability, slot);
						sender.sendMessage(ability.name() + " bound to slot "
								+ (slot + 1));
						return true;
					}
					if (config.isBender(player, BendingType.Air)
							&& Abilities.isAirbending(ability)) {
						config.setAbility(player, ability, slot);
						sender.sendMessage(ability.name() + " bound to slot "
								+ (slot + 1));
						return true;
					}
					if (config.isBender(player, BendingType.Earth)
							&& Abilities.isEarthbending(ability)) {
						config.setAbility(player, ability, slot);
						sender.sendMessage(ability.name() + " bound to slot "
								+ (slot + 1));
						return true;
					}
					if (config.isBender(player, BendingType.Fire)
							&& Abilities.isFirebending(ability)) {
						config.setAbility(player, ability, slot);
						sender.sendMessage(ability.name() + " bound to slot "
								+ (slot + 1));
						return true;
					}
					if (sender.isOp() && ability == Abilities.AvatarState) {
						config.setAbility(player, ability, slot);
						sender.sendMessage(ability.name() + " bound to slot "
								+ (slot + 1));
						return true;
					}
				}
			}
		}

		return false;

	}

}