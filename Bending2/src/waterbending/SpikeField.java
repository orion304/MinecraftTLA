package waterbending;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import tools.Tools;

public class SpikeField {
	
	private static int radius = 4;
	private static int numofspikes = ((radius * 2) * (radius * 2)) / 16;
	private static long cooldown = 20000;
	
	Random ran = new Random();
	private int damage = 2;
	private Vector thrown = new Vector(0, 1, 0);
	
	public SpikeField(Player p){
		//Tools.verbose("Trying to create IceField" + numofspikes);
		int locX = p.getLocation().getBlockX();
		int locY = p.getLocation().getBlockY();
		int locZ = p.getLocation().getBlockZ();
		List<Block> iceblocks = new ArrayList<Block>();
		for (int x = -(radius - 1); x <= (radius - 1); x++){
			for (int z = -(radius - 1); z <= (radius - 1); z++){
				for (int y = -1; y <= 3; y++){
				Block testblock = p.getWorld().getBlockAt(locX + x, locY + y, locZ + z);
				if (testblock.getType() == Material.ICE
						&& testblock.getRelative(BlockFace.UP).getType() == Material.AIR
						&& !(testblock.getX() == p.getEyeLocation().getBlock().getX() &&
								testblock.getZ() == p.getEyeLocation().getBlock().getZ())){
					iceblocks.add(testblock);
					///Tools.verbose("X: " + testblock.getLocation().getX() + " Y: " + testblock.getLocation().getY() + " Z: " + testblock.getLocation().getZ());
				}
				}
			}
		}
		for (int i = 0; i < numofspikes; i++){
			if (iceblocks.isEmpty())
				return;
			Block targetblock = iceblocks.get(ran.nextInt(iceblocks.size()));
			//Tools.verbose("X: " + targetblock.getLocation().getX() + " Y: " + targetblock.getLocation().getY() + " Z: " + targetblock.getLocation().getZ());
			if (targetblock.getRelative(BlockFace.UP).getType() != Material.ICE){
				new IceSpike(p, targetblock.getLocation(), damage, thrown, cooldown);
				iceblocks.remove(targetblock);
			}
		}
	}
}
//			for (int i = 0; i < (numofspikes / 2); i++){
//				int blockX = ran.nextInt(radius) + 1;
//				int blockZ = ran.nextInt((radius * 2) + 1) - radius;
//				Block b = p.getLocation().getWorld().getBlockAt(locX + blockX, locY - 1, locZ - blockZ);
//				if (b.getType() == Material.ICE){
//					new IceSpike(p, b.getLocation(), 2);
//				} else {
//					for (i = 0; i <= heigth; i++) {
//						b = b.getRelative(BlockFace.DOWN);
//						if (b.getType() == Material.ICE){
//							new IceSpike(p, b.getLocation(), 2);
//							break;
//						}
//					}
//				}
//				
//			}
//			for (int i = 0; i < (numofspikes / 2); i++){
//				int blockX = ran.nextInt(radius) + 1;
//				int blockZ = ran.nextInt((radius * 2) + 1) - radius;
//				Block b = p.getLocation().getWorld().getBlockAt(locX - blockX, locY - 1, locZ - blockZ);
//				if (b.getType() == Material.ICE) {
//					new IceSpike(p, b.getLocation(), 2);
//			} else {
//				for (i = 0; i <= heigth; i++) {
//					b = b.getRelative(BlockFace.DOWN);
//					if (b.getType() == Material.ICE){
//						new IceSpike(p, b.getLocation(), 2);
//						break;
//					}
//				}
//			}
//		}
//	}
//}
