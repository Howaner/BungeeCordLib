package de.howaner.BungeeCordLib.event;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class BungeeAddServerEvent extends Event {
	private String name;
	private static HandlerList handlers = new HandlerList();
	
	public BungeeAddServerEvent(String name) {
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
