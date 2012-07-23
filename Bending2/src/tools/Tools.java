package tools;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import main.Bending;
import main.BendingManager;
import main.BendingPlayers;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.World.Environment;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;
import org.bukkit.util.Vector;

import waterbending.FreezeMelt;
import waterbending.WalkOnWater;
import waterbending.WaterManipulation;
import waterbending.WaterSpout;
import waterbending.WaterWall;
import waterbending.Wave;
import airbending.AirBlast;
import airbending.AirBubble;
import airbending.AirScooter;

import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import com.sk89q.worldguard.protection.flags.DefaultFlag;

import earthbending.Catapult;
import earthbending.CompactColumn;
import earthbending.EarthBlast;
import earthbending.EarthColumn;
import earthbending.EarthPassive;
import firebending.FireStream;

public class Tools {

	private static BendingPlayers config;

	private static final Map<String, ChatColor> colors;

	private static Integer[] transparentEarthbending = { 0, 6, 8, 9, 10, 11,
			30, 31, 32, 37, 38, 39, 40, 50, 51, 59, 78, 83, 106 };

	private static Integer[] nonOpaque = { 0, 6, 8, 9, 10, 11, 27, 28, 30, 31,
			32, 37, 38, 39, 40, 50, 51, 55, 59, 66, 68, 69, 70, 72, 75, 76, 77,
			78, 83, 90, 93, 94, 104, 105, 106, 111, 115, 119, 127, 131, 132 };

	private static Integer[] plantIds = { 6, 18, 31, 32, 37, 38, 39, 40, 59,
			81, 83, 86, 99, 100, 103, 104, 105, 106, 111 };

	public static final long timeinterval = ConfigManager.globalCooldown;

	public static ConcurrentHashMap<Block, Information> movedearth = new ConcurrentHashMap<Block, Information>();
	public static ConcurrentHashMap<Block, Block> tempearthblocks = new ConcurrentHashMap<Block, Block>();
	public static ConcurrentHashMap<Player, Long> blockedchis = new ConcurrentHashMap<Player, Long>();

	public Tools(BendingPlayers instance) {
		config = instance;
	}

	public static HashSet<Byte> getTransparentEarthbending() {
		HashSet<Byte> set = new HashSet<Byte>();
		for (int i : transparentEarthbending) {
			set.add((byte) i);
		}
		return set;
	}

	public static boolean isTransparentToEarthbending(Block block) {
		if (Arrays.asList(transparentEarthbending).contains(block.getTypeId()))
			return true;
		return false;
	}

	public static List<Entity> getEntitiesAroundPoint(Location location,
			double radius) {

		List<Entity> entities = location.getWorld().getEntities();
		List<Entity> list = location.getWorld().getEntities();

		for (Entity entity : entities) {
			if (entity.getLocation().distance(location) > radius) {
				list.remove(entity);
			}
		}

		return list;

	}

	public static Location getTargetedLocation(Player player, int range) {
		return getTargetedLocation(player, range, null);
	}

	public static Location getTargetedLocation(Player player, int range,
			int... transparency) {
		Location origin = player.getEyeLocation();
		Vector direction = origin.getDirection();

		HashSet<Byte> trans = new HashSet<Byte>();
		trans.add((byte) 0);

		if (transparency == null) {
			trans = null;
		} else {
			for (int i : transparency) {
				trans.add((byte) i);
			}
		}

		Block block = player.getTargetBlock(trans, (int) range + 1);
		double distance = block.getLocation().distance(origin) - 1;
		Location location = origin.add(direction.multiply(distance));

		return location;
	}

	public static List<Block> getBlocksAroundPoint(Location location,
			double radius) {

		List<Block> blocks = new ArrayList<Block>();

		int xorg = location.getBlockX();
		int yorg = location.getBlockY();
		int zorg = location.getBlockZ();

		int r = (int) radius + 4;

		Block originblock = location.getBlock();

		for (int x = xorg - r; x <= xorg + r; x++) {
			for (int y = yorg - r; y <= yorg + r; y++) {
				for (int z = zorg - r; z <= zorg + r; z++) {
					Block block = location.getWorld().getBlockAt(x, y, z);
					if (block.getLocation().distance(originblock.getLocation()) <= radius) {
						blocks.add(block);
					}
				}
			}
		}

		return blocks;

	}

