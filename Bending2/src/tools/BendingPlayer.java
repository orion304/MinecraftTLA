package tools;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import main.Bending;
import main.StorageManager;

import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

public class BendingPlayer {

	private static ConcurrentHashMap<OfflinePlayer, BendingPlayer> players = new ConcurrentHashMap<OfflinePlayer, BendingPlayer>();

	private static StorageManager config = Tools.config;

	private OfflinePlayer player;
	private String language;

	private ConcurrentHashMap<Integer, Abilities> slotAbilities = new ConcurrentHashMap<Integer, Abilities>();
	private ConcurrentHashMap<Material, Abilities> itemAbilities = new ConcurrentHashMap<Material, Abilities>();

	private ArrayList<BendingType> bendingType = new ArrayList<BendingType>();

	public BendingPlayer(OfflinePlayer player) {
		if (players.containsKey(player)) {
			players.remove(player);
		}

		language = config.getLanguage(player);

		for (BendingType type : BendingType.values()) {
			if (config.isBender(player.getName(), type))
				bendingType.add(type);
		}

		if (ConfigManager.bendToItem) {

			for (Material item : Material.values()) {
				Abilities ability = config.getAbility(player, item);
				if (ability != null) {
					itemAbilities.put(item, ability);
				}
			}

		} else {

			for (int i = 0; i < 9; i++) {
				Abilities ability = config.getAbility(player, i);
				if (ability != null) {
					slotAbilities.put(i, ability);
				}
			}

		}

		this.player = player;

		players.put(player, this);
	}

	public static BendingPlayer getBendingPlayer(OfflinePlayer player) {
		if (players.containsKey(player)) {
			BendingPlayer bPlayer = players.get(player);
			bPlayer.player = Bending.plugin.getServer().getOfflinePlayer(
					player.getName());
			return bPlayer;
		}
		return new BendingPlayer(player);
	}

	public static BendingPlayer getBendingPlayer(String playername) {
		OfflinePlayer player = Bending.plugin.getServer().getOfflinePlayer(
				playername);
		if (player != null) {
			return getBendingPlayer(player);
		}
		// Tools.verbose("No player by that name!");
		return null;
	}

	public boolean isBender() {
		return !bendingType.isEmpty();
	}

	public boolean isBender(BendingType type) {
		return bendingType.contains(type);
	}

	public void setBender(BendingType type) {
		bendingType.clear();
		slotAbilities.clear();
		itemAbilities.clear();
		bendingType.add(type);
	}

	public void addBender(BendingType type) {
		if (!bendingType.contains(type))
			bendingType.add(type);
	}

	public void removeBender() {
		bendingType.clear();
		slotAbilities.clear();
		itemAbilities.clear();
	}

	public Abilities getAbility() {
		if (!(player instanceof Player)) {
			// Tools.verbose(this);
			// Tools.verbose("Player isn't online??");
			return null;
		}
		Player pPlayer = (Player) player;
		if (ConfigManager.bendToItem) {
			Material item = pPlayer.getItemInHand().getType();
			return getAbility(item);
		} else {
			int slot = pPlayer.getInventory().getHeldItemSlot();
			return getAbility(slot);
		}
	}

	public Abilities getAbility(int slot) {
		if (slotAbilities.containsKey(slot)) {
			return slotAbilities.get(slot);
		}
		return null;
	}

	public Abilities getAbility(Material item) {
		if (itemAbilities.containsKey(item)) {
			return itemAbilities.get(item);
		}
		return null;
	}

	public void setAbility(int slot, Abilities ability) {
		slotAbilities.put(slot, ability);
	}

	public void setAbility(Material item, Abilities ability) {
		itemAbilities.put(item, ability);
	}

	public void removeAbility(int slot) {
		if (slotAbilities.containsKey(slot)) {
			slotAbilities.remove(slot);
		}
	}

	public void removeAbility(Material item) {
		if (itemAbilities.containsKey(item)) {
			itemAbilities.remove(item);
		}
	}

	public OfflinePlayer getPlayer() {
		return player;
	}

	public static ArrayList<BendingPlayer> getBendingPlayers() {
		ArrayList<BendingPlayer> list = new ArrayList<BendingPlayer>();
		for (OfflinePlayer player : players.keySet()) {
			list.add(players.get(player));
		}
		return list;
	}

	public boolean hasAbility(Abilities ability) {

		return false;
	}

	public List<BendingType> getBendingTypes() {
		if (bendingType.isEmpty())
			return null;
		return bendingType;
	}

	public void setLanguage(String language) {
		this.language = language;
	}

	public String getLanguage() {
		return language;
	}

	public String toString() {
		String string = "BendingPlayer{";
		string += "Player=" + player;
		string += ", ";
		string += "BendingType=" + bendingType;
		string += ", ";
		string += "Language=" + language;
		string += ", ";
		if (ConfigManager.bendToItem) {
			string += "Binds=" + itemAbilities;
		} else {
			string += "Binds=" + slotAbilities;
		}
		string += "}";
		return string;
	}

}
