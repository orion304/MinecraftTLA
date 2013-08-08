package tools;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import main.Bending;
import main.BendingPlayers;
import main.ConfigValues;
import me.ryanhamshire.GriefPrevention.GriefPrevention;
import net.sacredlabyrinth.Phaed.PreciousStones.FieldFlag;
import net.sacredlabyrinth.Phaed.PreciousStones.PreciousStones;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.World.Environment;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.BlockState;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Entity;
import org.bukkit.entity.FallingBlock;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;
import org.bukkit.util.Vector;

import waterbending.Bloodbending;
import waterbending.FreezeMelt;
import waterbending.IceSpike;
import waterbending.IceSpike2;
import waterbending.OctopusForm;
import waterbending.Plantbending;
import waterbending.WaterManipulation;
import waterbending.WaterReturn;
import waterbending.WaterSpout;
import waterbending.WaterWall;
import waterbending.Wave;
import airbending.AirBlast;
import airbending.AirBubble;
import airbending.AirBurst;
import airbending.AirScooter;
import airbending.AirShield;
import airbending.AirSpout;
import airbending.AirSuction;
import airbending.AirSwipe;
import airbending.Speed;
import airbending.Tornado;
import chiblocking.Paralyze;
import chiblocking.RapidPunch;

import com.massivecraft.factions.listeners.FactionsListenerMain;
import com.massivecraft.mcore.ps.PS;
import com.palmergames.bukkit.towny.Towny;
import com.palmergames.bukkit.towny.TownyMessaging;
import com.palmergames.bukkit.towny.TownySettings;
import com.palmergames.bukkit.towny.object.Coord;
import com.palmergames.bukkit.towny.object.PlayerCache;
import com.palmergames.bukkit.towny.object.PlayerCache.TownBlockStatus;
import com.palmergames.bukkit.towny.object.TownyPermission;
import com.palmergames.bukkit.towny.object.TownyUniverse;
import com.palmergames.bukkit.towny.object.TownyWorld;
import com.palmergames.bukkit.towny.object.WorldCoord;
import com.palmergames.bukkit.towny.utils.PlayerCacheUtil;
import com.palmergames.bukkit.towny.war.flagwar.TownyWar;
import com.palmergames.bukkit.towny.war.flagwar.TownyWarConfig;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import com.sk89q.worldguard.protection.flags.DefaultFlag;

import earthbending.Catapult;
import earthbending.CompactColumn;
import earthbending.EarthArmor;
import earthbending.EarthBlast;
import earthbending.EarthColumn;
import earthbending.EarthPassive;
import earthbending.EarthTunnel;
import earthbending.Shockwave;
import earthbending.Tremorsense;
import firebending.Cook;
import firebending.FireBlast;
import firebending.FireBurst;
import firebending.FireJet;
import firebending.FireShield;
import firebending.FireStream;
import firebending.Fireball;
import firebending.Illumination;
import firebending.Lightning;
import firebending.WallOfFire;

import main.Bending;

public class Tools {

	public static BendingPlayers config;

	private static final ItemStack pickaxe = new ItemStack(
			Material.DIAMOND_PICKAXE);

	private static final Map<String, ChatColor> colors;

	private static Abilities[] harmlessAbilities = { Abilities.AirScooter,
		Abilities.AirSpout, Abilities.HealingWaters, Abilities.HighJump,
		Abilities.Illumination, Abilities.Tremorsense, Abilities.WaterSpout };

	private static Abilities[] localAbilities = { Abilities.AirScooter,
		Abilities.AirSpout, Abilities.HealingWaters, Abilities.HighJump,
		Abilities.Illumination, Abilities.Tremorsense,
		Abilities.WaterSpout, Abilities.AvatarState, Abilities.FireJet,
		Abilities.Paralyze, Abilities.RapidPunch };

	public static Integer[] transparentEarthbending = { 0, 6, 8, 9, 10, 11, 30,
		31, 32, 37, 38, 39, 40, 50, 51, 59, 78, 83, 106 };

	public static Integer[] nonOpaque = { 0, 6, 8, 9, 10, 11, 27, 28, 30, 31,
		32, 37, 38, 39, 40, 50, 51, 55, 59, 66, 68, 69, 70, 72, 75, 76, 77,
		78, 83, 90, 93, 94, 104, 105, 106, 111, 115, 119, 127, 131, 132 };

	private static Integer[] plantIds = { 6, 18, 31, 32, 37, 38, 39, 40, 59,
		81, 83, 86, 99, 100, 103, 104, 105, 106, 111 };

	public static final long timeinterval = ConfigValues.GlobalCooldown;

	public static ConcurrentHashMap<Block, Information> movedearth = new ConcurrentHashMap<Block, Information>();
	// public static ConcurrentHashMap<Block, Block> tempearthblocks = new
	// ConcurrentHashMap<Block, Block>();
	public static ConcurrentHashMap<Integer, Information> tempair = new ConcurrentHashMap<Integer, Information>();
	public static ConcurrentHashMap<Player, Long> blockedchis = new ConcurrentHashMap<Player, Long>();
	public static ConcurrentHashMap<Player, Player> tempflyers = new ConcurrentHashMap<Player, Player>();
	public static List<Player> toggledBending = new ArrayList<Player>();

	public static ArrayList<Block> tempnophysics = new ArrayList<Block>();

	private static boolean allowharmless = true;
	private static boolean respectWorldGuard = true;
	private static boolean respectPreciousStones = true;
	private static boolean respectFactions = true;
	private static boolean respectTowny = true;
	private static boolean respectGriefPrevention = true;

	// private static boolean logblockhook = true;

	public Tools(BendingPlayers config2) {
		config = config2;
	}

	public static HashSet<Byte> getTransparentEarthbending() {
		HashSet<Byte> set = new HashSet<Byte>();
		for (int i : transparentEarthbending) {
			set.add((byte) i);
		}
		return set;
	}

	public static boolean isTransparentToEarthbending(Player player, Block block) {
		return isTransparentToEarthbending(player, Abilities.RaiseEarth, block);
	}

	public static boolean isTransparentToEarthbending(Player player,
			Abilities ability, Block block) {
		if (Tools.isRegionProtectedFromBuild(player, ability,
				block.getLocation()))
			return false;
		if (Arrays.asList(transparentEarthbending).contains(block.getTypeId()))
			return true;
		return false;
	}

	public static List<Entity> getEntitiesAroundPoint(Location location,
			double radius) {

		List<Entity> entities = location.getWorld().getEntities();
		List<Entity> list = location.getWorld().getEntities();

		for (Entity entity : entities) {
			if (entity.getWorld() != location.getWorld()) {
				list.remove(entity);
			} else if (entity.getLocation().distance(location) > radius) {
				list.remove(entity);
			}
		}

		return list;

	}

	public static boolean isObstructed(Location location1, Location location2) {
		Vector loc1 = location1.toVector();
		Vector loc2 = location2.toVector();

		Vector direction = loc2.subtract(loc1);
		direction.normalize();

		Location loc;

		double max = location1.distance(location2);

		for (double i = 0; i <= max; i++) {
			loc = location1.clone().add(direction.clone().multiply(i));
			Material type = loc.getBlock().getType();
			if (type != Material.AIR
					&& !Arrays.asList(transparentEarthbending).contains(
							type.getId()))
				return true;
		}

		return false;
	}

	public static Location getTargetedLocation(Player player, int range) {
		return getTargetedLocation(player, range, 0);
	}

