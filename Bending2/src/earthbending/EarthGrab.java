package earthbending;

import java.util.ArrayList;

import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import tools.ConfigManager;
import tools.Tools;

public class EarthGrab {

	private static double range = ConfigManager.earthGrabRange;

	public EarthGrab(Player player) {
		// Tools.verbose("initiating");
		Location origin = player.getEyeLocation();
		Vector direction = origin.getDirection();
		double lowestdistance = range + 1;
		Entity closestentity = null;
		for (Entity entity : Tools.getEntitiesAroundPoint(origin, range)) {
			if (Tools.getDistanceFromLine(direction, origin,
					entity.getLocation()) <= 1
					&& (entity instanceof LivingEntity)
					&& (entity.getEntityId() != player.getEntityId())) {
				double distance = origin.distance(entity.getLocation());
				if (distance < lowestdistance) {
					closestentity = entity;
					lowestdistance = distance;
				}
			}
		}

		if (closestentity != null) {
			// Tools.verbose("grabbing");
			ArrayList<Block> blocks = new ArrayList<Block>();
			Location location = closestentity.getLocation();
			Location loc1 = location.clone();
			Location loc2 = location.clone();
			Location testloc, testloc2;
			double factor = 3;
			double factor2 = 4;
			int height1 = 3;
			int height2 = 2;
			for (double angle = 0; angle <= 360; angle += 20) {
				testloc = loc1.clone().add(
						factor * Math.cos(Math.toRadians(angle)), 1,
						factor * Math.sin(Math.toRadians(angle)));
				testloc2 = loc2.clone().add(
						factor2 * Math.cos(Math.toRadians(angle)), 1,
						factor2 * Math.sin(Math.toRadians(angle)));
				for (int y = 0; y < EarthColumn.standardheight - height1; y++) {
					testloc = testloc.clone().add(0, -1, 0);
					if (Tools.isEarthbendable(testloc.getBlock())) {
						if (!blocks.contains(testloc.getBlock())) {
							new EarthColumn(testloc, height1 + y - 1);
						}
						blocks.add(testloc.getBlock());
						break;
					}
				}
				for (int y = 0; y < EarthColumn.standardheight - height2; y++) {
					testloc2 = testloc2.clone().add(0, -1, 0);
					if (Tools.isEarthbendable(testloc2.getBlock())) {
						if (!blocks.contains(testloc2.getBlock())) {
							new EarthColumn(testloc2, height2 + y - 1);
						}
						blocks.add(testloc2.getBlock());
						break;
					}
				}
			}
		}
	}
}
