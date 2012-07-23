package main;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import tools.Abilities;
import tools.BendingType;
import tools.ConfigManager;
import tools.Tools;

public class BendingPlayers {

	private FileConfiguration bendingPlayers = null;
	private File bendingPlayersFile = null;

	private File dataFolder;

	// private InputStream defConfigStream;

	// public BendingPlayers(File file, InputStream inputStream) {
	// load();
	// dataFolder = file;
	// defConfigStream = inputStream;
	// }

	public BendingPlayers(File file) {
		load();
		dataFolder = file;
	}

	public void removeBending(Player player) {
		if (bendingPlayers == null) {
			return;
		} else if (bendingPlayers.getKeys(false).contains(player.getName())) {
			for (int i = 0; i <= 8; i++) {
				removeAbility(player, i);
			}
			bendingPlayers.set(player.getName(), "");
			player.setDisplayName(player.getName());
		}
		return;
	}

	public boolean isBender(Player player, BendingType type) {
		if (bendingPlayers == null) {
			return false;
		} else if (bendingPlayers.getKeys(false).contains(player.getName())) {
			if (bendingPlayers.getString(player.getName(), "").contains("a")
					&& type == BendingType.Air) {
				return true;
			}
			if (bendingPlayers.getString(player.getName(), "").contains("e")
					&& type == BendingType.Earth) {
				return true;
			}
			if (bendingPlayers.getString(player.getName(), "").contains("w")
					&& type == BendingType.Water) {
				return true;
			}
			if (bendingPlayers.getString(player.getName(), "").contains("f")
					&& type == BendingType.Fire) {
				return true;
			}
			if (bendingPlayers.getString(player.getName(), "").contains("c")
					&& type == BendingType.ChiBlocker) {
				return true;
			}
		}
		return false;
	}

	public boolean isBender(String player, BendingType type) {
		if (bendingPlayers == null) {
			return false;
		} else if (bendingPlayers.getKeys(false).contains(player)) {
			if (bendingPlayers.getString(player, "").contains("a")
					&& type == BendingType.Air) {
				return true;
			}
			if (bendingPlayers.getString(player, "").contains("e")
					&& type == BendingType.Earth) {
				return true;
			}
			if (bendingPlayers.getString(player, "").contains("w")
					&& type == BendingType.Water) {
				return true;
			}
			if (bendingPlayers.getString(player, "").contains("f")
					&& type == BendingType.Fire) {
				return true;
			}
			if (bendingPlayers.getString(player, "").contains("f")
					&& type == BendingType.ChiBlocker) {
				return true;
			}
		}
		return false;
	}