	public static List<Block> getBlocksOnPlane(Location location, int radius) {
		List<Block> blocks = new ArrayList<Block>();

		for (int x = -radius; x <= radius; x++) {
			for (int y = -radius; y <= radius; y++) {
				blocks.add(location.getBlock().getRelative(BlockFace.NORTH, x)
						.getRelative(BlockFace.EAST, y));
			}
		}

		return blocks;
	}

	public static void moveEarth(Location location, Vector direction,
			int chainlength) {
		Block block = location.getBlock();
		moveEarth(block, direction, chainlength);
		// if (isEarthbendable(block)) {
		// Vector norm = direction.clone().normalize();
		// Vector negnorm = norm.clone().multiply(-1);
		//
		// Block affectedblock = location.clone().add(norm).getBlock();
		// // verbose(isTransparentToEarthbending(affectedblock));
		// if (isTransparentToEarthbending(affectedblock)) {
		// for (Entity entity : getEntitiesAroundPoint(
		// affectedblock.getLocation(), 2)) {
		// entity.setVelocity(norm.clone().multiply(.75));
		// }
		// affectedblock.setType(block.getType());
		//
		// for (double i = 1; i < chainlength; i++) {
		// affectedblock = location
		// .clone()
		// .add(negnorm.getX() * i, negnorm.getY() * i,
		// negnorm.getZ() * i).getBlock();
		// if (!isEarthbendable(affectedblock)) {
		// block.setType(Material.AIR);
		// break;
		// }
		// block.setType(affectedblock.getType());
		// block = affectedblock;
		// }
		// block.setType(Material.AIR);
		// } else {
		// // block.setType(Material.COBBLESTONE);
		// // affectedblock.setType(Material.GLASS);
		// }
		// }
	}

	public static void moveEarth(Block block, Vector direction, int chainlength) {
		// verbose("Moving earth");
		// verbose(direction);
		// verbose(isEarthbendable(block));
		if (isEarthbendable(block)) {
			Vector norm = direction.clone().normalize();
			Vector negnorm = norm.clone().multiply(-1);

			Location location = block.getLocation();

			Block affectedblock = location.clone().add(norm).getBlock();
			block.getWorld().playEffect(block.getLocation(),
					Effect.GHAST_SHOOT, 0, 4);
			if (EarthPassive.isPassiveSand(affectedblock)) {
				EarthPassive.revertSand(affectedblock);
			}
			if (affectedblock == null)
				return;
			// verbose(isTransparentToEarthbending(affectedblock));
			if (isTransparentToEarthbending(affectedblock)) {
				for (Entity entity : getEntitiesAroundPoint(
						affectedblock.getLocation(), 1.75)) {
					entity.setVelocity(norm.clone().multiply(.75));
				}

				affectedblock.setType(block.getType());
				affectedblock.setData(block.getData());

				if (tempearthblocks.containsKey(block)) {
					Block index = tempearthblocks.get(block);
					tempearthblocks.remove(block);
					tempearthblocks.put(affectedblock, index);
					Information info = movedearth.get(index);
					info.setBlock(affectedblock);
					info.setTime(System.currentTimeMillis());
					movedearth.replace(index, info);
				} else {
					tempearthblocks.put(affectedblock, block);
					Information info = new Information();
					info.setBlock(affectedblock);
					info.setType(block.getType());
					info.setData(block.getData());
					info.setTime(System.currentTimeMillis());
					movedearth.put(block, info);
				}

				for (double i = 1; i < chainlength; i++) {
					affectedblock = location
							.clone()
							.add(negnorm.getX() * i, negnorm.getY() * i,
									negnorm.getZ() * i).getBlock();
					if (!isEarthbendable(affectedblock)) {
						if (!Tools.adjacentToThreeOrMoreSources(block)
								&& Tools.isWater(block)) {
							block.setType(Material.AIR);
						} else {
							byte full = 0x0;
							block.setType(Material.WATER);
							block.setData(full);
						}
						break;
					}
					if (EarthPassive.isPassiveSand(block)) {
						EarthPassive.revertSand(block);
					}
					if (block == null)
						return;
					block.setType(affectedblock.getType());
					block.setData(affectedblock.getData());
					if (tempearthblocks.containsKey(affectedblock)) {
						Block index = tempearthblocks.get(affectedblock);
						tempearthblocks.remove(affectedblock);
						tempearthblocks.put(block, index);
						Information info = movedearth.get(index);
						if (info != null) {
							info.setBlock(block);
							info.setTime(System.currentTimeMillis());
							movedearth.replace(index, info);
						}
					} else {
						tempearthblocks.put(block, affectedblock);
						Information info = new Information();
						info.setBlock(block);
						info.setType(affectedblock.getType());
						info.setData(affectedblock.getData());
						info.setTime(System.currentTimeMillis());
						movedearth.put(affectedblock, info);
					}
					block = affectedblock;
				}
				if (!Tools.adjacentToThreeOrMoreSources(block)) {
					block.setType(Material.AIR);
				} else {
					byte full = 0x0;
					block.setType(Material.WATER);
					block.setData(full);
				}
			} else {
				// block.setType(Material.COBBLESTONE);
				// affectedblock.setType(Material.GLASS);
			}
		}
	}

