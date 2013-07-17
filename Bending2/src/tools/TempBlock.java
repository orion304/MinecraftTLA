package tools;

import java.util.ArrayList;
import java.util.concurrent.ConcurrentHashMap;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.BlockState;

public class TempBlock {

	public static ConcurrentHashMap<Integer, TempBlock> instances = new ConcurrentHashMap<Integer, TempBlock>();
	private static ArrayList<TempBlock> unused = new ArrayList<TempBlock>();
	private static int ID = Integer.MIN_VALUE;

	String cause = "";

	private int id;
	Block block;
	Material newtype;
	byte newdata;
	BlockState state;

	public static TempBlock makeNewTempBlock(Block block, Material newtype,
			byte newdata) {
		// if (instances.containsKey(block)) {
		// TempBlock tempblock = instances.get(block);
		// tempblock.update(newtype, newdata);
		// tempblock.cause = "Rewrite";
		// instances.replace(block, tempblock);
		// return tempblock;
		// }

		if (unused.isEmpty()) {
			// Tools.verbose("Created new temp block.");
			TempBlock tempblock = new TempBlock(block, newtype, newdata);
			tempblock.cause = "Brand new";
			return tempblock;
		} else {
			// Tools.verbose("Used obsolete temp block.");
			int size = unused.size();
			TempBlock tempblock = unused.get(size - 1);
			tempblock.renew(block, newtype, newdata);
			tempblock.cause = "Reused";
			unused.remove(size - 1);
			return tempblock;
		}
	}

	public TempBlock(Block block, Material newtype, byte newdata) {
		id = ID++;

		this.block = block;
		this.newdata = newdata;
		this.newtype = newtype;

		state = block.getState();

		if (state.getType() == Material.FIRE)
			state.setType(Material.AIR);

		block.setType(newtype);
		block.setData(newdata);

		ArrayList<TempBlock> blocks = get(block);
		if (!blocks.isEmpty()) {
			state = blocks.get(0).state;
		}

		instances.put(id, this);

		// if (instances.containsKey(id)) {
		// Tools.verbose("This shouldn't even be called!");
		// // TempBlock temp = instances.get(block);
		// // if (newtype != temp.newtype) {
		// // temp.block.setType(newtype);
		// // temp.newtype = newtype;
		// // }
		// // if (newdata != temp.newdata) {
		// // temp.block.setData(newdata);
		// // temp.newdata = newdata;
		// // }
		// // state = temp.state;
		// // instances.replace(block, temp);
		// } else {
		// state = block.getState();
		// block.setType(newtype);
		// block.setData(newdata);
		// instances.put(id, this);
		// }
		// if (state.getType() == Material.FIRE)
		// state.setType(Material.AIR);
	}

	private void renew(Block block, Material newtype, byte newdata) {
		this.block = block;
		this.newdata = newdata;
		this.newtype = newtype;
		state = block.getState();
		block.setType(newtype);
		block.setData(newdata);
		instances.put(id, this);
	}

	// private void update(Material newtype, byte newdata) {
	// this.newdata = newdata;
	// this.newtype = newtype;
	// block.setType(newtype);
	// block.setData(newdata);
	// // instances.replace(block, this);
	// }

	private void remove() {
		if (instances.containsKey(id))
			instances.remove(id);
		unused.add(this);
	}

	private void tempRevertBlock() {
		instances.remove(id);
		// if (!isTempBlock(block)) {
		state.update(true);
		// }
	}

	public void revertBlock() {
		// Tools.verbose(block.getType());
		// if (block.getType() == newtype
		// || (Tools.isWater(block) && (newtype == Material.WATER || newtype ==
		// Material.STATIONARY_WATER))) {
		// if (type == Material.WATER || type == Material.STATIONARY_WATER
		// || type == Material.AIR) {
		// if (Tools.adjacentToThreeOrMoreSources(block)) {
		// type = Material.WATER;
		// data = (byte) 0x0;
		// }
		// }
		// block.setType(type);
		// block.setData(data);
		// }
		remove();
		// if (!isTempBlock(block)) {
		state.update(true);
		// }

		// if (!instances.containsKey(block)) {
		// Tools.verbose("Something called a " + cause
		// + " TempBlock that should be dead!");
		// Exception e = new RuntimeException();
		// Tools.writeToLog(ExceptionUtils.getStackTrace(e));
		// e.printStackTrace();
		// return;
		// }
		// state.update(true);
		// instances.remove(block);
		// remove();
	}

	public static void revertBlock(Block block, Material defaulttype) {
		ArrayList<TempBlock> blocks = get(block);
		if (blocks.isEmpty()) {
			if ((defaulttype == Material.WATER
					|| defaulttype == Material.STATIONARY_WATER || defaulttype == Material.AIR)
					&& Tools.adjacentToThreeOrMoreSources(block)) {
				block.setType(Material.WATER);
				block.setData((byte) 0x0);
			} else {
				block.setType(defaulttype);
			}
		} else {
			for (TempBlock tblock : blocks) {
				tblock.tempRevertBlock();
			}
		}
		// if (instances.containsKey(block)) {
		// instances.get(block).revertBlock();
		// } else {
		// if ((defaulttype == Material.WATER
		// || defaulttype == Material.STATIONARY_WATER || defaulttype ==
		// Material.AIR)
		// && Tools.adjacentToThreeOrMoreSources(block)) {
		// block.setType(Material.WATER);
		// block.setData((byte) 0x0);
		// } else {
		// block.setType(defaulttype);
		// }
		// }
		// block.setType(defaulttype);
	}

	// public static void removeBlock(Block block) {
	// if (instances.containsKey(block)) {
	// instances.get(block).remove();
	// // instances.remove(block);
	// }
	// }

	public static boolean isTempBlock(Block block) {
		for (int id : instances.keySet()) {
			if (instances.get(id).block.equals(block))
				return true;
		}
		// if (instances.containsKey(block))
		// return true;
		return false;
	}

	public static boolean isTouchingTempBlock(Block block) {
		BlockFace[] faces = { BlockFace.NORTH, BlockFace.SOUTH, BlockFace.EAST,
				BlockFace.WEST, BlockFace.UP, BlockFace.DOWN };
		for (BlockFace face : faces) {
			if (isTempBlock(block.getRelative(face)))
				return true;
		}
		return false;
	}

	public static ArrayList<TempBlock> get(Block block) {
		// if (isTempBlock(block))
		// return instances.get(block);
		ArrayList<TempBlock> blocks = new ArrayList<TempBlock>();
		for (int id : instances.keySet()) {
			TempBlock tblock = instances.get(id);
			if (tblock.block.equals(block)) {
				blocks.add(tblock);
			}
		}
		return blocks;
		// return null;
	}

	public Location getLocation() {
		return block.getLocation();
	}

	public Block getBlock() {
		return block;
	}

	public static void removeAll() {
		for (int id : instances.keySet()) {
			instances.get(id).revertBlock();
			// revertBlock(block, Material.AIR);
		}

	}

	public void setType(Material material) {
		setType(material, newdata);
	}

	public void setType(Material material, byte data) {
		newtype = material;
		newdata = data;
		block.setType(material);
		block.setData(data);
	}

}
