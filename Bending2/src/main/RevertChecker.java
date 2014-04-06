package main;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Future;

import org.apache.commons.lang.exception.ExceptionUtils;
import org.bukkit.Chunk;
import org.bukkit.Server;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

//import tools.ConfigManager;
import tools.Information;
import tools.Tools;

public class RevertChecker implements Runnable {

	static ConcurrentHashMap<Block, Block> revertQueue = new ConcurrentHashMap<Block, Block>();
	static ConcurrentHashMap<Integer, Integer> airRevertQueue = new ConcurrentHashMap<Integer, Integer>();
	private Future<ArrayList<Chunk>> returnFuture;
	// static ConcurrentHashMap<Block, Material> movedEarthQueue = new
	// ConcurrentHashMap<Block, Material>();

	static ConcurrentHashMap<Chunk, Chunk> chunks = new ConcurrentHashMap<Chunk, Chunk>();

	private Bending plugin;

	private static final boolean safeRevert = true;

	private long time;

	public RevertChecker(Bending bending) {
		plugin = bending;
	}

	private class getOccupiedChunks implements Callable<ArrayList<Chunk>> {

		private Server server;

		public getOccupiedChunks(Server server) {
			this.server = server;
		}

		@Override
		public ArrayList<Chunk> call() throws Exception {
			ArrayList<Chunk> chunks = new ArrayList<Chunk>();
			Player[] players = server.getOnlinePlayers();

			for (Player player : players) {
				Chunk chunk = player.getLocation().getChunk();
				if (!chunks.contains(chunk))
					chunks.add(chunk);
			}

			return chunks;
		}

	}

	public void run() {
		time = System.currentTimeMillis();

		if (ConfigValues.ReverseEarthbending) {

			// ArrayList<Chunk> chunks = new ArrayList<Chunk>();
			// Player[] players = plugin.getServer().getOnlinePlayers();
			//
			// for (Player player : players) {
			// Chunk chunk = player.getLocation().getChunk();
			// if (!chunks.contains(chunk))
			// chunks.add(chunk);
			// }
			try {
				// Tools.verbose("Calling future at t="
				// + System.currentTimeMillis());
				returnFuture = plugin
						.getServer()
						.getScheduler()
						.callSyncMethod(plugin,
								new getOccupiedChunks(plugin.getServer()));
				ArrayList<Chunk> chunks = returnFuture.get();
				// Tools.verbose("Future called, t=" +
				// System.currentTimeMillis());

				Map<Block, Information> earth = new HashMap<Block, Information>();
				earth.putAll(Tools.movedearth);

				for (Block block : earth.keySet()) {
					if (revertQueue.containsKey(block))
						continue;
					boolean remove = true;
					Information info = earth.get(block);
					if (time < info.getTime() + ConfigValues.ReverseEarthbendingCheckTime
							|| (chunks.contains(block.getChunk()) && safeRevert)) {
						remove = false;
					}
					if (remove) {
						addToRevertQueue(block);
					}
				}

				Map<Integer, Information> air = new HashMap<Integer, Information>();
				air.putAll(Tools.tempair);

				for (Integer i : air.keySet()) {
					if (airRevertQueue.containsKey(i))
						continue;
					boolean remove = true;
					Information info = air.get(i);
					Block block = info.getBlock();
					if (time < info.getTime() + ConfigValues.ReverseEarthbendingCheckTime
							|| (chunks.contains(block.getChunk()) && safeRevert)) {
						remove = false;
					}
					if (remove) {
						addToAirRevertQueue(i);
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
				Tools.writeToLog(ExceptionUtils.getStackTrace(e));
			}

			// for (Block block : Tools.tempearthblocks.keySet()) {
			// if (revertQueue.containsKey(block))
			// continue;
			// boolean remove = true;
			//
			// Block index = Tools.tempearthblocks.get(block);
			// if (Tools.movedearth.containsKey(index)) {
			// Information info = Tools.movedearth.get(index);
			// if (time < info.getTime() + ConfigManager.revertchecktime
			// || (chunks.contains(index.getChunk()) && safeRevert)) {
			// remove = false;
			// }
			// }
			//
			// if (remove)
			// addToRevertQueue(block);
			//
			// }

			// for (Block block : Tools.movedearth.keySet()) {
			// if (movedEarthQueue.containsKey(block))
			// continue;
			// Information info = Tools.movedearth.get(block);
			// if (time >= info.getTime() + ConfigManager.revertchecktime) {
			// // if (Tools.tempearthblocks.containsKey(info.getBlock()))
			// // Tools.verbose("PROBLEM!");
			// // block.setType(info.getType());
			// // Tools.movedearth.remove(block);
			// addToMovedEarthQueue(block, info.getType());
			// }
			// }

			// Tools.writeToLog("Still " + Tools.tempearthblocks.size()
			// + " remaining.");
		}
	}

	private void addToAirRevertQueue(int i) {
		if (!airRevertQueue.containsKey(i))
			airRevertQueue.put(i, i);

	}

	// void addToMovedEarthQueue(Block block, Material type) {
	// if (!movedEarthQueue.containsKey(block))
	// movedEarthQueue.put(block, type);
	//
	// }

	void addToRevertQueue(Block block) {
		if (!revertQueue.containsKey(block))
			revertQueue.put(block, block);
	}

}