	public void setBending(Player player, BendingType type) {
		String bending = "";
		String bendingstring = "";
		if (type == BendingType.Air) {
			bending = "a";
			bendingstring = "As an airbender, you now take no falling damage, have faster sprinting and higher "
					+ "jumps. Additionally, daily activities are easier for you - your food meter decays at a "
					+ "much slower rate";
		} else if (type == BendingType.Earth) {
			bending = "e";
			bendingstring = "As an earthbender, upon landing on bendable earth, you will briefly turn the "
					+ "area to soft sand, negating any fall damage you would have otherwise taken.";
		} else if (type == BendingType.Water) {
			bending = "w";
			bendingstring = "As a waterbender, you no longer take any fall damage when landing on ice, snow "
					+ "or even 1-block-deep water. Additionally, sneaking in the water with a bending ability "
					+ "selected that does not utilize sneak (or no ability at all)"
					+ " will give you accelerated swimming.";
		} else if (type == BendingType.Fire) {
			bending = "f";
			bendingstring = "As a firebender, you now more quickly smother yourself when you catch on fire.";
		} else if (type == BendingType.ChiBlocker) {
			bending = "c";
			bendingstring = "As a chiblocker, you have no active abilities to bind. Instead, you have improved "
					+ "sprinting and jumping, have a dodge chance and deal more damage with your fists. "
					+ "Additionally, punching a bender will block his/her chi for a few seconds, preventing "
					+ "him/her from bending (and even stopping their passive!)";
		} else {
			bending = "s";
			player.setDisplayName(player.getName());
			bendingPlayers.set(player.getName(), bending);
			save();
			return;
		}
		bendingPlayers.set(player.getName(), bending);
		player.sendMessage(ChatColor.GOLD + bendingstring);
		player.sendMessage(ChatColor.GOLD
				+ "Use '/bending help' if you need assistance.");

		if (ConfigManager.enabled) {
			String append = "";
			if (!player.isOp()) {
				if (Tools.isBender(player, BendingType.Air)) {
					append = ConfigManager.getPrefix("Air");
				} else if (Tools.isBender(player, BendingType.Earth)) {
					append = ConfigManager.getPrefix("Earth");
				} else if (Tools.isBender(player, BendingType.Fire)) {
					append = ConfigManager.getPrefix("Fire");
				} else if (Tools.isBender(player, BendingType.Water)) {
					append = ConfigManager.getPrefix("Water");
				} else if (Tools.isBender(player, BendingType.ChiBlocker)) {
					append = ConfigManager.getPrefix("ChiBlocker");
				}
				if (!(ConfigManager.compatibility))
					player.setDisplayName(append + player.getName());
			}
			if ((ConfigManager.compatibility) && (ConfigManager.enabled)) {
				ChatColor color = ChatColor.WHITE;
				if (ConfigManager.colors && (!player.isOp())) {
					if (Tools.isBender(player, BendingType.Air)) {
						color = Tools.getColor(ConfigManager.getColor("Air"));
					} else if (Tools.isBender(player, BendingType.Earth)) {
						color = Tools.getColor(ConfigManager.getColor("Earth"));
					} else if (Tools.isBender(player, BendingType.Fire)) {
						color = Tools.getColor(ConfigManager.getColor("Fire"));
					} else if (Tools.isBender(player, BendingType.Water)) {
						color = Tools.getColor(ConfigManager.getColor("Water"));
					} else if (Tools.isBender(player, BendingType.ChiBlocker)) {
						color = Tools.getColor(ConfigManager
								.getColor("ChiBlocker"));
					}
					player.setDisplayName("<" + color + append
							+ player.getName() + ChatColor.WHITE + ">");
				}
			}
		}

		save();
	}

	public void setBending(Player player, String type) {
		BendingType bendingtype = BendingType.getType(type);
		if (bendingtype != null) {
			setBending(player, bendingtype);
		}

	}

	public void addBending(Player player, BendingType type) {
		String bending = bendingPlayers.getString(player.getName(), "");
		if (!isBender(player, type)) {
			if (type == BendingType.Air) {
				bending += "a";
			} else if (type == BendingType.Earth) {
				bending += "e";
			} else if (type == BendingType.Water) {
				bending += "w";
			} else if (type == BendingType.Fire) {
				bending += "f";
			} else if (type == BendingType.ChiBlocker) {
				bending += "c";
			}
		}
		bendingPlayers.set(player.getName(), bending);
		save();
	}

	public void addBending(Player player, String type) {
		BendingType bendingtype = BendingType.getType(type);
		if (bendingtype != null) {
			addBending(player, bendingtype);
		}
	}

	public boolean isBender(Player player) {
		if (bendingPlayers == null) {
			return false;
		} else if (bendingPlayers.getKeys(false).contains(player.getName())) {
			if (bendingPlayers.getString(player.getName(), "").contains("a")
					|| bendingPlayers.getString(player.getName(), "").contains(
							"e")
					|| bendingPlayers.getString(player.getName(), "").contains(
							"w")
					|| bendingPlayers.getString(player.getName(), "").contains(
							"f")
					|| bendingPlayers.getString(player.getName(), "").contains(
							"s")
					|| bendingPlayers.getString(player.getName(), "").contains(
							"c")) {
				return true;
			}
		}
		return false;
	}

