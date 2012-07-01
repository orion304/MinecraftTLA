package firebending;

import java.util.concurrent.ConcurrentHashMap;

import org.bukkit.Effect;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import tools.Tools;

public class FireJet {

	public static ConcurrentHashMap<Player, Block> instances = new ConcurrentHashMap<Player, Block>();
	private static final double factor = 1.6;
	public static final long duration = 2000;
	private static Player player;
	public static ConcurrentHashMap<Player, Long> timers = new ConcurrentHashMap<Player, Long>();

	public FireJet(Player player) {
		if (timers.containsKey(player)) {
			if (System.currentTimeMillis() < timers.get(player) + duration) {
				return;
			}
		}
		Block block = player.getLocation().getBlock();
		if (FireStream.isIgnitable(block)) {
			player.setVelocity(player.getEyeLocation().getDirection().clone()
					.normalize().multiply(factor));
			block.setType(Material.FIRE);
			instances.put(player, block);
			FireJet.player = player;
			timers.put(player, System.currentTimeMillis());
		}

	}

	public static boolean checkTemporaryImmunity(Player player) {
		if (instances.containsKey(player)) {
			return true;
		}
		return false;
	}

	public static void checkBlocks() {
		for (Player player : instances.keySet()) {
			if (instances.get(player).getType() != Material.FIRE)
				instances.remove(player);
		}

	}
	
    public static void progress() {
    	    if (player != null){
    			if (Tools.isWater(player.getLocation().getBlock())){
    				timers.remove(player);
    			}
    			if (timers.containsKey(player)) {
    				if (System.currentTimeMillis() < timers.get(player) + duration) {
    					Vector vec = player.getLocation().getDirection().clone().normalize().multiply(factor - 1D);
    					player.setVelocity(vec);
    					player.getLocation().getWorld().playEffect(player.getLocation(), Effect.MOBSPAWNER_FLAMES, 1);
    					}
    				}
    			}
    	    }
    }


