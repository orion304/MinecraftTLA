package waterbending;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.Location;
import org.bukkit.Server;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import tools.BendingType;
import tools.ConfigManager;
import tools.Tools;

public class FastSwimming {
	
	private static Map<Player, Location> locations = new HashMap<Player, Location>();
	private static Map<Player, Long> timers = new HashMap<Player, Long>();
	private static long interval = ConfigManager.fastSwimmingInterval;
	private static double factor = ConfigManager.fastSwimmingFactor;
	
	public static void HandleSwim(Server s){
		for (Player p: s.getOnlinePlayers()){
			if (Tools.isBender(p, BendingType.Water) 
					&& Tools.canBendPassive(p, BendingType.Water)
					&& p.getLocation().getBlock().isLiquid()
					&& !timers.containsKey(p)){
			    timers.put(p, System.currentTimeMillis());
			}
			if (timers.containsKey(p)){
				if (timers.get(p) + (interval - 21) >= System.currentTimeMillis()){
				    locations.put(p, p.getLocation().getBlock().getLocation());
				}
			}
			if (timers.containsKey(p)){
			if (!(timers.get(p) + interval >= System.currentTimeMillis())
					&& locations.containsKey(p)
					&& ((int)locations.get(p).getX() != (int)p.getLocation().getBlock().getLocation().getX()
					|| (int)locations.get(p).getZ() != (int)p.getLocation().getBlock().getLocation().getZ())
					&& p.getLocation().getBlock().isLiquid()){
					
					if (!p.getEyeLocation().getBlock().isLiquid()){
						timers.put(p, System.currentTimeMillis());
						Vector v = p.getLocation().getDirection().setY(0);
						p.setVelocity(v.normalize().multiply(factor));
					} else {
						timers.put(p, System.currentTimeMillis());
						Vector v  = p.getLocation().getDirection().normalize().multiply(factor);
						p.setVelocity(v);
					}
				}
			}
		}
	}
}

