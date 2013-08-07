package customevents;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import tools.BendingType;

public class PlayerChooseBendingEvent extends Event {

	private static final HandlerList handlers = new HandlerList();

	private Player chooser;
	private Player receiver;
	private BendingType element;
	
	
	public PlayerChooseBendingEvent(Player Chooser, Player Receiver, BendingType Element) {
		chooser = Chooser;
		receiver = Receiver;
		element = Element;
	}
	
	public Player getChooser() {
		return chooser;
	}
	
	public Player getReceiver() {
		return receiver;
	}
	
	public BendingType getElement() {
		return element;
	}
	
	public HandlerList getHandlers() {
		return handlers;
	}
    
    public static HandlerList getHandlerList() {
        return handlers;
    }
}
