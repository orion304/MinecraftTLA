package firebending;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import main.Bending;

import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import tools.Abilities;
import tools.AvatarState;
import tools.ConfigManager;
import tools.Tools;
import waterbending.Plantbending;

public class FireBlast {

	public static ConcurrentHashMap<Integer, FireBlast> instances = new ConcurrentHashMap<Integer, FireBlast>();
	private static ConcurrentHashMap<Player, Long> timers = new ConcurrentHashMap<Player, Long>();
	static final long soonesttime = ConfigManager.fireBlastCooldown;

	private static int ID = Integer.MIN_VALUE;
	static final int maxticks = 10000;

	private static double speed = ConfigManager.fireBlastSpeed;
	static double affectingradius = ConfigManager.fireBlastRadius;
	private static double pushfactor = ConfigManager.fireBlastPush;
	private static boolean canPowerFurnace = true;
	static boolean dissipate = ConfigManager.fireBlastDissipate;
	// public static long interval = 2000;
	public static byte full = 0x0;

	private Location location;
	private List<Block> safe = new ArrayList<Block>();
	private Location origin;
	private Vector direction;
	private Player player;
	private int id;
	private double speedfactor;
	private int ticks = 0;
	private int damage = ConfigManager.fireBlastDamage;
	double range = ConfigManager.fireBlastRange;

	// private ArrayList<Block> affectedlevers = new ArrayList<Block>();

	// private long time;

	public FireBlast(Player player) {
		if (timers.containsKey(player)) {
			if (System.currentTimeMillis() < timers.get(player) + soonesttime) {
				return;
			}
		}
		if (player.getEyeLocation().getBlock().isLiquid()
				|| Fireball.isCharging(player)) {
			return;
		}
		range = Tools.firebendingDayAugment(range, player.getWorld());
		timers.put(player, System.currentTimeMillis());
		this.player = player;
		location = player.getEyeLocation();
		origin = player.getEyeLocation();
		direction = player.getEyeLocation().getDirection().normalize();
		location = location.add(direction.clone());
		id = ID;
		instances.put(id, this);
		if (ID == Integer.MAX_VALUE)
			ID = Integer.MIN_VALUE;
		ID++;
		// time = System.currentTimeMillis();
		timers.put(player, System.currentTimeMillis());
	}

	public FireBlast(Location location, Vector direction, Player player,
			int damage, List<Block> safeblocks) {
		if (location.getBlock().isLiquid()) {
			return;
		}
		safe = safeblocks;
		range = Tools.firebendingDayAugment(range, player.getWorld());
		timers.put(player, System.currentTimeMillis());
		this.player = player;
		this.location = location.clone();
		origin = location.clone();
		this.direction = direction.clone().normalize();
		this.damage *= 1.5;
		id = ID;
		instances.put(id, this);
		if (ID == Integer.MAX_VALUE)
			ID = Integer.MIN_VALUE;
		ID++;
	}

