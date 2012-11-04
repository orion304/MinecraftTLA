package waterbending;

import java.util.ArrayList;
import java.util.concurrent.ConcurrentHashMap;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import tools.Abilities;
import tools.TempBlock;
import tools.Tools;

public class Torrent {

	private static ConcurrentHashMap<Player, Torrent> instances = new ConcurrentHashMap<Player, Torrent>();

	static long interval = 30;
	private static int defaultrange = 20;
	private static int selectrange = 7;
	private static double radius = 3;

	private static final byte full = 0x0;

	private Block sourceblock;
	private TempBlock source;
	private Location location;
	private Player player;
	private long time;
	private double angle = 20;

	private ArrayList<TempBlock> blocks = new ArrayList<TempBlock>();

	private boolean sourceselected = false;
	private boolean settingup = false;
	private boolean forming = false;
	private boolean formed = false;
	private boolean launch = false;
	private boolean freeze = false;

	public Torrent(Player player) {
		if (instances.containsKey(player)) {
			instances.get(player).freeze();
			return;
		}
		this.player = player;
		time = System.currentTimeMillis();
		sourceblock = Tools.getWaterSourceBlock(player, selectrange,
				Tools.canPlantbend(player));
		if (sourceblock != null) {
			sourceselected = true;
			instances.put(player, this);
		}
	}

	private void freeze() {
		freeze = true;
	}

	private void progress() {
		if (player.isDead() || !player.isOnline()) {
			remove();
			return;
		}

		if (!Tools.canBend(player, Abilities.Torrent)) {
			remove();
			return;
		}

		if (System.currentTimeMillis() > time + interval) {
			time = System.currentTimeMillis();

			if (sourceselected) {
				if (!sourceblock.getWorld().equals(player.getWorld())) {
					remove();
					return;
				}

				if (sourceblock.getLocation().distance(player.getLocation()) > selectrange) {
					remove();
					return;
				}

				if (player.isSneaking()) {
					sourceselected = false;
					settingup = true;
					if (Tools.isPlant(sourceblock)) {
						new Plantbending(sourceblock);
						sourceblock.setType(Material.AIR);
					} else if (!Tools.adjacentToThreeOrMoreSources(sourceblock)) {
						sourceblock.setType(Material.AIR);
					}
					source = new TempBlock(sourceblock, Material.WATER, full);
					location = sourceblock.getLocation();
				} else {
					Tools.playFocusWaterEffect(sourceblock);
					return;
				}
			}

			if (settingup) {
				if (!player.isSneaking()) {
					remove();
					returnWater(source.getLocation());
					return;
				}
				Location eyeloc = player.getEyeLocation();
				double startangle = player.getEyeLocation().getDirection()
						.angle(new Vector(1, 0, 0));
				double dx = radius * Math.cos(startangle);
				double dy = 0;
				double dz = radius * Math.sin(startangle);
				Location setup = eyeloc.clone().add(dx, dy, dz);

				if (!location.getWorld().equals(player.getWorld())) {
					remove();
					return;
				}

				if (location.distance(setup) > defaultrange) {
					remove();
					return;
				}

				if (location.getBlockY() > setup.getBlockY()) {
					Vector direction = new Vector(0, -1, 0);
					location = location.clone().add(direction);
				} else if (location.getBlockY() < setup.getBlockY()) {
					Vector direction = new Vector(0, 1, 0);
					location = location.clone().add(direction);
				} else {
					Vector direction = Tools.getDirection(location, setup)
							.normalize();
					location = location.clone().add(direction);
				}

				if (location.distance(setup) <= 1) {
					settingup = false;
					source.revertBlock();
					source = null;
					forming = true;
				} else {
					if (!location.getBlock().equals(
							source.getLocation().getBlock())) {
						source.revertBlock();
						Block block = location.getBlock();
						if (!Tools.isTransparentToEarthbending(player, block)
								|| block.isLiquid()) {
							remove();
							return;
						}
						source = new TempBlock(location.getBlock(),
								Material.WATER, full);
					}
				}
			}

			if (forming && !player.isSneaking()) {
				remove();
				returnWater(player.getEyeLocation().add(radius, 0, 0));
				return;
			}

			if (forming || formed) {
				if (angle < 360) {
					angle += 20;
				} else {
					forming = false;
					formed = true;
				}
				formRing();
				if (blocks.isEmpty()) {
					remove();
					return;
				}
			}

			if (formed && !player.isSneaking()) {
				new TorrentBurst(player, radius);
				remove();
				return;
			}

		}

	}

	private void formRing() {
		clearRing();
		double startangle = Math.toDegrees(player.getEyeLocation()
				.getDirection().angle(new Vector(1, 0, 0)));
		Location loc = player.getEyeLocation();
		ArrayList<Block> doneblocks = new ArrayList<Block>();
		for (double theta = startangle; theta < angle + startangle; theta += 20) {
			double phi = Math.toRadians(theta);
			double dx = Math.cos(phi) * radius;
			double dy = 0;
			double dz = Math.sin(phi) * radius;
			Location blockloc = loc.clone().add(dx, dy, dz);
			Block block = blockloc.getBlock();
			if (!doneblocks.contains(block)) {
				if (Tools.isTransparentToEarthbending(player, block)
						&& !block.isLiquid()) {
					blocks.add(new TempBlock(block, Material.WATER, full));
					doneblocks.add(block);
				}
			}
		}
	}

	private void clearRing() {
		for (TempBlock block : blocks) {
			block.revertBlock();
		}
		blocks.clear();
	}

	private void remove() {
		clearRing();
		if (source != null)
			source.revertBlock();
		instances.remove(player);
	}

	private void returnWater(Location location) {
		new WaterReturn(player, location.getBlock());
	}

	public static void use(Player player) {

	}

	public static void progressAll() {
		for (Player player : instances.keySet())
			instances.get(player).progress();
	}

	public static void removeAll() {
		for (Player player : instances.keySet())
			instances.get(player).remove();
	}

	public static boolean wasBrokenFor(Player player, Block block) {
		if (instances.containsKey(player)) {
			Torrent torrent = instances.get(player);
			if (torrent.sourceblock == null)
				return false;
			if (torrent.sourceblock.equals(block))
				return true;
		}
		return false;
	}

	public static String getDescription() {
		return "Blargablarg";
	}

}
