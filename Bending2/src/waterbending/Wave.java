package waterbending;

import java.util.ArrayList;
import java.util.concurrent.ConcurrentHashMap;

import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import tools.Abilities;
import tools.AvatarState;
import tools.BendingPlayer;
import tools.ConfigManager;
import tools.TempBlock;
import tools.Tools;
import firebending.FireBlast;

public class Wave {

	public static ConcurrentHashMap<Integer, Wave> instances = new ConcurrentHashMap<Integer, Wave>();

	private static final long interval = 30;

	// public static ConcurrentHashMap<Block, Block> affectedblocks = new
	// ConcurrentHashMap<Block, Block>();

	private static final byte full = 0x0;
	// private static final byte half = 0x4;
	private static final double defaultmaxradius = ConfigManager.waveRadius;
	private static final double defaultfactor = ConfigManager.waveHorizontalPush;
	private static final double upfactor = ConfigManager.waveVerticalPush;
	private static final double maxfreezeradius = 7;

	static double defaultrange = 20;
	// private static int damage = 5;
	// private static double speed = 1.5;

	Player player;
	private Location location = null;
	private Block sourceblock = null;
	boolean progressing = false;
	private Location targetdestination = null;
	private Vector targetdirection = null;
	private ConcurrentHashMap<Block, Block> wave = new ConcurrentHashMap<Block, Block>();
	private ConcurrentHashMap<Block, Block> frozenblocks = new ConcurrentHashMap<Block, Block>();
	private double radius = 1;
	private long time;
	private double maxradius = defaultmaxradius;
	private boolean freeze = false;
	private boolean activatefreeze = false;
	private Location frozenlocation;
	double range = defaultrange;
	private double factor = defaultfactor;
	boolean canhitself = true;

	public Wave(Player player) {
		this.player = player;

		if (instances.containsKey(player.getEntityId())) {
			if (instances.get(player.getEntityId()).progressing
					&& !instances.get(player.getEntityId()).freeze) {
				instances.get(player.getEntityId()).freeze = true;
				return;
			}
		}

		if (AvatarState.isAvatarState(player)) {
			maxradius = AvatarState.getValue(maxradius);
		}
		maxradius = Tools
				.waterbendingNightAugment(maxradius, player.getWorld());
		if (prepare()) {
			if (instances.containsKey(player.getEntityId())) {
				instances.get(player.getEntityId()).cancel();
			}
			instances.put(player.getEntityId(), this);
			time = System.currentTimeMillis();
		}

	}

	public boolean prepare() {
		cancelPrevious();
		// Block block = player.getTargetBlock(null, (int) range);
		Block block = Tools.getWaterSourceBlock(player, range,
				Tools.canPlantbend(player));
		if (block != null) {
			sourceblock = block;
			focusBlock();
			return true;
		}
		return false;
	}

	private void cancelPrevious() {
		if (instances.containsKey(player.getEntityId())) {
			Wave old = instances.get(player.getEntityId());
			if (old.progressing) {
				old.breakBlock();
				old.thaw();
				old.returnWater();
			} else {
				old.cancel();
			}
		}
	}

	public void cancel() {
		unfocusBlock();
	}

	private void focusBlock() {
		location = sourceblock.getLocation();
	}

	private void unfocusBlock() {
		instances.remove(player.getEntityId());
	}

	public void moveWater() {
		BendingPlayer bPlayer = BendingPlayer.getBendingPlayer(player);

		if (bPlayer.isOnCooldown(Abilities.Surge))
			return;

		bPlayer.cooldown(Abilities.Surge);
		if (sourceblock != null) {
			if (sourceblock.getWorld() != player.getWorld()) {
				return;
			}
			range = Tools.waterbendingNightAugment(range, player.getWorld());
			if (AvatarState.isAvatarState(player))
				factor = AvatarState.getValue(factor);
			Entity target = Tools.getTargettedEntity(player, range);
			if (target == null) {
				targetdestination = player.getTargetBlock(
						Tools.getTransparentEarthbending(), (int) range)
						.getLocation();
			} else {
				targetdestination = ((LivingEntity) target).getEyeLocation();
			}
			if (targetdestination.distance(location) <= 1) {
				progressing = false;
				targetdestination = null;
			} else {
				progressing = true;
				targetdirection = getDirection(sourceblock.getLocation(),
						targetdestination).normalize();
				targetdestination = location.clone().add(
						targetdirection.clone().multiply(range));
				if (Tools.isPlant(sourceblock))
					new Plantbending(sourceblock);
				if (!Tools.adjacentToThreeOrMoreSources(sourceblock)) {
					sourceblock.setType(Material.AIR);
				}
				addWater(sourceblock);

			}

		}
	}

