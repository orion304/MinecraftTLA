package chiblocking;

import main.ConfigValues;

import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import tools.Abilities;
import tools.BendingPlayer;
import tools.Tools;

public class HighJump {

	private int jumpheight = ConfigValues.HighJumpHeight;
//	private long cooldown = ConfigValues.HighJumpCooldown;

	// private Map<String, Long> cooldowns = new HashMap<String, Long>();

	public HighJump(Player p) {
		BendingPlayer bPlayer = BendingPlayer.getBendingPlayer(p);

		if (bPlayer.isOnCooldown(Abilities.HighJump))
			return;
		// if (cooldowns.containsKey(p.getName())
		// && cooldowns.get(p.getName()) + cooldown >= System
		// .currentTimeMillis())
		// return;
		jump(p);
	}

	private void jump(Player p) {
		if (!Tools.isSolid(p.getLocation().getBlock()
				.getRelative(BlockFace.DOWN)))
			return;
		Vector vec = p.getVelocity();
		vec.setY(jumpheight);
		p.setVelocity(vec);
		// cooldowns.put(p.getName(), System.currentTimeMillis());
		BendingPlayer.getBendingPlayer(p).cooldown(Abilities.HighJump);
		return;
	}

	public static String getDescription() {
		return "To use this ability, simply click. You will jump quite high. This ability has a short cooldown.";
	}
}
