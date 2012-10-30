package main;

import java.util.ArrayList;
import java.util.concurrent.ConcurrentHashMap;

import org.bukkit.Chunk;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

import tools.ConfigManager;
import tools.Information;
import tools.Tools;

public class RevertChecker implements Runnable {

	static ConcurrentHashMap<Block, Block> revertQueue = new ConcurrentHashMap<Block, Block>();
	// static ConcurrentHashMap<Block, Material> movedEarthQueue = new
	// ConcurrentHashMap<Block, Material>();

	private Bending plugin;

	private static final boolean safeRevert = ConfigManager.safeRevert;

	private long time;

	public RevertChecker(Bending bending) {
		plugin = bending;
	}

	public void run() {
		time = System.currentTimeMillis();

		if (ConfigManager.reverseearthbending) {

			ArrayList<Chunk> chunks = new ArrayList<Chunk>();

			for (Player player : plugin.getServer().getOnlinePlayers()) {
				Chunk chunk = player.getLocation().getChunk();
				if (!chunks.contains(chunk))
					chunks.add(chunk);
			}

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
