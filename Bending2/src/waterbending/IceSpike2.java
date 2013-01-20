package waterbending;

import java.util.concurrent.ConcurrentHashMap;

import org.bukkit.block.Block;
import org.bukkit.entity.Player;

import tools.Tools;

public class IceSpike2 {

	private static ConcurrentHashMap<Integer, IceSpike2> instances = new ConcurrentHashMap<Integer, IceSpike2>();

	private static double defaultrange = 20;
	private static int ID = Integer.MIN_VALUE;

	private Player player;
	private int id;
	private double range;
	private boolean plantbending = false;
	private Block sourceblock;
	private boolean prepared = false;

	public IceSpike2(Player player) {
		if (Tools.canPlantbend(player))
			plantbending = true;
		Block sourceblock = Tools.getWaterSourceBlock(player, range,
				plantbending);

		if (sourceblock == null) {

		} else {
			prepare(sourceblock);
		}

	}

	private void prepare(Block block) {
		sourceblock = block;
		prepared = true;
		createInstance();
	}

	private void createInstance() {
		id = ID++;
		instances.put(id, this);
		if (ID >= Integer.MAX_VALUE) {
			ID = Integer.MIN_VALUE;
		}
	}
}
