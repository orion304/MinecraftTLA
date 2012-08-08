package main;

import java.io.File;
import java.io.IOException;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

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

	public String getKey(String s) {
		if (!(bendingPlayers == null))
			return bendingPlayers.getString(s, "");
		return "";
	}

	public Boolean checkKeys(String s) {
		if (!(bendingPlayers == null))
			return bendingPlayers.getKeys(false).contains(s);
		return false;
	}

	public Set<String> getKeys() {
		return bendingPlayers.getKeys(false);
	}

	public void setKey(String key, String field) {
		if (!(bendingPlayers == null))
			bendingPlayers.set(key, field);
		save();
	}

	public void reload() {
		if (bendingPlayersFile == null) {
			bendingPlayersFile = new File(dataFolder, "bendingPlayers.yml");
		}
		bendingPlayers = YamlConfiguration
				.loadConfiguration(bendingPlayersFile);
	}

	private void load() {
		if (bendingPlayers == null) {
			reload();
		}
	}

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

}