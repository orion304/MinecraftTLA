package firebending;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;

import org.bukkit.Bukkit;
import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;
import org.getspout.spoutapi.particle.Particle;
import org.getspout.spoutapi.particle.Particle.ParticleType;

import tools.ConfigManager;
import tools.Tools;

public class Lightning {
	
	public static int distance = ConfigManager.lightningrange;
	private static long warmup = ConfigManager.lightningwarmup;
	private static long duration = 3000;
	public static int damage = ConfigManager.lightningdamage;

	private static int ID = Integer.MIN_VALUE;
	private int id;
	private Player player;
	public static ConcurrentHashMap<Integer, Lightning> instances = new ConcurrentHashMap<Integer, Lightning>();
	public static Map<Player, Long> warmups = new HashMap<Player, Long>();
	public static Map<Player, Long> durations = new HashMap<Player, Long>();
	public static List<Player> ready = new ArrayList<Player>();
	public static Map<Player, List<Location>> locations = new HashMap<Player, List<Location>>();
	public static Map <Player, Location> playerlocs = new HashMap<Player, Location>();
	private static Random ran = new Random();
	private static Plugin sp = Bukkit.getPluginManager().getPlugin("SpoutPlugin");

	
	public Lightning (Player player){
		if (ID >= Integer.MAX_VALUE) {
			ID = Integer.MIN_VALUE;
		}
		id = ID++;
		this.player = player;
		instances.put(id, this);
		warmups.put(player, System.currentTimeMillis());
		
	}
	
	public static void ChargingLightning(int ID){
		Player player = instances.get(ID).player;
		if (warmups.containsKey(player)){
			if (warmups.get(player) + warmup <= System.currentTimeMillis()){
				if (!ready.contains(player))
					ready.add(player);
					player.addPotionEffect(new PotionEffect(PotionEffectType.FIRE_RESISTANCE, 2, 1), false);
					if (sp != null && ConfigManager.lightningspout){
				Random ran = new Random();
				Location loc = player.getEyeLocation();
				loc.setY(player.getEyeLocation().getY() - 0.3);
					Particle part = new Particle(ParticleType.SNOWBALLPOOF, loc, new Vector((ran.nextFloat() - 0.5f) * 0.35f, -0.4f, (ran.nextFloat() - 0.5) * 0.35f)).setParticleBlue(250 / 237).setParticleGreen(149 / 250).setParticleRed(100 / 250).setGravity(1).setMaxAge(18).setAmount(1).setScale(0.35f);
					part.spawn();
				}
			}
		}
	}
	
	public static void StrikeLightning (Player player){
		if (ready.contains(player)){
			ready.remove(player);
			warmups.remove(player);
				Block tblock = player.getTargetBlock(null, 20);
				tblock.getWorld().strikeLightningEffect(tblock.getLocation());
				for (Entity entity: Tools.getEntitiesAroundPoint(tblock.getLocation(), 2))
					Tools.damageEntity(player, entity, damage);
			if (sp != null && ConfigManager.lightningspout){			
				Location loc = player.getEyeLocation();
				List<Location> locs = new ArrayList<Location>();
				locs.add(loc);
				populateMap(player);
				//spoutLocs.put(player, locs);
				//playerlocs.put(player, loc);
				durations.put(player, System.currentTimeMillis());
				//for (int i = 0; i < distance; i++){
				//	new Particle(ParticleType.SNOWBALLPOOF, loc, new Vector((ran.nextFloat() - 0.5) * 0.35 , 0, (ran.nextFloat() - 0.5) * 0.35)).setParticleBlue(250 / 237).setParticleGreen(149 / 250).setParticleRed(100 / 250).setGravity(0).setMaxAge(18000).setAmount(2).setScale(0.35f).spawn();
				//}
				
				//if (loc.distance(player.getEyeLocation()) > distance) {
				//	loc.getWorld().strikeLightningEffect(loc);
				//}
				
			}
		}
	}
	
	//private static void advanceLocation(Player player){
		//List<Location> locs = spoutLocs.get(player);
		//Location loc = locs.get(locs.size() - 1);
		//Location newloc = loc.add(loc.getDirection().clone().multiply(1));
		//Location playerloc = playerlocs.get(player);
		//if (player.getLocation().distance(newloc) <= distance){
		//	locs.add(newloc);
		//	Tools.verbose(newloc);
			//Tools.verbose("Dist : " + playerloc.distance(newloc)
				//	+ "  Player X: " +  (int)playerloc.getX() + "  Player Z: " +  (int)playerloc.getZ()
					//+ "  New loc X: " + (int)newloc.getX() + "  New loc Z: " + (int)newloc.getZ());//.distance(playerlocs.get(player)));
		//}
		//if (!locs.contains(newloc)){
			//}
		//for (Location loca :spoutLocs.get(player)){
		//		newlocs.add(loca);
		//}
		//if (newlocs.contains(newloc))
		//	Tools.verbose("FUCKYEA");
		//	newlocs.add(newloc);
		//spoutLocs.put(player, locs);
		//Tools.verbose(newlocs.size());
	//}
	
	public static void populateMap(Player p){
		List<Location> locs = new ArrayList<Location>();
		Location loc = null;
		Vector vec = p.getLocation().getDirection();
		int playerZ = (int) p.getLocation().getZ();
		int playerX = (int) p.getLocation().getX();
		for (Block b: p.getLineOfSight(null, distance)){
			//loc = b.getLocation();
			//locs.add(loc);
			loc = b.getLocation().add(b.getLocation().getDirection().clone().multiply(0.1));
			loc.setX(loc.getX() + (p.getLocation().getX() - (playerX - 1)));
			loc.setZ(loc.getZ() + (p.getLocation().getZ() - playerZ));
			locs.add(loc);
			//loc = b.getLocation().add(vec.multiply(-0.2));
			//locs.add(loc);
			//loc = b.getLocation().add(vec.multiply(-0.8));
			//locs.add(loc);
		}
		locations.put(p, locs);
	}
	
	public static void progress(int ID){
		Player player = instances.get(ID).player;
		if (durations.containsKey(player)){
			if (durations.get(player) + duration >= System.currentTimeMillis()){
		for (Location loc: locations.get(player)){
			new Particle(ParticleType.REDDUST, loc, new Vector((ran.nextFloat() - 0.5) * 0.1 , 0.6, (ran.nextFloat() - 0.5) * 0.1)).setParticleBlue(250 / 237).setParticleGreen(149 / 250).setParticleRed(100 / 250).setGravity(0f).setMaxAge(30).setAmount(1).setScale(0.01f).spawn();
		}
			}
		}
			//if (spoutLocs.containsKey(player)){
				//List<Location> locs = spoutLocs.get(player);
				//Tools.verbose(locs.size());
				//for (int i = 0; i < locs.size(); i++){
				//	part = new Particle(ParticleType.SNOWBALLPOOF, locs.get(i), new Vector((ran.nextFloat() - 0.5) * 0.35 , 0, (ran.nextFloat() - 0.5) * 0.35)).setParticleBlue(250 / 237).setParticleGreen(149 / 250).setParticleRed(100 / 250).setGravity(0).setMaxAge(18).setAmount(1).setScale(0.35f);
				//	part.spawn();
				//}
				//advanceLocation(player);
			//}
	}

	public static String getDescription() {
		return "Hold sneak while selecting this ability to charge up a lightning strike. Once "
				+ "charged, release sneak to discharge the lightning to the targetted location.";
	}

}