	public void setAbility(Player player, String ability, int slot) {
		for (Abilities a : Abilities.values()) {
			if (ability.equalsIgnoreCase(a.name())) {
				setAbility(player, a, slot);
			}
		}
	}

	public void setAbility(Player player, Abilities ability, int slot) {
		String setter = player.getName() + "<Bind" + slot + ">";
		bendingPlayers.set(setter, ability.name());
		save();
	}

	// Bind to item
	public void setAbility(Player player, String ability, Material mat) {
		for (Abilities a : Abilities.values()) {
			if (ability.equalsIgnoreCase(a.name())) {
				setAbility(player, a, mat);
			}
		}
	}

	public void setAbility(Player player, Abilities ability, Material mat) {
		String setter = player.getName() + "<Bind" + mat.name() + ">";
		bendingPlayers.set(setter, ability.name());
		save();
	}

	public Abilities getAbility(Player player) {
		if (ConfigManager.bendToItem == false)
			return getAbility(player, player.getInventory().getHeldItemSlot());
		return getAbility(player, player.getItemInHand().getType());
	}

	public Abilities getAbility(Player player, int slot) {
		String setter = player.getName() + "<Bind" + slot + ">";
		String ability = bendingPlayers.getString(setter, "");

		for (Abilities a : Abilities.values()) {
			if (ability.equalsIgnoreCase(a.name()))
				return a;
		}
		return null;
	}

	// Bind to item

	public Abilities getAbility(Player player, Material mat) {
		String setter = player.getName() + "<Bind" + mat.name() + ">";
		String ability = bendingPlayers.getString(setter, "");

		for (Abilities a : Abilities.values()) {
			if (ability.equalsIgnoreCase(a.name()))
				return a;
		}
		return null;
	}

	public boolean hasAbility(Player player, Abilities ability) {
		for (int i = 0; i <= 8; i++) {
			if (getAbility(player, i) != null)
				if (getAbility(player, i) == ability)
					return true;
		}
		return false;
	}

	public List<BendingType> getBendingTypes(Player player) {
		List<BendingType> list = Arrays.asList();

		for (BendingType type : BendingType.values()) {
			if (isBender(player, type)) {
				list.add(type);
			}
		}
		return list;
	}

	public void removeAbility(Player player, int slot) {
		String setter = player.getName() + "<Bind" + slot + ">";
		bendingPlayers.set(setter, null);
		save();
	}

	public void removeAbility(Player player, Material mat) {
		String setter = player.getName() + "<Bind" + mat.name() + ">";
		bendingPlayers.set(setter, null);
		save();
	}

	public void reload() {
		if (bendingPlayersFile == null) {
			bendingPlayersFile = new File(dataFolder, "bendingPlayers.yml");
		}
		bendingPlayers = YamlConfiguration
				.loadConfiguration(bendingPlayersFile);

		// Look for defaults in the jar
		// if (defConfigStream != null) {
		// YamlConfiguration defConfig = YamlConfiguration
		// .loadConfiguration(defConfigStream);
		// bendingPlayers.setDefaults(defConfig);
		// }
	}

	// Next, you need to write the getter method. Check if bendingPlayers is
	// null,
	// if it is load from disk.

	private void load() {
		if (bendingPlayers == null) {
			reload();
		}
	}

	// Finally, write the save method, which saves changes and overwrites the
	// file on disk.

	public void save() {
		if (bendingPlayers == null || bendingPlayersFile == null) {
			return;
		}
		try {
			bendingPlayers.save(bendingPlayersFile);
		} catch (IOException ex) {
			Logger.getLogger(JavaPlugin.class.getName()).log(Level.SEVERE,
					"Could not save config to " + bendingPlayersFile, ex);
		}
	}

	public void permaRemoveBending(Player player) {
		removeBending(player);
		BendingType type = null;
		setBending(player, type);

	}

}
