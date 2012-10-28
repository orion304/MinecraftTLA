package main;

import java.util.ArrayList;
import java.util.TreeSet;
import java.util.concurrent.ConcurrentHashMap;

import org.bukkit.Material;
import org.bukkit.OfflinePlayer;

import tools.Abilities;
import tools.BendingPlayer;
import tools.BendingType;
import tools.Tools;

public class PlayerStorageWriter implements Runnable {

	private static ConcurrentHashMap<Integer, Queue> queue = new ConcurrentHashMap<Integer, Queue>();
	private static int ID = Integer.MIN_VALUE;

	private static class Queue {

		public boolean addBending, removeBending, setBending, bindSlot,
				bindItem, removeSlot, removeItem, setLanguage,
				permaRemoveBending;
		public int slot;
		public Material item;
		public Abilities ability;
		public BendingType type;
		public OfflinePlayer player;
		private String language;

		public Queue(OfflinePlayer player, int slot, Abilities ability) {
			this.player = player;
			bindSlot = true;
			this.slot = slot;
			this.ability = ability;
			BendingPlayer bPlayer = BendingPlayer.getBendingPlayer(player);
			bPlayer.setAbility(slot, ability);
		}

		public Queue(OfflinePlayer player, Material item, Abilities ability) {
			this.player = player;
			bindItem = true;
			this.item = item;
			this.ability = ability;
			BendingPlayer bPlayer = BendingPlayer.getBendingPlayer(player);
			bPlayer.setAbility(item, ability);
		}

		public Queue(OfflinePlayer player, BendingType type, boolean add) {
			this.player = player;
			this.type = type;
			BendingPlayer bPlayer = BendingPlayer.getBendingPlayer(player);
			if (add) {
				bPlayer.addBender(type);
				addBending = true;
			} else {
				bPlayer.setBender(type);
				setBending = true;
			}

		}

		public Queue(OfflinePlayer player, int slot) {
			this.player = player;
			removeSlot = true;
			this.slot = slot;
			BendingPlayer bPlayer = BendingPlayer.getBendingPlayer(player);
			bPlayer.removeAbility(slot);
		}

		public Queue(OfflinePlayer player, Material item) {
			this.player = player;
			removeItem = true;
			this.item = item;
			BendingPlayer bPlayer = BendingPlayer.getBendingPlayer(player);
			bPlayer.removeAbility(item);
		}

		public Queue(OfflinePlayer player, boolean permanent) {
			this.player = player;
			if (permanent) {
				permaRemoveBending = true;
			} else {
				removeBending = true;
			}
			BendingPlayer bPlayer = BendingPlayer.getBendingPlayer(player);
			bPlayer.removeBender();
		}

		public Queue(OfflinePlayer player, String language) {
			this.player = player;
			setLanguage = true;
			this.language = language;
			BendingPlayer bPlayer = BendingPlayer.getBendingPlayer(player);
			bPlayer.setLanguage(language);
		}

	}

	public static void addBending(OfflinePlayer player, BendingType type) {
		queue.put(ID++, new Queue(player, type, true));
	}

	public static void removeBending(OfflinePlayer player) {
		queue.put(ID++, new Queue(player, false));
	}

	public static void permaRemoveBending(OfflinePlayer player) {
		queue.put(ID++, new Queue(player, true));
	}

	public static void setBending(OfflinePlayer player, BendingType type) {
		queue.put(ID++, new Queue(player, type, false));
	}

	public static void bindSlot(OfflinePlayer player, int slot,
			Abilities ability) {
		queue.put(ID++, new Queue(player, slot, ability));
	}

	public static void bindItem(OfflinePlayer player, Material item,
			Abilities ability) {
		queue.put(ID++, new Queue(player, item, ability));
	}

	public static void removeSlot(OfflinePlayer player, int slot) {
		queue.put(ID++, new Queue(player, slot));
	}

	public static void removeItem(OfflinePlayer player, Material item) {
		queue.put(ID++, new Queue(player, item));
	}

	public static void setLanguage(OfflinePlayer player, String language) {
		queue.put(ID++, new Queue(player, language));
	}

	@Override
	public void run() {
		if (queue.isEmpty()) {
			ID = Integer.MIN_VALUE;
			return;
		}

		ArrayList<Integer> index = new ArrayList<Integer>(new TreeSet<Integer>(
				queue.keySet()));

		for (int i : index) {
			Queue item = queue.get(i);

			if (item.addBending) {
				Tools.config.addBending(item.player.getName(), item.type);
			} else if (item.removeBending) {
				Tools.config.removeBending(item.player);
			} else if (item.setBending) {
				Tools.config.setBending(item.player, item.type);
			} else if (item.bindSlot) {
				Tools.config.setAbility(item.player.getName(), item.ability,
						item.slot);
			} else if (item.bindItem) {
				Tools.config.setAbility(item.player.getName(), item.ability,
						item.item);
			} else if (item.removeSlot) {
				Tools.config.removeAbility(item.player, item.slot);
			} else if (item.removeItem) {
				Tools.config.removeAbility(item.player, item.item);
			} else if (item.permaRemoveBending) {
				Tools.config.permaRemoveBending(item.player);
			} else if (item.setLanguage) {
				Tools.config.setLanguage(item.player, item.language);
			}

		}
	}

}