	private Vector getDirection(Location location, Location destination) {
		double x1, y1, z1;
		double x0, y0, z0;

		x1 = destination.getX();
		y1 = destination.getY();
		z1 = destination.getZ();

		x0 = location.getX();
		y0 = location.getY();
		z0 = location.getZ();

		return new Vector(x1 - x0, y1 - y0, z1 - z0);

	}

	public boolean progress() {
		if (player.isDead() || !player.isOnline()
				|| !Tools.canBend(player, Abilities.Surge)) {
			breakBlock();
			thaw();
			// instances.remove(player.getEntityId());
			return false;
		}
		if (System.currentTimeMillis() - time >= interval) {
			time = System.currentTimeMillis();

			if (!progressing
					&& Tools.getBendingAbility(player) != Abilities.Surge) {
				unfocusBlock();
				return false;
			}

			if (!progressing) {
				sourceblock.getWorld().playEffect(location, Effect.SMOKE, 4,
						(int) range);
				return false;
			}

			if (location.getWorld() != player.getWorld()) {
				thaw();
				breakBlock();
				return false;
			}

			if (activatefreeze) {
				if (location.distance(player.getLocation()) > range) {
					progressing = false;
					thaw();
					breakBlock();
					return false;
				}
				if (!Tools.hasAbility(player, Abilities.PhaseChange)
						&& Tools.getBendingAbility(player) != Abilities.Surge) {
					progressing = false;
					thaw();
					breakBlock();
					returnWater();
					return false;
				}
				if (!Tools.canBend(player, Abilities.Surge)) {
					progressing = false;
					thaw();
					breakBlock();
					returnWater();
					return false;
				}

			} else {

				Vector direction = targetdirection;

				location = location.clone().add(direction);
				Block blockl = location.getBlock();

				ArrayList<Block> blocks = new ArrayList<Block>();

				if (!Tools.isRegionProtectedFromBuild(player, Abilities.Surge,
						location)
						&& (((blockl.getType() == Material.AIR
								|| blockl.getType() == Material.FIRE
								|| Tools.isPlant(blockl)
								|| Tools.isWater(blockl) || Tools
									.isWaterbendable(blockl, player))) && blockl
								.getType() != Material.LEAVES)) {

					for (double i = 0; i <= radius; i += .5) {
						for (double angle = 0; angle < 360; angle += 10) {
							Vector vec = Tools.getOrthogonalVector(
									targetdirection, angle, i);
							Block block = location.clone().add(vec).getBlock();
							if (!blocks.contains(block)
									&& (block.getType() == Material.AIR || block
											.getType() == Material.FIRE)
									|| Tools.isWaterbendable(block, player)) {
								blocks.add(block);
								FireBlast.removeFireBlastsAroundPoint(
										block.getLocation(), 2);
							}
							// if (!blocks.contains(block)
							// && (Tools.isPlant(block) && block.getType() !=
							// Material.LEAVES)) {
							// blocks.add(block);
							// block.breakNaturally();
							// }
						}
					}
				}

				for (Block block : wave.keySet()) {
					if (!blocks.contains(block))
						finalRemoveWater(block);
				}

				for (Block block : blocks) {
					if (!wave.containsKey(block))
						addWater(block);
				}

				if (wave.isEmpty()) {
					// blockl.setType(Material.GLOWSTONE);
					breakBlock();
					progressing = false;
					return false;
				}

				for (Entity entity : Tools.getEntitiesAroundPoint(location,
						2 * radius)) {

					boolean knockback = false;
					for (Block block : wave.keySet()) {
						if (entity.getLocation().distance(block.getLocation()) <= 2) {
							if (entity instanceof LivingEntity
									&& freeze
									&& entity.getEntityId() != player
											.getEntityId()) {
								activatefreeze = true;
								frozenlocation = entity.getLocation();
								freeze();
								break;
							}
							if (entity.getEntityId() != player.getEntityId()
									|| canhitself)
								knockback = true;
						}
					}
					if (knockback) {
						Vector dir = direction.clone();
						dir.setY(dir.getY() * upfactor);
						entity.setVelocity(entity
								.getVelocity()
								.clone()
								.add(dir.clone().multiply(
										Tools.waterbendingNightAugment(factor,
												player.getWorld()))));
						entity.setFallDistance(0);
						if (entity.getFireTicks() > 0)
							entity.getWorld().playEffect(entity.getLocation(),
									Effect.EXTINGUISH, 0);
						entity.setFireTicks(0);
					}

				}

				if (!progressing) {
					breakBlock();
					return false;
				}

				if (location.distance(targetdestination) < 1) {
					progressing = false;
					breakBlock();
					returnWater();
					return false;
				}

				if (radius < maxradius)
					radius += .5;

				return true;
			}
		}

		return false;

	}

	private void breakBlock() {
		for (Block block : wave.keySet()) {
			finalRemoveWater(block);
		}
		instances.remove(player.getEntityId());
	}