	public boolean progress() {
		if (player.isDead() || !player.isOnline()) {
			instances.remove(id);
			return false;
		}

		if (Tools.isRegionProtectedFromBuild(player, Abilities.Blaze, location)) {
			instances.remove(id);
			return false;
		}

		speedfactor = speed * (Bending.time_step / 1000.);

		ticks++;

		if (ticks > maxticks) {
			instances.remove(id);
			return false;
		}

		// if (player.isSneaking()
		// && Tools.getBendingAbility(player) == Abilities.AirBlast) {
		// new AirBlast(player);
		// }

		Block block = location.getBlock();
		// for (Block testblock : Tools.getBlocksAroundPoint(location,
		// affectingradius)) {
		// if (testblock.getType() == Material.FIRE) {
		// testblock.setType(Material.AIR);
		// testblock.getWorld().playEffect(testblock.getLocation(),
		// Effect.EXTINGUISH, 0);
		// }
		// if (((block.getType() == Material.LEVER) || (block.getType() ==
		// Material.STONE_BUTTON))
		// && !affectedlevers.contains(block)) {
		// EntityHuman eH = ((CraftPlayer) player).getHandle();
		//
		// net.minecraft.server.Block.byId[block.getTypeId()].interact(
		// ((CraftWorld) block.getWorld()).getHandle(),
		// block.getX(), block.getY(), block.getZ(), eH);
		//
		// affectedlevers.add(block);
		// }
		// }
		if (Tools.isSolid(block) || block.isLiquid()) {
			if (block.getType() == Material.FURNACE && canPowerFurnace) {
				// BlockState state = block.getState();
				// Furnace furnace = (Furnace) state;
				// FurnaceInventory inv = furnace.getInventory();
				// if (inv.getFuel() == null) {
				// ItemStack temp = inv.getSmelting();
				// ItemStack tempfuel = new ItemStack(Material.WOOD_AXE, 1);
				// ItemStack tempsmelt = new ItemStack(Material.COBBLESTONE);
				// inv.setFuel(tempfuel);
				// inv.setSmelting(tempsmelt);
				// state.update(true);
				// inv.setSmelting(temp);
				// state.update(true);
				// }
			} else if (FireStream.isIgnitable(player,
					block.getRelative(BlockFace.UP))) {
				ignite(location);
			}
			instances.remove(id);
			return false;
		}

		if (location.distance(origin) > range) {
			instances.remove(id);
			return false;
		}

		Tools.removeSpouts(location, player);

		for (Entity entity : Tools.getEntitiesAroundPoint(location,
				affectingradius)) {
			affect(entity);
			if (entity instanceof LivingEntity)
				break;
		}

		advanceLocation();

		return true;
	}

	private void advanceLocation() {
		location.getWorld().playEffect(location, Effect.MOBSPAWNER_FLAMES, 0,
				(int) range);
		location = location.add(direction.clone().multiply(speedfactor));
	}

	private void ignite(Location location) {
		for (Block block : Tools
				.getBlocksAroundPoint(location, affectingradius)) {
			if (FireStream.isIgnitable(player, block) && !safe.contains(block)) {
				if (Tools.isPlant(block))
					new Plantbending(block);
				block.setType(Material.FIRE);
				if (dissipate) {
					FireStream.ignitedblocks.put(block, player);
					FireStream.ignitedtimes.put(block,
							System.currentTimeMillis());
				}
			}
		}
	}

	public static boolean progress(int ID) {
		return instances.get(ID).progress();
	}

	public static void progressAll() {
		for (int id : instances.keySet()) {
			progress(id);
		}
	}

	private void affect(Entity entity) {
		if (entity.getEntityId() != player.getEntityId()) {
			if (AvatarState.isAvatarState(player)) {
				entity.setVelocity(direction.clone().multiply(
						AvatarState.getValue(pushfactor)));
			} else {
				entity.setVelocity(direction.clone().multiply(pushfactor));
			}
			if (entity instanceof LivingEntity) {
				entity.setFireTicks(50);
				Tools.damageEntity(player, entity, (int) Tools
						.firebendingDayAugment((double) damage,
								entity.getWorld()));
				new Enflamed(entity, player);
				instances.remove(id);
			}
		}
	}

	public static void removeFireBlastsAroundPoint(Location location,
			double radius) {
		for (int id : instances.keySet()) {
			Location fireblastlocation = instances.get(id).location;
			if (location.getWorld() == fireblastlocation.getWorld()) {
				if (location.distance(fireblastlocation) <= radius)
					instances.remove(id);
			}
		}
		Fireball.removeFireballsAroundPoint(location, radius);
	}

	public static void removeAll() {
		for (int id : instances.keySet()) {
			instances.remove(id);
		}
	}

	public static String getDescription() {
		return "FireBlast is the most fundamental bending technique of a firebender. "
				+ "To use, simply left-click in a direction. A blast of fire will be created at your fingertips. "
				+ "If this blast contacts an enemy, it will dissipate and engulf them in flames, "
				+ "doing additional damage and knocking them back slightly. "
				+ "If the blast hits terrain, it will ignite the nearby area. "
				+ "Additionally, if you hold sneak, you will charge up the fireblast. "
				+ "If you release it when it's charged, it will instead launch a powerful "
				+ "fireball that explodes on contact.";
	}

}
