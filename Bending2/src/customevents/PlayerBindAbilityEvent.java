package customevents;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class PlayerBindAbilityEvent extends Event {
	
	private static final HandlerList handlers = new HandlerList();
    
    private Player player;
    private String ability;
    private String material;
    private int slot;
    
    public PlayerBindAbilityEvent(Player p, String Ability, int Slot, String Material) {
    	player = p;
    	ability = Ability;
    	slot = Slot;
    	material = Material;
    }
    
    public HandlerList getHandlers() {
		return handlers;
	}
    
    public static HandlerList getHandlerList() {
        return handlers;
    }
    
    public Player getPlayer() {
    	return player;
    }
    
    public String getAbility() {
    	return ability;
    }
    
    public String getMaterial() {
    	return material;
    }
    
    public int getSlot() {
    	return slot;
    }

}