	public static boolean isWater(Block block) {
		if (block.getType() == Material.WATER
				|| block.getType() == Material.STATIONARY_WATER)
			return true;
		return false;
	}

	public static int getEarthbendableBlocksLength(Block block,
			Vector direction, int maxlength) {
		Location location = block.getLocation();
		direction = direction.normalize();
		double j;
		for (int i = 0; i <= maxlength; i++) {
			j = (double) i;
			if (!isEarthbendable(location.add(direction.multiply(j)).getBlock())) {
				return i;
			}
		}
		return maxlength;
	}

	public static boolean isEarthbendable(Block block) {
		Material material = block.getType();

		// if ((material == Material.STONE) || (material == Material.CLAY)
		// || (material == Material.COAL_ORE)
		// || (material == Material.DIAMOND_ORE)
		// || (material == Material.DIRT)
		// || (material == Material.GOLD_ORE)
		// || (material == Material.GRASS)
		// || (material == Material.GRAVEL)
		// || (material == Material.IRON_ORE)
		// || (material == Material.LAPIS_ORE)
		// || (material == Material.NETHERRACK)
		// || (material == Material.REDSTONE_ORE)
		// || (material == Material.SAND)
		// || (material == Material.SANDSTONE)) {
		// return true;
		// }
		for (String s : ConfigManager.earthbendable) {

			if (material == Material.getMaterial(s)) {

				return true;

			}

		}
		return false;

	}

	public static boolean isWeapon(Material mat) {

		if (mat == Material.WOOD_AXE || mat == Material.WOOD_PICKAXE
				|| mat == Material.WOOD_SPADE || mat == Material.WOOD_SWORD

				|| mat == Material.STONE_AXE || mat == Material.STONE_PICKAXE
				|| mat == Material.STONE_SPADE || mat == Material.STONE_SWORD

				|| mat == Material.IRON_AXE || mat == Material.IRON_PICKAXE
				|| mat == Material.IRON_SPADE || mat == Material.IRON_SWORD

				|| mat == Material.GOLD_AXE || mat == Material.GOLD_PICKAXE
				|| mat == Material.GOLD_SPADE || mat == Material.GOLD_SWORD

				|| mat == Material.DIAMOND_AXE
				|| mat == Material.DIAMOND_PICKAXE
				|| mat == Material.DIAMOND_SPADE
				|| mat == Material.DIAMOND_SWORD)

			return true;

		return false;

	}

