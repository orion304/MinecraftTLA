package main;

import java.util.ArrayList;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Future;

import org.apache.commons.lang.exception.ExceptionUtils;
import org.bukkit.Chunk;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

import tools.ConfigManager;
import tools.Information;
import tools.Tools;

public class RevertChecker implements Runnable {

	static ConcurrentHashMap<Block, Block> revertQueue = new ConcurrentHashMap<Block, Block>();
	static ConcurrentHashMap<Block, Block> airRevertQueue = new ConcurrentHashMap<Block, Block>();
	private Future<ArrayList<Chunk>> returnFuture;
	// static ConcurrentHashMap<Block, Material> movedEarthQueue = new
	// ConcurrentHashMap<Block, Material>();

	static ConcurrentHashMap<Chunk, Chunk> chunks = new ConcurrentHashMap<Chunk, Chunk>();

	private Bending plugin;

	private static final boolean safeRevert = ConfigManager.safeRevert;

	private long time;

	public RevertChecker(Bending bending) {
		plugin = bending;
	}

	private class getOccupiedChunks implements Callable<ArrayList<Chunk>> {

		@Override
		public ArrayList<Chunk> call() throws Exception {
			ArrayList<Chunk> chunks = new ArrayList<Chunk>();
			Player[] players = plugin.getServer().getOnlinePlayers();

			for (Player player : players) {
				Chunk chunk = player.getLocation().getChunk();
				if (!chunks.contains(chunk))
					chunks.add(chunk);
			}

			return chunks;
		}

	}

	public void run() {
		if (returnFuture == null)
			returnFuture = plugin.getServer().getScheduler()
					.callSyncMethod(plugin, new getOccupiedChunks());

		time = System.currentTimeMillis();

		if (ConfigManager.reverseearthbending) {

			// ArrayList<Chunk> chunks = new ArrayList<Chunk>();
			// Player[] players = plugin.getServer().getOnlinePlayers();
			//
			// for (Player player : players) {
			// Chunk chunk = player.getLocation().getChunk();
			// if (!chunks.contains(chunk))
			// chunks.add(chunk);
			// }

			try {
				ArrayList<Chunk> chunks = returnFuture.get();

				for (Block block : Tools.movedearth.keySet()) {
					if (revertQueue.containsKey(block))
						continue;
					boolean remove = true;
					Information info = Tools.movedearth.get(block);
					if (time < info.getTime() + ConfigManager.revertchecktime
							|| (chunks.contains(block.getChunk()) && safeRevert)) {
						remove = false;
					}
					if (remove) {
						addToRevertQueue(block);
					}
				}

				for (Block block : Tools.tempair.keySet()) {
					if (airRevertQueue.containsKey(block))
						continue;
					boolean remove = true;
					Information info = Tools.tempair.get(block);
					if (time < info.getTime() + ConfigManager.revertchecktime
							|| (chunks.contains(block.getChunk()) && safeRevert)) {
						remove = false;
					}
					if (remove) {
						addToAirRevertQueue(block);
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

	private void addToAirRevertQueue(Block block) {
		if (!airRevertQueue.containsKey(block))
			airRevertQueue.put(block, block);

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
