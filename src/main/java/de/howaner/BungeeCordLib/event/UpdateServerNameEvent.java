package de.howaner.BungeeCordLib.event;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

/**
 * This was call, when BungeeCord sends the Servername
 */
public class UpdateServerNameEvent extends Event {
	private String name;
	private static HandlerList handlers = new HandlerList();
	
	public UpdateServerNameEvent(String name) {
		this.name = name;
	}
	
	public String getName() {
		return this.name;
	}
	
	@Override
	public HandlerList getHandlers() {
		return handlers;
	}
	
	public static HandlerList getHandlerList() {
		return handlers;
	}
	
}
