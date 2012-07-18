package tools;

import java.util.concurrent.ConcurrentHashMap;

import org.bukkit.Material;
import org.bukkit.block.Block;

public class TempBlock {

	public static ConcurrentHashMap<Block, TempBlock> instances = new ConcurrentHashMap<Block, TempBlock>();

	Block block;
	Material type, newtype;
	byte data, newdata;

	public TempBlock(Block block, Material newtype, byte newdata) {
		this.block = block;
		this.newdata = newdata;
		this.newtype = newtype;
		if (instances.containsKey(block)) {
			TempBlock temp = instances.get(block);
			if (newtype != temp.newtype) {
				temp.block.setType(newtype);
				temp.newtype = newtype;
			}
			if (newdata != temp.newdata) {
				temp.block.setData(newdata);
				temp.newdata = newdata;
			}
			type = temp.type;
			data = temp.data;
			instances.replace(block, temp);
		} else {
			type = block.getType();
			data = block.getData();
			block.setType(newtype);
			block.setData(newdata);
			instances.put(block, this);
		}
	}

	public void revertBlock() {
		// Tools.verbose(block.getType());
		if (block.getType() == newtype
				|| (Tools.isWater(block) && (newtype == Material.WATER || newtype == Material.STATIONARY_WATER))) {
			if (type == Material.WATER || type == Material.STATIONARY_WATER
					|| type == Material.AIR) {
				if (Tools.adjacentToThreeOrMoreSources(block)) {
					type = Material.WATER;
					data = (byte) 0x0;
				}
			}
			block.setType(type);
			block.setData(data);
		}
		instances.remove(block);
	}

	public static void revertBlock(Block block, Material defaulttype) {
		if (instances.containsKey(block)) {
			instances.get(block).revertBlock();
		} else {
			if ((defaulttype == Material.WATER
					|| defaulttype == Material.STATIONARY_WATER || defaulttype == Material.AIR)
					&& Tools.adjacentToThreeOrMoreSources(block)) {
				block.setType(Material.WATER);
				block.setData((byte) 0x0);
			} else {
				block.setType(defaulttype);
			}
		}
		// block.setType(defaulttype);
	}

	public static void removeBlock(Block block) {
		if (instances.containsKey(block)) {
			instances.remove(block);
		}
	}

	public static boolean isTempBlock(Block block) {
		if (instances.containsKey(block))
			return true;
		return false;
	}

}
