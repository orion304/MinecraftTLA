package chiblocking;

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
	private static int punches = 5;

	private static Map<String, Long> cooldowns = new HashMap<String, Long>();
	public static ConcurrentHashMap<Player, RapidPunch> instance = new ConcurrentHashMap<Player, RapidPunch>();
	private int numpunches;
	//private long timers;
	private Entity target;
	public static List<Player> punching = new ArrayList<Player>();

	public RapidPunch(Player p) {// , Entity t) {
		if (instance.contains(p))
			return;
		if (cooldowns.containsKey(p.getName())
				&& cooldowns.get(p.getName()) + cooldown >= System
						.currentTimeMillis())
			return;
		
		Entity t = Tools.getTargettedEntity(p, distance);
		
		if (t == null)
			return;
		
		target = t;
		numpunches= 0;
		instance.put(p, this);
		Tools.verbose("PUNCH MOFO");
	}

	public void startPunch(Player p) {
		if (numpunches >= punches)
			instance.remove(p);
		if (target instanceof LivingEntity && target != null) {
			LivingEntity lt = (LivingEntity) target;
			Tools.damageEntity(p, target, damage);
			lt.setNoDamageTicks(0);
			Tools.verbose("PUNCHIN MOFO");
		}
		cooldowns.put(p.getName(), System.currentTimeMillis());
		swing(p);
		numpunches++;
	}

	private void swing(Player p) {
		// punching.add(p);
		//timers = System.currentTimeMillis();
		Packet18ArmAnimation packet = new Packet18ArmAnimation();
		packet.a = p.getEntityId();
		packet.b = (byte) 1;
		for (Player observer : p.getWorld().getPlayers())
			((CraftPlayer) observer).getHandle().netServerHandler
					.sendPacket(packet);
	}

	public static String getDescription() {
		return "This ability allows the chiblocker to punch rapidly in a short period. To use, simply punch."
				+ " This has a short cooldown.";
	}

}