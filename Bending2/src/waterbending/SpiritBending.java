package waterbending;

import java.util.Arrays;
import java.util.concurrent.ConcurrentHashMap;

import main.ConfigValues;

import org.bukkit.Effect;
import org.bukkit.entity.Enderman;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.PigZombie;
import org.bukkit.entity.Player;
import org.bukkit.entity.Spider;
import org.bukkit.entity.Wolf;
import tools.Abilities;
import tools.Tools;

public class SpiritBending {

	private static ConcurrentHashMap<Player, SpiritBending> instances = new ConcurrentHashMap<Player, SpiritBending>();

	private static final long effecttime = ConfigValues.SpiritBendingTime;
	private static final EntityType[] applicable = { EntityType.ENDERMAN,
		EntityType.PIG_ZOMBIE, EntityType.WOLF};
	int range = 5;

	private Player player;
	private long time;
	private Entity target;

	public SpiritBending(Player player) {
		this.player = player;
		time = System.currentTimeMillis();
		target = Tools.getTargettedEntity(player, Tools.waterbendingNightAugment(range, player.getWorld()));
		if (target != null) {
			if (canSpiritBeBent(target)) {
				instances.put(player, this);
			}
		}
	}

	//	public Cook(Player player) {
	//		this.player = player;
	//		items = player.getItemInHand();
	//		time = System.currentTimeMillis();
	//		if (isCookable(items.getType())) {
	//			instances.put(player, this);
	//		}
	//	}

	private void progress() {
		if (player.isDead() || !player.isOnline()) {
			cancel();
			return;
		}

		if (!player.isSneaking()
				|| Tools.getBendingAbility(player) != Abilities.SpiritBending) {
			cancel();
			return;
		}

		if (!Arrays.asList(applicable).contains(target.getType())) {
			time = System.currentTimeMillis();
		}

		if (System.currentTimeMillis() > time + effecttime) {
			spiritbend();
			time = System.currentTimeMillis();
		}
		
		player.getWorld().playEffect(target.getLocation(), Effect.ENDER_SIGNAL, 0, 10);
	}

	private void cancel() {
		instances.remove(player);
	}

	private boolean canSpiritBeBent(Entity target) {
		if (target != null) {
			if (target.getType() == EntityType.PIG_ZOMBIE) {
				if (((PigZombie) target).isAngry()) {
					return true;
				}
			}
			if (target.getType() == EntityType.WOLF) {
				if (((Wolf) target).isAngry()) {
					return true;
				}
			}
			if (target.getType() == EntityType.ENDERMAN) {
				if (((Enderman) target).getTarget() != null) {
					return true;
				}
			}
			if (target.getType() == EntityType.SPIDER) {
				if (((Spider) target).getTarget() != null) {
					return true;
				}
			}
			return false;
		}
		return false;
	}

	private void spiritbend() {
		if (target.getType() == EntityType.WOLF) {
			Wolf w = (Wolf) target;
			if (w.isAngry()) {
				w.setSitting(true);
				w.setTarget(null);
				w.setAngry(false);
			}
		}
		
		if (target.getType() == EntityType.PIG_ZOMBIE) {
			PigZombie pz = (PigZombie) target;
			if (pz.isAngry()) {
				pz.setTarget(null);
				pz.setAngry(false);
			}
		}
		if (target.getType() == EntityType.ENDERMAN) {
			Enderman ender = (Enderman) target;
			ender.setTarget(null);
		}
		if (target.getType() == EntityType.SPIDER) {
			Spider spider = (Spider) target;
			spider.setTarget(null);
		}

	}
	
	public static void progressAll() {
		for (Player player : instances.keySet()) {
			instances.get(player).progress();
		}
	}

	public static void removeAll() {
		instances.clear();
	}
	
	public static String getDescription() {
		return "This is a utility ability available to waterbenders that allows the bender to calm target mobs. "
				+ "To use, hold sneak (Default: Shift) while targeting an angry mob (Wolf / Pig Zombie / Enderman / Spider). "
				+ "If you hold sneak long enough, you will calm the mob.";
	}

}
