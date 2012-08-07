package chiblocker;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import net.minecraft.server.Packet18ArmAnimation;

import org.bukkit.craftbukkit.entity.CraftPlayer;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

import tools.Tools;

public class RapidPunch {
	
	private static int damage = 1;
	private int distance = 8;
	private long cooldown = 15000;
	private static int punches = 2;
	
	private static Map<String, Long> cooldowns = new HashMap<String,Long>();
	public static ConcurrentHashMap<Player, Entity> targets = new ConcurrentHashMap<Player, Entity>();
	private static Map<String, Integer> numpunches = new HashMap<String, Integer>();
	public static Map<String, Long> timers = new HashMap<String, Long>();
	public static List<Player> punching = new ArrayList<Player>();
	
	public RapidPunch(Player p) {//, Entity t) {
		if (targets.contains(p))
			return;
		if (cooldowns.containsKey(p.getName()) && cooldowns.get(p.getName()) + cooldown >= System.currentTimeMillis())
			return;
		double lowestdistance = distance + 1;
		Entity t = null;
		for (Entity entity : Tools.getEntitiesAroundPoint(p.getEyeLocation(), distance)) {
			if (Tools.getDistanceFromLine(p.getEyeLocation().getDirection(), p.getEyeLocation(),
					entity.getLocation()) <= 2
					&& (entity instanceof LivingEntity)
					&& (entity.getEntityId() != p.getEntityId())) {
				double distance = p.getEyeLocation().distance(entity.getLocation());
				if (distance < lowestdistance) {
					t = entity;
					lowestdistance = distance;
				}
			}
		}
		if (t == null)
			return;
		targets.put(p , t);
		numpunches.put(p.getName(), 0);
	}

	public static void startPunch(Player p) {
		if (numpunches.get(p.getName()) >= punches)
			//punching.remove(p);
			targets.remove(p);
		Entity t = targets.get(p);
		//((LivingEntity)t).setHealth(((LivingEntity)t).getHealth() - damage);
		if (t instanceof LivingEntity && t != null){
			//Tools.damageEntity(p, t, damage);
			LivingEntity lt = (LivingEntity)t;
			lt.setNoDamageTicks(0);
			//int finalhealth = (lt.getHealth() - damage < 0)? 0:(lt.getHealth() - damage);
			//lt.setHealth(finalhealth);
			lt.damage(damage, p);
			lt.setNoDamageTicks(0);
		}
		//((LivingEntity)t).damage(damage);
		//Tools.damageEntity(p, t, damage);
		cooldowns.put(p.getName(), System.currentTimeMillis());
		swing(p);
		int incr = numpunches.get(p.getName());
		incr++;
		numpunches.put(p.getName(), incr);
		
	}

	private static void swing(Player p) {
		//punching.add(p);
		timers.put(p.getName(), System.currentTimeMillis());
		Packet18ArmAnimation packet = new Packet18ArmAnimation();
		packet.a = p.getEntityId();
		packet.b = (byte)1;
		for (Player observer : p.getWorld().getPlayers())
		        ((CraftPlayer)observer).getHandle().netServerHandler.sendPacket(packet);
				Tools.verbose("Swingning" + p.getName());
	}

}
