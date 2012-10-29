package earthbending;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import tools.AvatarState;
import tools.ConfigManager;
import tools.Tools;

public class EarthWall {

	private static final int range = ConfigManager.earthWallRange;
	private static final int defaultheight = ConfigManager.earthWallHeight;
	private static final int defaulthalfwidth = ConfigManager.earthWallWidth / 2;

	private int height = defaultheight;
	private int halfwidth = defaulthalfwidth;

	public EarthWall(Player player) {
		if (AvatarState.isAvatarState(player)) {
			height = (int) (2. / 5. * (double) AvatarState.getValue(height));
			halfwidth = AvatarState.getValue(halfwidth);
		}

		Vector direction = player.getEyeLocation().getDirection().normalize();

		double ox, oy, oz;
		ox = -direction.getZ();
		oy = 0;
		oz = direction.getX();

		Vector orth = new Vector(ox, oy, oz);
		orth = orth.normalize();

		Location origin = player.getTargetBlock(
				Tools.getTransparentEarthbending(), range).getLocation();
		World world = origin.getWorld();

		for (int i = -halfwidth; i <= halfwidth; i++) {
			Block block = world.getBlockAt(origin.clone().add(
					orth.clone().multiply((double) i)));
			// if (block.getType() == Material.AIR || block.isLiquid()) {
			if (Tools.isTransparentToEarthbending(player, block)) {
				for (int j = 1; j < height; j++) {
					block = block.getRelative(BlockFace.DOWN);
					if (Tools.isEarthbendable(player, block)) {
						new EarthColumn(player, block.getLocation(), height);
						// } else if (block.getType() != Material.AIR
						// && !block.isLiquid()) {
					} else if (!Tools
							.isTransparentToEarthbending(player, block)) {
						break;
					}
				}
			} else if (Tools.isEarthbendable(player,
					block.getRelative(BlockFace.UP))) {
				for (int j = 1; j < height; j++) {
					block = block.getRelative(BlockFace.UP);
					// if (block.getType() == Material.AIR || block.isLiquid())
					// {
					if (Tools.isTransparentToEarthbending(player, block)) {
						new EarthColumn(player, block.getRelative(
								BlockFace.DOWN).getLocation(), height);
					} else if (!Tools.isEarthbendable(player, block)) {
						break;
					}
				}
			} else if (Tools.isEarthbendable(player, block)) {
				new EarthColumn(player, block.getLocation(), height);
			}
		}
	}

}
