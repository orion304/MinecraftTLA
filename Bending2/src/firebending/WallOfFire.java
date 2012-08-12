package firebending;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.util.Vector;

import firebending.FireBlast;
import firebending.FireStream;

import tools.ConfigManager;
import tools.Tools;

public class WallOfFire {

	public static Map<Player, WallOfFire> instance = new HashMap<Player, WallOfFire>();
	
	private static int range = ConfigManager.wallOfFireRange;
	private int height = ConfigManager.wallOfFireHeight;
	private int width = ConfigManager.wallOfFireWidth;
	private long duration = ConfigManager.wallOfFireDuration;
	private int damage = ConfigManager.wallOfFireDamage;
	private static long interval = ConfigManager.wallOfFireInterval;
	private static long cooldown = ConfigManager.wallOfFireCooldown;
	
	private Location playerLoc;
	private Player player;
	private long timer;
	private List<Block> affectedblocks = new ArrayList<Block>();
	private List<Entity> damaged = new ArrayList<Entity>();
	
	public WallOfFire(Player player){
		if (instance.containsKey(player))
			return; 
		
		playerLoc = player.getLocation();
		this.player = player;
		loadAffectedBlock();
		int testopenair = 0;
		for (Block b : affectedblocks){
			if (b.getType() == Material.AIR
					|| b.getType() == Material.SNOW
					|| b.getType() == Material.RED_MUSHROOM
					|| b.getType() == Material.BROWN_MUSHROOM
					|| b.getType() == Material.DEAD_BUSH 
					|| b.getType() == Material.LONG_GRASS){
				testopenair++;
			}
		}
		if (testopenair > ((height * width) * 0.5))
			instance.put(player, this);
	}
	
	public void progress(){
		for (Block b : affectedblocks){
			if (timer + interval <= System.currentTimeMillis())
				b.getLocation().getWorld().playEffect(b.getLocation(), Effect.MOBSPAWNER_FLAMES, 1, 20);
			List<Entity> entities = Tools.getEntitiesAroundPoint(b.getLocation(), 1.4);
			FireBlast.removeFireBlastsAroundPoint(b.getLocation(), 2);
			for (Entity en: entities){
				knockbackEntities(en, b.getLocation());
				if (!(en instanceof Projectile)
						&& en.getLocation().distance(b.getLocation()) < 1.1){
					Tools.damageEntity(player, en, damage);
					damaged.add(en);
				}
			}
		}
		if (timer + interval <= System.currentTimeMillis()){
			timer = System.currentTimeMillis();
			damaged.removeAll(damaged);
		}		
	}
	
	public void knockbackEntities(Entity en, Location loc){
		en.setVelocity(new Vector((en.getLocation()
				.getX() - loc.getBlock()
				.getLocation().getX()) * 0.3, 0.1,
				(en.getLocation().getZ() - loc
						.getBlock().getLocation()
						.getZ()) * 0.3));
	}
	
	public void loadAffectedBlock(){		
		Vector direction = playerLoc.getDirection().normalize();
		Vector orth = new Vector(-direction.getZ(), 0, direction.getX());
		orth = orth.normalize();
		for (int i = -width; i <= width; i++) {
			Block block = playerLoc.getWorld().getBlockAt(
					playerLoc.clone().add(
							orth.clone().multiply(
									(double) i)));
			for (int y = block.getY(); y <= block.getY()
					+ height; y++) {
				Location loca = new Location(
						block.getWorld(), block.getX(),
						(int) y, block.getZ());
				affectedblocks.add(playerLoc.getWorld().getBlockAt(loca));
			}
		}
	}
	
	public static String getDescription() {
		return "To use this ability, click at a location. A wall of fire "
				+ "will appear at this location, igniting enemies caught in it "
				+ "and blocking projectiles.";
	}
}
