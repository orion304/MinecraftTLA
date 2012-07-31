package firebending;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;

import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;
import org.getspout.spoutapi.SpoutManager;
import org.getspout.spoutapi.particle.Particle;
import org.getspout.spoutapi.particle.Particle.ParticleType;
import org.getspout.spoutapi.player.SpoutPlayer;

import tools.ConfigManager;

public class Lightning {
	
	public static int distance = ConfigManager.lightningrange;
	private static long warmup = ConfigManager.lightningwarmup;
	private static long duration = 3000;

	private static int ID = Integer.MIN_VALUE;
	private int id;
	private Player player;
	public static ConcurrentHashMap<Integer, Lightning> instances = new ConcurrentHashMap<Integer, Lightning>();
	public static Map<Player, Long> warmups = new HashMap<Player, Long>();
	public static Map<Player, Long> durations = new HashMap<Player, Long>();
	public static List<Player> ready = new ArrayList<Player>();
	public static List<Block> blocks = new ArrayList<Block>();
	public static Map <Player, Location> playerlocs = new HashMap<Player, Location>();
	private static Random ran = new Random();

	
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
		SpoutPlayer splayer = SpoutManager.getPlayer(player);
		if (warmups.containsKey(player)){
			if (warmups.get(player) + warmup <= System.currentTimeMillis()){
				if (!ready.contains(player))
					ready.add(player);
				if (!splayer.isSpoutCraftEnabled()) {
					player.getLocation().getWorld().playEffect(player.getLocation(), Effect.MOBSPAWNER_FLAMES, 1);
			} else {
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
			SpoutPlayer splayer = SpoutManager.getPlayer(player);
			if (!splayer.isSpoutCraftEnabled()){
				Block tblock = player.getTargetBlock(null, 20);
				tblock.getWorld().strikeLightning(tblock.getLocation());
			} else {				
				Location loc = player.getEyeLocation();
				List<Location> locs = new ArrayList<Location>();
				locs.add(loc);
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
	
	public static void progress(int ID){
		Player player = instances.get(ID).player;
		if (durations.containsKey(player)){
			if (durations.get(player) + duration >= System.currentTimeMillis()){
		blocks = player.getLineOfSight(null, distance);
		for (Block b: blocks){
			new Particle(ParticleType.REDDUST, b.getLocation(), new Vector((ran.nextFloat() - 0.5) * 0.1 , 0.6, (ran.nextFloat() - 0.5) * 0.1)).setParticleBlue(250 / 237).setParticleGreen(149 / 250).setParticleRed(100 / 250).setGravity(0f).setMaxAge(30).setAmount(1).setScale(0.2f).spawn();
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