	public static Location getTargetedLocation(Player player,
			double originselectrange, Integer... nonOpaque2) {
		Location origin = player.getEyeLocation();
		Vector direction = origin.getDirection();

		HashSet<Byte> trans = new HashSet<Byte>();
		trans.add((byte) 0);

		if (nonOpaque2 == null) {
			trans = null;
		} else {
			for (int i : nonOpaque2) {
				trans.add((byte) i);
			}
		}

		Block block = player.getTargetBlock(trans, (int) originselectrange + 1);
		double distance = block.getLocation().distance(origin) - 1.5;
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

		// Block originblock = location.getBlock();

		for (int x = xorg - r; x <= xorg + r; x++) {
			for (int y = yorg - r; y <= yorg + r; y++) {
				for (int z = zorg - r; z <= zorg + r; z++) {
					Block block = location.getWorld().getBlockAt(x, y, z);
					// if
					// (block.getLocation().distance(originblock.getLocation())
					// <= radius) {
					if (block.getLocation().distance(location) <= radius) {
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

	public static void moveEarth(Player player, Location location,
			Vector direction, int chainlength) {
		moveEarth(player, location, direction, chainlength, true);
	}

	public static void moveEarth(Player player, Location location,
			Vector direction, int chainlength, boolean throwplayer) {
		Block block = location.getBlock();
		moveEarth(player, block, direction, chainlength, throwplayer);
	}

	public static void moveEarth(Player player, Block block, Vector direction,
			int chainlength) {
		moveEarth(player, block, direction, chainlength, true);
	}

	public static boolean moveEarth(Player player, Block block,
			Vector direction, int chainlength, boolean throwplayer) {
		if (isEarthbendable(player, block)
				&& !isRegionProtectedFromBuild(player, Abilities.RaiseEarth,
						block.getLocation())) {

			boolean up = false;
			boolean down = false;
			Vector norm = direction.clone().normalize();
			if (norm.dot(new Vector(0, 1, 0)) == 1) {
				up = true;
			} else if (norm.dot(new Vector(0, -1, 0)) == 1) {
				down = true;
			}
			Vector negnorm = norm.clone().multiply(-1);

			Location location = block.getLocation();

			ArrayList<Block> blocks = new ArrayList<Block>();
			for (double j = -2; j <= chainlength; j++) {
				Block checkblock = location.clone()
						.add(negnorm.clone().multiply(j)).getBlock();
				if (!tempnophysics.contains(checkblock)) {
					blocks.add(checkblock);
					tempnophysics.add(checkblock);
				}
			}

			Block affectedblock = location.clone().add(norm).getBlock();
			if (EarthPassive.isPassiveSand(block)) {
				EarthPassive.revertSand(block);
			}
			// if (block.getType() == Material.SAND) {
			// block.setType(Material.SANDSTONE);
			// }

			if (affectedblock == null)
				return false;
			if (isTransparentToEarthbending(player, affectedblock)) {
				if (throwplayer) {
					for (Entity entity : getEntitiesAroundPoint(
							affectedblock.getLocation(), 1.75)) {
						if (entity instanceof LivingEntity) {
							LivingEntity lentity = (LivingEntity) entity;
							if (lentity.getEyeLocation().getBlockX() == affectedblock
									.getX()
									&& lentity.getEyeLocation().getBlockZ() == affectedblock
									.getZ())
								if (!(entity instanceof FallingBlock))
									entity.setVelocity(norm.clone().multiply(
											.75));
						} else {
							if (entity.getLocation().getBlockX() == affectedblock
									.getX()
									&& entity.getLocation().getBlockZ() == affectedblock
									.getZ())
								if (!(entity instanceof FallingBlock))
									entity.setVelocity(norm.clone().multiply(
											.75));
						}
					}

				}

				if (up) {
					Block topblock = affectedblock.getRelative(BlockFace.UP);
					if (topblock.getType() != Material.AIR) {
						breakBlock(affectedblock);
					} else if (!affectedblock.isLiquid()
							&& affectedblock.getType() != Material.AIR) {
						// affectedblock.setType(Material.GLASS);
						moveEarthBlock(affectedblock, topblock);
					}
				} else {
					breakBlock(affectedblock);
				}

				// affectedblock.setType(block.getType());
				// affectedblock.setData(block.getData());
				//
				// addTempEarthBlock(block, affectedblock);
				moveEarthBlock(block, affectedblock);
				block.getWorld().playEffect(block.getLocation(),
						Effect.GHAST_SHOOT, 0, 4);

				for (double i = 1; i < chainlength; i++) {
					affectedblock = location
							.clone()
							.add(negnorm.getX() * i, negnorm.getY() * i,
									negnorm.getZ() * i).getBlock();
					if (!isEarthbendable(player, affectedblock)) {
						// verbose(affectedblock.getType());
						if (down) {
							if (isTransparentToEarthbending(player,
									affectedblock)
									&& !affectedblock.isLiquid()
									&& affectedblock.getType() != Material.AIR) {
								moveEarthBlock(affectedblock, block);
							}
						}
						// if (!Tools.adjacentToThreeOrMoreSources(block)
						// && Tools.isWater(block)) {
						// block.setType(Material.AIR);
						// } else {
						// byte full = 0x0;
						// block.setType(Material.WATER);
						// block.setData(full);
						// }
						break;
					}
					if (EarthPassive.isPassiveSand(affectedblock)) {
						EarthPassive.revertSand(affectedblock);
					}
					// if (affectedblock.getType() == Material.SAND) {
					// affectedblock.setType(Material.SANDSTONE);
					// }
					if (block == null) {
						for (Block checkblock : blocks) {
							tempnophysics.remove(checkblock);
						}
						return false;
					}
					// block.setType(affectedblock.getType());
					// block.setData(affectedblock.getData());
					// addTempEarthBlock(affectedblock, block);
					moveEarthBlock(affectedblock, block);
					block = affectedblock;
				}

				int i = chainlength;
				affectedblock = location
						.clone()
						.add(negnorm.getX() * i, negnorm.getY() * i,
								negnorm.getZ() * i).getBlock();
				if (!isEarthbendable(player, affectedblock)) {
					if (down) {
						if (isTransparentToEarthbending(player, affectedblock)
								&& !affectedblock.isLiquid()) {
							moveEarthBlock(affectedblock, block);
						}
					}
				}

			} else {
				for (Block checkblock : blocks) {
					tempnophysics.remove(checkblock);
				}
				return false;
			}
			for (Block checkblock : blocks) {
				tempnophysics.remove(checkblock);
			}
			return true;
		}
		return false;
	}

	public static void moveEarthBlock(Block source, Block target) {
		byte full = 0x0;
		Information info;
		if (movedearth.containsKey(source)) {
			// verbose("Moving something already moved.");
			info = movedearth.get(source);
			info.setTime(System.currentTimeMillis());
			movedearth.remove(source);
			movedearth.put(target, info);
		} else {
			// verbose("Moving something for the first time.");
			info = new Information();
			info.setBlock(source);
			// info.setType(source.getType());
			// info.setData(source.getData());
			info.setTime(System.currentTimeMillis());
			info.setState(source.getState());
			movedearth.put(target, info);
		}

		if (adjacentToThreeOrMoreSources(source)) {
			source.setType(Material.WATER);
			source.setData(full);
		} else {
			source.setType(Material.AIR);
		}
		if (info.getState().getType() == Material.SAND) {
			target.setType(Material.SANDSTONE);
		} else {
			target.setType(info.getState().getType());
			target.setData(info.getState().getRawData());
		}
	}

	public static void addTempAirBlock(Block block) {
		if (movedearth.containsKey(block)) {
			Information info = movedearth.get(block);
			block.setType(Material.AIR);
			info.setTime(System.currentTimeMillis());
			movedearth.remove(block);
			tempair.put(info.getID(), info);
		} else {
			Information info = new Information();
			info.setBlock(block);
			// info.setType(block.getType());
			// info.setData(block.getData());
			info.setState(block.getState());
			info.setTime(System.currentTimeMillis());
			block.setType(Material.AIR);
			tempair.put(info.getID(), info);
		}

	}

	public static void revertAirBlock(int i) {
		revertAirBlock(i, false);
	}

	public static void revertAirBlock(int i, boolean force) {
		if (!tempair.containsKey(i))
			return;
		Information info = tempair.get(i);
		Block block = info.getState().getBlock();
		if (block.getType() != Material.AIR && !block.isLiquid()) {
			if (force || !movedearth.containsKey(block)) {
				dropItems(
						block,
						getDrops(block, info.getState().getType(), info
								.getState().getRawData(), pickaxe));
				// ItemStack item = new ItemStack(info.getType());
				// item.setData(new MaterialData(info.getType(),
				// info.getData()));
				// block.getWorld().dropItem(block.getLocation(), item);
				tempair.remove(i);
			} else {
				info.setTime(info.getTime() + 10000);
			}
			return;
		} else {
			// block.setType(info.getType());
			// block.setData(info.getData());
			info.getState().update(true);
			tempair.remove(i);
		}
	}

	// public static boolean revertBlock(Block block) {
	// return revertBlock(block, true);
	// }

	public static boolean revertBlock(Block block) {
		byte full = 0x0;
		if (movedearth.containsKey(block)) {
			Information info = movedearth.get(block);
			Block sourceblock = info.getState().getBlock();

			if (info.getState().getType() == Material.AIR) {
				movedearth.remove(block);
				return true;
			}

			if (block.equals(sourceblock)) {
				// verbose("Equals!");
				// if (block.getType() == Material.SANDSTONE
				// && info.getState().getType() == Material.SAND)
				// block.setType(Material.SAND);
				info.getState().update(true);
				if (EarthColumn.blockInAllAffectedBlocks(sourceblock))
					EarthColumn.revertBlock(sourceblock);
				if (EarthColumn.blockInAllAffectedBlocks(block))
					EarthColumn.revertBlock(block);
				EarthColumn.resetBlock(sourceblock);
				EarthColumn.resetBlock(block);
				movedearth.remove(block);
				return true;
			}

			if (movedearth.containsKey(sourceblock)) {
				addTempAirBlock(block);
				movedearth.remove(block);
				return true;
				// verbose("Block: " + block);
				// verbose("Sourceblock: " + sourceblock);
				// verbose("StartBlock: " + startblock);
				// if (startblock != null) {
				// if (startblock.equals(sourceblock)) {
				// sourceblock.setType(info.getType());
				// sourceblock.setData(info.getData());
				// if (adjacentToThreeOrMoreSources(block)) {
				// block.setType(Material.WATER);
				// block.setData(full);
				// } else {
				// block.setType(Material.AIR);
				// }
				// movedearth.get(startblock).setInteger(10);
				// if (EarthColumn
				// .blockInAllAffectedBlocks(sourceblock))
				// EarthColumn.revertBlock(sourceblock);
				// if (EarthColumn.blockInAllAffectedBlocks(block))
				// EarthColumn.revertBlock(block);
				// EarthColumn.resetBlock(sourceblock);
				// EarthColumn.resetBlock(block);
				// movedearth.remove(block);
				// return true;
				// }
				//
				// } else {
				// startblock = block;
				// }
				// revertBlock(sourceblock, startblock, true);
			}

			if (sourceblock.getType() == Material.AIR || sourceblock.isLiquid()) {
				// sourceblock.setType(info.getType());
				// sourceblock.setData(info.getData());
				info.getState().update(true);
			} else {
				// if (info.getType() != Material.AIR) {
				// ItemStack item = new ItemStack(info.getType());
				// item.setData(new MaterialData(info.getType(), info
				// .getData()));
				// block.getWorld().dropItem(block.getLocation(), item);
				dropItems(
						block,
						getDrops(block, info.getState().getType(), info
								.getState().getRawData(), pickaxe));
				// }
			}

			// if (info.getInteger() != 10) {
			if (adjacentToThreeOrMoreSources(block)) {
				block.setType(Material.WATER);
				block.setData(full);
			} else {
				block.setType(Material.AIR);
			}
			// }

			if (EarthColumn.blockInAllAffectedBlocks(sourceblock))
				EarthColumn.revertBlock(sourceblock);
			if (EarthColumn.blockInAllAffectedBlocks(block))
				EarthColumn.revertBlock(block);
			EarthColumn.resetBlock(sourceblock);
			EarthColumn.resetBlock(block);
			movedearth.remove(block);
		}
		return true;
	}

	public static void removeRevertIndex(Block block) {
		if (movedearth.containsKey(block)) {
			Information info = movedearth.get(block);
			if (block.getType() == Material.SANDSTONE
					&& info.getType() == Material.SAND)
				block.setType(Material.SAND);
			if (EarthColumn.blockInAllAffectedBlocks(block))
				EarthColumn.revertBlock(block);
			EarthColumn.resetBlock(block);
			movedearth.remove(block);
		}
	}

	public static void removeAllEarthbendedBlocks() {
		for (Block block : movedearth.keySet()) {
			// block.setType(Material.GLASS);
			// movedearth.remove(block);
			// removeEarthbendedBlockByIndex(block);
			revertBlock(block);
		}

		for (Integer i : tempair.keySet()) {
			revertAirBlock(i, true);
		}
	}

	public static Collection<ItemStack> getDrops(Block block, Material type,
			byte data, ItemStack breakitem) {
		BlockState tempstate = block.getState();
		// byte olddata = block.getData();
		// Material oldtype = block.getType();
		block.setType(type);
		block.setData(data);
		// Collection<ItemStack> item = block.getDrops(breakitem);
		Collection<ItemStack> item = block.getDrops();
		// block.setType(oldtype);
		// block.setData(olddata);
		tempstate.update(true);
		return item;
	}

	public static void dropItems(Block block, Collection<ItemStack> items) {
		for (ItemStack item : items)
			block.getWorld().dropItem(block.getLocation(), item);
	}

	public static boolean isWater(Block block) {
		if (block.getType() == Material.WATER
				|| block.getType() == Material.STATIONARY_WATER)
			return true;
		return false;
	}

	public static int getEarthbendableBlocksLength(Player player, Block block,
			Vector direction, int maxlength) {
		Location location = block.getLocation();
		direction = direction.normalize();
		double j;
		for (int i = 0; i <= maxlength; i++) {
			j = (double) i;
			if (!isEarthbendable(player,
					location.clone().add(direction.clone().multiply(j))
					.getBlock())) {
				return i;
			}
		}
		return maxlength;
	}

	public static boolean isEarthbendable(Player player, Block block) {

		return isEarthbendable(player, Abilities.RaiseEarth, block);

	}

	public static boolean isEarthbendable(Player player, Abilities ability,
			Block block) {
		if (Tools.isRegionProtectedFromBuild(player, ability,
				block.getLocation()))
			return false;
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
		for (String s : ConfigValues.EarthBendable) {

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
		if (block.getType() == Material.ICE || block.getType() == Material.SNOW)
			// || block.getType() == Material.SNOW_BLOCK)
			return true;
		if (canPlantbend(player) && isPlant(block))
			return true;
		return false;
	}

	public static boolean canPlantbend(Player player) {
		return player.hasPermission("bending.water.plantbending");
	}

	public static boolean hasAbility(Player player, Abilities ability) {
		// return config.hasAbility(player, ability);
		return canBend(player, ability);
	}

	public static boolean isPlant(Block block) {
		if (Arrays.asList(plantIds).contains(block.getTypeId()))
			return true;
		return false;
	}

	// public static boolean isBender(Player player, BendingType type) {
	// //return config.isBender(player, type);
	// return Bending.benders.get(player.getName()).contains(type);
	// }

	public static boolean isBender(String player, BendingType type) {
		// return config.isBender(player, type);
		// if (Bending.benders.contains(player))
		BendingPlayer bPlayer = BendingPlayer.getBendingPlayer(player);
		if (bPlayer == null)
			return false;
		return bPlayer.isBender(type);
		// if (Bending.benders.containsKey(player))
		// return Bending.benders.get(player).contains(type);
		// return false;
	}

	public static boolean isBender(String player) {
		// return config.isBender(player, type);
		BendingPlayer bPlayer = BendingPlayer.getBendingPlayer(player);
		if (bPlayer == null)
			return false;
		return bPlayer.isBender();
		// if (Bending.benders.containsKey(player)) {
		// if (Bending.benders.get(player).size() > 0)
		// return true;
		// }
		// return false;
	}

	public static Abilities getBendingAbility(Player player) {
		BendingPlayer bPlayer = BendingPlayer.getBendingPlayer(player);
		if (bPlayer == null)
			return null;
		return bPlayer.getAbility();
		// return config.getAbility(player);
	}

	public static List<BendingType> getBendingTypes(Player player) {
		BendingPlayer bPlayer = BendingPlayer.getBendingPlayer(player);
		if (bPlayer == null)
			return null;
		return bPlayer.getBendingTypes();
		// return config.getBendingTypes(player);
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
			Bending.log.info("[Bending] " + something.toString());
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

	// public static void updatePhysics(Block block) {
	// Tools.verbose(Bending.plugin.getServer().getBukkitVersion());
	// // CraftWorld world = (CraftWorld) block.getWorld();
	// // world.getHandle().applyPhysics(block.getX(), block.getY(),
	// // block.getZ(), block.getTypeId());
	// }

	public static Entity getTargettedEntity(Player player, double range) {
		return getTargettedEntity(player, range, new ArrayList<Entity>());
	}

	public static Entity getTargettedEntity(Player player, double range,
			List<Entity> avoid) {
		double longestr = range + 1;
		Entity target = null;
		Location origin = player.getEyeLocation();
		Vector direction = player.getEyeLocation().getDirection().normalize();
		for (Entity entity : origin.getWorld().getEntities()) {
			if (avoid.contains(entity))
				continue;
			if (entity.getLocation().distance(origin) < longestr
					&& getDistanceFromLine(direction, origin,
							entity.getLocation()) < 2
							&& (entity instanceof LivingEntity)
							&& entity.getEntityId() != player.getEntityId()
							&& entity.getLocation().distance(
									origin.clone().add(direction)) < entity
									.getLocation().distance(
											origin.clone().add(
													direction.clone().multiply(-1)))) {
				target = entity;
				longestr = entity.getLocation().distance(origin);
			}
		}
		return target;
	}

	public static void damageEntity(Player player, Entity entity, double damage) {
		if (entity instanceof LivingEntity) {
			if (AvatarState.isAvatarState(player)) {
				damage = AvatarState.getValue(damage);
			}

			// EntityDamageByEntityEvent event = new EntityDamageByEntityEvent(
			// player, entity, DamageCause.CUSTOM, damage);
			// Bending.plugin.getServer().getPluginManager().callEvent(event);
			// verbose(event.isCancelled());

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
		if (block.getType() == Material.ICE || block.getType() == Material.SNOW) {
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
			if (FreezeMelt.frozenblocks.containsKey(blocki)) {
				if (FreezeMelt.frozenblocks.get(blocki) == full)
					sources++;
			} else if (blocki.getType() == Material.ICE) {
				sources++;
			}
		}
		if (sources >= 2)
			return true;
		return false;
	}

	public static boolean adjacentToAnyWater(Block block) {
		BlockFace[] faces = { BlockFace.EAST, BlockFace.WEST, BlockFace.NORTH,
				BlockFace.SOUTH, BlockFace.UP, BlockFace.DOWN };
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
		AirShield.instances.clear();
		AirSuction.instances.clear();
		AirScooter.removeAll();
		AirSpout.removeAll();
		AirSwipe.instances.clear();
		Speed.instances.clear();
		Tornado.instances.clear();
		AirBurst.removeAll();

		Catapult.removeAll();
		CompactColumn.removeAll();
		EarthBlast.removeAll();
		EarthColumn.removeAll();
		EarthPassive.removeAll();
		EarthArmor.removeAll();
		EarthTunnel.instances.clear();
		Shockwave.removeAll();
		Tremorsense.removeAll();

		FreezeMelt.removeAll();
		IceSpike.removeAll();
		IceSpike2.removeAll();
		WaterManipulation.removeAll();
		WaterSpout.removeAll();
		WaterWall.removeAll();
		Wave.removeAll();
		Plantbending.regrowAll();
		OctopusForm.removeAll();
		Bloodbending.instances.clear();

		FireStream.removeAll();
		Fireball.removeAll();
		WallOfFire.instances.clear();
		Lightning.instances.clear();
		FireShield.removeAll();
		FireBlast.removeAll();
		FireBurst.removeAll();
		FireJet.instances.clear();
		Cook.removeAll();
		Illumination.removeAll();

		RapidPunch.instance.clear();

		// BendingManager.removeFlyers();
		Flight.removeAll();
		WaterReturn.removeAll();
		TempBlock.removeAll();
		removeAllEarthbendedBlocks();
	}

	public static boolean canBend(Player player, Abilities ability) {
		if (ability == null)
			return false;
		if (hasPermission(player, ability) && ability == Abilities.AvatarState)
			return true;

		if (!hasPermission(player, ability))
			return false;

		if ((isChiBlocked(player) || Bloodbending.isBloodbended(player)))
			return false;

		if (Abilities.isAirbending(ability)
				&& !isBender(player.getName(), BendingType.Air)) {
			return false;
		}

		if (Abilities.isChiBlocking(ability)
				&& !isBender(player.getName(), BendingType.ChiBlocker)) {
			return false;
		}

		if (Abilities.isEarthbending(ability)
				&& !isBender(player.getName(), BendingType.Earth)) {
			return false;
		}

		if (Abilities.isFirebending(ability)
				&& !isBender(player.getName(), BendingType.Fire)) {
			return false;
		}

		if (Abilities.isWaterbending(ability)
				&& !isBender(player.getName(), BendingType.Water)) {
			return false;
		}

		if (hasPermission(player, ability)
				&& (!isLocalAbility(ability) || !isRegionProtectedFromBuild(
						player, Abilities.AirBlast, player.getLocation()))
						&& !toggledBending(player))
			return true;

		if (allowharmless && Tools.isHarmlessAbility(ability)
				&& !toggledBending(player))
			return true;
		return false;

	}

	public static boolean canBeBloodbent(Player player) {
		if (AvatarState.isAvatarState(player))
			return false;
		if ((isChiBlocked(player)))
			return true;
		Abilities ability = Abilities.Bloodbending;
		if (canBend(player, ability) && !toggledBending(player))
			return false;
		return true;
	}

	public static boolean isHarmlessAbility(Abilities ability) {
		return Arrays.asList(harmlessAbilities).contains(ability);
	}

	public static boolean isLocalAbility(Abilities ability) {
		return Arrays.asList(localAbilities).contains(ability);
	}

	public static boolean isRangedAbility(Abilities ability) {
		return !isLocalAbility(ability);
	}

	public static boolean toggledBending(Player player) {
		if (toggledBending.contains(player))
			return true;
		return false;
	}

	public static void printHooks() {
		Plugin wgp = Bukkit.getPluginManager().getPlugin("WorldGuard");
		if (wgp != null) {
			verbose("Recognized WorldGuard...");
			if (respectWorldGuard) {
				verbose("Bending is set to respect WorldGuard's build flags.");
			} else {
				verbose("But Bending is set to ignore WorldGuard's flags.");
			}
		}

		Plugin psp = Bukkit.getPluginManager().getPlugin("PreciousStone");
		if (psp != null) {
			verbose("Recognized PreciousStones...");
			if (respectPreciousStones) {
				verbose("Bending is set to respect PreciousStones' build flags.");
			} else {
				verbose("But Bending is set to ignore PreciousStones' flags.");
			}
		}

		Plugin fcp = Bukkit.getPluginManager().getPlugin("Factions");
		if (fcp != null) {
			verbose("Recognized Factions...");
			if (respectFactions) {
				verbose("Bending is set to respect Factions' claimed lands.");
			} else {
				verbose("But Bending is set to ignore Factions' claimed lands.");
			}
		}

		Plugin twnp = Bukkit.getPluginManager().getPlugin("Towny");
		if (twnp != null) {
			verbose("Recognized Towny...");
			if (respectTowny) {
				verbose("Bending is set to respect Towny's towns.");
			} else {
				verbose("But Bending is set to ignore Towny.");
			}
		}

		// Plugin lgbk = Bukkit.getPluginManager().getPlugin("LogBlock");
		// if (lgbk != null) {
		// verbose("Recognized LogBlock...");
		// if (logblockhook) {
		// verbose("Bending is set to log to LogBlock.");
		// } else {
		// verbose("But Bending isn't set to hook into LogBlock.");
		// }
		// }
	}

	public static boolean isRegionProtectedFromBuild(Player player,
			Abilities ability, Location loc) {

		List<Abilities> ignite = new ArrayList<Abilities>();
		ignite.add(Abilities.Blaze);
		List<Abilities> explode = new ArrayList<Abilities>();
		explode.add(Abilities.FireBlast);
		explode.add(Abilities.Lightning);

		if (ability == null && allowharmless)
			return false;
		if (isHarmlessAbility(ability) && allowharmless)
			return false;

		// if (ignite.contains(ability)) {
		// BlockIgniteEvent event = new BlockIgniteEvent(location.getBlock(),
		// IgniteCause.FLINT_AND_STEEL, player);
		// Bending.plugin.getServer().getPluginManager().callEvent(event);
		// if (event.isCancelled())
		// return false;
		// event.setCancelled(true);
		// }

		PluginManager pm = Bukkit.getPluginManager();

		Plugin wgp = pm.getPlugin("WorldGuard");
		Plugin psp = pm.getPlugin("PreciousStone");
		Plugin fcp = pm.getPlugin("Factions");
		Plugin twnp = pm.getPlugin("Towny");
		Plugin gpp = pm.getPlugin("GriefPrevention");
		Plugin mcore = pm.getPlugin("mcore");

		for (Location location : new Location[] { loc, player.getLocation() }) {

			if (wgp != null && respectWorldGuard) {
				WorldGuardPlugin wg = (WorldGuardPlugin) Bukkit
						.getPluginManager().getPlugin("WorldGuard");
				if (!player.isOnline())
					return true;

				if (ignite.contains(ability)) {
					if (!wg.hasPermission(player, "worldguard.override.lighter")) {
						if (wg.getGlobalStateManager().get(location.getWorld()).blockLighter)
							return true;
						if (!wg.getGlobalRegionManager().hasBypass(player,
								location.getWorld())
								&& !wg.getGlobalRegionManager()
								.get(location.getWorld())
								.getApplicableRegions(location)
								.allows(DefaultFlag.LIGHTER,
										wg.wrapPlayer(player)))
							return true;
					}

				}

				if (explode.contains(ability)) {
					if (wg.getGlobalStateManager().get(location.getWorld()).blockTNTExplosions)
						return true;
					if (!wg.getGlobalRegionManager().get(location.getWorld())
							.getApplicableRegions(location)
							.allows(DefaultFlag.TNT))
						return true;
				}

				if ((!(wg.getGlobalRegionManager().canBuild(player, location)) || !(wg
						.getGlobalRegionManager()
						.canConstruct(player, location)))) {
					return true;
				}
			}

			if (psp != null && respectPreciousStones) {
				PreciousStones ps = (PreciousStones) psp;

				if (ignite.contains(ability)) {
					if (ps.getForceFieldManager().hasSourceField(location,
							FieldFlag.PREVENT_FIRE))
						return true;
				}

				if (explode.contains(ability)) {
					if (ps.getForceFieldManager().hasSourceField(location,
							FieldFlag.PREVENT_EXPLOSIONS))
						return true;
				}

				if (ps.getForceFieldManager().hasSourceField(location,
						FieldFlag.PREVENT_PLACE))
					return true;
			}

			if (fcp != null && mcore != null && respectFactions) {
				if (ignite.contains(ability)) {

				}

				if (explode.contains(ability)) {

				}

				if (!FactionsListenerMain.canPlayerBuildAt(player,
						PS.valueOf(loc.getBlock()), false)) {
					return true;
				}

				// if (!FactionsBlockListener.playerCanBuildDestroyBlock(player,
				// location, "build", true)) {
				// return true;
				// }
			}

			if (twnp != null && respectTowny) {
				Towny twn = (Towny) twnp;

				WorldCoord worldCoord;

				try {
					TownyWorld world = TownyUniverse.getDataSource().getWorld(
							location.getWorld().getName());
					worldCoord = new WorldCoord(world.getName(),
							Coord.parseCoord(location));

					boolean bBuild = PlayerCacheUtil.getCachePermission(player,
							location, 3, (byte) 0,
							TownyPermission.ActionType.BUILD);

					if (ignite.contains(ability)) {

					}

					if (explode.contains(ability)) {

					}

					if (!bBuild) {
						PlayerCache cache = twn.getCache(player);
						TownBlockStatus status = cache.getStatus();

						if (((status == TownBlockStatus.ENEMY) && TownyWarConfig
								.isAllowingAttacks())) {

							try {
								TownyWar.callAttackCellEvent(twn, player,
										location.getBlock(), worldCoord);
							} catch (Exception e) {
								TownyMessaging.sendErrorMsg(player,
										e.getMessage());
							}

							return true;

						} else if (status == TownBlockStatus.WARZONE) {
						} else {
							return true;
						}

						if ((cache.hasBlockErrMsg()))
							TownyMessaging.sendErrorMsg(player,
									cache.getBlockErrMsg());
					}

				} catch (Exception e1) {
					TownyMessaging.sendErrorMsg(player, TownySettings
							.getLangString("msg_err_not_configured"));
				}

			}

			if (gpp != null && respectGriefPrevention) {
				String reason = GriefPrevention.instance.allowBuild(player,
						location);

				if (ignite.contains(ability)) {

				}

				if (explode.contains(ability)) {

				}

				if (reason != null)
					return true;
			}
		}

		return false;
	}

	// public static boolean isRegionProtected(Player player, Abilities ability,
	// boolean look) {
	//
	// Plugin wgp = Bukkit.getPluginManager().getPlugin("WorldGuard");
	// Plugin psp = Bukkit.getPluginManager().getPlugin("PreciousStone");
	// Plugin fcp = Bukkit.getPluginManager().getPlugin("Factions");
	// Plugin twnp = Bukkit.getPluginManager().getPlugin("Towny");
	//
	// if (wgp != null && respectWorldGuard) {
	// WorldGuardPlugin wg = (WorldGuardPlugin) Bukkit.getPluginManager()
	// .getPlugin("WorldGuard");
	// if (!player.isOnline())
	// return true;
	// if (look) {
	// int range = 20;
	// Block c = player.getTargetBlock(null, range);
	// if ((!(wg.getGlobalRegionManager().canBuild(player,
	// c.getLocation())) || !(wg.getGlobalRegionManager()
	// .canConstruct(player, c.getLocation())))) {
	// return true;
	// }
	// } else if (!(wg.getGlobalRegionManager().canBuild(player,
	// player.getLocation()))
	// || !(wg.getGlobalRegionManager().canConstruct(player,
	// player.getLocation()))) {
	// return true;
	// }
	// }
	//
	// if (psp != null && respectPreciousStones) {
	// PreciousStones ps = (PreciousStones) psp;
	// Block b = player.getLocation().getBlock();
	//
	// if (look) {
	//
	// int range = 20;
	// Block c = player.getTargetBlock(null, range);
	// if (ps.getForceFieldManager().hasSourceField(c.getLocation(),
	// FieldFlag.PREVENT_PLACE))
	// return true;
	//
	// } else {
	// if (ps.getForceFieldManager().hasSourceField(
	// player.getLocation(), FieldFlag.PREVENT_PLACE))
	// return true;
	// }
	//
	// if (ps.getForceFieldManager().hasSourceField(b.getLocation(),
	// FieldFlag.PREVENT_PLACE))
	// return true;
	// }
	//
	// if (fcp != null && respectFactions) {
	// if (isLocalAbility(ability)
	// && !FactionsBlockListener.playerCanBuildDestroyBlock(
	// player, player.getLocation(), "build", false)) {
	// return true;
	// } else if (!isLocalAbility(ability)
	// && !FactionsBlockListener.playerCanBuildDestroyBlock(
	// player, getTargetedLocation(player, 20), "build",
	// false)) {
	// return true;
	// }
	// }
	//
	// if (twnp != null && respectTowny) {
	// Towny twn = (Towny) twnp;
	// Block block;
	// if (isLocalAbility(ability)) {
	// block = player.getLocation().getBlock();
	// } else {
	// block = player.getTargetBlock(null, 20);
	// }
	//
	// WorldCoord worldCoord;
	//
	// try {
	// TownyWorld world = TownyUniverse.getDataSource().getWorld(
	// block.getWorld().getName());
	// worldCoord = new WorldCoord(world.getName(),
	// Coord.parseCoord(block));
	//
	// // Get build permissions (updates if none exist)
	// boolean bBuild = PlayerCacheUtil.getCachePermission(player,
	// block.getLocation(), 3, (byte) 0,
	// TownyPermission.ActionType.BUILD);
	//
	// // Allow build if we are permitted
	// if (!bBuild) {
	//
	// /*
	// * Fetch the players cache
	// */
	// PlayerCache cache = twn.getCache(player);
	// TownBlockStatus status = cache.getStatus();
	//
	// /*
	// * Flag war
	// */
	// if (((status == TownBlockStatus.ENEMY) && TownyWarConfig
	// .isAllowingAttacks())) {
	//
	// try {
	// TownyWar.callAttackCellEvent(twn, player, block,
	// worldCoord);
	// } catch (Exception e) {
	// TownyMessaging.sendErrorMsg(player, e.getMessage());
	// }
	//
	// return true;
	//
	// } else if (status == TownBlockStatus.WARZONE) {
	// } else {
	// return true;
	// }
	//
	// /*
	// * display any error recorded for this plot
	// */
	// if ((cache.hasBlockErrMsg()))
	// TownyMessaging.sendErrorMsg(player,
	// cache.getBlockErrMsg());
	// }
	//
	// } catch (Exception e1) {
	// TownyMessaging.sendErrorMsg(player,
	// TownySettings.getLangString("msg_err_not_configured"));
	// }
	//
	// }
	//
	// // EntityDamageByEntityEvent damageEvent = new
	// // EntityDamageByEntityEvent(player, player,
	// // EntityDamageEvent.DamageCause.ENTITY_ATTACK, 1);
	// // Bukkit.getServer().getPluginManager().callEvent(damageEvent);
	//
	// // if (damageEvent.isCancelled())
	// // {
	// // return true;
	// // }
	// // }
	//
	// return false;
	// }

	public static boolean canBendPassive(Player player, BendingType type) {
		if ((isChiBlocked(player) || Bloodbending.isBloodbended(player))
				&& !AvatarState.isAvatarState(player))
			return false;
		if (!player.hasPermission("bending." + type + ".passive")) {
			return false;
		}
		if (allowharmless && type != BendingType.Earth)
			return true;
		if (isRegionProtectedFromBuild(player, null, player.getLocation()))
			return false;
		return true;
	}

	public static boolean hasPermission(Player player, Abilities ability) {
		if (ability == Abilities.AvatarState
				&& player.hasPermission("bending.admin.AvatarState")) {
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
		if (Abilities.isChiBlocking(ability)
				&& player.hasPermission("bending.chiblocker." + ability)) {
			return true;
		}
		if (Abilities.isChiBlocking(ability)
				&& player.hasPermission("bending.chiblocking." + ability)) {
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
			return ConfigValues.FireDayPowerFactor * value;
		}
		return value;
	}

	public static double getFirebendingDayAugment(World world) {
		if (isDay(world))
			return ConfigValues.FireDayPowerFactor;
		return 1;
	}

	public static double waterbendingNightAugment(double value, World world) {
		if (isNight(world)) {
			return ConfigValues.WaterNightPowerFactor * value;
		}
		return value;
	}

	public static double getWaterbendingNightAugment(World world) {
		if (isNight(world))
			return ConfigValues.WaterNightPowerFactor;
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

	public static void blockChi(Player player, long time) {
		if (blockedchis.containsKey(player)) {
			blockedchis.replace(player, time);
		} else {
			blockedchis.put(player, time);
		}
	}

	public static boolean isChiBlocked(Player player) {
		if (Paralyze.isParalyzed(player) && !AvatarState.isAvatarState(player))
			return true;
		if (blockedchis.containsKey(player)) {
			long time = System.currentTimeMillis();
			if (time > blockedchis.get(player) + ConfigValues.ChiBlockDuration
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

	public static <T> void writeToLog(T something) {
		String string = "";
		if (something != null) {
			string = something.toString();
		}
		try {
			FileWriter fstream = new FileWriter("bending.log", true);
			BufferedWriter out = new BufferedWriter(fstream);
			out.write(string);
			out.newLine();
			out.close();
		} catch (Exception e) {
			System.err.println("Error: " + e.getMessage());
		}

	}

	public static void removeBlock(Block block) {
		if (adjacentToThreeOrMoreSources(block)) {
			block.setType(Material.WATER);
			block.setData((byte) 0x0);
		} else {
			block.setType(Material.AIR);
		}
	}

	public static void removeSpouts(Location location, double radius,
			Player sourceplayer) {
		WaterSpout.removeSpouts(location, radius, sourceplayer);
		AirSpout.removeSpouts(location, radius, sourceplayer);
	}

	public static void removeSpouts(Location location, Player sourceplayer) {
		removeSpouts(location, 1.5, sourceplayer);
	}

	public static Block getEarthSourceBlock(Player player, double range) {
		Block testblock = player.getTargetBlock(getTransparentEarthbending(),
				(int) range);
		if (Tools.isEarthbendable(player, testblock))
			return testblock;
		Location location = player.getEyeLocation();
		Vector vector = location.getDirection().clone().normalize();
		for (double i = 0; i <= range; i++) {
			Block block = location.clone().add(vector.clone().multiply(i))
					.getBlock();
			if (isRegionProtectedFromBuild(player, Abilities.RaiseEarth,
					location))
				continue;
			if (isEarthbendable(player, block)) {
				return block;
			}
		}
		return null;
	}

	public static Block getWaterSourceBlock(Player player, double range,
			boolean plantbending) {
		// byte full = 0x0;
		// Block block = player.getTargetBlock(null, range);
		Location location = player.getEyeLocation();
		Vector vector = location.getDirection().clone().normalize();
		for (double i = 0; i <= range; i++) {
			Block block = location.clone().add(vector.clone().multiply(i))
					.getBlock();
			if (isRegionProtectedFromBuild(player, Abilities.WaterManipulation,
					location))
				continue;
			if (isWaterbendable(block, player)
					&& (!isPlant(block) || plantbending)) {
				if (TempBlock.isTempBlock(block)) {
					TempBlock tb = TempBlock.get(block);
					byte full = 0x0;
					if (tb.state.getRawData() != full
							&& (tb.state.getType() != Material.WATER || tb.state
							.getType() != Material.STATIONARY_WATER)) {
						continue;
					}
				}
				return block;
			}
			// } else if ((block.getType() == Material.WATER || block.getType()
			// == Material.STATIONARY_WATER)
			// && block.getData() == full) {
			// return block;
			// }
		}
		return null;
	}

	public static void playFocusWaterEffect(Block block) {
		block.getWorld().playEffect(block.getLocation(), Effect.SMOKE, 4, 20);
	}

	public static BlockFace getCardinalDirection(Vector vector) {
		BlockFace[] faces = { BlockFace.NORTH, BlockFace.NORTH_EAST,
				BlockFace.EAST, BlockFace.SOUTH_EAST, BlockFace.SOUTH,
				BlockFace.SOUTH_WEST, BlockFace.WEST, BlockFace.NORTH_WEST };
		Vector n, ne, e, se, s, sw, w, nw;
		w = new Vector(-1, 0, 0);
		n = new Vector(0, 0, -1);
		s = n.clone().multiply(-1);
		e = w.clone().multiply(-1);
		ne = n.clone().add(e.clone()).normalize();
		se = s.clone().add(e.clone()).normalize();
		nw = n.clone().add(w.clone()).normalize();
		sw = s.clone().add(w.clone()).normalize();

		Vector[] vectors = { n, ne, e, se, s, sw, w, nw };

		double comp = 0;
		int besti = 0;
		for (int i = 0; i < vectors.length; i++) {
			double dot = vector.dot(vectors[i]);
			if (dot > comp) {
				comp = dot;
				besti = i;
			}
		}

		return faces[besti];

	}

	public static String getSupportedLanguages() {
		String languages = "";
		List<String> suplangs = Bending.language.getSupportedLanguages();
		for (int i = 0; i < suplangs.size(); i++) {
			String string = suplangs.get(i);
			if (i != suplangs.size() - 1) {
				string = string + ", ";
			}
			languages = languages + string;
		}
		return languages;
	}

	public static String getDefaultLanguage() {
		return Bending.language.getDefaultLanguage();
	}

	public static void sendMessage(Player player, String key) {
		sendMessage(player, ChatColor.WHITE, key);
	}

	public static void sendMessage(Player player, ChatColor color, String key) {
		String message = getMessage(player, key);
		if (player == null) {
			verbose(color + message);
		} else {
			player.sendMessage(color + message);
		}
	}

	public static String getMessage(Player player, String key) {
		String language = getLanguage(player);
		String message = Bending.language.getMessage(language, key);
		return message;
	}

	public static String getLanguage(Player player) {
		String language = getDefaultLanguage();
		if (player != null)
			language = BendingPlayer.getBendingPlayer(player).getLanguage();
		return language;
	}

	public static boolean isLanguageSupported(String language) {
		return (Bending.language.getSupportedLanguages().contains(language
				.toLowerCase()));
	}

	// public static void logBending(Player player, Abilities ability) {
	// if (Bending.logblock != null) {
	//
	// }
	// }

	public static int getIntCardinalDirection(Vector vector) {
		BlockFace face = getCardinalDirection(vector);

		switch (face) {
		case SOUTH:
			return 7;
		case SOUTH_WEST:
			return 6;
		case WEST:
			return 3;
		case NORTH_WEST:
			return 0;
		case NORTH:
			return 1;
		case NORTH_EAST:
			return 2;
		case EAST:
			return 5;
		case SOUTH_EAST:
			return 8;
		}

		return 4;

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
	
	public static void configCheck() {
		Plugin bending = Bukkit.getPluginManager().getPlugin("Bending");
		FileConfiguration config = bending.getConfig();
		bending.getConfig().addDefault("Chat.enabled", true);
		bending.getConfig().addDefault("Chat.colors", true);
		bending.getConfig().addDefault("Chat.Compatibility", false);
		bending.getConfig().addDefault("Chat.Format", "<name>: <message>");
		bending.getConfig().addDefault("Chat.Prefix.Avatar", "[Avatar] ");
		bending.getConfig().addDefault("Chat.Prefix.Air", "[Airbender] ");
		bending.getConfig().addDefault("Chat.Prefix.Water", "[Waterbender] ");
		bending.getConfig().addDefault("Chat.Prefix.Earth", "[Earthbender] ");
		bending.getConfig().addDefault("Chat.Prefix.Fire", "[Firebender] ");
		bending.getConfig().addDefault("Chat.Prefix.Chi", "[Chiblocker] ");
		bending.getConfig().addDefault("Chat.Color.Avatar", "DARK_PURPLE");
		bending.getConfig().addDefault("Chat.Color.Air", "GRAY");
		bending.getConfig().addDefault("Chat.Color.Water", "AQUA");
		bending.getConfig().addDefault("Chat.Color.Earth", "GREEN");
		bending.getConfig().addDefault("Chat.Color.Fire", "RED");
		bending.getConfig().addDefault("Chat.Color.Chi", "GOLD");
		
		//Options
		bending.getConfig().addDefault("Options.Bend-To-Item", false);
		bending.getConfig().addDefault("Options.Use-TagAPI", true);
		bending.getConfig().addDefault("Options.SeaLevel", 62);
		
		//Abilities
		bending.getConfig().addDefault("Abilities.GlobalCooldown", 500);
		
		//Air
		//Settings
		bending.getConfig().addDefault("Abilities.Air.Settings.BendWithWeapons", false);
		//AirBlast
		bending.getConfig().addDefault("Abilities.Air.AirBlast.Speed", 25.0);
		bending.getConfig().addDefault("Abilities.Air.AirBlast.Range", 25.0);
		bending.getConfig().addDefault("Abilities.Air.AirBlast.Radius", 2.0);
		bending.getConfig().addDefault("Abilities.Air.AirBlast.Push", 3.5);
		
		//AirBubble
		bending.getConfig().addDefault("Abilities.Air.AirBubble.Radius", 6);
		
		//AirBurst
		bending.getConfig().addDefault("Abilities.Air.AirBurst.PushFactor", 1.5);
		bending.getConfig().addDefault("Abilities.Air.AirBurst.ChargeTime", 1750);
		
		//AirScooter
		bending.getConfig().addDefault("Abilities.Air.AirScooter.Speed", .0675);
		bending.getConfig().addDefault("Abilities.Air.AirScooter.Radius", 1);
		
		//AirShield
		bending.getConfig().addDefault("Abilities.Air.AirShield.Radius", 7.0);
		
		//AirSpout
		bending.getConfig().addDefault("Abilities.Air.AirSpout.Height", 20.0);
		
		//AirSuction
		bending.getConfig().addDefault("Abilities.Air.AirSuction.Speed", 25.0);
		bending.getConfig().addDefault("Abilities.Air.AirSuction.Range", 20.0);
		bending.getConfig().addDefault("Abilities.Air.AirSuction.Radius", 2.0);
		bending.getConfig().addDefault("Abilities.Air.AirSuction.Push", 3.5);

		//AirSwipe
		bending.getConfig().addDefault("Abilities.Air.AirSwipe.Damage", 2);
		bending.getConfig().addDefault("Abilities.Air.AirSwipe.Radius", 2.0);
		bending.getConfig().addDefault("Abilities.Air.AirSwipe.Push", 1.0);
		bending.getConfig().addDefault("Abilities.Air.AirSwipe.Range", 16.0);
		bending.getConfig().addDefault("Abilities.Air.AirSwipe.ArcSize", 20);
		bending.getConfig().addDefault("Abilities.Air.AirSwipe.Speed", 25.0);
		bending.getConfig().addDefault("Abilities.Air.AirSwipe.Cooldown", 1500);
		
		//Passive
		bending.getConfig().addDefault("Abilities.Air.Passive.Factor", 0.3);
		
		//Tornado
		bending.getConfig().addDefault("Abilities.Air.Tornado.Radius", 10.0);
		bending.getConfig().addDefault("Abilities.Air.Tornado.Height", 25.0);
		bending.getConfig().addDefault("Abilities.Air.Tornado.Range", 25.0);
		bending.getConfig().addDefault("Abilities.Air.Tornado.MobPush", 1.0);
		bending.getConfig().addDefault("Abilities.Air.Tornado.PlayerPush", 1.0);

		//Water
		//Settings
		bending.getConfig().addDefault("Abilities.Water.Settings.NightPowerFactor", 1.5);
		config.addDefault("Abilities.Water.Settings.BendWithWeapons", true);
		
		//Bloodbending
		bending.getConfig().addDefault("Abilities.Water.Bloodbending.ThrowFactor", 2.0);
		bending.getConfig().addDefault("Abilities.Water.Bloodbending.Range", 2.0);
		
		//FastSwimming
		bending.getConfig().addDefault("Abilities.Water.FastSwimming.Factor", 0.7);

		//PhaseChange
		bending.getConfig().addDefault("Abilities.Water.PhaseChange.Range", 20);
		bending.getConfig().addDefault("Abilities.Water.PhaseChange.Radius", 5);
		
		//HealingWaters
		bending.getConfig().addDefault("Abilities.Water.HealingWaters.Radius", 5.0);
		bending.getConfig().addDefault("Abilities.Water.HealingWaters.Interval", 750);
		
		//IceSpike
		bending.getConfig().addDefault("Abilities.Water.IceSpike.Height", 6);
		bending.getConfig().addDefault("Abilities.Water.IceSpike.Range", 20);
		bending.getConfig().addDefault("Abilities.Water.IceSpike.Cooldown", 2000);
		bending.getConfig().addDefault("Abilities.Water.IceSpike.Damage", 2);
		bending.getConfig().addDefault("Abilities.Water.IceSpike.ThrowingMult", 1.0);

		//OctopusForm
		bending.getConfig().addDefault("Abilities.Water.OctopusForm.Range", 10);
		bending.getConfig().addDefault("Abilities.Water.OctopusForm.Radius", 3);
		bending.getConfig().addDefault("Abilities.Water.OctopusForm.Interval", 50);
		bending.getConfig().addDefault("Abilities.Water.OctopusForm.Damage", 3);
		
		//Plantbending
		bending.getConfig().addDefault("Abilities.Water.Plantbending.RegrowthTime", 180000);
		
		//Surge
		bending.getConfig().addDefault("Abilities.Water.Surge.Wave.Radius", 3.0);
		bending.getConfig().addDefault("Abilities.Water.Surge.Wave.HorizontalPush", 1.0);
		bending.getConfig().addDefault("Abilities.Water.Surge.Wave.VerticalPush", 0.2);
		bending.getConfig().addDefault("Abilities.Water.Surge.Wall.Range", 5.0);
		bending.getConfig().addDefault("Abilities.Water.Surge.Wall.Radius", 2.0);

		//Torrent
		bending.getConfig().addDefault("Abilities.Water.Torrent.Range", 25);
		bending.getConfig().addDefault("Abilities.Water.Torrent.Radius", 3);
		bending.getConfig().addDefault("Abilities.Water.Torrent.Damage", 2);
		bending.getConfig().addDefault("Abilities.Water.Torrent.DeflectDamage", 1);
		bending.getConfig().addDefault("Abilities.Water.Torrent.Factor", 1);
		
		//WaterManipulation
		bending.getConfig().addDefault("Abilities.Water.WaterManipulation.Range", 20.0);
		bending.getConfig().addDefault("Abilities.Water.WaterManipulation.Push", 0.3);
		bending.getConfig().addDefault("Abilities.Water.WaterManipulation.Damage", 4);
		bending.getConfig().addDefault("Abilities.Water.WaterManipulation.Speed", 35.0);

		//WaterSpout
		bending.getConfig().addDefault("Abilities.Water.WaterSpout.Height", 15);
		
		//WaterBubble
		bending.getConfig().addDefault("Abilities.Water.WaterBubble", 7);

		//Earth
		//Settings
		config.addDefault("Abilities.Earth.Settings.BendWithWeapons", true);
		bending.getConfig().addDefault("Abilities.Earth.Settings.ReverseEarthbending", true);
		bending.getConfig().addDefault("Abilities.Earth.Settings.ReverseEarthbendingCheckTime", 300000);
		List<String> earthbendable = new ArrayList<String>();
		earthbendable.add("STONE");
		earthbendable.add("CLAY");
		earthbendable.add("COAL_ORE");
		earthbendable.add("DIAMOND_ORE");
		earthbendable.add("DIRT");
		earthbendable.add("GOLD_ORE");
		earthbendable.add("EMERALD_ORE");
		earthbendable.add("GRASS");
		earthbendable.add("GRAVEL");
		earthbendable.add("IRON_ORE");
		earthbendable.add("LAPIS_ORE");
		earthbendable.add("REDSTONE_ORE");
		earthbendable.add("SAND");
		earthbendable.add("SANDSTONE");
		earthbendable.add("GLOWING_REDSTONE_ORE");
		earthbendable.add("MYCEL");
		bending.getConfig().addDefault("Abilities.Earth.Settings.EarthBendable", earthbendable);
		
		//Catapult
		bending.getConfig().addDefault("Abilities.Earth.Catapult.Length", 7);
		bending.getConfig().addDefault("Abilities.Earth.Catapult.Speed", 12.0);
		bending.getConfig().addDefault("Abilities.Earth.Catapult.Push", 5.0);
		
		//Collapse
		bending.getConfig().addDefault("Abilities.Earth.Collapse.Range", 20);
		bending.getConfig().addDefault("Abilities.Earth.Collapse.Radius", 7.0);
		
		//EarthArmor
		bending.getConfig().addDefault("Abilities.Earth.EarthArmor.Duration", 10000);
		bending.getConfig().addDefault("Abilities.Earth.EarthArmor.Strength", 2);
		bending.getConfig().addDefault("Abilities.Earth.EarthArmor.Cooldown", 17500);

		//EarthBlast
		bending.getConfig().addDefault("Abilities.Earth.EarthBlast.HitSelf", false);
		bending.getConfig().addDefault("Abilities.Earth.EarthBlast.Range", 20.0);
		bending.getConfig().addDefault("Abilities.Earth.EarthBlast.PrepareRange", 7.0);
		bending.getConfig().addDefault("Abilities.Earth.EarthBlast.Revert", true);
		bending.getConfig().addDefault("Abilities.Earth.EarthBlast.Damage", 4);
		bending.getConfig().addDefault("Abilities.Earth.EarthBlast.Speed", 35.0);
		bending.getConfig().addDefault("Abilities.Earth.EarthBlast.Push", 0.3);

		//EarthGrab
		config.addDefault("Abilities.Earth.EarthGrab.Range", 15.0);
		
		//EarthTunnel
		config.addDefault("Abilities.Earth.EarthTunnel.Radius", 0.25);
		config.addDefault("Abilities.Earth.EarthTunnel.Maxradius", 1);
		config.addDefault("Abilities.Earth.EarthTunnel.Range", 10.0);
		config.addDefault("Abilities.Earth.EarthTunnel.Revert", true);
		config.addDefault("Abilities.Earth.EarthTunnel.Interval", 30);
		
		//Passive
		config.addDefault("Abilities.Earth.Passive.WaitBeforeRevert", 3000);
		
		//RaiseEarth
		config.addDefault("Abilities.Earth.RaiseEarth.Height", 6);
		config.addDefault("Abilities.Earth.RaiseEarth.Range", 15);
		config.addDefault("Abilities.Earth.RaiseEarth.Width", 6);
		
		//Shockwave
		config.addDefault("Abilities.Earth.Shockwave.ChargeTime", 2500);
		
		//Tremorsense
		config.addDefault("Abilities.Earth.Tremorsense.MaxDepth", 10);
		config.addDefault("Abilities.Earth.Tremorsense.Radius", 5);
		config.addDefault("Abilities.Earth.Tremorsense.LightThreshold", 7);
		config.addDefault("Abilities.Earth.Tremorsense.Cooldown", 1000);

		//Fire
		//Settings
		config.addDefault("Abilities.Fire.Settings.BendWithWeapons", true);
		config.addDefault("Abilities.Fire.Settings.DissippateTime", 400);
		config.addDefault("Abilities.Fire.Settings.DayPowerFactor", 1.5);
		
		//Blaze
		config.addDefault("Abilities.Fire.Blaze.Size", 20);
		config.addDefault("Abilities.Fire.Blaze.Range", 9);
		
		//HeatControl
		config.addDefault("Abilities.Fire.HeatControl.Range", 15);
		config.addDefault("Abilities.Fire.HeatControl.Radius", 15);
		
		//FireBlast
		config.addDefault("Abilities.Fire.FireBlast.Radius", 2.0);
		config.addDefault("Abilities.Fire.FireBlast.Speed", 15.0);
		config.addDefault("Abilities.Fire.FireBlast.Push", 0.3);
		config.addDefault("Abilities.Fire.FireBlast.Dissipates", false);
		config.addDefault("Abilities.Fire.FireBlast.Damage", 2);
		config.addDefault("Abilities.Fire.FireBlast.Range", 15.0);
		config.addDefault("Abilities.Fire.FireBlast.Cooldown", 1500);

		//FireBurst
		config.addDefault("Abilities.Fire.FireBurst.Damage", 3);
		config.addDefault("Abilities.Fire.FireBurst.ChargeTime", 2500);
		
		//FireJet
		config.addDefault("Abilities.Fire.FireJet.Speed", 0.7);
		config.addDefault("Abilities.Fire.FireJet.Duration", 1500);
		config.addDefault("Abilities.Fire.FireJet.Cooldown", 6000);
		
		//FireShield
		config.addDefault("Abilities.Fire.FireShield.Radius", 3);
		config.addDefault("Abilities.Fire.FireShield.Ignites", true);
		
		//Illumination
		config.addDefault("Abilities.Fire.Illumination.Range", 5);
		
		//Lightning
		config.addDefault("Abilities.Fire.Lightning.Range", 15);
		config.addDefault("Abilities.Fire.Lightning.Warmup", 3500);
		config.addDefault("Abilities.Fire.Lightning.MissChance", 10.0);
		
		//WallOfFire
		config.addDefault("Abilities.Fire.WallOfFire.Range", 4);
		config.addDefault("Abilities.Fire.WallOfFire.Height", 4);
		config.addDefault("Abilities.Fire.WallOfFire.Width", 4);
		config.addDefault("Abilities.Fire.WallOfFire.Duration", 5000);
		config.addDefault("Abilities.Fire.WallOfFire.Damage", 2);
		config.addDefault("Abilities.Fire.WallOfFire.Cooldown", 7500);
		config.addDefault("Abilities.Fire.WallOfFire.Interval", 500);

		//Chi
		//Settings
		config.addDefault("Abilities.Chi.Settings.BendWithWeapons", false);
		config.addDefault("Abilities.Chi.Settings.ChiBlockDuration", 2500);
		config.addDefault("Abilities.Chi.Settings.DodgeChange", 25.0);
		config.addDefault("Abilities.Chi.Settings.PunchDamage", 3.0);
		config.addDefault("Abilities.Chi.Settings.FallDamageReduction", 50.0);
		
		//HighJump
		config.addDefault("Abilities.Chi.HighJump.Height", 1);
		config.addDefault("Abilities.Chi.HighJump.Cooldown", 10000);

		//Paralyze
		config.addDefault("Abilities.Chi.Paralyze.Cooldown", 15000);
		config.addDefault("Abilities.Chi.Paralyze.Duration", 2000);
		
		//RapidPunch
		config.addDefault("Abilities.Chi.RapidPunch.Damage", 1);
		config.addDefault("Abilities.Chi.RapidPunch.Distance", 1);
		config.addDefault("Abilities.Chi.RapidPunch.Punches", 4);
		config.addDefault("Abilities.Chi.RapidPunch.Cooldown", 15000);

		config.options().copyDefaults(true);
		bending.saveConfig();
		
		ConfigValues.ChatEnabled = config.getBoolean("Chat.enabled");
		ConfigValues.ChatColorsEnabled = config.getBoolean("Chat.colors");
		ConfigValues.ChatCompatibility = config.getBoolean("Chat.Compatibility");
		ConfigValues.ChatFormat = config.getString("Chat.Format");
		ConfigValues.AvatarPrefix = config.getString("Chat.Prefix.Avatar");
		ConfigValues.AirPrefix = config.getString("Chat.Prefix.Air");
		ConfigValues.WaterPrefix = config.getString("Chat.Prefix.Water");
		ConfigValues.EarthPrefix = config.getString("Chat.Prefix.Earth");
		ConfigValues.FirePrefix = config.getString("Chat.Prefix.Fire");
		ConfigValues.ChiPrefix = config.getString("Chat.Prefix.Chi");
		ConfigValues.AvatarColor = config.getString("Chat.Color.Avatar");
		ConfigValues.AirColor = config.getString("Chat.Color.Air");
		ConfigValues.WaterColor = config.getString("Chat.Color.Water");
		ConfigValues.EarthColor = config.getString("Chat.Color.Earth");
		ConfigValues.FireColor = config.getString("Chat.Color.Fire");
		ConfigValues.ChiColor = config.getString("Chat.Color.Chi");

		ConfigValues.BendToItem = config.getBoolean("Options.Bend-To-Item");
		ConfigValues.UseTagAPI = config.getBoolean("Options.Use-TagAPI");
		ConfigValues.SeaLevel = config.getInt("Options.SeaLevel");
		
		ConfigValues.GlobalCooldown = config.getInt("Abilities.GlobalCooldown");
		
		ConfigValues.AirBendWithWeapons = config.getBoolean("Abilities.Air.Settings.BendWithWeapons");
		
		ConfigValues.AirBlastSpeed = config.getInt("Abilities.Air.AirBlast.Speed");
		ConfigValues.AirBlastRange = config.getDouble("Abilities.Air.AirBlast.Range");
		ConfigValues.AirBlastRadius = config.getDouble("Abilities.Air.AirBlast.Radius");
		ConfigValues.AirBlastPush = config.getDouble("Abilities.Air.AirBlast.Push");
		
		ConfigValues.AirBubbleRadius = config.getInt("Abilities.Air.AirBubble.Radius");

		ConfigValues.AirBurstPushFactor = config.getDouble("Abilities.Air.AirBurst.PushFactor");
		ConfigValues.AirBurstChargeTime = config.getInt("Abilities.Air.AirBurst.ChargeTime");
		
		ConfigValues.AirScooterSpeed = config.getDouble("Abilities.Air.AirScooter.Speed");
		ConfigValues.AirScooterRadius = config.getInt("Abilities.Air.AirScooter.Radius");
		
		ConfigValues.AirShieldRadius = config.getDouble("Abilities.Air.AirShield.Radius");
	
		ConfigValues.AirSpoutHeight = config.getDouble("Abilities.Air.AirSpout.Height");
		
		ConfigValues.AirSuctionSpeed = config.getDouble("Abilities.Air.AirSuction.Speed");
		ConfigValues.AirSuctionRange = config.getDouble("Abilities.Air.AirSuction.Range");
		ConfigValues.AirSuctionRadius = config.getDouble("Abilities.Air.AirSuction.Radius");
		ConfigValues.AirSuctionPush = config.getDouble("Abilities.Air.AirSuction.Push");
		
		ConfigValues.AirSwipeDamage = config.getInt("Abilities.Air.AirSwipe.Damage");
		ConfigValues.AirSwipeRadius = config.getDouble("Abilities.Air.AirSwipe.Radius");
		ConfigValues.AirSwipePush = config.getDouble("Abilities.Air.AirSwipe.Push");
		ConfigValues.AirSwipeRange = config.getDouble("Abilities.Air.AirSwipe.Range");
		ConfigValues.AirSwipeArcSize = config.getInt("Abilities.Air.AirSwipe.ArcSize");
		ConfigValues.AirSwipeSpeed = config.getDouble("Abilities.Air.AirSwipe.Speed");
		ConfigValues.AirSwipeCooldown = config.getInt("Abilities.Air.AirSwipe.Cooldown");
		
		ConfigValues.AirPassiveFactor = config.getDouble("Abilities.Air.Passive.Factor");
		
		ConfigValues.TornadoRadius = config.getDouble("Abilities.Air.Tornado.Radius");
		ConfigValues.TornadoHeight = config.getDouble("Abilities.Air.Tornado.Height");
		ConfigValues.TornadoRange = config.getDouble("Abilities.Air.Tornado.Range");
		ConfigValues.TornadoMobPush = config.getDouble("Abilities.Air.Tornado.MobPush");
		ConfigValues.TornadoPlayerPush = config.getDouble("Abilities.Air.Tornado.PlayerPush");

		ConfigValues.WaterBendWithWeapons = config.getBoolean("Abilities.Water.Settings.BendWithWeapons");
		ConfigValues.WaterNightPowerFactor = config.getDouble("Abilities.Water.Settings.NightPowerFactor");
		
		ConfigValues.BloodbendingThrowFactor = config.getDouble("Abilities.Water.Bloodbending.ThrowFactor");
		ConfigValues.BloodbendingRange = config.getInt("Abilities.Water.Bloodbending.Range");
		
		ConfigValues.FastSwimmingFactor = config.getDouble("Abilities.Water.FastSwimming.Factor");
		
		ConfigValues.PhaseChangeRadius = config.getInt("Abilities.Water.PhaseChange.Radius");
		ConfigValues.PhaseChangeRange = config.getInt("Abilities.Water.PhaseChange.Range");

		ConfigValues.HealingWatersInterval = config.getInt("Abilities.Water.HealingWaters.Interval");
		ConfigValues.HealingWatersRadius = config.getDouble("Abilities.Water.HealingWaters.Radius");
		
		ConfigValues.IceSpikeHeight = config.getInt("Abilities.Water.IceSpike.Height");
		ConfigValues.IceSpikeRange = config.getInt("Abilities.Water.IceSpike.Range");
		ConfigValues.IceSpikeCooldown = config.getInt("Abilities.Water.IceSpike.Cooldown");
		ConfigValues.IceSpikeThrowingMult = config.getInt("Abilities.Water.IceSpike.ThrowingMult");
		ConfigValues.IceSpikeDamage = config.getInt("Abilities.Water.IceSpike.Damage");
		
		ConfigValues.OctopusFormRange = config.getInt("Abilities.Water.OctopusForm.Range");
		ConfigValues.OctopusFormRadius = config.getInt("Abilities.Water.OctopusForm.Radius");
		ConfigValues.OctopusFormInterval = config.getInt("Abilities.Water.OctopusForm.Interval");
		ConfigValues.OctopusFormDamage = config.getInt("Abilities.Water.OctopusForm.Damage");
		
		ConfigValues.PlantbendingRegrowthTime = config.getInt("Abilities.Water.Plantbending.RegrowthTime");
		
		ConfigValues.SurgeWallRange = config.getDouble("Abilities.Water.Surge.Wall.Range");
		ConfigValues.SurgeWallRadius = config.getDouble("Abilities.Water.Surge.Wall.Radius");
		ConfigValues.SurgeWaveRadius = config.getDouble("Abilities.Water.Surge.Wave.Radius");
		ConfigValues.SurgeWaveHorizontalPush = config.getDouble("Abilities.Water.Surge.Wave.HorizontalPush");
		ConfigValues.SurgeWaveVerticalPush = config.getDouble("Abilities.Water.Surge.Wave.VerticalPush");
		
		ConfigValues.TorrentRange = config.getInt("Abilities.Water.Torrent.Range");
		ConfigValues.TorrentRadius = config.getInt("Abilities.Water.Torrent.Radius");
		ConfigValues.TorrentDamage = config.getInt("Abilities.Water.Torrent.Damage");
		ConfigValues.TorrentDeflectDamage = config.getInt("Abilities.Water.Torrent.DeflectDamage");
		ConfigValues.TorrentFactor = config.getInt("Abilities.Water.Torrent.Factor");
			    
		ConfigValues.WaterManipulationRange = config.getDouble("Abilities.Water.WaterManipulation.Range");
		ConfigValues.WaterManipulationPush = config.getDouble("Abilities.Water.WaterManipulation.Push");
		ConfigValues.WaterManipulationDamage = config.getInt("Abilities.Water.WaterManipulation.Damage");
		ConfigValues.WaterManipulationSpeed = config.getDouble("Abilities.Water.WaterManipulation.Speed");
		
		ConfigValues.WaterSpoutHeight = config.getInt("Abilities.Water.WaterSpout.Height");
		
		ConfigValues.WaterBubbleRadius = config.getInt("Abilities.Water.WaterBubble.Radius");
		
		ConfigValues.EarthBendWithWeapons = config.getBoolean("Abilities.Earth.Settings.BendWithWeapons");
		ConfigValues.ReverseEarthbending = config.getBoolean("Abilities.Earth.Settings.ReverseEarthbending");
		ConfigValues.ReverseEarthbendingCheckTime = config.getInt("Abilities.Earth.Settings.ReverseEarthbendingCheckTime");
		ConfigValues.EarthBendable = config.getStringList("Abilities.Earth.Settings.EarthBendable");

		ConfigValues.CatapultLength = config.getInt("Abilities.Earth.Catapult.Length");
		ConfigValues.CatapultSpeed = config.getDouble("Abilities.Earth.Catapult.Speed");
		ConfigValues.CatapultPush = config.getDouble("Abilities.Earth.Catapult.Push");
		
		ConfigValues.CollapseRadius = config.getDouble("Abilities.Earth.Collapse.Radius");
		ConfigValues.CollapseRange = config.getInt("Abilities.Earth.Collapse.Range");

		ConfigValues.EarthArmorCooldown = config.getInt("Abilities.Earth.EarthArmor.Cooldown");
		ConfigValues.EarthArmorDuration = config.getInt("Abilities.Earth.EarthArmor.Duration");
		ConfigValues.EarthArmorStrength = config.getInt("Abilities.Earth.EarthArmor.Strength");
		
		ConfigValues.EarthBlastHitSelf = config.getBoolean("Abilities.Earth.EarthBlast.HitSelf");
		ConfigValues.EarthBlastRange = config.getInt("Abilities.Earth.EarthBlast.Range");
		ConfigValues.EarthBlastPrepareRange = config.getInt("Abilities.Earth.EarthBlast.PrepareRange");
		ConfigValues.EarthBlastRevert = config.getBoolean("Abilities.Earth.EarthBlast.Revert");
		ConfigValues.EarthBlastDamage = config.getInt("Abilities.Earth.EarthBlast.Damage");
		ConfigValues.EarthBlastSpeed = config.getDouble("Abilities.Earth.EarthBlast.Speed");
		ConfigValues.EarthBlastPush = config.getDouble("Abilities.Earth.EarthBlast.Push");
		
		ConfigValues.EarthGrabRange = config.getDouble("Abilities.Earth.EarthGrab.Range");
		
		ConfigValues.EarthTunnelRadius = config.getDouble("Abilities.Earth.EarthTunnel.Radius");
		ConfigValues.EarthTunnelMaxRadius = config.getInt("Abilities.Earth.EarthTunnel.MaxRadius");
		ConfigValues.EarthTunnelRange = config.getDouble("Abilities.Earth.EarthTunnel.Range");
		ConfigValues.EarthTunnelRevert = config.getBoolean("Abilities.Earth.EarthTunnel.Revert");
		ConfigValues.EarthTunnelInterval = config.getInt("Abilities.Earth.EarthTunnel.Interval");
		
		ConfigValues.EarthPassiveWaitBeforeRevert = config.getInt("Abilities.Earth.Passive.WaitBeforeRevert");
		
		ConfigValues.RaiseEarthHeight = config.getInt("Abilities.Earth.RaiseEarth.Height");
		ConfigValues.RaiseEarthRange = config.getInt("Abilities.Earth.RaiseEarth.Range");
		ConfigValues.RaiseEarthWidth  = config.getInt("Abilities.Earth.RaiseEarth.Width");
		
		ConfigValues.ShockwaveChargeTime = config.getInt("Abilities.Earth.Shockwave.ChargeTime");
		ConfigValues.TremorsenseMaxDepth = config.getInt("Abilities.Earth.Tremorsense.MaxDepth");
		ConfigValues.TremorsenseRadius = config.getInt("Abilities.Earth.Tremorsense.Radius");
		ConfigValues.TremorsenseLightThreshold = config.getInt("Abilities.Earth.Tremorsense.LightThreshold");
		ConfigValues.TremorsenseCooldown = config.getInt("Abilities.Earth.Tremorsense.Cooldown");

		ConfigValues.FireBendWithWeapons = config.getBoolean("Abilities.Fire.Settings.BendWithWeapons");
		ConfigValues.FireDissipateTime = config.getInt("Abilities.Fire.Settings.DissipateTime");
		ConfigValues.FireDayPowerFactor = config.getDouble("Abilities.Fire.Settings.DayPowerFactor");
		
		ConfigValues.BlazeSize = config.getInt("Abilities.Fire.Blaze.Size");
		ConfigValues.BlazeRange = config.getInt("Abilities.Fire.Blaze.Range");
		
		ConfigValues.HeatControlRange = config.getInt("Abilities.Fire.HeatControl.Range");
		ConfigValues.HeatControlRadius = config.getInt("Abilities.Fire.HeatControl.Radius");

		ConfigValues.FireBlastRadius = config.getDouble("Abilities.Fire.FireBlast.Radius");
		ConfigValues.FireBlastSpeed = config.getDouble("Abilities.Fire.FireBlast.Speed");
		ConfigValues.FireBlastPush = config.getDouble("Abilities.Fire.FireBlast.Push");
		ConfigValues.FireBlastDissipates = config.getBoolean("Abilities.Fire.FireBlast.Dissipates");
		ConfigValues.FireBlastDamage = config.getInt("Abilities.Fire.FireBlast.Damage");
		ConfigValues.FireBlastRange = config.getInt("Abilities.Fire.FireBlast.Range");
		ConfigValues.FireBlastCooldown = config.getInt("Abilities.Fire.FireBlast.Cooldown");
	
		ConfigValues.FireBurstChargeTime = config.getInt("Abilities.Fire.FireBurst.ChargeTime");
		ConfigValues.FireBurstDamage = config.getInt("Abilities.Fire.FireBurst.Damage");
		
		ConfigValues.FireJetSpeed = config.getDouble("Abilities.Fire.FireJet.Speed");
		ConfigValues.FireJetDuration = config.getInt("Abilities.Fire.FireJet.Duration");
		ConfigValues.FireJetCooldown = config.getInt("Abilities.Fire.FireJet.Cooldown");
		
		ConfigValues.FireShieldRadius = config.getInt("Abilities.Fire.FireShield.Radius");
		ConfigValues.FireShieldIgnites = config.getBoolean("Abilities.Fire.FireShield.Ignites");

		ConfigValues.IlluminationRange = config.getInt("Abilities.Fire.Illumination.Range");
		
		ConfigValues.LightningRange = config.getInt("Abilities.Fire.Lightning.Range");
		ConfigValues.LightningWarmup = config.getInt("Abilities.Fire.Lightning.Warmup");
		ConfigValues.LightningMissChance = config.getInt("Abilities.Fire.Lightning.MissChance");
			   
		ConfigValues.WallOfFireRange = config.getInt("Abilities.Fire.WallOfFire.Range");
		ConfigValues.WallOfFireHeight = config.getInt("Abilities.Fire.WallOfFire.Height");
		ConfigValues.WallOfFireWidth = config.getInt("Abilities.Fire.WallOfFire.Width");
		ConfigValues.WallOfFireDuration = config.getInt("Abilities.Fire.WallOfFire.Duration");
		ConfigValues.WallOfFireDamage = config.getInt("Abilities.Fire.WallOfFire.Damage");
		ConfigValues.WallOfFireCooldown = config.getInt("Abilities.Fire.WallOfFire.Cooldown");
		ConfigValues.WallOfFireInterval = config.getInt("Abilities.Fire.WallOfFire.Interval");
			    
		ConfigValues.ChiBendWithWeapons = config.getBoolean("Abilities.Chi.Settings.BendWithWeapons");
		ConfigValues.ChiBlockDuration = config.getInt("Abilities.Chi.Settings.ChiBlockDuration");
		ConfigValues.ChiDodgeChance = config.getDouble("Abilities.Chi.Settings.DodgeChance");
		ConfigValues.ChiPunchDamage = config.getDouble("Abilities.Chi.Settings.PunchDamage");
		ConfigValues.ChiFallDamageReduction = config.getDouble("Abilities.Chi.Settings.FallDamageReduction");
		
		ConfigValues.HighJumpHeight = config.getInt("Abilities.Chi.HighJump.Height");
		ConfigValues.HighJumpCooldown = config.getInt("Abilities.Chi.HighJump.Cooldown");
		
		ConfigValues.ParalyzeCooldown = config.getInt("Abilities.Chi.Paralyze.Cooldown");
		ConfigValues.ParalyzeDuration = config.getInt("Abilities.Chi.Paralyze.Duration");
		
		ConfigValues.RapidPunchDamage = config.getInt("Abilities.Chi.RapidPunch.Damage");
		ConfigValues.RapidPunchDistance = config.getInt("Abilities.Chi.RapidPunch.Distance");
		ConfigValues.RapidPunchPunches = config.getInt("Abilities.Chi.RapidPunch.Punches");
		ConfigValues.RapidPunchCooldown = config.getInt("Abilities.Chi.RapidPunch.Cooldown");

	}
	
	

}