	private void finalRemoveWater(Block block) {
		if (wave.containsKey(block)) {
			// block.setType(Material.WATER);
			// block.setData(half);
			// if (!Tools.adjacentToThreeOrMoreSources(block) || radius > 1) {
			// block.setType(Material.AIR);
			// }
			TempBlock.revertBlock(block, Material.AIR);
			wave.remove(block);
		}
	}

	private void addWater(Block block) {
		if (Tools.isRegionProtectedFromBuild(player, Abilities.Surge,
				block.getLocation()))
			return;
		if (!TempBlock.isTempBlock(block)) {
			new TempBlock(block, Material.WATER, full);
			// new TempBlock(block, Material.ICE, (byte) 0);
			wave.put(block, block);
		}
		// block.setType(Material.WATER);
		// block.setData(full);
		// wave.put(block, block);
	}

	private void clearWave() {
		for (Block block : wave.keySet()) {
			TempBlock.revertBlock(block, Material.AIR);
		}
		wave.clear();
	}

	public static void moveWater(Player player) {
		if (instances.containsKey(player.getEntityId())) {
			instances.get(player.getEntityId()).moveWater();
		}
	}

	public static boolean progress(int ID) {
		return instances.get(ID).progress();
	}

	public static boolean isBlockWave(Block block) {
		for (int ID : instances.keySet()) {
			if (instances.get(ID).wave.containsKey(block))
				return true;
		}
		return false;
	}

	public static void launch(Player player) {
		moveWater(player);
	}

	public static void removeAll() {
		for (int id : instances.keySet()) {
			for (Block block : instances.get(id).wave.keySet()) {
				block.setType(Material.AIR);
				instances.get(id).wave.remove(block);
			}
			for (Block block : instances.get(id).frozenblocks.keySet()) {
				block.setType(Material.AIR);
				instances.get(id).frozenblocks.remove(block);
			}
		}
	}

	private void freeze() {
		clearWave();

		double freezeradius = radius;
		if (freezeradius > maxfreezeradius) {
			freezeradius = maxfreezeradius;
		}

		for (Block block : Tools.getBlocksAroundPoint(frozenlocation,
				freezeradius)) {
			if (Tools.isRegionProtectedFromBuild(player, Abilities.Surge,
					block.getLocation())
					|| Tools.isRegionProtectedFromBuild(player,
							Abilities.PhaseChange, block.getLocation()))
				continue;
			if (TempBlock.isTempBlock(block))
				continue;
			if (block.getType() == Material.AIR
					|| block.getType() == Material.SNOW) {
				// block.setType(Material.ICE);
				new TempBlock(block, Material.ICE, (byte) 0);
				frozenblocks.put(block, block);
			}
			if (Tools.isWater(block)) {
				FreezeMelt.freeze(player, block);
			}
			if (Tools.isPlant(block) && block.getType() != Material.LEAVES) {
				block.breakNaturally();
				// block.setType(Material.ICE);
				new TempBlock(block, Material.ICE, (byte) 0);
				frozenblocks.put(block, block);
			}
		}
	}

	private void thaw() {
		for (Block block : frozenblocks.keySet()) {
			// if (block.getType() == Material.ICE) {
			// // block.setType(Material.WATER);
			// // block.setData((byte) 0x7);
			// block.setType(Material.AIR);
			// }
			TempBlock.revertBlock(block, Material.AIR);
			frozenblocks.remove(block);
		}
	}

	public static void thaw(Block block) {
		for (int id : instances.keySet()) {
			if (instances.get(id).frozenblocks.containsKey(block)) {
				// if (block.getType() == Material.ICE) {
				// // block.setType(Material.WATER);
				// // block.setData((byte) 0x7);
				// block.setType(Material.AIR);
				// }
				TempBlock.revertBlock(block, Material.AIR);
				instances.get(id).frozenblocks.remove(block);
			}
		}
	}

	public static boolean canThaw(Block block) {
		for (int id : instances.keySet()) {
			if (instances.get(id).frozenblocks.containsKey(block)) {
				return false;
			}
		}
		return true;
	}

	void returnWater() {
		if (location != null) {
			new WaterReturn(player, location.getBlock());
		}
	}

	public static String getDescription() {
		return "To use, place your cursor over a waterbendable object "
				+ "(water, ice, plants if you have plantbending) and tap sneak "
				+ "(default: shift). Smoke will appear where you've selected, "
				+ "indicating the origin of your ability. After you have selected an origin, "
				+ "simply left-click in any direction and you will see your water spout off in that "
				+ "direction and form a large wave, knocking back all within its path. "
				+ "If you look towards a creature when you use this ability, it will target that creature. "
				+ "Additionally, tapping sneak while the wave is en route will cause that wave to encase the "
				+ "first target it hits in ice.";
	}

}