	public static boolean isWaterbendable(Block block, Player player) {
		byte full = 0x0;
		if (TempBlock.isTempBlock(block))
			return false;
		if ((block.getType() == Material.WATER || block.getType() == Material.STATIONARY_WATER)
				&& block.getData() == full)
			return true;
		if (block.getType() == Material.ICE || block.getType() == Material.SNOW
				|| block.getType() == Material.SNOW_BLOCK)
			return true;
		if (canPlantbend(player) && isPlant(block))
			return true;
		return false;
	}

	public static boolean canPlantbend(Player player) {
		return config.hasAbility(player, Abilities.Plantbending);
	}

	public static boolean hasAbility(Player player, Abilities ability) {
		return config.hasAbility(player, ability);
	}

	public static boolean isPlant(Block block) {
		if (Arrays.asList(plantIds).contains(block.getTypeId()))
			return true;
		return false;
	}

	public static boolean isBender(Player player, BendingType type) {
		return config.isBender(player, type);
	}

	public static Abilities getBendingAbility(Player player) {
		return config.getAbility(player);
	}

	public static List<BendingType> getBendingTypes(Player player) {
		return config.getBendingTypes(player);
	}

	public static double getDistanceFromLine(Vector line, Location pointonline,
			Location point) {

		Vector AP = new Vector();
		double Ax, Ay, Az;
		Ax = pointonline.getX();
		Ay = pointonline.getY();
		Az = pointonline.getZ();

		double Px, Py, Pz;
		Px = point.getX();
		Py = point.getY();
		Pz = point.getZ();

		AP.setX(Px - Ax);
		AP.setY(Py - Ay);
		AP.setZ(Pz - Az);

		return (AP.crossProduct(line).length()) / (line.length());
	}

	public static <T> void verbose(T something) {
		if (something != null) {
			Bending.log.info(something.toString());
		}
	}

	public static boolean isBlockTouching(Block block1, Block block2) {
		BlockFace[] faces = { BlockFace.DOWN, BlockFace.UP, BlockFace.NORTH,
				BlockFace.EAST, BlockFace.WEST, BlockFace.SOUTH };
		block1 = block1.getLocation().getBlock();
		block2 = block2.getLocation().getBlock();
		for (BlockFace face : faces) {
			if (block1.getRelative(face).equals(block2)) {
				return true;
			}
		}
		return false;
	}

	public static Entity getTargettedEntity(Player player, double range) {
		double longestr = range;
		Entity target = null;
		Location origin = player.getEyeLocation();
		Vector direction = player.getEyeLocation().getDirection();
		for (Entity entity : origin.getWorld().getEntities()) {
			if (entity.getLocation().distance(origin) < longestr
					&& getDistanceFromLine(direction, origin,
							entity.getLocation()) < 2
					&& (entity instanceof LivingEntity)
					&& entity.getEntityId() != player.getEntityId()) {
				target = entity;
				longestr = entity.getLocation().distance(origin);
			}
		}
		return target;
	}

	public static void damageEntity(Player player, Entity entity, int damage) {
		if (entity instanceof LivingEntity) {
			if (AvatarState.isAvatarState(player)) {
				damage = AvatarState.getValue(damage);
			}
			((LivingEntity) entity).damage(damage, player);
			((LivingEntity) entity)
					.setLastDamageCause(new EntityDamageByEntityEvent(player,
							entity, DamageCause.CUSTOM, damage));
		}
	}

	public static Vector rotateVectorAroundVector(Vector axis, Vector rotator,
			double degrees) {
		double angle = Math.toRadians(degrees);
		Vector rotation = axis.clone();
		Vector rotate = rotator.clone();
		rotation = rotation.normalize();

		Vector thirdaxis = rotation.crossProduct(rotate).normalize()
				.multiply(rotate.length());

		return rotate.multiply(Math.cos(angle)).add(
				thirdaxis.multiply(Math.sin(angle)));

		// return new Vector(x, z, y);
	}

	public static Vector getOrthogonalVector(Vector axis, double degrees,
			double length) {

		Vector ortho = new Vector(axis.getY(), -axis.getX(), 0);
		ortho = ortho.normalize();
		ortho = ortho.multiply(length);

		return rotateVectorAroundVector(axis, ortho, degrees);

	}

