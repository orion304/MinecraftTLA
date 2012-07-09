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
		if (instances.containsKey(block)) {
			revertBlock(block, Material.AIR);
		}
		this.block = block;
		type = block.getType();
		data = block.getData();
		this.newdata = newdata;
		this.newtype = newtype;
		block.setType(newtype);
		block.setData(newdata);
		instances.put(block, this);
	}

	public void revertBlock() {
		block.setType(type);
		block.setData(data);
		instances.remove(block);
	}

	public static void revertBlock(Block block, Material defaulttype) {
		if (instances.containsKey(block)) {
			instances.get(block).revertBlock();
		} else {
			block.setType(defaulttype);
		}
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
