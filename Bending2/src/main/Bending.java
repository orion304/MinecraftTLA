package main;

import java.io.File;
import java.util.Arrays;
import java.util.logging.Logger;

import net.minecraft.server.EntityFireball;

import org.bukkit.Material;
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

			if (args[0].equalsIgnoreCase("reload") && args.length == 1
					&& sender.isOp()) {
				configManager
						.load(new File(this.getDataFolder(), "config.yml"));
				config.reload();
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
						config.removeBending(player);
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
								|| args[2].toLowerCase().startsWith("e")) {
							part = " an ";
						}
						part = part + args[2].toLowerCase();
						targetplayer.sendMessage(sender.getName()
								+ " has made you" + part + "bender!");
						sender.sendMessage("You have changed "
								+ targetplayer.getName() + "'s bending.");
						config.removeBending(targetplayer);
						config.setBending(targetplayer, args[2]);
						return true;
					}
					sender.sendMessage("Usage: /bending add [player] [element]");
				} else {
					return false;
				}
			}

			if (args[0].equalsIgnoreCase("add") && sender.isOp()) {
				if (args.length == 1)
					return false;
				if (args.length == 2) {
					if (args[1].equalsIgnoreCase("water")
							&& Tools.isBender(player, BendingType.Water)) {
						sender.sendMessage("You are already a waterbender!");
						return true;
					} else if (args[1].equalsIgnoreCase("air")
							&& Tools.isBender(player, BendingType.Air)) {
						sender.sendMessage("You are already an airbender!");
						return true;
					} else if (args[1].equalsIgnoreCase("earth")
							&& Tools.isBender(player, BendingType.Earth)) {
						sender.sendMessage("You are already an earthbender!");
						return true;
					} else if (args[1].equalsIgnoreCase("fire")
							&& Tools.isBender(player, BendingType.Fire)) {
						sender.sendMessage("You are already a firebender!");
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
						sender.sendMessage("You are now also" + part
								+ "bender!");
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
							&& Tools.isBender(targetplayer, BendingType.Water)) {
						sender.sendMessage(targetplayer.getName()
								+ " is already a waterbender!");
						return true;
					} else if (args[2].equalsIgnoreCase("air")
							&& Tools.isBender(targetplayer, BendingType.Air)) {
						sender.sendMessage(targetplayer.getName()
								+ " is already an airbender!");
						return true;
					} else if (args[2].equalsIgnoreCase("earth")
							&& Tools.isBender(targetplayer, BendingType.Earth)) {
						sender.sendMessage(targetplayer.getName()
								+ " is already an earthbender!");
						return true;
					} else if (args[2].equalsIgnoreCase("fire")
							&& Tools.isBender(targetplayer, BendingType.Fire)) {
						sender.sendMessage(targetplayer.getName()
								+ " is already a firebender!");
						return true;
					} else if (args[2].equalsIgnoreCase("water")
							|| args[2].equalsIgnoreCase("air")
							|| args[2].equalsIgnoreCase("fire")
							|| args[2].equalsIgnoreCase("earth")) {
						String part = " a ";
						if (args[2].toLowerCase().startsWith("a")
								|| args[2].toLowerCase().startsWith("e")) {
							part = " an ";
						}
						part = part + args[2].toLowerCase();
						targetplayer.sendMessage(sender.getName()
								+ " has made you also" + part + "bender!");
						sender.sendMessage("You have added to "
								+ targetplayer.getName() + "'s bending.");
						config.addBending(targetplayer, args[2]);
						return true;
					}
					sender.sendMessage("Usage: /bending add [player] [element]");
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
				} else if (args[0].equalsIgnoreCase("clear")) {
					if (!ConfigManager.bendToItem){
						if (Integer.parseInt(args[1]) > 0
								&& Integer.parseInt(args[1]) < 10){
							config.removeAbility(player, Integer.parseInt(args[1]) - 1);
							return true;
						}
					} else { 
						if (Material.matchMaterial(args[1]) != null) {
							config.removeAbility(player, Material.matchMaterial(args[1]));
							return true;
						}
					}
				}
			}

			else if (args[0].equalsIgnoreCase("display")) {
				if (!ConfigManager.bendToItem){
					for (int i = 0; i <= 8; i++) {
						Abilities a = config.getAbility(player, i);
						if (a != null) {
							String ability = config.getAbility(player, i).name();
							sender.sendMessage("Slot " + (i + 1) + ": " + ability);
						}
					}
				} else {
				
					for (Material mat: Material.values()){
						int i = mat.getId();
						Abilities a = config.getAbility(player, i);
						if (a != null) {
							String ability = config.getAbility(player, i).name();
							sender.sendMessage(mat.name().replaceAll("_", " ") + ": " + ability);
						}
					}
				}
				return true;
			} else if (args[0].equalsIgnoreCase("clear")) {
				if (!ConfigManager.bendToItem){
					for (int i = 0; i <= 8; i++) {
						config.removeAbility(player, i);

					}
				} else {
					for (Material mat: Material.values()){
						config.removeAbility(player, mat.getId());
					}
				}
				return true;
			}

			if (args[0].equalsIgnoreCase("bind") && args.length == 2) {

				String a = args[1];
				Abilities ability = Abilities.getAbility(a);

				if (ability != null) {

					int slot = (player).getInventory().getHeldItemSlot();
					Material mat = player.getInventory().getItemInHand().getType();

					if (config.isBender(player, BendingType.Water)
							&& Abilities.isWaterbending(ability)) {
						if (!ConfigManager.bendToItem) {
							config.setAbility(player, ability, slot);
							sender.sendMessage(ability.name() + " bound to slot "
									+ (slot + 1));
						} else {
							config.setAbility(player, ability, mat);
							sender.sendMessage(ability.name() + " bound to "
									+ mat.name().replaceAll("_", " "));
						}
						return true;
					}
					if (config.isBender(player, BendingType.Air)
							&& Abilities.isAirbending(ability)) {
						if (!ConfigManager.bendToItem) {
							config.setAbility(player, ability, slot);
							sender.sendMessage(ability.name() + " bound to slot "
									+ (slot + 1));
						} else {
							config.setAbility(player, ability, mat);
							sender.sendMessage(ability.name() + " bound to "
									+ mat.name().replaceAll("_", " "));
						}
						return true;
					}
					if (config.isBender(player, BendingType.Earth)
							&& Abilities.isEarthbending(ability)) {
						if (!ConfigManager.bendToItem) {
							config.setAbility(player, ability, slot);
							sender.sendMessage(ability.name() + " bound to slot "
									+ (slot + 1));
						} else {
							config.setAbility(player, ability, mat);
							sender.sendMessage(ability.name() + " bound to "
									+ mat.name().replaceAll("_", " "));
						}
						return true;
					}
					if (config.isBender(player, BendingType.Fire)
							&& Abilities.isFirebending(ability)) {
						if (!ConfigManager.bendToItem) {
							config.setAbility(player, ability, slot);
							sender.sendMessage(ability.name() + " bound to slot "
									+ (slot + 1));
						} else {
							config.setAbility(player, ability, mat);
							sender.sendMessage(ability.name() + " bound to "
									+ mat.name().replaceAll("_", " "));
						}
						return true;
					}
					if (sender.isOp() && ability == Abilities.AvatarState) {
						if (!ConfigManager.bendToItem) {
							config.setAbility(player, ability, slot);
							sender.sendMessage(ability.name() + " bound to slot "
									+ (slot + 1));
						} else {
							config.setAbility(player, ability, mat);
							sender.sendMessage(ability.name() + " bound to "
									+ mat.name().replaceAll("_", " "));
						}
						return true;
					}
				}
			}
		}

		return false;

	}
}