	public static Location getPointOnLine(Location origin, Location target,
			double distance) {
		return origin.clone().add(
				getDirection(origin, target).normalize().multiply(distance));

	}

	public static Vector getDirection(Location location, Location destination) {
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

	public static boolean isMeltable(Block block) {
		if (block.getType() == Material.ICE) {
			return true;
		}
		return false;
	}

	public static boolean adjacentToThreeOrMoreSources(Block block) {
		if (TempBlock.isTempBlock(block))
			return false;
		int sources = 0;
		byte full = 0x0;
		BlockFace[] faces = { BlockFace.EAST, BlockFace.WEST, BlockFace.NORTH,
				BlockFace.SOUTH };
		for (BlockFace face : faces) {
			Block blocki = block.getRelative(face);
			if ((blocki.getType() == Material.WATER || blocki.getType() == Material.STATIONARY_WATER)
					&& blocki.getData() == full
					&& WaterManipulation.canPhysicsChange(blocki))
				sources++;
		}
		if (sources >= 3)
			return true;
		return false;
	}

	public static boolean adjacentToAnyWater(Block block) {
		BlockFace[] faces = { BlockFace.EAST, BlockFace.WEST, BlockFace.NORTH,
				BlockFace.SOUTH };
		for (BlockFace face : faces) {
			Block blocki = block.getRelative(face);
			if (isWater(blocki))
				return true;
		}
		return false;
	}

	public static void stopAllBending() {
		AirBlast.removeAll();
		AirBubble.removeAll();
		Catapult.removeAll();
		CompactColumn.removeAll();
		EarthBlast.removeAll();
		EarthColumn.removeAll();
		EarthPassive.removeAll();
		FreezeMelt.removeAll();
		WalkOnWater.removeAll();
		WaterSpout.removeAll();
		WaterWall.removeAll();
		Wave.removeAll();
		AirScooter.removeAll();
		FireStream.removeAll();
		BendingManager.removeFlyers();
		for (Block block : tempearthblocks.keySet()) {
			removeEarthbendedBlock(block);
		}
	}

	public static boolean canBend(Player player, Abilities ability) {
		if (ability == null)
			return false;
		if (isChiBlocked(player) && ability != Abilities.AvatarState)
			return false;
		if (hasPermission(player, ability)
				&& !isRegionProtected(player, ability, true))
			return true;
		return false;

	}

	public static boolean isRegionProtected(Player player, Abilities ability,
			boolean look) {
		Plugin p = Bukkit.getPluginManager().getPlugin("WorldGuard");
		if (p == null)
			return false;
		WorldGuardPlugin wg = (WorldGuardPlugin) Bukkit.getPluginManager()
				.getPlugin("WorldGuard");
		// List<Block> lb = getBlocksAroundPoint(player.getLocation(), 20);
		// for (Block b: lb){
		Block b = player.getLocation().getBlock();
		if (!player.isOnline())
			return false;
		if (look) {
			try {
				int range = 20;
				if (ability == Abilities.Fireball)
					range = 100;
				Block c = player.getTargetBlock(null, range);
				if (!(wg.getGlobalRegionManager()
						.get(c.getLocation().getWorld())
						.getApplicableRegions(c.getLocation())
						.allows(DefaultFlag.PVP))) {
					return true;
				}
			} catch (IllegalStateException e) {
				return false;
			}
		}
		if (!(wg.getGlobalRegionManager().get(b.getLocation().getWorld())
				.getApplicableRegions(b.getLocation()).allows(DefaultFlag.PVP))) {
			return true;
		}

		// EntityDamageByEntityEvent damageEvent = new
		// EntityDamageByEntityEvent(player, player,
		// EntityDamageEvent.DamageCause.ENTITY_ATTACK, 1);
		// Bukkit.getServer().getPluginManager().callEvent(damageEvent);

		// if (damageEvent.isCancelled())
		// {
		// return true;
		// }
		// }

		return false;
	}

	public static boolean canBendPassive(Player player, BendingType type) {
		if (isRegionProtected(player, null, false))
			return false;
		if (isChiBlocked(player))
			return false;
		if (player.hasPermission("bending." + type + ".passive")) {
			return true;
		}
		return false;
	}

	public static boolean hasPermission(Player player, Abilities ability) {
		if (ability == Abilities.AvatarState
				&& player.hasPermission("bending.avatarstate")) {
			return true;
		}
		if (Abilities.isAirbending(ability)
				&& player.hasPermission("bending.air." + ability)) {
			return true;
		}
		if (Abilities.isWaterbending(ability)
				&& player.hasPermission("bending.water." + ability)) {
			return true;
		}
		if (Abilities.isEarthbending(ability)
				&& player.hasPermission("bending.earth." + ability)) {
			return true;
		}
		if (Abilities.isFirebending(ability)
				&& player.hasPermission("bending.fire." + ability)) {
			return true;
		}
		return false;
	}

	public static ChatColor getColor(String input) {
		return (ChatColor) colors.get(input.toLowerCase().replace("&", ""));

	}

	public static boolean isDay(World world) {
		long time = world.getTime();
		if (time >= 23500 || time <= 12500) {
			return true;
		}
		return false;
	}

	public static double firebendingDayAugment(double value, World world) {
		if (isDay(world)) {
			return ConfigManager.dayFactor * value;
		}
		return value;
	}

	public static double getFirebendingDayAugment(World world) {
		if (isDay(world))
			return ConfigManager.dayFactor;
		return 1;
	}

	public static double waterbendingNightAugment(double value, World world) {
		if (isNight(world)) {
			return ConfigManager.nightFactor * value;
		}
		return value;
	}

	public static double getWaterbendingNightAugment(World world) {
		if (isNight(world))
			return ConfigManager.nightFactor;
		return 1;
	}

	public static boolean isNight(World world) {
		if (world.getEnvironment() == Environment.NETHER
				|| world.getEnvironment() == Environment.THE_END) {
			return false;
		}
		long time = world.getTime();
		if (time >= 12950 && time <= 23050) {
			return true;
		}
		return false;
	}

	public static boolean isSolid(Block block) {
		if (Arrays.asList(nonOpaque).contains(block.getTypeId()))
			return false;
		return true;
	}

	public static void removeEarthbendedBlock(Block block) {
		if (tempearthblocks.containsKey(block)) {
			Block index = Tools.tempearthblocks.get(block);
			if (!Tools.adjacentToThreeOrMoreSources(block)) {
				block.setType(Material.AIR);
			} else {
				byte full = 0x0;
				block.setType(Material.WATER);
				block.setData(full);
			}
			if (movedearth.containsKey(index)) {
				Information info = Tools.movedearth.get(index);
				index.setType(info.getType());
				index.setData(info.getData());
				Tools.movedearth.remove(index);
			}
			Tools.tempearthblocks.remove(block);
			if (EarthColumn.blockInAllAffectedBlocks(block)) {
				EarthColumn.revertBlock(block);
			}
			if (EarthColumn.blockInAllAffectedBlocks(index)) {
				EarthColumn.revertBlock(index);
			}
			EarthColumn.resetBlock(block);
			EarthColumn.resetBlock(index);
		}
	}

	public static void removeEarthbendedBlockIndex(Block block) {
		if (tempearthblocks.containsKey(block)) {
			Block index = tempearthblocks.get(block);
			tempearthblocks.remove(block);
			movedearth.remove(index);
		}
	}

	public static void blockChi(Player player, long time) {
		if (blockedchis.containsKey(player)) {
			blockedchis.replace(player, time);
		} else {
			blockedchis.put(player, time);
		}
	}

	public static boolean isChiBlocked(Player player) {
		if (blockedchis.containsKey(player)) {
			long time = System.currentTimeMillis();
			if (time > blockedchis.get(player) + ConfigManager.chiblockduration
					|| AvatarState.isAvatarState(player)) {
				blockedchis.remove(player);
				return false;
			}
			return true;
		}
		return false;
	}

	public static void breakBlock(Block block) {
		block.breakNaturally(new ItemStack(Material.AIR));
	}

	public static boolean adjacentToFrozenBlock(Block block) {
		BlockFace[] faces = { BlockFace.DOWN, BlockFace.UP, BlockFace.NORTH,
				BlockFace.EAST, BlockFace.WEST, BlockFace.SOUTH };
		boolean adjacent = false;
		for (BlockFace face : faces) {
			if (FreezeMelt.frozenblocks.containsKey((block.getRelative(face))))
				adjacent = true;
		}
		return adjacent;
	}

	static {
		Map<String, ChatColor> tmpMap = new HashMap<String, ChatColor>();
		tmpMap.put("black", ChatColor.BLACK);
		tmpMap.put("0", ChatColor.BLACK);

		tmpMap.put("dark blue", ChatColor.DARK_BLUE);
		tmpMap.put("dark_blue", ChatColor.DARK_BLUE);
		tmpMap.put("1", ChatColor.DARK_BLUE);

		tmpMap.put("dark green", ChatColor.DARK_GREEN);
		tmpMap.put("dark_green", ChatColor.DARK_GREEN);
		tmpMap.put("2", ChatColor.DARK_GREEN);

		tmpMap.put("dark aqua", ChatColor.DARK_AQUA);
		tmpMap.put("dark_aqua", ChatColor.DARK_AQUA);
		tmpMap.put("teal", ChatColor.DARK_AQUA);
		tmpMap.put("3", ChatColor.DARK_AQUA);

		tmpMap.put("dark red", ChatColor.DARK_RED);
		tmpMap.put("dark_red", ChatColor.DARK_RED);
		tmpMap.put("4", ChatColor.DARK_RED);

		tmpMap.put("dark purple", ChatColor.DARK_PURPLE);
		tmpMap.put("dark_purple", ChatColor.DARK_PURPLE);
		tmpMap.put("purple", ChatColor.DARK_PURPLE);
		tmpMap.put("5", ChatColor.DARK_PURPLE);

		tmpMap.put("gold", ChatColor.GOLD);
		tmpMap.put("orange", ChatColor.GOLD);
		tmpMap.put("6", ChatColor.GOLD);

		tmpMap.put("gray", ChatColor.GRAY);
		tmpMap.put("grey", ChatColor.GRAY);
		tmpMap.put("7", ChatColor.GRAY);

		tmpMap.put("dark gray", ChatColor.DARK_GRAY);
		tmpMap.put("dark_gray", ChatColor.DARK_GRAY);
		tmpMap.put("dark grey", ChatColor.DARK_GRAY);
		tmpMap.put("dark_grey", ChatColor.DARK_GRAY);
		tmpMap.put("8", ChatColor.DARK_GRAY);

		tmpMap.put("blue", ChatColor.BLUE);
		tmpMap.put("9", ChatColor.BLUE);

		tmpMap.put("bright green", ChatColor.GREEN);
		tmpMap.put("bright_green", ChatColor.GREEN);
		tmpMap.put("green", ChatColor.GREEN);
		tmpMap.put("a", ChatColor.GREEN);

		tmpMap.put("aqua", ChatColor.AQUA);
		tmpMap.put("b", ChatColor.AQUA);

		tmpMap.put("red", ChatColor.RED);
		tmpMap.put("c", ChatColor.RED);

		tmpMap.put("light purple", ChatColor.LIGHT_PURPLE);
		tmpMap.put("light_purple", ChatColor.LIGHT_PURPLE);
		tmpMap.put("pink", ChatColor.LIGHT_PURPLE);
		tmpMap.put("d", ChatColor.LIGHT_PURPLE);

		tmpMap.put("yellow", ChatColor.YELLOW);
		tmpMap.put("e", ChatColor.YELLOW);

		tmpMap.put("white", ChatColor.WHITE);
		tmpMap.put("f", ChatColor.WHITE);

		tmpMap.put("random", ChatColor.MAGIC);
		tmpMap.put("magic", ChatColor.MAGIC);
		tmpMap.put("k", ChatColor.MAGIC);

		colors = Collections.unmodifiableMap(tmpMap);
	}

}
